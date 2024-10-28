package com.example.milky_way_back.member.mypage.dto;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class MyPageResponse {
    private String memberId;
    private String memberName;
    private String memberPhoneNum;
    private String memberEmail;
}
