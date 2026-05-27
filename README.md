# 🧠 AI Speech Backend for Communication Rehabilitation

성인 의사소통 및 조음 장애 사용자의 재활을 돕기 위한 **AI 기반 언어 훈련 서비스의 백엔드 서버**입니다.

사용자의 음성 훈련 기록, 시나리오 학습, 자유 대화, 레포트 기능을 안정적으로 제공하기 위해 Spring Boot 기반 REST API 서버로 구현되었으며, AI 분석 서버와 연동하여 발음 평가·음성 분석·피드백 결과를 제공합니다.

---

## ✨ 주요 기능 (Key Features)

### 1. 사용자 인증 및 회원 관리 (auth, member)
- JWT 기반 로그인/인증 시스템
- Access Token + Refresh Token 구조
- 회원가입 및 사용자 정보 관리
- Spring Security 기반 인가 처리

### 2. 기초 발성 연습 (basicpronunciation)
- 한국어 단모음 중심 발성 훈련 기능
- 사용자의 음성 업로드 및 분석 요청
- AI 서버와 연동하여 발음 정확도 평가
- 음성 및 분석 결과 기록 저장

### 3. 나만의 문장 노트 (mysentence)
- 사용자가 직접 연습 문장을 생성·관리
- 문장별 발화 기록 저장
- TTS 음성 제공
- 반복 훈련 결과 누적 관리

### 4. 시나리오 (scenario)
- 상황 기반 시나리오 자동 생성
- 단계별 발화 연습 진행
- 시나리오 재생성 가능

### 5. 자유 대화 (conversation)
- 형식에 구애받지 않는 자유 대화
- 자유 대화 세션 관리
- 대화 이력 저장 및 조회

### 6. 훈련 결과 레포트 (report)
- 발음 정확도 통계 제공
- 주간/월간 변화 추이 조회
- 훈련 횟수 및 누적 성과 분석

### 7. 음성 파일 저장 및 관리 (storage)
- AWS S3 기반 음성 업로드
- Presigned URL 생성
- 오디오 파일 관리 및 삭제

---

## 🚀 기술 스택
- **Language**: Java 21
- **Framework**: SpringBoot 4.0.3
- **Build Tool**: Gradle
- **Database**: MySQL, JPA
- **Infra**: AWS EC2, AWS S3, Docker, Nginx, Github Actions
- **Auth**: JWT, Spring Security
- **외부 API**: AI Server (FastAPI)

## 🏗️ 서버 아키텍처
<img src="https://github.com/user-attachments/assets/02a8d313-0985-4d79-ab94-4c37c0aaad50" />

## 📂 프로젝트 구조 (Project Structure)
```
kwcapstone-Server/
├── .gitignore                          # Git에 올리지 않을 파일/디렉토리 설정
├── .github/                            # GitHub 관련 설정 디렉토리
│   ├── ISSUE_TEMPLATE/                 # 이슈 템플릿
│   ├── workflows/                      # GitHub Actions 워크플로우 (CI/CD)
│   │   └── ci-cd.yml
│   └── pull_request_template.md        # PR 템플릿
├── nginx/                              # Nginx 설정
│   └── kwcapstone.conf                 # Reverse Proxy / Blue-Green 설정
├── scripts/                            # 운영 및 배포 스크립트
│   └── bg-rollout.sh                   # Blue/Green 배포 자동화 스크립트
├── Dockerfile                          # Spring Boot Docker 이미지 빌드 설정
├── docker-compose.yml                  # Docker 컨테이너 구성
├── build.gradle                        # Gradle 빌드 스크립트
├── settings.gradle                     # Gradle 프로젝트 설정
├── .env.example                        # 환경 변수 예시
└── src/
    └── main/
        ├── java/
        │   └── com/kwcapstone/server
        │       ├── ServerApplication.java          # Spring Boot 애플리케이션 진입점
        │       ├── domain/                         # 비즈니스 도메인 계층
        │       │   ├── auth/                       # 인증/인가
        │       │   │   ├── controller/
        │       │   │   ├── converter/
        │       │   │   ├── dto/
        │       │   │   └── service/
        │       │   ├── member/                     # 회원 정보 및 마이페이지
        │       │   ├── home/                       # 홈/대시보드
        │       │   ├── basicpronunciation/         # 기초 발성 연습
        │       │   │   ├── client/                 # AI 서버 통신
        │       │   │   ├── controller/
        │       │   │   ├── converter/
        │       │   │   ├── dto/
        │       │   │   ├── entity/
        │       │   │   ├── enums/
        │       │   │   ├── exception/
        │       │   │   ├── repository/
        │       │   │   └── service/
        │       │   ├── mysentence/                 # 나만의 문장 노트
        │       │   ├── scenario/                   # 시나리오
        │       │   ├── conversation/               # 자유 대화
        │       │   └── report/                     # 훈련 레포트 및 통계
        │       └── global/                         # 전역 공통 모듈
        │           ├── config/                     # 설정 파일
        │           ├── entity/                     # 공통 BaseEntity
        │           ├── storage/                    # S3 음성 저장소 관리
        │           ├── security/                   # Spring Security, JWT Filter
        │           ├── apiPayload/                 # 공통 응답/예외 포맷
        │           │   ├── exception/              # 전역 예외 정의
        │           │   ├── handler/                # 핸들러
        │           │   └── response/               # 공통 API 응답 래퍼
        │           └── util/                       # 공통 유틸리티 클래스
        └── resources/
            └── application.yaml                     # Spring Boot 환경 설정
```

## 📌 Git convention
<details>
<summary><b>작업 규칙</b></summary>

    - 직접 main 브랜치에서 작업 금지, 모든 개발은 develop 브랜치를 기준으로 진행
    - 기능 단위로 브랜치를 생성하여 작업 후 PR을 통해 병합, 브랜치 이름은 기능/작업 내용이 명확하게 드러나도록 작성
    - 모든 작업 시작 전 작업 브랜치에서 최신 develop 브랜치 pull하여 최신 상태 반영 후 작업 시작
    - PR 생성 전 원격 develop 브랜치에 변경 사항이 있는 경우, 작업 브랜치에 develop 브랜치 merge 후 PR 생성
    - PR은 코드 리뷰 및 승인 후 병합
    
</details>


<details>
<summary><b>브랜치 전략</b></summary>
    
**Github Flow**

```
1. 작업 단위로 Issue 생성
2. develop 브랜치에서 분기한 작업용 브랜치 생성
3. 생성한 브랜치에서 작업 진행
4. 작업 완료 후 develop 브랜치로 Pull Request(PR) 생성
5. 코드 리뷰 및 승인 후 PR merge
6. 배포 가능한 상태 확인 후, CI/CD 파이프라인을 통해 자동 빌드, 테스트, 배포 진행
```

- **main** : 프로젝트가 최종적으로 배포되는 브랜치
    - Production 환경에 언제 배포해도 문제없는 안정(stable) 브랜치입니다.
    - 장애나 긴급 버그 발생 시 main 브랜치에서 핫픽스를 진행합니다.
    - Initial commit을 제외하고 main 브랜치에 직접 커밋하지 않으며, 반드시 Pull Request로만 병합합니다.
- **develop** : 통합 개발 브랜치
    - 새로운 기능 개발 시 main을 기준으로 develop 브랜치를 생성합니다.
    - feature 브랜치들을 병합하는 곳입니다.
    - 모든 기능이 통합되고 버그가 수정된 후 main 브랜치로 PR을 생성합니다.
    - main 브랜치는 항상 안정적이어야 하며, 불완전한 작업은 develop에서 처리합니다.
- **feat/*** : 기능 개발 브랜치
    - 이슈 기반으로 브랜치를 생성하며, 브랜치명은 반드시 `feat/{도메인}-{이슈번호}-{기능명}` 형식을 따릅니다. 예: `feat/auth-3-login`
    - develop 브랜치를 기준으로 분기하여 새로운 기능을 개발합니다.
    - 기능에 대한 버그 수정은 해당 feature 브랜치 내에서 완료 후 develop으로 PR을 생성합니다.

</details>


<details>
<summary><b>Issue/PR</b></summary>
    
**Issue**
```
[Tag] Title
```
- 이슈 내용에는 다음을 포함:
  - 이슈 요약
  - 상세 내용
  - 체크리스트
  - 참고 사항
- Assignees, Labels 지정
  
**Pull Request (PR)**
```
[Tag] Title
```
- PR 내용에는 다음을 포함:
  - 관련 이슈
  - 작업 내용
  - 테스트 결과
  - 스크린샷
  - 참고 사항
- Reviewers, Assignees, Labels 지정

#### Issue/PR Tag 종류
| Tag      | 설명                         |
|----------|-----------------------------|
| **Feat**     | 새로운 기능 추가             |
| **Fix**      | 버그 수정                    |
| **Docs**     | 문서 수정                    |
| **Style**    | 코드 포맷/스타일 변경        |
| **Refactor** | 코드 리팩토링                |
| **Test**     | 테스트 코드 추가/수정        |
| **Chore**    | 빌드/설정/패키지 관련 작업   |
| **Merge**    | 브랜치 병합                  |

</details>


<details>
<summary><b>Commit message</b></summary>

 ```
Type: Title

<body>               

<footer>            
 ```

- 제목 (Subject)
    - 커밋 유형과 간단한 제목 작성
    - 끝부분에 이슈 번호 포함
- 본문 (Body) (선택)
    - 72자 내로 줄바꿈하여 작성
    - 무엇을, 왜 변경했는지 상세히 설명
- 꼬리말 (Footer) (선택)
    - 이슈 트래커와 함께 사용할 때 해결한 이슈나 참고할 부분을 명시
        - ```Resolves: #이슈번호``` → 커밋이 해당 이슈를 해결할 때
        - ```Refs: #이슈번호``` → 커밋이 관련된 참고 이슈일 때
        - ```See also: #이슈번호``` → 관련 이슈를 참조할 때

예:
```
Feat: S3 음성 업로드 및 presigned URL 생성 기능 구현 (#5)

S3 관련 기능을 AudioStorageService로 분리하여 공용화
하였습니다. 추후 다른 기능 구현 시 의존성만 주입받아
메서드를 사용할 수 있도록 설계했습니다.

Resolves: #5
```

**Commit message Type 종류**

| Type | 의미 | 사용 예시 |
|------|------|-----------|
| **Feat** | 새로운 기능 추가 | `Feat: 기초 발성 연습 API 추가` |
| **Fix** | 버그 수정 | `Fix: 월 평균 발음 정확도 계산 오류 수정` |
| **Docs** | 문서 수정 | `Docs: API 명세서 및 사용 가이드 업데이트` |
| **Style** | 코드 스타일 변경 (로직 영향 없음) | `Style: import 정리 및 코드 포맷 수정` |
| **Refactor** | 코드 리팩토링 (기능 변경 없음) | `Refactor: 시나리오 재생성 로직 분리` |
| **Test** | 테스트 코드 추가/수정 | `Test: 시나리오 재생성 테스트 추가` |
| **Chore** | 빌드/설정/기타 작업 | `Chore: Gradle 의존성 업데이트` |
| **Comment** | 주석 추가/수정 | `Comment: 위클리 스탬프 계산 로직 설명 추가` |
| **Rename** | 파일/폴더명 변경 | `Rename: ConversationDetailResponse → ConversationDetailResDTO` |
| **Remove** | 파일/코드 삭제 | `Remove: 사용하지 않는 테스트 코드 삭제` |
| **Init** | 프로젝트 초기 세팅 | `Init: Spring Boot 프로젝트 초기화` |
| **Merge** | 브랜치 병합 | `Merge: feature/auth-signup` |
| **!BREAKING CHANGE** | 하위 호환 불가 변경 | `!BREAKING CHANGE: 시나리오 생성 API 응답 구조 변경` |
| **!HOTFIX** | 운영 긴급 수정 | `!HOTFIX: 시나리오 생성 실패 긴급 수정` |
    
</details>


## 🫂 BE Team

| **[박예진](https://github.com/ye-zin)** | **[한종서](https://github.com/dosp74)** |
| :---: | :---: |
| <img src="https://avatars.githubusercontent.com/ye-zin" width="140"> | <img src="https://avatars.githubusercontent.com/dosp74" width="140"> |
| Backend | Backend |

## 🔹 역할

### 박예진
- 홈 API 개발
- 시나리오 기능 API 개발
- 나만의 문장 노트 기능 API 개발

### 한종서
- 회원 및 인증 API 개발
- 기초 발성 연습 기능 API 개발
- 자유 대화 기능 API 개발
- 레포트 API 개발
- 인프라 구축 및 배포
