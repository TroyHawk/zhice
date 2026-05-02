package com.zhice.task.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("zc_task")
@Schema(description = "敏捷任务实体")
public class Task {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "任务标题")
    private String title;

    @Schema(description = "任务详细描述")
    private String description;

    @Schema(description = "状态: 0-待处理, 1-进行中, 2-审核中, 3-已完成", example = "0")
    private Integer status;

    @Schema(description = "优先级: 0-低, 1-中, 2-高", example = "1")
    private Integer priority;

    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "被指派的负责人ID")
    private Long assigneeId;

    @Schema(description = "截止时间")
    private LocalDateTime deadline;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(select = false) // 查询时不返回这个字段
    private Integer deleted;
}