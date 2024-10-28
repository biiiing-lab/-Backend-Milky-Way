package com.example.milky_way_back.member;
import com.example.milky_way_back.member.dto.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<StatusResponse> signup(@RequestBody SignupRequest request) {
        return memberService.signup(request);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(memberService.login(loginRequest));
    }

    // 중복 확인
    @PostMapping("/signup/checkId")
    public ResponseEntity<StatusResponse> checkId(@RequestBody IdRequest idRequest) {
        return memberService.duplicationIdCheck(idRequest);
    }

    // 리프레시 토큰
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(memberService.reissue(httpServletRequest));
    }

    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity<StatusResponse> logout() {
        return memberService.logout();
    }

}