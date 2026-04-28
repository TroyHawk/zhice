package com.zhice.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhice.project.entity.Project;

/**
 * 项目空间业务接口层
 */
public interface ProjectService extends IService<Project> {
    // TODO: 后续在这里定义扩展业务，例如"创建项目并同时将创建者设为组长"的复合逻辑
}