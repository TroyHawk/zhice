package com.zhice.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "成员贡献统计视图")
public class MemberContributionVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户真实姓名")
    private String realName;

    @Schema(description = "角色：1-组长, 2-成员, 3-指导老师")
    private Integer role;

    @Schema(description = "被分配的任务总数")
    private Long totalTasks;

    @Schema(description = "已完成任务数")
    private Long completedTasks;
}
