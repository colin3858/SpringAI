package com.yupi.yuaiagent.service;

import com.yupi.yuaiagent.model.QuestionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@ActiveProfiles("test")
class ApiQaServiceTest {

    @Autowired
    private ApiQaService apiQaService;

    @Test
    void testAnswerQuestionWithRAG() {
        // 准备测试数据
        QuestionRequest request = new QuestionRequest();
        request.setQuestion("如何处理API认证错误？");
        request.setContext("Spring Boot项目中的REST API");

        // 执行测试
        String answer = apiQaService.answerQuestion(request);

        // 验证结果
        assertNotNull(answer);
        assertFalse(answer.trim().isEmpty());
        System.out.println("RAG回答结果:");
        System.out.println(answer);
    }

    @Test
    void testAnswerQuestionAboutAPIDocumentation() {
        QuestionRequest request = new QuestionRequest();
        request.setQuestion("API文档应该包含哪些关键信息？");

        String answer = apiQaService.answerQuestion(request);

        assertNotNull(answer);
        assertTrue(answer.length() > 100); // 确保回答有一定长度
        System.out.println("API文档问题回答:");
        System.out.println(answer);
    }

    @Test
    void testAnswerQuestionWithTechnicalContext() {
        QuestionRequest request = new QuestionRequest();
        request.setQuestion("如何优化API响应性能？");
        request.setContext("高并发场景下的微服务架构");

        String answer = apiQaService.answerQuestion(request);

        assertNotNull(answer);
        assertFalse(answer.trim().isEmpty());
        // 验证回答中包含相关技术术语
        assertTrue(answer.toLowerCase().contains("性能") || 
                  answer.toLowerCase().contains("优化") ||
                  answer.toLowerCase().contains("响应"));
        System.out.println("技术问题回答:");
        System.out.println(answer);
    }

    @Test
    void testAnswerQuestionWithEmptyContext() {
        QuestionRequest request = new QuestionRequest();
        request.setQuestion("什么是RESTful API？");
        // 不设置context，测试默认处理

        String answer = apiQaService.answerQuestion(request);

        assertNotNull(answer);
        assertFalse(answer.trim().isEmpty());
        System.out.println("无上下文问题回答:");
        System.out.println(answer);
    }
}
