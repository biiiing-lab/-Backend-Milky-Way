package com.example.milky_way_back.member.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name="RefreshToken")
@NoArgsConstructor
@Entity
@Data
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refreshToken_no")
    private Long authNo; // 고유 번호

    // member join
    @JoinColumn(name="refreshToken_member_no", referencedColumnName = "member_no")
    @OneToOne
    private Member member; // memberNo로 조인

    @Column(name="refreshToken_refreshtoken")
    private String authRefreshToken; // 리프레시 토큰

    public RefreshToken updateToken(String token) {
        this.authRefreshToken = token;
        return this;
    }

}
