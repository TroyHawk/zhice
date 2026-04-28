//package com.zhice.task.controller;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.zhice.common.api.Result;
//import com.zhice.common.context.UserContext;
//import com.zhice.project.entity.ProjectMember;
//import com.zhice.project.service.ProjectMemberService;
//import com.zhice.task.entity.Task;
//import com.zhice.task.service.TaskService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/tasks")
//@RequiredArgsConstructor
//@Tag(name = "Agile Task Board", description = "敏捷任务看板接口，负责任务分配与进度流转")
//public class TaskController {
//
//    private final TaskService taskService;
//    private final ProjectMemberService projectMemberService;
//
//    @Operation(summary = "获取项目的任务看板", description = "获取指定项目下的所有任务（包含状态分类）")
//    @GetMapping("/project/{projectId}")
//    public Result<List<Task>> getProjectTasks(
//            @Parameter(description = "项目ID", required = true) @PathVariable Long projectId) {
//
//        // 1. 安全校验：当前登录用户是否为该项目的成员/指导老师
//        Long currentUserId = UserContext.getUserId();
//        boolean isMember = projectMemberService.lambdaQuery()
//                .eq(ProjectMember::getProjectId, projectId)
//                .eq(ProjectMember::getUserId, currentUserId)
//                .exists();
//
//        if (!isMember) {
//            return Result.error(403, "无权查看该项目的任务看板");
//        }
//
//        // 2. 获取任务列表（前端拿到后可根据 status 字段渲染成 TODO / DOING / DONE 三列）
//        List<Task> tasks = taskService.lambdaQuery()
//                .eq(Task::getProjectId, projectId)
//                .orderByDesc(Task::getPriority) // 高优先级排前面
//                .orderByAsc(Task::getDueDate)   // 截止日期近的排前面
//                .list();
//
//        return Result.success(tasks);
//    }
//
//    @Operation(summary = "创建新任务", description = "组长或指导老师下发竞赛阶段性任务")
//    @PostMapping
//    public Result<Task> createTask(@RequestBody Task task) {
//        // 此处可增加角色校验，如 UserContext.getUserRole() 是否具备分配权限
//        task.setStatus(0); // 默认状态为 TODO
//        taskService.save(task);
//        return Result.success(task);
//    }
//
//    @Operation(summary = "更新任务状态 (拖拽看板)", description = "前端拖拽任务卡片时调用此接口更新状态")
//    @PatchMapping("/{taskId}/status")
//    public Result<Boolean> updateTaskStatus(
//            @PathVariable Long taskId,
//            @Parameter(description = "目标状态 (0, 1, 2)", required = true) @RequestParam Integer status) {
//
//        // 使用 LambdaUpdateWrapper 仅更新状态字段，提升性能
//        boolean success = taskService.lambdaUpdate()
//                .eq(Task::getId, taskId)
//                .set(Task::getStatus, status)
//                .update();
//
//        return success ? Result.success(true) : Result.error(500, "状态更新失败");
//    }
//
//    @Operation(summary = "删除任务")
//    @DeleteMapping("/{taskId}")
//    public Result<Boolean> deleteTask(@PathVariable Long taskId) {
//        // 由于配置了 @TableLogic，这里底层会自动执行 UPDATE set deleted = 1 逻辑删除
//        boolean success = taskService.removeById(taskId);
//        return success ? Result.success(true) : Result.error(500, "删除失败");
//    }
//}