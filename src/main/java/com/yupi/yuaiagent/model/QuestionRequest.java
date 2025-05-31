
package com.yupi.yuaiagent.model;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class QuestionRequest {
    @NotBlank(message = "问题不能为空")
    private String question;
    
    private String context; // API相关上下文
}