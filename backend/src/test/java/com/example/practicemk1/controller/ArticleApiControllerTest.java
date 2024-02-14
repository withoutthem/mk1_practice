package com.example.practicemk1.controller;

import com.example.practicemk1.domain.Article;
import com.example.practicemk1.dto.AddArticleRequest;
import com.example.practicemk1.dto.UpdateArticleRequest;
import com.example.practicemk1.repository.ArticleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ArticleApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ArticleRepository articleRepository;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        articleRepository.deleteAll();
    }

    @DisplayName("getAllArticles : 모든 게시글 조회에 성공")
    @Test
    void getAllArticles() throws Exception {

        //given : article 2개 생성
        final String url = "/api/articles";
        final String title1 = "title1";
        final String content1 = "content1";
        final String title2 = "title2";
        final String content2 = "content2";
        articleRepository.save(Article.builder().title(title1).content(content1).build());
        articleRepository.save(Article.builder().title(title2).content(content2).build());

        //when : 모든 게시글 조회
        final ResultActions result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON_VALUE));

        //then : 모든 게시글 조회 성공
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(title1))
                .andExpect(jsonPath("$[0].content").value(content1))
                .andExpect(jsonPath("$[1].title").value(title2))
                .andExpect(jsonPath("$[1].content").value(content2));

        List<Article> articles = articleRepository.findAll();
        assertThat(articles).hasSize(2);

    }

    @DisplayName("createArticle : 게시글 생성에 성공")
    @Test
    void createArticle() throws Exception {
        //given : 게시글 생성
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        //when : 게시글 생성
        ResultActions result = mockMvc
                .perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        //then : 게시글 생성 성공

        // 잘 던지는지 확인
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content));

        // DB에 잘 들어갔는지 확인
        List<Article> articles = articleRepository.findAll();
        assertThat(articles).hasSize(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("updateArticle : 게시글 수정에 성공")
    @Test
    void updateArticle() throws Exception {
        //given : 게시글 생성
        final String url = "/api/articles/{id}";
        final String title = "title";
        final String content = "content";
        Article savedArticle = articleRepository.save(Article.builder().title(title).content(content).build());
        final String newTitle = "newTitle";
        final String newContent = "newContent";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        //when : 게시글 수정
        ResultActions result = mockMvc
                .perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        //then : 수정 응답 성공
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(newTitle))
                .andExpect(jsonPath("$.content").value(newContent));

        // DB에 잘 들어갔는지 확인
        Optional<Article> article = articleRepository.findById(savedArticle.getId());
        assertThat(article).isPresent();
        assertThat(article.get().getTitle()).isEqualTo(newTitle);
        assertThat(article.get().getContent()).isEqualTo(newContent);
    }


    @DisplayName("getArticleById : 게시글 조회에 성공")
    @Test
    void getArticleById() throws Exception {
        //given : 게시글 생성
        final String url = "/api/articles/{id}";
        final String title = "title";
        final String content = "content";
        Article savedArticle = articleRepository.save(Article.builder().title(title).content(content).build());

        //when : 게시글 조회
        ResultActions result = mockMvc
                .perform(get(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        //then : 조회 응답 성공
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content));

        // DB에 잘 들어갔는지 확인
        Optional<Article> article = articleRepository.findById(savedArticle.getId());

        assertThat(article).isPresent();
    }

    @DisplayName("deleteArticleById : 게시글 삭제에 성공")
    @Test
    void deleteArticleById() throws Exception {
        //given
        final String url = "/api/articles/{id}";
        final String title = "title";
        final String content = "content";
        Article savedArticle = articleRepository.save(Article.builder().title(title).content(content).build());

        //when
        ResultActions result = mockMvc
                .perform(delete(url, savedArticle.getId()));

        //then
        result
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        // DB에서 삭제되었는지 확인
        Optional<Article> article = articleRepository.findById(savedArticle.getId());
        assertThat(article.isPresent()).isFalse();
    }
}