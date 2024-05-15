package com.example.milky_way_back.Member.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MyPageResponse {
    private String memberId;
    private String memberPassword;
    private String memberName;
    private String memberPhoneNum;
    private String memberEmail;
}
