package com.zhice.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhice.project.entity.Project;

import java.util.List;

/**
 * 项目空间业务接口层
 */
public interface ProjectService extends IService<Project> {
    // TODO: 后续在这里定义扩展业务，例如"创建项目并同时将创建者设为组长"的复合逻辑

    /**
     * 获取指定用户参与的项目列表
     */
    List<Project> findUserProjects(Long userId);

    /**
     * 创建项目并自动将创建者绑定为组长
     * @param project 项目基本信息
     * @param userId 当前创建者的用户ID
     * @return 创建成功的项目（包含回填的数据库主键ID）
     */
    Project createProjectWithLeader(Project project, Long userId);


    /**
     * 邀请新成员加入项目
     * @param projectId 项目ID
     * @param currentUserId 当前操作用户的ID (必须是组长)
     * @param targetUsername 被邀请人的用户名/学号
     * @param role 赋予的角色 (2或3)
     */
    void inviteMember(Long projectId, Long currentUserId, String targetUsername, Integer role);
}