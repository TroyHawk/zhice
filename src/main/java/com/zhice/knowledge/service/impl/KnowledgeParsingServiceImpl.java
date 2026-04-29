package com.zhice.knowledge.service.impl;

import com.zhice.knowledge.entity.KnowledgeDocument;
import com.zhice.knowledge.mapper.KnowledgeDocumentMapper;
import com.zhice.knowledge.service.KnowledgeParsingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Slf4j
@Service
public class KnowledgeParsingServiceImpl implements KnowledgeParsingService {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private KnowledgeDocumentMapper documentMapper;

    @Async // 开启异步执行
    @Override
    public void parseAndVectorizeAsync(KnowledgeDocument document) {
        log.info("开始解析文件并向量化: {}", document.getFileName());

        // 1. 更新状态为: 1-解析中
        document.setStatus(1);
        documentMapper.updateById(document);

        try {
            List<Document> splitDocuments;
            String fileType = document.getFileType().toLowerCase();

            // 2. 根据文件类型选择不同的阅读器
            if ("pdf".equals(fileType)) {
                // 解析 PDF 文件
                PagePdfDocumentReader pdfReader = new PagePdfDocumentReader("file:" + document.getFilePath());
                List<Document> rawDocuments = pdfReader.get();

                TokenTextSplitter splitter = new TokenTextSplitter(1000, 400, 10, 10000, true);
                splitDocuments = splitter.apply(rawDocuments);

            } else if ("doc".equals(fileType) || "docx".equals(fileType)) {
                // 解析 Word 文件 (使用 TikaDocumentReader)
                FileSystemResource resource = new FileSystemResource(document.getFilePath());
                TikaDocumentReader tikaReader = new TikaDocumentReader(resource);
                List<Document> rawDocuments = tikaReader.get();

                // 将提取出的 Word 文本同样进行分块切片
                TokenTextSplitter splitter = new TokenTextSplitter(1000, 400, 10, 10000, true);
                splitDocuments = splitter.apply(rawDocuments);

            } else {
                throw new UnsupportedOperationException("当前仅支持 PDF 和 Word 格式的向量化解析");
            }

            // 3. 为每个切片打上项目ID的元数据标签，保证检索时的数据隔离
            splitDocuments.forEach(doc -> doc.getMetadata().put("projectId", document.getProjectId()));

            // 4. 将切片送入向量数据库 (请求大模型 Embedding API)
            vectorStore.add(splitDocuments);

            // 将 SimpleVectorStore 持久化到本地磁盘
            if (vectorStore instanceof SimpleVectorStore) {
                File vectorStoreFile = new File(System.getProperty("user.dir") + "/uploads/vector_store.json");
                ((SimpleVectorStore) vectorStore).save(vectorStoreFile);
            }

            // 5. 更新状态为: 2-已解析
            document.setStatus(2);
            documentMapper.updateById(document);
            log.info("文件向量化成功: {}", document.getFileName());

        } catch (Exception e) {
            log.error("文件向量化失败: {}", document.getFileName(), e);
            // 更新状态为: 3-解析失败
            document.setStatus(3);
            documentMapper.updateById(document);
        }
    }
}