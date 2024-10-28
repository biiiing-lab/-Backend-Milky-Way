package com.example.milky_way_back.member.repository;

import com.example.milky_way_back.member.entity.Member;
import com.example.milky_way_back.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMember(Member member);
    void deleteByMember(Member memberNo);
    RefreshToken findByAuthRefreshToken(String authRefreshToken);
}
