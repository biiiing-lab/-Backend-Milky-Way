package com.example.milky_way_back.resume;

import com.example.milky_way_back.member.dto.StatusResponse;
import com.example.milky_way_back.resume.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class StudentResumeController {

    private final StudentResumeService studentResumeService;


    // 기본 정보 등록
    @PostMapping("/member/update/info")
    public ResponseEntity<StatusResponse> updateInfo(@RequestBody BasicInfoReqeustDto basicInfoReqeustDto) {
        return studentResumeService.updateBasicInfo(basicInfoReqeustDto);
    }

    // 기본 정보 수정
    @PutMapping("/member/modify/info")
    public ResponseEntity<StatusResponse> modifyInfo(@RequestBody BasicInfoReqeustDto basicInfoReqeustDto) {
        return studentResumeService.modifyBasicInfo( basicInfoReqeustDto);
    }

    // 경력, 자격증 저장
    @PostMapping("/member/update/profile")
    public ResponseEntity<StatusResponse> updateCareerAndCertification(@RequestBody MemberInfoResponse memberInfoResponse) {
        return studentResumeService.updateCarCert( memberInfoResponse);
    }

    // 경력, 자격증 수정
    @PutMapping("/member/modify/profile")
    public ResponseEntity<StatusResponse> modifyCareerAndCertification(@RequestBody MemberInfoResponse memberInfoResponse)  {
        studentResumeService.modifyCareerAndCertification();
        return studentResumeService.updateCarCert(memberInfoResponse);
    }

    // 기본 정보 조회
    @GetMapping("/myResume/basicInfo")
    public ResponseEntity<BasicInfoResponse> memberInfoResponse() {
        return studentResumeService.findBasicInfo();
    }

    // 자격증, 경력 조회
    @GetMapping("/myResume/careerAndCertification")
    public ResponseEntity<MemberInfoResponse> memberCareerAndCertificationResponse() {
        return studentResumeService.findCareerAndCertification();
    }

}
