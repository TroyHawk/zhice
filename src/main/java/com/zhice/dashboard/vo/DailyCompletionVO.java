package com.zhice.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "单日完成统计")
public class DailyCompletionVO {

    @Schema(description = "日期，格式 yyyy-MM-dd")
    private String date;

    @Schema(description = "当日完成任务数")
    private Long count;
}
