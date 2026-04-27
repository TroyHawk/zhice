package com.zhice.document.controller;

import com.zhice.document.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "文档成果生成", description = "AI 辅助一键生成并导出符合赛事规范的 Word 文档")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping("/export/4c/{projectId}")
    @Operation(summary = "一键生成 4C 大赛说明书", description = "基于项目数据和AI分析，直接下载标准 Word 文档")
    public void export4CDocument(
            @Parameter(description = "项目唯一ID", required = true) @PathVariable Long projectId,
            HttpServletResponse response) throws IOException {

        // 获取生成的 Word 字节流
        byte[] wordBytes = documentService.generate4cDocument(projectId);

        // 设置标准的文件下载请求头 (MIME Type 对应 docx)
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setCharacterEncoding("utf-8");

        // 对文件名进行 URL 编码，防止中文字符在部分浏览器中变成乱码
        String fileName = URLEncoder.encode("4C大赛作品说明书_初稿.docx", StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

        // 写入输出流返回给前端
        try (OutputStream os = response.getOutputStream()) {
            os.write(wordBytes);
            os.flush();
        }
    }
}