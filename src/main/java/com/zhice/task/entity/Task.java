package com.zhice.task.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 敏捷任务看板实体类
 */
@Data
@TableName("zc_task")
@Schema(description = "敏捷任务实体")
public class Task {

    @TableId(type = IdType.AUTO)
    @Schema(description = "任务唯一ID")
    private Long id;

    @Schema(description = "所属项目ID", example = "1001")
    private Long projectId;

    @Schema(description = "任务标题", example = "完成4C大赛平台原型设计")
    private String title;

    @Schema(description = "任务详情描述/要求")
    private String description;

    @Schema(description = "负责人(成员)ID", example = "501")
    private Long assigneeId;

    @Schema(description = "任务状态: 0-TODO(待办), 1-DOING(进行中), 2-DONE(已完成)", example = "0")
    private Integer status;

    @Schema(description = "任务优先级: 1-低, 2-中, 3-高", example = "3")
    private Integer priority;

    @Schema(description = "截止日期（针对竞赛节点）")
    private LocalDateTime dueDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}