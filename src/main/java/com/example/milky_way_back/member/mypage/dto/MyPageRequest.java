package com.example.milky_way_back.member.mypage.dto;

import com.example.milky_way_back.member.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MyPageRequest {
    private String memberPassword;
    private String memberName;
    private String memberPhoneNum;
    private String memberEmail;
}
