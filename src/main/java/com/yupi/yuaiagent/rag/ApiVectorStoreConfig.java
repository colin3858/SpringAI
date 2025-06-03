package com.yupi.yuaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * API文档向量数据库配置
 * 
 * 主要作用：
 * 1. 配置向量存储引擎（SimpleVectorStore）
 * 2. 加载API文档并转换为向量
 * 3. 为RAG检索提供向量数据源
 * 4. 支持语义相似度搜索
 */
@Configuration
public class ApiVectorStoreConfig {

    @Resource
    private ApiDocumentLoader apiDocumentLoader;

    /**
     * 创建API文档向量存储
     * - 使用阿里云DashScope的嵌入模型
     * - 加载并向量化所有API文档
     * - 提供相似度搜索能力
     */
    @Bean
    VectorStore apiVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        // 加载文档
        List<Document> documents = apiDocumentLoader.loadMarkdowns();
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }
}