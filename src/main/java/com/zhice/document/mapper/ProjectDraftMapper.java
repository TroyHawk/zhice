package com.zhice.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhice.document.entity.ProjectDraft;
import org.apache.ibatis.annotations.Mapper;

/**
 * 申报书草稿表数据访问层
 */
@Mapper
public interface ProjectDraftMapper extends BaseMapper<ProjectDraft> {
}