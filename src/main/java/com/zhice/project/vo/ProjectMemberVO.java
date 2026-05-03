package com.zhice.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 项目成员列表展示对象
 */
@Data
@Schema(description = "项目成员视图对象")
public class ProjectMemberVO {

    @Schema(description = "成员关联记录的主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户账号(学号/邮箱)")
    private String username;

    @Schema(description = "用户真实姓名/昵称")
    private String realName;

    @Schema(description = "项目角色: 1-组长, 2-普通成员, 3-指导老师")
    private Integer role;

    @Schema(description = "加入项目的时间")
    private LocalDateTime joinTime;
}