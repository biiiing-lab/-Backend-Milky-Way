package com.example.milky_way_back.member.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String memberId;
    private String memberPassword;
}
