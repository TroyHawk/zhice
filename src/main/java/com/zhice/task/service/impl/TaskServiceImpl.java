package com.zhice.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhice.common.context.UserContext;
import com.zhice.project.entity.ProjectMember;
import com.zhice.project.mapper.ProjectMemberMapper;
import com.zhice.task.dto.TaskCreateDTO;
import com.zhice.task.dto.TaskStatusUpdateDTO;
import com.zhice.task.dto.TaskUpdateDTO;
import com.zhice.task.entity.Task;
import com.zhice.task.mapper.TaskMapper;
import com.zhice.task.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Override
    public void createTask(TaskCreateDTO dto) {
        Long currentUserId = UserContext.getUserId();

        // 1. 查询当前用户在本项目中的角色
        ProjectMember memberRecord = projectMemberMapper.selectOne(
                new LambdaQueryWrapper<ProjectMember>()
                        .eq(ProjectMember::getProjectId, dto.getProjectId())
                        .eq(ProjectMember::getUserId, currentUserId)
        );
        if (memberRecord == null) {
            throw new RuntimeException("您不是该项目的成员，无法创建任务");
        }

        Task task = new Task();
        // 将 DTO 里的同名字段拷贝到 Entity 中
        BeanUtils.copyProperties(dto, task);

        // 初始状态默认为 0 (待处理)
        task.setStatus(0);
        // 设置当前操作者为创建人
        task.setCreatorId(UserContext.getUserId() );
        // 角色: 1-队长, 2-普通成员, 3-指导老师
        if (memberRecord.getRole() == 2) {
            // 如果是普通成员，强行指派给自己，无视前端传的 assigneeId
            task.setAssigneeId(currentUserId);
        } else {
            // 队长或老师，使用前端传来的指派对象（如果没传，默认设为自己）
            task.setAssigneeId(dto.getAssigneeId() != null ? dto.getAssigneeId() : currentUserId);
        }
        taskMapper.insert(task);
    }

    @Override
    public void updateTaskStatus(TaskStatusUpdateDTO dto) {
        Task task = taskMapper.selectById(dto.getTaskId());
        if (task == null) {
            throw new IllegalArgumentException("任务不存在");
        }

        task.setStatus(dto.getStatus());
        taskMapper.updateById(task);
    }

    @Override
    public Map<String, List<Task>> getProjectBoard(Long projectId) {
        // 1. 查出该项目下的所有任务
        List<Task> allTasks = taskMapper.selectList(
                new QueryWrapper<Task>().eq("project_id", projectId)
        );

        // 2. 按照 status 字段进行分组
        Map<Integer, List<Task>> groupedTasks = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus));

        // 3. 封装为字典结构 (键名和前端 Vue 看板列名绑定)
        Map<String, List<Task>> board = new HashMap<>();
        board.put("todo", groupedTasks.getOrDefault(0, new ArrayList<>()));
        board.put("inProgress", groupedTasks.getOrDefault(1, new ArrayList<>()));
        board.put("review", groupedTasks.getOrDefault(2, new ArrayList<>()));
        board.put("done", groupedTasks.getOrDefault(3, new ArrayList<>()));

        return board;
    }
    @Override
    public void updateTaskDetails(Long taskId, TaskUpdateDTO dto) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在");
        }

        // 只更新前端传过来的非空字段
        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());
        if (dto.getAssigneeId() != null) task.setAssigneeId(dto.getAssigneeId());
        if (dto.getDeadline() != null) task.setDeadline(dto.getDeadline());

        taskMapper.updateById(task);
    }
}