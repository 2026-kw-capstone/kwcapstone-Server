package com.kwcapstone.server.domain.member.repository;

import com.kwcapstone.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 해당 이메일을 가진 회원이 존재하는지 확인하는 메서드
    boolean existsByEmail(String email);

    // email로 Member를 조회하는 메서드
    Optional<Member> findByEmail(String email);
}
