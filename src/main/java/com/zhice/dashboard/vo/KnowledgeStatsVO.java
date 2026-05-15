package com.zhice.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "知识库统计视图")
public class KnowledgeStatsVO {

    @Schema(description = "文档总数")
    private Long totalDocuments;

    @Schema(description = "已解析文档数")
    private Long parsedCount;

    @Schema(description = "解析中文档数")
    private Long parsingCount;

    @Schema(description = "解析失败文档数")
    private Long failedCount;

    @Schema(description = "文件总大小，单位 Byte")
    private Long totalFileSize;
}
