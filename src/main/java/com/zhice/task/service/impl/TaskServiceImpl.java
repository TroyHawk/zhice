package com.zhice.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhice.common.context.UserContext;
import com.zhice.task.dto.TaskCreateDTO;
import com.zhice.task.dto.TaskStatusUpdateDTO;
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

    @Override
    public void createTask(TaskCreateDTO dto) {
        Task task = new Task();
        // 将 DTO 里的同名字段拷贝到 Entity 中
        BeanUtils.copyProperties(dto, task);

        // 初始状态默认为 0 (待处理)
        task.setStatus(0);
        // 设置当前操作者为创建人
        task.setCreatorId(UserContext.getUserId() );

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
}