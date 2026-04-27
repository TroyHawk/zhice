package com.zhice.project.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目团队成员映射实体 (打通用户与项目的关系)
 */
@Data
@TableName("zc_project_member")
@Schema(description = "项目团队成员关联表")
public class ProjectMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联的项目ID", example = "1001")
    private Long projectId;

    @Schema(description = "关联的用户ID", example = "501")
    private Long userId;

    @Schema(description = "团队角色：1-组长/项目负责人, 2-核心成员, 3-指导老师", example = "2")
    private Integer roleType;

    @Schema(description = "具体分工（如：前端开发、文档撰写、PPT制作等）")
    private String taskRole;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime joinTime;
}