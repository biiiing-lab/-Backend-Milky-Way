package com.example.milky_way_back.member.mypage;

import com.example.milky_way_back.article.DTO.response.MyPageArticleResponse;
import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.entity.Dibs;
import com.example.milky_way_back.article.exception.MemberNotFoundException;
import com.example.milky_way_back.article.repository.ArticleRepository;
import com.example.milky_way_back.article.repository.DibsRepository;
import com.example.milky_way_back.member.dto.TokenDto;
import com.example.milky_way_back.member.entity.Member;
import com.example.milky_way_back.member.jwt.TokenProvider;
import com.example.milky_way_back.member.jwt.UserDetailService;
import com.example.milky_way_back.member.mypage.dto.MyPageRequest;
import com.example.milky_way_back.member.mypage.dto.MyPageResponse;
import com.example.milky_way_back.member.repository.MemberRepository;
import com.example.milky_way_back.member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final UserDetailService userDetailService;
    private final DibsRepository dibsRepository;

    public ResponseEntity<MyPageResponse> getMemberInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member optionalMember = memberRepository.findByMemberId(authentication.getName()).orElseThrow();

        MyPageResponse myPageResponse =  MyPageResponse.builder()
                .memberId(optionalMember.getMemberId())
                .memberName(optionalMember.getMemberName())
                .memberEmail(optionalMember.getMemberEmail())
                .memberPhoneNum(optionalMember.getMemberPhoneNum())
                .build();

        return ResponseEntity.ok(myPageResponse);
    }

    @Transactional
    public TokenDto updateMemberInfo(MyPageRequest myPageRequest) {
        // SecurityContext에서 인증 정보 가져오기
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        // 인증 정보에서 회원 ID 가져오기
        String memberId = authentication.getName();

        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        if (optionalMember.isPresent()) {

            Member member = optionalMember.get();
            boolean isUpdated = false;


            if (myPageRequest.getMemberPassword() != null) {
                String encodedPassword = passwordEncoder.encode(myPageRequest.getMemberPassword());
                member.setMemberPassword(encodedPassword);
                isUpdated = true;
            }
            if (myPageRequest.getMemberName() != null) {
                member.setMemberName(myPageRequest.getMemberName());
                isUpdated = true;
            }
            if (myPageRequest.getMemberPhoneNum() != null) {
                member.setMemberPhoneNum(myPageRequest.getMemberPhoneNum());
                isUpdated = true;
            }
            if (myPageRequest.getMemberEmail() != null) {
                member.setMemberEmail(myPageRequest.getMemberEmail());
                isUpdated = true;
            }
            member.setLastModifiedDate(LocalDateTime.now());
            memberRepository.save(member);

            // Update authentication token if any information has been changed
            if (isUpdated) {
                return updateAuthenticationToken(member.getMemberId());
            }

        }
        return null; // No update was made or member not found
    }

    private TokenDto updateAuthenticationToken(String newMemberId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = userDetailService.loadUserByUsername(newMemberId);
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                userDetails, authentication.getCredentials(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        // Generate new tokens
        TokenDto newTokenDto = tokenProvider.updateUser(newAuth);

        // Log the new access token
        System.out.println("New JWT Access Token: " + newTokenDto.getAccessToken());

        // Return the new tokens
        return newTokenDto;
    }


    public List<MyPageArticleResponse> getLikedArticlesByMemberId() {
        // 토큰에서 회원 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName();

        // 회원 ID로 회원 정보 조회
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with ID: " + memberId));

        // 회원이 좋아요를 누른 게시물 목록 조회
        List<Dibs> dibsList = dibsRepository.findByMemberNo(member);
        List<MyPageArticleResponse> likedArticles = new ArrayList<>();

        // 좋아요를 누른 게시물 정보를 MyPageArticleResponse로 변환
        for (Dibs dibs : dibsList) {
            Article article = dibs.getArticleNo();
            MyPageArticleResponse response = new MyPageArticleResponse();
            response.setCardArticle_no(article.getArticle_no());
            response.setCardArticleType(article.getArticleType());
            response.setCardTitle(article.getTitle());
            response.setCardFindMentor(article.isFindMentor());
            response.setCardRecruit(article.getRecruit());
            response.setCardApply(article.getApply());
            response.setCardApplyNow(article.getApplyNow());
            response.setCardLikes(article.getLikes());
            response.setCardEndDay(article.getEndDay());
            response.setCardStartDay(article.getStartDay());
            likedArticles.add(response);
        }

        return likedArticles;
    }

}
