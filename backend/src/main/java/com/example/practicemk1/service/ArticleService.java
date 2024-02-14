package com.example.practicemk1.service;

import com.example.practicemk1.domain.Article;
import com.example.practicemk1.dto.AddArticleRequest;
import com.example.practicemk1.dto.ArticleResponse;
import com.example.practicemk1.dto.UpdateArticleRequest;
import com.example.practicemk1.repository.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * 전체 게시글 조회
     * @return List<ArticleResponse>
     */
    @Transactional(readOnly = true)
    public List<ArticleResponse> getAllArticles(){
        return articleRepository.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();
    }

    /**
     * 게시글 생성
     * @param request AddArticleRequest
     * @return ArticleResponse
     */
    public ArticleResponse createArticle(AddArticleRequest request) {
            Article savedArticle = articleRepository.save(request.toEntity());
        return new ArticleResponse(savedArticle);
    }

    /**
     * 게시글 하나 조회
     * @param id Long
     * @return ArticleResponse
     */
    @Transactional(readOnly = true)
    public ArticleResponse getArticleById(Long id){
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        return new ArticleResponse(article);
    }

    /**
     * 게시글 수정
     * @param id Long
     * @param request UpdateArticleRequest
     * @return ArticleResponse
     */
    public ArticleResponse updateArticle(Long id, UpdateArticleRequest request) {
        Article article = articleRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("수정할 게시글이 없습니다. id=" + id));
        article.update(request.getTitle(), request.getContent());
        return new ArticleResponse(article);
    }

    /**
     * 게시글 삭제
     * @param id Long
     */
    public void deleteArticleById(Long id) {
        boolean exists = articleRepository.existsById(id);
        if (!exists) {
            throw new IllegalArgumentException("삭제할 게시글이 없습니다. id=" + id);
        }
        articleRepository.deleteById(id);
    }
}
