package com.example.milky_way_back.member.repository;

import com.example.milky_way_back.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberId(String memberId);
    boolean existsBymemberId(String memberId);
}
