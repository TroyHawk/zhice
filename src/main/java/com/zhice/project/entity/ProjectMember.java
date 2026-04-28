package com.zhice.project.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目成员关联实体类
 */
@Data
@TableName("zc_project_member")
@Schema(description = "项目成员及角色信息")
public class ProjectMember {

    @TableId(type = IdType.AUTO)
    @Schema(description = "关联记录唯一ID")
    private Long id;

    @Schema(description = "项目ID", example = "1001")
    private Long projectId;

    @Schema(description = "用户ID", example = "2023001")
    private Long userId;

    @Schema(description = "团队角色：1-组长, 2-成员, 3-指导老师", example = "1")
    private Integer role;

    @Schema(description = "加入时间")
    private LocalDateTime joinTime;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "记录更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "逻辑删除标志")
    private Integer deleted;
}