package com.zhice.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "更新任务详情参数")
public class TaskUpdateDTO {
    @Schema(description = "任务标题")
    private String title;

    @Schema(description = "任务详细描述 (支持富文本/Markdown)")
    private String description;

    @Schema(description = "优先级: 1-普通, 2-高, 3-紧急")
    private Integer priority;

    @Schema(description = "负责人用户ID")
    private Long assigneeId;

    @Schema(description = "截止日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deadline;
}