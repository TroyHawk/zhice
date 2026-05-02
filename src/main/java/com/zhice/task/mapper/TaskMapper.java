package com.zhice.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhice.task.entity.Task;
import org.apache.ibatis.annotations.Mapper;

/**
 * 敏捷任务看板数据访问层
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}