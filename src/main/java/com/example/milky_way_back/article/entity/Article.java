package com.example.milky_way_back.article.entity;

import com.example.milky_way_back.Member.Entity.Member;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name="Article")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Data
@Getter
public class Article {
    //멤버 일대다 적용
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="article_no", updatable = false)
    private Long article_no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member memberId;

    @Column(name="article_type")
    private String articleType;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean articleRecruitmentState = true;

    @Column(name="article_title", nullable = false)
    private String title;

    @Column(name="article_content", nullable = false)
    private String content;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean recruit = true;

    @ColumnDefault("0")
    private int likes;

    @Column(name="article_apply", nullable = false)
    private int apply;

    @ColumnDefault("0")
    private int applyNow; //이렇게 해도 상관없을려나?

    @Column(name="article_start_day", updatable = false)
    private String startDay;

    @Column(name="article_end_day", updatable = false)
    private String endDay;

    @Column(name="article_find_mentor", updatable = false)
    private boolean findMentor;

    @Column(name="article_mentor_tag", updatable = false) //articleMentorTag
    private String mentorTag;

    @Column(name="article_con_method", updatable = false, nullable = false)
    private String conMethod;

    @Column(name="article_con_info", updatable = false, nullable = false)
    private String conInfo;

    @CreatedDate
    @Column(name="regDate", updatable = false)
    private LocalDateTime regDate;

    @Builder
    public Article(String articleType, String title,Member memberId, String content,String mentorTag,
                   String startDay, String endDay,
                   int apply, boolean findMentor, String conMethod, String conInfo){
        this.memberId = memberId;
        this.articleType = articleType;
        this.title = title;
        this.content = content;
        this.apply = apply;
        this.startDay = startDay;
        this.endDay = endDay;
        this.findMentor=findMentor;
        this.mentorTag = mentorTag;
        this.conMethod = conMethod;
        this.conInfo = conInfo;
    }

}
