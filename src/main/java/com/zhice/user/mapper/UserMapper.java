package com.zhice.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhice.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {}