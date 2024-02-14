package com.example.practicemk1.controller;

import com.example.practicemk1.dto.AddArticleRequest;
import com.example.practicemk1.dto.ArticleResponse;
import com.example.practicemk1.dto.UpdateArticleRequest;
import com.example.practicemk1.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ArticleApiController {

    private final ArticleService articleService;

    /**
     * 전체 게시글 조회
     * @return List<ArticleResponse>
     */
    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> getAllArticles(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(articleService.getAllArticles());
    }

    /**
     * 게시글 생성
     * @param request AddArticleRequest
     * @return ArticleResponse
     */
    @PostMapping("/api/articles")
    public ResponseEntity<ArticleResponse> createArticle(@RequestBody AddArticleRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(articleService.createArticle(request));
    }

    /**
     * 게시글 수정
     * @param id Long
     * @param request AddArticleRequest
     * @return ArticleResponse
     */
    @PutMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable Long id, @RequestBody UpdateArticleRequest request){
        return ResponseEntity.status(HttpStatus.OK)
                .body(articleService.updateArticle(id, request));
    }

    /**
     * 게시글 한개 조회
     * @param id Long
     * @return ArticleResponse
     */
    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(articleService.getArticleById(id));
    }

    /**
     * 게시글 삭제
     * @param id Long
     */
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticleById(@PathVariable Long id){
        articleService.deleteArticleById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
