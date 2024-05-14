package com.example.milky_way_back.article.service;

import com.example.milky_way_back.Member.Entity.Member;
import com.example.milky_way_back.Member.Jwt.JwtUtils;
import com.example.milky_way_back.Member.Repository.MemberRepository;
import com.example.milky_way_back.article.DTO.request.AddArticle;
import com.example.milky_way_back.article.exception.MemberNotFoundException;
import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class ArticleService {

    private final JwtUtils jwtUtils;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    public Article save(AddArticle request) {
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
            request.setMemberId(memberId);
            // Article 엔티티로 변환
            Article article = request.toEntity(member);
            // 게시물 저장
            return articleRepository.save(article);
        } else {
            // 회원을 찾지 못한 경우에는 예외 처리 또는 다른 방법으로 처리
            throw new MemberNotFoundException("Member not found with ID: " + memberId);
        }
    }

    //게시글 조회
    public Page<Article> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    public Article findById (long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
    }
    public void delete(long id){
        articleRepository.deleteById(id);
    }
//    @Transactional
//    public Article update(long id, UpdateRequest request) {
//        Article article = articleRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("not found: "+id));
//
//        article.update(request.getTitle(), request.getContent());
//        return article;
//    } 수정 넣을건가요?
// 게시글 업데이트
    public Article updateRecruit(long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        article.setRecruit(false);
        return article;
    }
}
