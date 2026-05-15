package com.zhice.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "任务统计视图")
public class TaskStatsVO {

    @Schema(description = "任务总数")
    private Long total;

    @Schema(description = "待处理任务数")
    private Long todo;

    @Schema(description = "进行中任务数")
    private Long inProgress;

    @Schema(description = "审核中任务数")
    private Long review;

    @Schema(description = "已完成任务数")
    private Long done;

    @Schema(description = "完成率百分比，保留两位小数")
    private Double completionRate;
}
