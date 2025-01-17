package com.example.milky_way_back.member.dto;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
public class TokenDto {
    private String grantType; // 형식 Bearer
    private String accessToken;
    private String refreshToken;
    private String memberName;
}
