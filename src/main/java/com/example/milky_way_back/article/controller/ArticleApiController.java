package com.example.milky_way_back.article.controller;

import com.example.milky_way_back.Member.Jwt.JwtUtils;
import com.example.milky_way_back.Member.Repository.MemberRepository;
import com.example.milky_way_back.article.DTO.request.AddArticle;
import com.example.milky_way_back.article.DTO.response.ArticleListView;
import com.example.milky_way_back.article.DTO.response.ArticleViewResponse;
import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.exception.UnauthorizedException;
import com.example.milky_way_back.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController //http 응답으로 객체 데이터를 json 형태로 변환
public class ArticleApiController {
    private final ArticleService articleService;
    private final MemberRepository memberRepository;
    private final JwtUtils jwtUtils;
@PostMapping("/posts/write")
public ResponseEntity<Article> addBoard(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody AddArticle addArticle,
                                        HttpServletRequest request) {
    if (userDetails == null) {
        // 사용자가 인증되지 않은 경우
        throw new UnauthorizedException("사용자가 인증되지 않았습니다.");
    }
    // Jwt 토큰에서 회원 정보를 가져옴
    String accessToken = jwtUtils.getJwtFromHeader(request);
    if (accessToken == null) {
        throw new UnauthorizedException("토큰이 유효하지 않습니다.");
    }
    // 회원 정보를 사용하여 게시글 저장
    Article savedArticle = articleService.save(addArticle);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
}
    //http://localhost:8080/posts/list?page=1&size=30
    //http://localhost:8080/posts/list?page={page-id}&size={size-id}
    //기본값
    @GetMapping("/posts/list")
    public ResponseEntity<Page<ArticleListView>> getArticles(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articlePage = articleService.findAll(pageable);

        List<ArticleListView> articles = articlePage.getContent()
                .stream()
                .map(ArticleListView::new)
                .collect(Collectors.toList());

        Page<ArticleListView> articleListViewPage = new PageImpl<>(articles, pageable, articlePage.getTotalElements());

        return ResponseEntity.ok(articleListViewPage);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<ArticleViewResponse> findBoard(@PathVariable long id){
        Article article = articleService.findById(id);
        return ResponseEntity.ok().body(new ArticleViewResponse(article));
    }
    //DELETE
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        System.out.println(id);
        articleService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/posts/done/{id}")
    public ResponseEntity<ArticleViewResponse> updateRecruit(@PathVariable long id) {
        Article updatedArticle = articleService.updateRecruit(id);
        return ResponseEntity.ok().body(new ArticleViewResponse(updatedArticle));
    }

//    @PutMapping("/posts/done/{article-id}")
//    public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody UpdateArticleRequest request) {
//        Article articleToUpdate = articleService.findById(id);
//
//        if (articleToUpdate == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        // 요청에서 전달된 값을 엔티티에 설정
//        articleToUpdate.setRecruit(request.getRecruit());
//
//        // 엔티티를 저장하고 업데이트된 엔티티 반환
//        Article updatedArticle = articleService.save(articleToUpdate);
//
//        return ResponseEntity.ok(updatedArticle);
//    }



    //UPDATE
//    @PutMapping("/api/boards/{id}")
//    public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody UpdateRequest request) {
//        Board updatedBoard = boardService.update(id, request);
//        return ResponseEntity.ok().body(updatedBoard);
//    } 수정 넣을건가요?
}
