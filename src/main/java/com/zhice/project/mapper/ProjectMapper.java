package com.zhice.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhice.project.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 项目表数据访问层
 * 继承 BaseMapper 即可获得基础的 CRUD 能力
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
    // 后续可在此手写复杂的联表 SQL（如查询项目及包含的成员）

    /**
     * 根据用户ID查询其参与的所有项目
     * 涉及 zc_project (p) 和 zc_project_member (pm) 的联表查询
     */
    @Select("SELECT p.*, pm.role " +   // 重点：把 pm.role 也查出来映射到实体类
            "FROM zc_project p " +
            "INNER JOIN zc_project_member pm ON p.id = pm.project_id " +
            "WHERE pm.user_id = #{userId} AND p.deleted = 0 AND pm.deleted = 0 " +
            "ORDER BY p.create_time DESC") // 重点：新项目排在前面
    List<Project> selectProjectsByUserId(@Param("userId") Long userId);
}