package com.zhice.knowledge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhice.knowledge.entity.KnowledgeDocument;
import org.springframework.web.multipart.MultipartFile;

public interface KnowledgeDocumentService extends IService<KnowledgeDocument> {

    /**
     * 上传项目素材到智库
     * @param file 物理文件
     * @param projectId 所属项目ID
     * @param userId 上传人ID
     * @return 存储的文档记录
     */
    KnowledgeDocument uploadMaterial(MultipartFile file, Long projectId, Long userId);
}