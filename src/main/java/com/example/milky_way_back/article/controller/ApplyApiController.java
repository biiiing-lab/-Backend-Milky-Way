package com.example.milky_way_back.article.controller;


import com.example.milky_way_back.Member.Jwt.JwtUtils;
import com.example.milky_way_back.article.DTO.request.ApplyRequest;
import com.example.milky_way_back.article.DTO.response.ApplyResponse;
import com.example.milky_way_back.article.entity.Apply;
import com.example.milky_way_back.article.exception.UnauthorizedException;
import com.example.milky_way_back.article.service.ApplyService;
import com.example.milky_way_back.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ApplyApiController {
    private final ApplyService applyService;
    private final ArticleService articleService;
    private final JwtUtils jwtUtils;

    //지원 신청
        @PostMapping("/posts/apply/{id}")
        public ResponseEntity<Apply> applyToPost(@AuthenticationPrincipal UserDetails userDetails,
                                                 ApplyRequest applyRequest, @PathVariable long id, HttpServletRequest request
        ) {
            if (userDetails == null) {
                // 사용자가 인증되지 않은 경우
                throw new UnauthorizedException("사용자가 인증되지 않았습니다.");
            }
            // Jwt 토큰에서 회원 정보를 가져옴
            String accessToken = jwtUtils.getJwtFromHeader(request);
            if (accessToken == null) {
                throw new UnauthorizedException("토큰이 유효하지 않습니다.");
            }

            // 받은 데이터로 지원 처리
            Apply saveApply = applyService.apply(accessToken, id, applyRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(saveApply);
        }


    //결과 지원 바꾸기
    @PutMapping("/posts/accepted-or-denied")
    public ResponseEntity<?> updateApplyStatus(@PathVariable Long applyNo, @RequestParam boolean newStatus) {
        applyService.updateApplyResult(applyNo, newStatus);
        return ResponseEntity.ok().build();
    }

    //지원 목록자 조회
    @GetMapping("/posts/applylist/{id}")
    public ResponseEntity<List<ApplyResponse>> findMemberNamesByArticleNo(@PathVariable Long id) {
        List<ApplyResponse> memberNames = applyService.findMemberNamesByArticleNo(id);
        return new ResponseEntity<>(memberNames, HttpStatus.OK);
    }
}
