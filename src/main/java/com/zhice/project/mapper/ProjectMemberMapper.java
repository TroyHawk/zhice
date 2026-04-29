package com.zhice.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhice.project.entity.ProjectMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目成员关联表数据访问层
 */
@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {
}