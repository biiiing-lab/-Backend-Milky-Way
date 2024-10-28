package com.example.milky_way_back.member.jwt;

import com.example.milky_way_back.member.dto.LoginRequest;
import com.example.milky_way_back.member.dto.StatusResponse;
import com.example.milky_way_back.member.dto.TokenDto;
import com.example.milky_way_back.member.entity.Member;
import com.example.milky_way_back.member.entity.RefreshToken;
import com.example.milky_way_back.member.repository.MemberRepository;
import com.example.milky_way_back.member.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.*;

@Component
@Slf4j
public class TokenProvider {

    private final Key secretKey;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;


    public TokenProvider(@Value("${jwt.secret.key}") String key, MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // todo 겹치는 코드가 많기 때문에 어떻게 효율적이게 할 것인지 생각할 것
    // 로그인 정보로 refresh, access 토큰 생성 : 첫 로그인 시
    public TokenDto createToken(LoginRequest loginRequest) {

        Member member = memberRepository.findByMemberId(loginRequest.getMemberId()).orElseThrow();
        long now = (new Date()).getTime(); // 현재 시간

        Date accessTokenExpire = new Date(now + 3600000); // 1시간
        Date refreshTokenExpire = new Date(now + 86400000); // 1일

        // 어세스 토큰 생성
        String accessToken = Jwts.builder()
                .setSubject(loginRequest.getMemberId())
                .claim("name", member.getMemberName())
                .setExpiration(accessTokenExpire)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        // 리프레시 토큰 생성
        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpire)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        RefreshToken saveToken = RefreshToken.builder()
                .member(member)
                .authRefreshToken(refreshToken)
                .build();

        refreshTokenRepository.save(saveToken);

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberName(member.getMemberName())
                .build();
    }

    // Authentication 가지고 refresh, access 토큰 생성 : 정보 변동 시
    public TokenDto updateUser(Authentication authentication) {

        Member member = memberRepository.findByMemberId(authentication.getName()).orElseThrow();
        long now = (new Date()).getTime(); // 현재 시간

        Date accessTokenExpire = new Date(now + 3600000); // 1시간
        Date refreshTokenExpire = new Date(now + 86400000); // 1일

        // 어세스 토큰 생성
        String accessToken = Jwts.builder()
                .setSubject(member.getMemberId())
                .claim("name", member.getMemberName())
                .setExpiration(accessTokenExpire)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        // 리프레시 토큰 생성
        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpire)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        RefreshToken saveToken = RefreshToken.builder()
                .member(member)
                .authRefreshToken(refreshToken)
                .build();

        RefreshToken existRefreshToken = refreshTokenRepository.findByMember(member).orElseThrow();

        if(existRefreshToken != null) {
            refreshTokenRepository.deleteByMember(member);
        }

        refreshTokenRepository.save(saveToken);

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberName(member.getMemberName())
                .build();
    }

    // access token만 재생성
    public TokenDto createAccessToken(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization").substring(7);

        long now = (new Date()).getTime(); // 현재 시간
        Date accessTokenExpire = new Date(now + 1800000); // 30분

        RefreshToken refreshToken = refreshTokenRepository.findByAuthRefreshToken(token);
        Member member = memberRepository.findByMemberId(refreshToken.getMember().getMemberId()).orElseThrow();

        // 어세스 토큰 생성
        String accessToken = Jwts.builder()
                .setSubject(member.getMemberId())
                .claim("name", member.getMemberName()) /* todo claim 추가 */
                .setExpiration(accessTokenExpire)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", Collections.emptyList());
        return new UsernamePasswordAuthenticationToken(principal, "", Collections.emptyList());
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


    // 토큰 검증 : response entity 형태로 메세지 반환
    public ResponseEntity<StatusResponse> validateResponse(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "vaild"));

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);

            // 만료된 리프레시 토큰 삭제
            RefreshToken refreshToken = refreshTokenRepository.findByAuthRefreshToken(token);

            if(refreshToken != null) {
                refreshTokenRepository.delete(refreshToken);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StatusResponse(HttpStatus.UNAUTHORIZED.value(), "expired"));
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StatusResponse(HttpStatus.UNAUTHORIZED.value(), "invalid"));
    }

    // jwt 검증
    public boolean validationToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey).build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }
}
