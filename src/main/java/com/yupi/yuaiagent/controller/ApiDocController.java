
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
    @Operation(summary = "生成API文档", description = "基于API元数据自动生成详细的API文档")
    public ResponseEntity<String> generateApiDoc(@Valid @RequestBody ApiDocRequest request) {
        try {
            String doc = apiDocService.generateApiDoc(request);
            return ResponseEntity.ok(doc);
        } catch (Exception e) {
            log.error("生成API文档失败", e);
            return ResponseEntity.internalServerError().body("生成API文档失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/qa")
    @Operation(summary = "API智能问答", description = "回答关于API使用、错误排查等问题")
    public ResponseEntity<String> answerQuestion(@Valid @RequestBody QuestionRequest request) {
        try {
            String answer = apiQaService.answerQuestion(request);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            log.error("回答问题失败", e);
            return ResponseEntity.internalServerError().body("回答问题失败: " + e.getMessage());
        }
    }
}