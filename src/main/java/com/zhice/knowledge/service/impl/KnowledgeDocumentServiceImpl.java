package com.zhice.knowledge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhice.knowledge.entity.KnowledgeDocument;
import com.zhice.knowledge.mapper.KnowledgeDocumentMapper;
import com.zhice.knowledge.service.KnowledgeDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.zhice.knowledge.service.KnowledgeParsingService;
import org.springframework.context.annotation.Lazy;


import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class KnowledgeDocumentServiceImpl extends ServiceImpl<KnowledgeDocumentMapper, KnowledgeDocument> implements KnowledgeDocumentService {

    // 暂时将文件存储在项目根目录下的 uploads 文件夹中
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "knowledge";


    @Autowired
    @Lazy // 使用 Lazy 防止循环依赖
    private KnowledgeParsingService parsingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KnowledgeDocument uploadMaterial(MultipartFile file, Long projectId, Long userId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }

        // 1. 获取原始文件名和后缀
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }

        // 2. 校验文件类型 (MVP阶段先支持常见的文档格式)
        if (!fileExtension.matches("pdf|docx|doc|txt|md")) {
            throw new IllegalArgumentException("不支持的文件类型，仅支持 pdf, docx, txt, md");
        }

        // 3. 构建本地存储目录
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("创建上传目录失败");
        }

        // 4. 生成唯一文件名，防止重名覆盖
        String newFileName = UUID.randomUUID().toString() + "." + fileExtension;
        File destFile = new File(dir, newFileName);

        try {
            // 5. 将文件流写入物理磁盘
            file.transferTo(destFile);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件存储失败，请稍后重试");
        }

        // 6. 构造元数据对象并存入数据库
        KnowledgeDocument document = new KnowledgeDocument();
        document.setProjectId(projectId);
        document.setUploaderId(userId);
        document.setFileName(originalFilename);
        document.setFileType(fileExtension);
        document.setFileSize(file.getSize());
        document.setFilePath(destFile.getAbsolutePath());
        document.setStatus(0); // 0-未解析 (等待下一阶段切片和向量化)
        this.save(document);

        parsingService.parseAndVectorizeAsync(document);
        return document;
    }
}