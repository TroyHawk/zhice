package com.zhice.project.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 竞赛项目实体类
 */
@Data
@TableName("zc_project")
@Schema(description = "项目空间实体对象")
public class Project {

    @TableId(type = IdType.AUTO)
    @Schema(description = "项目唯一ID", example = "1001")
    private Long id;

    @Schema(description = "项目名称（例如：第十七届挑战杯）", example = "AI医疗辅助诊断系统")
    private String name;

    @Schema(description = "项目简介/描述")
    private String description;

    @Schema(description = "竞赛类型：1-大创, 2-互联网+, 3-挑战杯, 4-4C大赛等", example = "4")
    private Integer competitionType;

    @Schema(description = "项目状态：0-筹备中, 1-进行中, 2-已结题", example = "1")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "逻辑删除标志")
    private Integer deleted;
}