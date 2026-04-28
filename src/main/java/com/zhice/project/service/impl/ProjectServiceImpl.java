package com.zhice.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhice.project.entity.Project;
import com.zhice.project.mapper.ProjectMapper;
import com.zhice.project.service.ProjectService;
import org.springframework.stereotype.Service;

/**
 * 项目空间业务实现类
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
    // 基础的 CRUD 已由 ServiceImpl 提供，暂时无需重写
}