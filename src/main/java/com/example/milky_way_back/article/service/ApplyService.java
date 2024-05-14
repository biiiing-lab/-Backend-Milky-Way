package com.example.milky_way_back.article.service;

import com.example.milky_way_back.Member.Entity.Member;
import com.example.milky_way_back.Member.Jwt.JwtUtils;
import com.example.milky_way_back.Member.Repository.MemberRepository;
import com.example.milky_way_back.article.DTO.request.ApplyRequest;
import com.example.milky_way_back.article.DTO.response.ApplyResponse;
import com.example.milky_way_back.article.entity.Apply;
import com.example.milky_way_back.article.exception.ArticleNotFoundException;
import com.example.milky_way_back.article.exception.MemberNotFoundException;
import com.example.milky_way_back.article.repository.ApplyRepository;
import com.example.milky_way_back.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ApplyService {
    private final JwtUtils jwtUtils;
    private final ApplyRepository applyRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

//    public List<Apply> findAll() {
//        return applyRepository.findAll();
//    }
    @Transactional
     //회원 번호와 게시글 번호를 받아 지원 정보를 처리하는 메서드
    public Apply apply(String accessToken, Long articleNo, ApplyRequest request) {
        // SecurityContext에서 인증 정보 가져오기
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        // 인증 정보에서 회원 ID 가져오기
        String memberId = authentication.getName();
        // 회원 ID로 회원 정보 조회
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            // AddArticle에 회원 정보 설정
            Apply apply = Apply.builder()
                    .article(articleRepository.findById(articleNo).orElseThrow(() -> new ArticleNotFoundException("Article not found with ID: " + articleNo)))
                    .memberId(member) // Set the Member object directly
                    .build();
            // 게시물 저장
            return applyRepository.save(apply);
        } else {
            // 회원을 찾지 못한 경우에는 예외 처리 또는 다른 방법으로 처리
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
    }


    public void updateApplyResult(Long applyNo, boolean newStatus) {
        Apply apply = applyRepository.findById(applyNo).orElseThrow(() -> new RuntimeException("Apply not found with id: " + applyNo));
        apply.setApplyResult(newStatus);
        applyRepository.save(apply);
    }

    public List<ApplyResponse> findMemberNamesByArticleNo(Long article_no) {
        return applyRepository.findMemberNamesByArticleNo(article_no);
    }
}
