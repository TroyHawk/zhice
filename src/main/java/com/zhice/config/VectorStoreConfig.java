package com.zhice.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class VectorStoreConfig {

    @Bean
    public VectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        // SimpleVectorStore 是一个基于内存的向量库，适合 MVP 阶段和本地测试
        SimpleVectorStore vectorStore = new SimpleVectorStore(embeddingModel);

        // 尝试从本地加载已有的向量数据，防止重启后丢失
        File vectorStoreFile = new File(System.getProperty("user.dir") + "/uploads/vector_store.json");
        if (vectorStoreFile.exists()) {
            vectorStore.load(vectorStoreFile);
        }
        return vectorStore;
    }
}