package com.zhice.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "拖拽更新任务状态参数")
public class TaskStatusUpdateDTO {

    @NotNull(message = "任务ID不能为空")
    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long taskId;

    @NotNull(message = "目标状态不能为空")
    @Schema(description = "拖拽后的新状态: 0-待处理, 1-进行中, 2-审核中, 3-已完成", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;
}