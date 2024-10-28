package com.example.milky_way_back.member;

import com.example.milky_way_back.member.dto.*;
import com.example.milky_way_back.member.entity.Member;
import com.example.milky_way_back.member.jwt.TokenProvider;
import com.example.milky_way_back.member.repository.MemberRepository;
import com.example.milky_way_back.member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입
    public ResponseEntity<StatusResponse> signup(SignupRequest request) {

        String memberId = request.getId();
        String password = passwordEncoder.encode(request.getPassword()); // 비밀번호 암호화

        Member member = Member.builder()
                .memberId(memberId)
                .memberPassword(password)
                .memberName(request.getName())
                .memberPhoneNum(request.getTel())
                .memberEmail(request.getEmail())
                .build();

        memberRepository.save(member);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "회원가입 성공"));
    }

    // 중복 확인
    public ResponseEntity<StatusResponse> duplicationIdCheck(IdRequest idRequest) {
        if (memberRepository.existsBymemberId(idRequest.getMemberId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new StatusResponse(HttpStatus.CONFLICT.value(), "중복 아이디 있음"));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "중복 아이디 없음"));
        }
    }

    // 로그인
    @Transactional
    public TokenDto login(LoginRequest loginRequest) {
        return tokenProvider.createToken(loginRequest);
    }

    // 토큰 확인 후 재발급 관련 메서드
    @Transactional
    public TokenDto reissue(HttpServletRequest httpServletRequest) {
        return tokenProvider.createAccessToken(httpServletRequest);
    }

    // 로그아웃
    @Transactional
    public ResponseEntity<StatusResponse> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByMemberId(authentication.getName()).orElseThrow();
        refreshTokenRepository.deleteByMember(member);
        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "로그아웃 완료"));
    }
}
