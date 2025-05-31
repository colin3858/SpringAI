package com.yupi.yuaiagent.controller;

import com.yupi.yuaiagent.model.ApiDocRequest;
import com.yupi.yuaiagent.model.QuestionRequest;
import com.yupi.yuaiagent.service.ApiDocService;
import com.yupi.yuaiagent.service.ApiQaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/doc")
@Tag(name = "智能API文档", description = "AI驱动的API文档生成与问答")
public class ApiDocController {
    
    private final ApiDocService apiDocService;
    private final ApiQaService apiQaService;
    
    public ApiDocController(ApiDocService apiDocService, ApiQaService apiQaService) {
        this.apiDocService = apiDocService;
        this.apiQaService = apiQaService;
    }
    
    @PostMapping("/generate")
    @Operation(summary = "生成API文档", description = "基于API元数据自动生成详细的API文档并保存为Markdown文件")
    public ResponseEntity<String> generateApiDoc(@Valid @RequestBody ApiDocRequest request) {
        try {
            String doc = apiDocService.generateApiDoc(request);
            
            // 在响应中添加文件保存提示
            String response = doc + "\n\n---\n**注意**: 文档已自动保存为 Markdown 文件到 `generated-docs` 目录中。";
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("生成API文档失败", e);
            return ResponseEntity.internalServerError().body("生成API文档失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/qa")
    @Operation(summary = "API智能问答", description = "回答关于API使用、错误排查等问题并保存为Markdown文件")
    public ResponseEntity<String> answerQuestion(@Valid @RequestBody QuestionRequest request) {
        try {
            String answer = apiQaService.answerQuestion(request);
            
            // 在响应中添加文件保存提示
            String response = answer + "\n\n---\n**注意**: 问答记录已自动保存为 Markdown 文件到 `generated-docs` 目录中。";
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("回答问题失败", e);
            return ResponseEntity.internalServerError().body("回答问题失败: " + e.getMessage());
        }
    }
}