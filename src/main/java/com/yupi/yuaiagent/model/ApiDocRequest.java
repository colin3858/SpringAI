
package com.yupi.yuaiagent.model;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ApiDocRequest {
    @NotBlank(message = "API名称不能为空")
    private String apiName;
    
    @NotBlank(message = "API路径不能为空")
    private String apiPath;
    
    @NotBlank(message = "HTTP方法不能为空")
    private String httpMethod;
    
    private String description;
    private String parameters;
    private String responseFormat;
}