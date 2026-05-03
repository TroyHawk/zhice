package com.zhice.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhice.project.entity.Project;
import com.zhice.project.entity.ProjectMember;
import com.zhice.project.mapper.ProjectMapper;
import com.zhice.project.service.ProjectService;
import com.zhice.project.mapper.ProjectMemberMapper;
import com.zhice.user.entity.User;
import com.zhice.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zhice.project.mapper.ProjectMemberMapper;
import com.zhice.project.vo.ProjectMemberVO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * 项目空间业务实现类
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UserMapper userMapper; // 新增注入，用于校验目标用户

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inviteMember(Long projectId, Long currentUserId, String targetUsername, Integer role) {

        // 1. 越权校验：查出当前操作者在项目中的身份
        ProjectMember inviter = projectMemberMapper.selectOne(
                new LambdaQueryWrapper<ProjectMember>()
                        .eq(ProjectMember::getProjectId, projectId)
                        .eq(ProjectMember::getUserId, currentUserId)
        );

        if (inviter == null) {
            throw new IllegalArgumentException("无权限：您不是该项目的成员");
        }
        if (inviter.getRole() != 1) {
            throw new IllegalArgumentException("无权限：只有项目组长可以邀请新成员");
        }

        // 2. 目标存在性校验：根据用户名查目标用户
        User targetUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, targetUsername)
        );
        if (targetUser == null) {
            throw new IllegalArgumentException("目标用户不存在，请检查学号/教工号输入是否正确");
        }

        // 3. 防重复校验：目标用户是否已经在项目中了
        Long existCount = projectMemberMapper.selectCount(
                new LambdaQueryWrapper<ProjectMember>()
                        .eq(ProjectMember::getProjectId, projectId)
                        .eq(ProjectMember::getUserId, targetUser.getId())
        );
        if (existCount > 0) {
            throw new IllegalArgumentException("该用户已经是团队成员，无需重复邀请");
        }

        // 4. 全部校验通过，执行插入逻辑
        ProjectMember newMember = new ProjectMember();
        newMember.setProjectId(projectId);
        newMember.setUserId(targetUser.getId());
        // 默认赋予核心成员身份
        newMember.setRole(role != null ? role : 2);
        newMember.setJoinTime(LocalDateTime.now());

        projectMemberMapper.insert(newMember);
    }

    @Override
    public List<ProjectMemberVO> getProjectMembers(Long projectId) {
        // 直接调用刚才写好的 Mapper 联表查询方法
        return projectMemberMapper.selectMembersWithUserInfo(projectId);
    }

    @Override
    public void updateProject(Project project) {
        // 1. 校验项目是否存在
        Project existing = projectMapper.selectById(project.getId());
        if (existing == null) {
            throw new IllegalArgumentException("该项目不存在或已被删除");
        }

        // 2. Mybatis-Plus 会自动根据非空字段进行动态 SQL 更新 (修改名称、描述、赛道等)
        projectMapper.updateById(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务，如果删成员报错，删项目也会回滚
    public void deleteProject(Long id) {
        // 1. 校验项目是否存在
        Project existing = projectMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("该项目不存在或已被删除");
        }

        // 2. 逻辑删除项目主表记录
        projectMapper.deleteById(id);

        // 3. 级联逻辑删除该项目下的所有成员记录 (清理 zc_project_member 表)
        LambdaQueryWrapper<ProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMember::getProjectId, id);
        projectMemberMapper.delete(wrapper);

        // (如果有任务 zc_task 或智库文档，理论上也应该在这里清理，后续模块加上后再补充)
        // ToDo
    }
}