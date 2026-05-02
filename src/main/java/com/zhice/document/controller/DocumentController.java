package com.zhice.document.controller;

import com.zhice.common.api.Result;
import com.zhice.common.context.UserContext;
import com.zhice.document.dto.IterateDTO;
import com.zhice.document.entity.ProjectDraft;
import com.zhice.document.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "成果生成中心", description = "AI 协同申报书生成与导出")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/{projectId}/generate-initial")
    @Operation(summary = "1. 初次生成草稿", description = "读取项目的AI智库资料，一键生成初稿")
    public Result<String> generateInitial(@PathVariable Long projectId) {
        return Result.success(documentService.generateInitialContent(projectId));
    }

    @PostMapping("/{projectId}/iterate")
    @Operation(summary = "2. 对话式微调", description = "输入修改要求，AI重写内容")
    public Result<String> iterate(@PathVariable Long projectId, @RequestBody IterateDTO dto) {
        return Result.success(documentService.iterateContent(projectId, dto.getOldContent(), dto.getUserPrompt()));
    }

    @PostMapping("/drafts")
    @Operation(summary = "3. 保存版本", description = "将满意的 JSON 文本存入数据库")
    public Result<Long> saveDraft(@RequestBody ProjectDraft draft) {
        draft.setCreatorId(UserContext.getUserId());
        documentService.saveDraft(draft);
        // 返回刚插入数据库自动生成的自增 ID，极大方便测试
        return Result.success(draft.getId());
    }

    @GetMapping("/{draftId}/export")
    @Operation(summary = "4. 导出定稿Word", description = "将指定的草稿渲染进Word模板并下载")
    public void export(@PathVariable Long draftId, HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            String fileName = URLEncoder.encode("申报书定稿.docx", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
            documentService.exportDraftToWord(draftId, response.getOutputStream());
        } catch (Exception e) {
            response.reset();
            response.setContentType("application/json;charset=utf-8");
            try {
                response.getWriter().println("{\"code\":500,\"message\":\"导出失败：" + e.getMessage() + "\"}");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}