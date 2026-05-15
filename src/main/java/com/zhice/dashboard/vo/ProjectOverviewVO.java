package com.zhice.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "项目概览视图")
public class ProjectOverviewVO {

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "竞赛类型：1-大创, 2-互联网+, 3-挑战杯, 4-应用设计大赛, 5-其他")
    private Integer competitionType;

    @Schema(description = "团队成员数量")
    private Long memberCount;

    @Schema(description = "项目状态：0-筹备中, 1-进行中, 2-已结题")
    private Integer projectStatus;
}
