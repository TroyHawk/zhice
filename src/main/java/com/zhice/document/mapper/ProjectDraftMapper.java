package com.zhice.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhice.document.entity.ProjectDraft;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 申报书草稿表数据访问层
 */
@Mapper
public interface ProjectDraftMapper extends BaseMapper<ProjectDraft> {
    @Select("SELECT * " +
            "FROM zc_project_draft  " +
            "WHERE project_id = #{projectId} " +
            "ORDER BY create_time DESC")
    List<ProjectDraft> selectDraftById(@Param("projectId") Long projectId);
}