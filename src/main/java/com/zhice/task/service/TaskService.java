package com.zhice.task.service;

import com.zhice.task.dto.TaskCreateDTO;
import com.zhice.task.dto.TaskStatusUpdateDTO;
import com.zhice.task.entity.Task;

import java.util.List;
import java.util.Map;

public interface TaskService {

    /**
     * 创建新任务
     */
    void createTask(TaskCreateDTO dto);

    /**
     * 更新任务状态（拖拽流转）
     */
    void updateTaskStatus(TaskStatusUpdateDTO dto);

    /**
     * 获取项目专属看板视图
     * 返回按状态分组的字典，方便前端直接渲染各列
     */
    Map<String, List<Task>> getProjectBoard(Long projectId);
}