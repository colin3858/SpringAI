package com.yupi.yuaiagent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.yuaiagent.model.QuestionRequest;
import com.yupi.yuaiagent.service.ApiDocService;
import com.yupi.yuaiagent.service.ApiQaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ApiDocController.class, excludeAutoConfiguration = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ActiveProfiles("test")
class ApiQaControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApiDocService apiDocService;

    @MockBean
    private ApiQaService apiQaService;

    @Test
    void testQaEndpointWithRAG() throws Exception {
        // Mock service response
        String mockAnswer = "API接口设计的最佳实践包括：\n1. 使用RESTful架构\n2. 合理的HTTP状态码\n3. 版本控制策略";
        when(apiQaService.answerQuestion(any(QuestionRequest.class))).thenReturn(mockAnswer);

        QuestionRequest request = new QuestionRequest();
        request.setQuestion("API接口设计的最佳实践有哪些？");
        request.setContext("RESTful API设计");

        String requestJson = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/doc/qa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("API QA响应:");
        System.out.println(response);
    }

    @Test
    void testQaEndpointWithTechnicalQuestion() throws Exception {
        // Mock service response
        String mockAnswer = "API版本控制可以通过以下方式实现：\n1. URL路径版本控制\n2. 请求头版本控制\n3. 查询参数版本控制";
        when(apiQaService.answerQuestion(any(QuestionRequest.class))).thenReturn(mockAnswer);

        QuestionRequest request = new QuestionRequest();
        request.setQuestion("如何处理API版本控制？");

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/doc/qa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"));
    }

    @Test
    void testQaEndpointWithInvalidRequest() throws Exception {
        QuestionRequest request = new QuestionRequest();
        // 不设置question，测试验证

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/doc/qa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}