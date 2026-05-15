package com.zhice.dashboard.mapper;

import com.zhice.dashboard.vo.DailyCompletionVO;
import com.zhice.dashboard.vo.MemberContributionVO;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DashboardMapper {

    @Select("SELECT status, COUNT(*) AS cnt " +
            "FROM zc_task " +
            "WHERE project_id = #{projectId} AND deleted = 0 " +
            "GROUP BY status")
    List<StatusCount> countTasksByStatus(@Param("projectId") Long projectId);

    @Select("SELECT DATE(update_time) AS date, COUNT(*) AS count " +
            "FROM zc_task " +
            "WHERE project_id = #{projectId} " +
            "  AND status = 3 " +
            "  AND deleted = 0 " +
            "  AND update_time >= #{sevenDaysAgo} " +
            "GROUP BY DATE(update_time) " +
            "ORDER BY date ASC")
    List<DailyCompletionVO> countDailyCompleted(@Param("projectId") Long projectId,
                                                 @Param("sevenDaysAgo") String sevenDaysAgo);

    @Select("SELECT u.id AS userId, u.real_name AS realName, pm.role, " +
            "       COUNT(t.id) AS totalTasks, " +
            "       SUM(CASE WHEN t.status = 3 THEN 1 ELSE 0 END) AS completedTasks " +
            "FROM zc_project_member pm " +
            "INNER JOIN zc_user u ON pm.user_id = u.id AND u.deleted = 0 " +
            "LEFT JOIN zc_task t ON t.project_id = pm.project_id " +
            "                    AND t.assignee_id = pm.user_id " +
            "                    AND t.deleted = 0 " +
            "WHERE pm.project_id = #{projectId} AND pm.deleted = 0 " +
            "GROUP BY u.id, u.real_name, pm.role " +
            "ORDER BY totalTasks DESC")
    List<MemberContributionVO> getMemberContributions(@Param("projectId") Long projectId);

    @Data
    class StatusCount {
        private Integer status;
        private Long cnt;
    }
}
