package com.yupi.yuaiagent.service;

import com.yupi.yuaiagent.model.ApiDocRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ApiDocService {
    
    private final ChatClient chatClient;
    
    public ApiDocService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    public String generateApiDoc(ApiDocRequest request) {
        try {
            String promptTemplate = """
                请根据以下API信息生成详细的API文档：
                
                API名称：{apiName}
                API路径：{apiPath}
                HTTP方法：{httpMethod}
                描述：{description}
                参数信息：{parameters}
                响应格式：{responseFormat}
                
                请生成包含以下内容的API文档：
                1. API概述和功能描述
                2. 请求参数详细说明
                3. 响应参数详细说明
                4. 完整的请求示例（包括curl命令和代码示例）
                5. 响应示例
                6. 错误码说明
                7. 注意事项和最佳实践
                
                请使用Markdown格式输出，内容要详细、专业、易懂。
                """;
            
            PromptTemplate template = new PromptTemplate(promptTemplate);
            Map<String, Object> variables = Map.of(
                "apiName", request.getApiName(),
                "apiPath", request.getApiPath(),
                "httpMethod", request.getHttpMethod(),
                "description", request.getDescription() != null ? request.getDescription() : "暂无描述",
                "parameters", request.getParameters() != null ? request.getParameters() : "暂无参数信息",
                "responseFormat", request.getResponseFormat() != null ? request.getResponseFormat() : "暂无响应格式信息"
            );
            
            Prompt prompt = template.create(variables);
            ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
            
            return response.getResult().getOutput().getText();
            
        } catch (Exception e) {
            log.error("生成API文档失败", e);
            throw new RuntimeException("生成API文档失败: " + e.getMessage());
        }
    }
}