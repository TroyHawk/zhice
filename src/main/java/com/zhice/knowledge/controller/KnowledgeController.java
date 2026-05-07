package com.zhice.knowledge.controller;

import com.zhice.common.api.Result;
import com.zhice.common.context.UserContext;
import com.zhice.knowledge.entity.KnowledgeDocument;
import com.zhice.knowledge.service.KnowledgeDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

@RestController
@RequestMapping("/api/v1/knowledge")
@Tag(name = "AI 智库中心", description = "处理竞赛背景资料的上传、向量化与 RAG 检索")
public class KnowledgeController {

    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;

    @PostMapping(value = "/{projectId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传项目素材资料", description = "支持 PDF/Word/MD 格式，上传后将进入向量化解析队列")
    public Result<KnowledgeDocument> uploadMaterial(
            @Parameter(description = "项目唯一ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "文件对象", required = true) @RequestPart("file") MultipartFile file) {

        // 获取当前登录用户 (从拦截器上下文)
        Long currentUserId = UserContext.getUserId();

        // TODO: 实际生产中这里应调用 ProjectService 校验 currentUserId 是否为该 projectId 的成员

        KnowledgeDocument document = knowledgeDocumentService.uploadMaterial(file, projectId, currentUserId);

        return Result.success(document);
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "获取文档列表", description = "获取当前项目已上传的所有智库文档")
    public Result<List<KnowledgeDocument>> getDocumentList(@PathVariable Long projectId) {
        // 使用 MyBatis-Plus 的条件构造器查询该 projectId 下的文档
        LambdaQueryWrapper<KnowledgeDocument> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeDocument::getProjectId, projectId)
                .orderByDesc(KnowledgeDocument::getId); // 按 ID 倒序，新上传的在最上面

        List<KnowledgeDocument> documentList = knowledgeDocumentService.list(wrapper);
        return Result.success(documentList);
    }

    @DeleteMapping("/{projectId}/{documentId}")
    @Operation(summary = "删除智库文档", description = "从数据库中删除文档记录")
    public Result<String> deleteDocument(@PathVariable Long projectId, @PathVariable Long documentId) {
        // 实际企业开发中，这里除了删数据库记录，还需要去 VectorStore (向量库) 和磁盘里删掉真实文件和向量
        knowledgeDocumentService.removeById(documentId);
        return Result.success("文档删除成功");
    }
}