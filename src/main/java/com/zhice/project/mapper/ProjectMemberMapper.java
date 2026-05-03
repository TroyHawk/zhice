package com.zhice.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhice.project.entity.ProjectMember;
import com.zhice.project.vo.ProjectMemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {

    /**
     * 联表查询：获取项目下所有成员的详细信息
     * 按照角色优先级（组长->成员->老师）和加入时间排序
     */
    @Select("SELECT pm.id, pm.user_id AS userId, u.username, u.real_name AS realName, pm.role, pm.create_time AS joinTime " +
            "FROM zc_project_member pm " +
            "INNER JOIN zc_user u ON pm.user_id = u.id " +
            "WHERE pm.project_id = #{projectId} AND pm.deleted = 0 AND u.deleted = 0 " +
            "ORDER BY pm.role ASC, pm.create_time ASC")
    List<ProjectMemberVO> selectMembersWithUserInfo(@Param("projectId") Long projectId);
}