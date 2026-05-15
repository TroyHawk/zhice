package com.zhice.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "AI 申报书生成统计视图")
public class DraftStatsVO {

    @Schema(description = "生成的申报书草稿总数")
    private Long totalDrafts;

    @Schema(description = "最近一次生成时间")
    private LocalDateTime latestDraftTime;
}
