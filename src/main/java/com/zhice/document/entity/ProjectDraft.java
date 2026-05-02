package com.zhice.document.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("zc_project_draft")
@Schema(description = "申报书草稿版本")
public class ProjectDraft {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;
    private Long creatorId;

    @Schema(description = "版本名", example = "v1-AI初稿")
    private String versionName;

    @Schema(description = "申报书正文内容")
    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted = 0;
}