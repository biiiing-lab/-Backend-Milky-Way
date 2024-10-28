package com.example.milky_way_back.article.controller;


import com.example.milky_way_back.article.exception.MemberNotFoundException;
import com.example.milky_way_back.article.DTO.ChangeApplyResult;
import com.example.milky_way_back.article.DTO.request.ApplyRequest;
import com.example.milky_way_back.article.DTO.response.ApplyResponse;
import com.example.milky_way_back.article.entity.Apply;
import com.example.milky_way_back.article.repository.ApplyRepository;
import com.example.milky_way_back.article.service.ApplyService;
import com.example.milky_way_back.resume.StudentResumeService;
import com.example.milky_way_back.resume.dto.BasicInfoResponse;
import com.example.milky_way_back.resume.dto.MemberInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ApplyApiController {
    private final ApplyService applyService;
    private final ApplyRepository applyRepository;

    //지원 신청
    @PostMapping("/posts/apply/{id}")
        public ResponseEntity<Apply> applyToPost(@PathVariable long id) {
            Apply saveApply = applyService.apply(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(saveApply);
    }

    //지원 결과 바꾸기
    @PutMapping("/update/{applyId}")
    public ResponseEntity<String> updateApplyResult(
                                                    @PathVariable Long applyId,
                                                    @RequestBody ChangeApplyResult requestBody) {
        String response = applyService.updateApplyResult(applyId, requestBody);
        return ResponseEntity.ok(response);
    }

    //지원 목록자 조회
    @GetMapping("/posts/applylist/{id}")
    public ResponseEntity<List<ApplyResponse>> findMemberNamesByArticleNo(@PathVariable Long id) {
        try {
            List<ApplyResponse> memberNames = applyService.findMemberNamesByArticleNo(id);
            return new ResponseEntity<>(memberNames, HttpStatus.OK);
        } catch (MemberNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 기본 정보 조회
    @GetMapping("/apply/basicInfo/{id}")
    public ResponseEntity<BasicInfoResponse> applyInfoResponse(@PathVariable Long id) {
        // Apply 엔티티에서 applyNo를 기반으로 member 조회
        Apply apply = applyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Apply not found with ID: " + id));
        // memberNo를 사용하여 기본 정보 조회
        System.out.println(apply.getMemberId());
        System.out.println(apply.getMemberId().getMemberNo());
        return applyService.findBasicInfo(apply.getMemberId().getMemberNo());
    }

    // 자격증, 경력 조회
    @GetMapping("/apply/careerAndCertification/{id}")
    public ResponseEntity<MemberInfoResponse> applyCareerAndCertificationResponse(@PathVariable Long id) {
        Apply apply = applyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Apply not found with ID: " + id));
        return applyService.findCareerAndCertification(apply.getMemberId().getMemberNo());
    }
}
