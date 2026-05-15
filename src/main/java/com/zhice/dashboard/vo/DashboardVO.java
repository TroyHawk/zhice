package com.zhice.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "仪表盘聚合数据视图对象")
public class DashboardVO {

    @Schema(description = "项目概览信息")
    private ProjectOverviewVO projectOverview;

    @Schema(description = "任务统计概览（饼图数据源）")
    private TaskStatsVO taskStats;

    @Schema(description = "近7天任务完成趋势（折线图数据源）")
    private List<DailyCompletionVO> dailyCompletion;

    @Schema(description = "成员贡献排行（柱状图数据源）")
    private List<MemberContributionVO> memberContributions;

    @Schema(description = "知识库素材统计")
    private KnowledgeStatsVO knowledgeStats;

    @Schema(description = "AI 申报书生成统计")
    private DraftStatsVO draftStats;
}
