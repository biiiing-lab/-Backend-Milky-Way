package com.example.milky_way_back.member.mypage;

import com.example.milky_way_back.article.DTO.response.MyPageApplyResponse;
import com.example.milky_way_back.article.DTO.response.MyPageArticleResponse;
import com.example.milky_way_back.article.service.ApplyService;
import com.example.milky_way_back.article.service.ArticleService;
import com.example.milky_way_back.member.dto.TokenDto;
import com.example.milky_way_back.member.mypage.dto.MyPageRequest;
import com.example.milky_way_back.member.mypage.dto.MyPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController //http 응답으로 객체 데이터를 json 형태로 변환
public class MyPageController {

    private final MyPageService myPageService;
    private final ArticleService articleService;
    private final ApplyService applyService;

    @GetMapping("/info")
    public ResponseEntity<MyPageResponse> getMyPageInfo() {
            return myPageService.getMemberInfo();
    }

    // 지원 목록 확인
    @GetMapping("/applyinfo")
    public ResponseEntity<?> getMyApplyInfo() {
        try {
             // 신청 정보 조회
                List<MyPageApplyResponse> applies = applyService.getAppliesByMemberId();
            return ResponseEntity.ok(applies);
        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching my page data");
        }
    }

    @GetMapping("/articleinfo")
    public ResponseEntity<?> getMyArticleInfo(){
        try {
            // 아티클 정보 조회
            List<MyPageArticleResponse> articles = articleService.getArticlesByMemberId();
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while fetching my page data");
        }
    }

    // 회원 정보 변경
    @PutMapping("/update")
    public ResponseEntity<?> updateMemberInfo(@RequestBody MyPageRequest myPageRequest) {
        TokenDto updatedTokenDto = myPageService.updateMemberInfo(myPageRequest);

        if (updatedTokenDto != null) {
            return ResponseEntity.ok(updatedTokenDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Member not found or no information was updated.");
        }
    }

    // 좋아요 목록
    @GetMapping("/dibsinfo")
    public ResponseEntity<?> getMyDibsInfo() {
            try {
            // 아티클 정보 조회
            List<MyPageArticleResponse> articles = myPageService.getLikedArticlesByMemberId();
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while fetching my page data");
        }
    }
}
