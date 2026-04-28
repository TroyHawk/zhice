package com.zhice.project.controller;

import com.zhice.common.api.Result;
import com.zhice.project.entity.Project;
import com.zhice.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.zhice.common.context.UserContext;
import java.util.List;

/**
 * 项目空间前台控制器
 */
@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "项目空间管理", description = "提供项目的创建、查询、更新和删除等RESTful接口")
public class ProjectController {

    @Autowired
    private ProjectService projectService; // 假设 Service 层已实现基础的 CRUD


    @GetMapping
    @Operation(summary = "获取当前用户的项目列表", description = "返回当前登录用户参与的所有项目（含创建和加入的）")
    public Result<List<Project>> listProjects() {
        // 直接从上下文中优雅地获取用户ID，无需在接口传参
        Long currentUserId = UserContext.getUserId();

        // TODO: 结合 zc_project_member 表查询关联项目 (下个阶段完善多表联查)
        // 暂且打印出来以验证鉴权是否成功
        System.out.println("====== 当前发起请求的用户ID是: " + currentUserId + " ======");

        List<Project> list = projectService.list();
        return Result.success(list);
    }

    @PostMapping
    @Operation(summary = "创建新项目空间", description = "组长或发起人创建一个新的竞赛项目")
    public Result<Project> createProject(@RequestBody Project project) {
        projectService.save(project);
        return Result.success(project);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取项目详情", description = "根据项目ID获取详细信息")
    public Result<Project> getProject(
            @Parameter(description = "项目唯一ID", required = true) @PathVariable Long id) {
        Project project = projectService.getById(id);
        if (project == null) {
            return Result.error(404, "项目不存在");
        }
        return Result.success(project);
    }
}