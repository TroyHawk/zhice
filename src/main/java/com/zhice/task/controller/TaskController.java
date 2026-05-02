package com.zhice.task.controller;

import com.zhice.common.api.Result; // 假设这是你统一定义的返回对象类名
import com.zhice.task.dto.TaskCreateDTO;
import com.zhice.task.dto.TaskStatusUpdateDTO;
import com.zhice.task.entity.Task;
import com.zhice.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "敏捷任务看板中心", description = "项目任务的生命周期流转与管理")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    @Operation(summary = "创建新任务", description = "在指定项目中创建一条待办卡片")
    public Result<String> createTask(@Validated @RequestBody TaskCreateDTO dto) {
        taskService.createTask(dto);
        return Result.success("任务创建成功");
    }

    @PutMapping("/status")
    @Operation(summary = "拖拽更新状态", description = "前端拖拽卡片后调用此接口进行状态流转")
    public Result<String> updateTaskStatus(@Validated @RequestBody TaskStatusUpdateDTO dto) {
        taskService.updateTaskStatus(dto);
        return Result.success("状态更新成功");
    }

    @GetMapping("/projects/{projectId}/board")
    @Operation(summary = "加载看板视图", description = "获取项目下所有任务，并自动按状态列分类好")
    public Result<Map<String, List<Task>>> getProjectBoard(@PathVariable Long projectId) {
        Map<String, List<Task>> board = taskService.getProjectBoard(projectId);
        return Result.success(board);
    }
}