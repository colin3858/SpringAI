package com.yupi.yuaiagent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class FileService {
    
    private static final String OUTPUT_DIR = "generated-docs";
    
    public String saveMarkdownFile(String content, String fileName) {
        try {
            // 创建输出目录
            Path outputPath = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }
            
            // 生成文件名（如果未提供）
            if (fileName == null || fileName.isEmpty()) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                fileName = "api_doc_" + timestamp + ".md";
            } else if (!fileName.endsWith(".md")) {
                fileName += ".md";
            }
            
            // 保存文件
            Path filePath = outputPath.resolve(fileName);
            Files.write(filePath, content.getBytes("UTF-8"));
            
            String absolutePath = filePath.toAbsolutePath().toString();
            log.info("Markdown 文件已保存到: {}", absolutePath);
            
            return absolutePath;
            
        } catch (IOException e) {
            log.error("保存 Markdown 文件失败", e);
            throw new RuntimeException("保存文件失败: " + e.getMessage());
        }
    }
    
    public String saveQaMarkdownFile(String question, String answer) {
        String content = String.format("""
            # API 智能问答
            
            ## 问题
            %s
            
            ## 回答
            %s
            
            ---
            *生成时间: %s*
            """, 
            question, 
            answer, 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "qa_" + timestamp + ".md";
        
        return saveMarkdownFile(content, fileName);
    }
}
