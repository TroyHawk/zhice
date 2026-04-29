package com.zhice.knowledge.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 智库素材实体类
 */
@Data
@TableName("zc_knowledge_document")
@Schema(description = "AI智库素材实体对象")
public class KnowledgeDocument {

    @TableId(type = IdType.AUTO)
    @Schema(description = "文档唯一ID")
    private Long id;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "上传者ID")
    private Long uploaderId;

    @Schema(description = "原始文件名")
    private String fileName;

    @Schema(description = "文件扩展名")
    private String fileType;

    @Schema(description = "文件大小(Byte)")
    private Long fileSize;

    @Schema(description = "物理存储路径")
    private String filePath;

    @Schema(description = "向量化解析状态：0-未解析, 1-解析中, 2-已解析, 3-解析失败")
    private Integer status = 0;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted = 0;
}