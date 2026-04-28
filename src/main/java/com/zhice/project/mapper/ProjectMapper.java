package com.zhice.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhice.project.entity.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目表数据访问层
 * 继承 BaseMapper 即可获得基础的 CRUD 能力
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
    // 后续可在此手写复杂的联表 SQL（如查询项目及包含的成员）
}