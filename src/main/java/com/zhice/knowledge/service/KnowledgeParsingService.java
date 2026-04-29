package com.zhice.knowledge.service;

import com.zhice.knowledge.entity.KnowledgeDocument;

public interface KnowledgeParsingService {
    /**
     * 异步解析并向量化文档
     */
    void parseAndVectorizeAsync(KnowledgeDocument document);
}