package com.yupi.yuaiagent.service;

import com.yupi.yuaiagent.model.QuestionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ApiQaService {
    
    private final ChatClient chatClient;
    private final FileService fileService;
    
    public ApiQaService(ChatClient chatClient, FileService fileService) {
        this.chatClient = chatClient;
        this.fileService = fileService;
    }
    
    public String answerQuestion(QuestionRequest request) {
        try {
            String promptTemplate = """
                你是一个专业的API技术支持助手，请根据用户的问题提供准确、详细的回答。
                
                用户问题：{question}
                相关上下文：{context}
                
                请遵循以下要求：
                1. 回答要准确、专业、易懂
                2. 如果是技术问题，提供具体的解决步骤
                3. 如果涉及代码，提供完整可用的示例
                4. 如果是错误排查，提供系统的排查思路
                5. 回答要结构清晰，使用Markdown格式
                
                如果问题不够清楚，请主动询问需要补充的信息。
                """;
            
            PromptTemplate template = new PromptTemplate(promptTemplate);
            Map<String, Object> variables = Map.of(
                "question", request.getQuestion(),
                "context", request.getContext() != null ? request.getContext() : "无特定上下文"
            );
            
            Prompt prompt = template.create(variables);
            ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
            
            String answer = response.getResult().getOutput().getText();
            
            // 保存问答为 Markdown 文件
            String filePath = fileService.saveQaMarkdownFile(request.getQuestion(), answer);
            log.info("问答记录已保存到文件: {}", filePath);
            
            return answer;
            
        } catch (Exception e) {
            log.error("回答问题失败", e);
            throw new RuntimeException("回答问题失败: " + e.getMessage());
        }
    }
}