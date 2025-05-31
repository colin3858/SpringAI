package com.yupi.yuaiagent.service;

import com.yupi.yuaiagent.model.ApiDocRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
public class ApiDocService {
    
    private final ChatClient chatClient;
    private final FileService fileService;
    
    public ApiDocService(ChatClient chatClient, FileService fileService) {
        this.chatClient = chatClient;
        this.fileService = fileService;
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
            
            String generatedDoc = response.getResult().getOutput().getText();
            
            // 添加文档头部信息
            String fullDoc = String.format("""
                # %s API文档
                
                > **生成时间**: %s  
                > **API路径**: `%s`  
                > **HTTP方法**: `%s`  
                > **文档版本**: 1.0
                
                ---
                
                %s
                
                ---
                *本文档由AI智能生成，如有疑问请联系API负责人*
                """, 
                request.getApiName(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                request.getApiPath(),
                request.getHttpMethod(),
                generatedDoc
            );
            
            // 保存为 Markdown 文件
            String fileName = sanitizeFileName(request.getApiName()) + "_api_doc";
            String filePath = fileService.saveMarkdownFile(fullDoc, fileName);
            log.info("API文档已保存到文件: {}", filePath);
            
            return fullDoc;
            
        } catch (Exception e) {
            log.error("生成API文档失败", e);
            throw new RuntimeException("生成API文档失败: " + e.getMessage());
        }
    }
    
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_")
                      .replaceAll("\\s+", "_");
    }
}