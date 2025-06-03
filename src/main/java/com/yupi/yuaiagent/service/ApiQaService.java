package com.yupi.yuaiagent.service;

import com.yupi.yuaiagent.model.QuestionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ApiQaService {
    
    private final ChatClient chatClient;
    private final FileService fileService;
    private final VectorStore vectorStore;
    
    public ApiQaService(ChatClient chatClient, FileService fileService, VectorStore apiVectorStore) {
        this.chatClient = chatClient;
        this.fileService = fileService;
        this.vectorStore = apiVectorStore;
    }
    
    public String answerQuestion(QuestionRequest request) {
        try {
            // 使用RAG检索相关文档
            String retrievedContext = retrieveRelevantDocuments(request.getQuestion());
            
            String promptTemplate = """
                你是一个专业的API技术支持助手，请根据用户的问题和检索到的相关文档提供准确、详细的回答。
                
                用户问题：{question}
                用户提供的上下文：{userContext}
                
                检索到的相关文档：
                {retrievedContext}
                
                请遵循以下要求：
                1. 优先基于检索到的文档内容回答问题
                2. 回答要准确、专业、易懂
                3. 如果是技术问题，提供具体的解决步骤
                4. 如果涉及代码，提供完整可用的示例
                5. 如果是错误排查，提供系统的排查思路
                6. 回答要结构清晰，使用Markdown格式
                7. 如果检索到的文档不足以回答问题，请说明并基于通用知识回答
                
                如果问题不够清楚，请主动询问需要补充的信息。
                """;
            
            PromptTemplate template = new PromptTemplate(promptTemplate);
            Map<String, Object> variables = Map.of(
                "question", request.getQuestion(),
                "userContext", request.getContext() != null ? request.getContext() : "无特定上下文",
                "retrievedContext", retrievedContext
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
    
    /**
     * RAG检索增强生成的完整流程：
     * 
     * 1. 【文档准备阶段】(ApiDocumentLoader)
     *    - 扫描加载API文档(.md文件)
     *    - 文档分割成小片段(500 tokens)
     *    - 添加元数据(文件名等)
     * 
     * 2. 【向量化阶段】(ApiVectorStoreConfig)  
     *    - 使用DashScope嵌入模型
     *    - 将文档片段转换为向量
     *    - 存储到向量数据库
     * 
     * 3. 【检索阶段】(retrieveRelevantDocuments)
     *    - 用户问题向量化
     *    - 相似度搜索匹配文档
     *    - 返回最相关的文档片段
     * 
     * 4. 【生成阶段】(answerQuestion)
     *    - 组合用户问题+检索文档+上下文
     *    - 发送��大模型生成回答
     *    - 返回专业的API技术回答
     */
    private String retrieveRelevantDocuments(String question) {
        try {
            // 修复相似度搜索方法调用
            List<Document> documents = vectorStore.similaritySearch(question);
            
            // 如果需要限制返回数量，手动截取前5个
            if (documents.size() > 5) {
                documents = documents.subList(0, 5);
            }
            
            if (documents.isEmpty()) {
                log.warn("未检索到相关文档，问题: {}", question);
                return "未找到相关文档";
            }
            
            log.info("检索到 {} 个相关文档片段", documents.size());
            
            // 将文档内容组合成上下文字符串
            return documents.stream()
                    .map(doc -> {
                        String content = doc.getText();
                        String filename = doc.getMetadata().getOrDefault("filename", "未知文件").toString();
                        return String.format("文件: %s\n内容: %s", filename, content);
                    })
                    .collect(Collectors.joining("\n\n---\n\n"));
                    
        } catch (Exception e) {
            log.error("检索相关文档失败", e);
            return "文档检索失败: " + e.getMessage();
        }
    }
}