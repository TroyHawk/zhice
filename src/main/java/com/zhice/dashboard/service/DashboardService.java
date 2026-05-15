package com.zhice.dashboard.service;

import com.zhice.dashboard.vo.DashboardVO;

public interface DashboardService {

    /**
     * 获取指定项目的仪表盘全量聚合数据
     * @param projectId 项目ID
     * @return 仪表盘聚合视图，包含任务统计、完成趋势、成员贡献、知识库和AI生成等维度
     */
    DashboardVO getProjectDashboard(Long projectId);
}
