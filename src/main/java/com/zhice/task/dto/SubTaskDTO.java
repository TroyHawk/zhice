package com.zhice.task.dto;

import lombok.Data;

/**
 * 接收 AI 拆解返回的子任务对象
 */
@Data
public class SubTaskDTO {
    private String title;
    private String description;
    private Integer priority; // 0-低, 1-中, 2-高
}