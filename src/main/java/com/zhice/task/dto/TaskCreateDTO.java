package com.zhice.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "创建任务参数对象")
public class TaskCreateDTO {

    @NotNull(message = "项目ID不能为空")
    @Schema(description = "所属项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long projectId;

    @NotBlank(message = "任务标题不能为空")
    @Schema(description = "任务标题", example = "设计 MySQL 数据库表结构", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "任务描述", example = "需要包含字段说明和索引设计")
    private String description;

    @Schema(description = "优先级: 0-低, 1-中, 2-高 (默认1)", example = "1")
    private Integer priority = 1;

    @Schema(description = "分配给谁处理(用户ID)")
    private Long assigneeId;

    @Schema(description = "截止日期")
    private LocalDateTime deadline;
}