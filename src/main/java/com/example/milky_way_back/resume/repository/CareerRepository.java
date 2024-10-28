package com.example.milky_way_back.resume.repository;

import com.example.milky_way_back.member.entity.Member;
import com.example.milky_way_back.resume.entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Integer> {
    Career findByMember(Member memberNo);
    List<Career> findAllByMember(Member member);
    List<Object[]> findCareerAndCertificationByMember(Member member);
    void deleteByMember(Member member);
}
