package com.zhice.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhice.project.entity.Project;
import com.zhice.project.entity.ProjectMember;
import com.zhice.project.mapper.ProjectMapper;
import com.zhice.project.service.ProjectService;
import com.zhice.project.mapper.ProjectMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目空间业务实现类
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Override
    public List<Project> findUserProjects(Long userId) {
        // 调用自定义的 Mapper 方法
        return baseMapper.selectProjectsByUserId(userId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务，遇到任何异常都会回滚
    public Project createProjectWithLeader(Project project, Long userId) {
        // 1. 保存项目基本信息到 zc_project 表
        // MyBatis-Plus 插入成功后，会自动将生成的自增 ID 赋值给 project 对象的 id 属性
        this.save(project);

        // 2. 构建项目成员关联对象
        ProjectMember member = new ProjectMember();
        member.setProjectId(project.getId()); // 获取刚刚生成的项目ID
        member.setUserId(userId);             // 绑定当前用户ID
        member.setRole(1);                    // 角色设定为 1 (组长/发起人)
        member.setJoinTime(LocalDateTime.now()); // 设置加入时间

        // 3. 将关联记录插入到 zc_project_member 表
        projectMemberMapper.insert(member);

        // 4. 返回包含最新ID的项目对象
        return project;
    }
}