#!/usr/bin/env bash

set -euo pipefail

# Blue/Green rollout
# - inactive app 컨테이너에 새 버전 배포
# - health check 성공 시 Nginx upstream 전환
# - mysql 컨테이너는 유지

# echo 출력 시 글자 색상 지정
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

info()    { echo -e "${BLUE}[INFO]${NC} $*"; }
success() { echo -e "${GREEN}[SUCCESS]${NC} $*"; }
warn()    { echo -e "${YELLOW}[WARNING]${NC} $*"; }
error()   { echo -e "${RED}[ERROR]${NC} $*" >&2; }

KEEP_OLD=false
ROLLBACK=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --keep-old)
      KEEP_OLD=true
      shift
      ;;
    --rollback)
      ROLLBACK=true
      KEEP_OLD=true
      shift
      ;;
    *)
      error "알 수 없는 옵션입니다: $1"
      echo "사용법: $0 [--keep-old] [--rollback]"
      exit 1
      ;;
  esac
done

# 기본 경로 및 설정
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.yml}"
ENV_FILE="${ENV_FILE:-.env}"
NGINX_CONF="${NGINX_CONF:-/etc/nginx/sites-available/kwcapstone}"
CONTAINER_PREFIX="${CONTAINER_PREFIX:-kwcapstone-app}"

cd "$PROJECT_ROOT"

info "프로젝트 경로: $PROJECT_ROOT"
info "Compose 파일: $COMPOSE_FILE"
info "env: $ENV_FILE"
info "Nginx 설정 파일: $NGINX_CONF"

if [[ ! -f "$COMPOSE_FILE" ]]; then
  error "Compose 파일을 찾을 수 없습니다: $PROJECT_ROOT/$COMPOSE_FILE"
  exit 1
fi

if [[ ! -f "$ENV_FILE" ]]; then
  error ".env 파일을 찾을 수 없습니다: $PROJECT_ROOT/$ENV_FILE"
  exit 1
fi

if [[ ! -f "$NGINX_CONF" ]]; then
  error "Nginx 설정 파일을 찾을 수 없습니다: $NGINX_CONF"
  exit 1
fi

if ! command -v docker >/dev/null 2>&1; then
  error "docker 명령어를 찾을 수 없습니다."
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  error "docker compose를 사용할 수 없습니다."
  exit 1
fi

DOCKER_COMPOSE=(docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE")

# Nginx가 현재 blue/green 중 어느 컨테이너를 바라보고 있는지 판단
ACTIVE_LINE="$(grep 'ACTIVE_CONTAINER' "$NGINX_CONF" || true)"

if [[ -z "$ACTIVE_LINE" ]]; then
  error "Nginx 설정에서 ACTIVE_CONTAINER 주석을 찾을 수 없습니다."
  error "예: server 127.0.0.1:8080;  # ACTIVE_CONTAINER"
  exit 1
fi

if echo "$ACTIVE_LINE" | grep -Eq '127\.0\.0\.1:8080|localhost:8080'; then
  ACTIVE="blue"
  ACTIVE_PORT=8080
  INACTIVE="green"
  INACTIVE_PORT=8081
elif echo "$ACTIVE_LINE" | grep -Eq '127\.0\.0\.1:8081|localhost:8081'; then
  ACTIVE="green"
  ACTIVE_PORT=8081
  INACTIVE="blue"
  INACTIVE_PORT=8080
else
  error "현재 active 포트를 판단할 수 없습니다."
  error "현재 ACTIVE_CONTAINER 라인: $ACTIVE_LINE"
  exit 1
fi

ACTIVE_SERVICE="app-$ACTIVE"
INACTIVE_SERVICE="app-$INACTIVE"
ACTIVE_CONTAINER="${CONTAINER_PREFIX}-${ACTIVE}"
INACTIVE_CONTAINER="${CONTAINER_PREFIX}-${INACTIVE}"

info "현재 active 환경: $ACTIVE ($ACTIVE_PORT)"
info "배포 대상 inactive 환경: $INACTIVE ($INACTIVE_PORT)"

# Health Check 함수
wait_for_health() {
  local container_name="$1"
  local port="$2"
  local timeout="${3:-180}"
  local interval="${4:-5}"
  local elapsed=0
  local health_file="/tmp/kwcapstone-health-check-${container_name}.json"

  info "Health Check 대기 시작: $container_name / port $port"

  while [[ "$elapsed" -lt "$timeout" ]]; do
    local inspect_status
    inspect_status="$(docker inspect --format='{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' "$container_name" 2>/dev/null || echo "unknown")"

    if [[ "$inspect_status" == "healthy" ]]; then
      success "Docker health check 성공: $container_name (${elapsed}s)"
      return 0
    fi

    if curl -fs "http://127.0.0.1:${port}/actuator/health" > "$health_file" 2>/dev/null; then
      if grep -q '"status":"UP"' "$health_file"; then
        success "HTTP health check 성공: http://127.0.0.1:${port}/actuator/health (${elapsed}s)"
        return 0
      fi
    fi

    info "아직 준비 중: container=$container_name, status=$inspect_status, elapsed=${elapsed}s/${timeout}s"
    sleep "$interval"
    elapsed=$((elapsed + interval))
  done

  error "Health Check 실패: $container_name"
  error "최근 로그 확인 명령어: docker logs $container_name --tail=100"

  return 1
}

# Nginx 전환 함수
switch_nginx() {
  local target_port="$1"
  local backup_file="${NGINX_CONF}.bak.$(date +%Y%m%d%H%M%S)"

  if [[ "$target_port" != "8080" && "$target_port" != "8081" ]]; then
    error "지원하지 않는 target_port입니다: $target_port"
    exit 1
  fi

  warn "Nginx 트래픽 전환 시작: $ACTIVE_PORT → $target_port"
  sudo cp "$NGINX_CONF" "$backup_file"

  sudo sed -i -E \
    "s/server (127\.0\.0\.1|localhost):808[01];[[:space:]]*# ACTIVE_CONTAINER/server 127.0.0.1:${target_port};  # ACTIVE_CONTAINER/" \
    "$NGINX_CONF"

  if ! grep -q "server 127.0.0.1:${target_port};  # ACTIVE_CONTAINER" "$NGINX_CONF"; then
    error "Nginx active upstream 변경 실패. 백업 파일로 복구합니다."
    sudo cp "$backup_file" "$NGINX_CONF"
    exit 1
  fi

  sudo nginx -t || {
    error "Nginx 설정 문법 오류. 백업 파일로 복구합니다."
    sudo cp "$backup_file" "$NGINX_CONF"
    sudo nginx -t || true
    exit 1
  }

  sudo systemctl reload nginx
  success "Nginx 전환 완료: active port = $target_port"
}

# Rollback 모드
if [[ "$ROLLBACK" == true ]]; then
  warn "Rollback 모드입니다. 새 build 없이 inactive 컨테이너로 트래픽을 되돌립니다."

  if ! docker ps --format '{{.Names}}' | grep -qx "$INACTIVE_CONTAINER"; then
    error "Rollback 대상 컨테이너가 실행 중이 아닙니다: $INACTIVE_CONTAINER"
    exit 1
  fi

  if ! wait_for_health "$INACTIVE_CONTAINER" "$INACTIVE_PORT" 60 5; then
    error "Rollback 대상 컨테이너 health check 실패. 전환을 중단합니다."
    exit 1
  fi

  switch_nginx "$INACTIVE_PORT"

  success "Rollback 완료! 현재 active 환경: $INACTIVE"
  warn "문제가 있던 컨테이너($ACTIVE_CONTAINER)는 확인 후 직접 정지하세요."
  exit 0
fi

# MySQL 컨테이너 실행 보장
info "MySQL 컨테이너 실행 상태 보장 중..."
"${DOCKER_COMPOSE[@]}" up -d mysql

# inactive 환경에 새 버전 build & up
info "$INACTIVE_SERVICE 새 버전 build 시작..."
"${DOCKER_COMPOSE[@]}" build "$INACTIVE_SERVICE"

info "기존 inactive 컨테이너 정리: $INACTIVE_SERVICE"
"${DOCKER_COMPOSE[@]}" stop "$INACTIVE_SERVICE" || true
"${DOCKER_COMPOSE[@]}" rm -f "$INACTIVE_SERVICE" || true

info "새 inactive 컨테이너 실행: $INACTIVE_SERVICE"
"${DOCKER_COMPOSE[@]}" up -d "$INACTIVE_SERVICE"

# Health Check
if ! wait_for_health "$INACTIVE_CONTAINER" "$INACTIVE_PORT" 180 5; then
  error "$INACTIVE 환경이 정상적으로 시작되지 않아 배포를 중단합니다."
  error "트래픽은 기존 active 환경($ACTIVE)으로 유지됩니다."
  "${DOCKER_COMPOSE[@]}" stop "$INACTIVE_SERVICE" || true
  exit 1
fi

# Nginx 트래픽 전환
switch_nginx "$INACTIVE_PORT"

# 이전 active 컨테이너 정리
if [[ "$KEEP_OLD" == true ]]; then
  warn "이전 active 컨테이너를 유지합니다: $ACTIVE_CONTAINER"
  warn "문제 발생 시 rollback 가능: ./scripts/bg-rollout.sh --rollback"
else
  info "10초 후 이전 active 컨테이너를 정지합니다: $ACTIVE_CONTAINER"
  info "취소하려면 Ctrl + C를 누르세요."
  sleep 10

  "${DOCKER_COMPOSE[@]}" stop "$ACTIVE_SERVICE" || true
  success "이전 active 컨테이너 정지 완료: $ACTIVE_CONTAINER"
fi

# 배포 완료
success "======================================"
success "Blue/Green 배포 완료"
success "======================================"
info "새 active 환경: $INACTIVE"
info "새 active 포트: $INACTIVE_PORT"
info "이전 환경: $ACTIVE"
info ""
info "확인 명령어:"
info "  docker ps"
info "  grep ACTIVE_CONTAINER $NGINX_CONF"
info "  curl http://localhost/nginx-health"
info "  curl http://localhost/actuator/health"
info "  curl http://localhost/health/$INACTIVE"
info ""