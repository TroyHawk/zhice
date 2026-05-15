package com.zhice.dashboard.controller;

import com.zhice.common.api.Result;
import com.zhice.dashboard.service.DashboardService;
import com.zhice.dashboard.vo.DashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "可视化仪表盘", description = "提供项目维度的多维度统计数据，供前端 ECharts 图表渲染")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/projects/{projectId}")
    @Operation(summary = "获取项目仪表盘数据",
               description = "返回项目的任务统计、完成趋势、成员贡献、知识库统计和 AI 生成统计等聚合数据")
    public Result<DashboardVO> getProjectDashboard(
            @Parameter(description = "项目ID", required = true)
            @PathVariable Long projectId) {
        DashboardVO dashboard = dashboardService.getProjectDashboard(projectId);
        return Result.success(dashboard);
    }
}
