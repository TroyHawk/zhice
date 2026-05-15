package com.zhice.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhice.dashboard.mapper.DashboardMapper;
import com.zhice.dashboard.service.DashboardService;
import com.zhice.dashboard.vo.*;
import com.zhice.document.entity.ProjectDraft;
import com.zhice.document.mapper.ProjectDraftMapper;
import com.zhice.knowledge.entity.KnowledgeDocument;
import com.zhice.knowledge.mapper.KnowledgeDocumentMapper;
import com.zhice.project.entity.Project;
import com.zhice.project.entity.ProjectMember;
import com.zhice.project.mapper.ProjectMapper;
import com.zhice.project.mapper.ProjectMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Autowired
    private ProjectDraftMapper projectDraftMapper;

    @Override
    public DashboardVO getProjectDashboard(Long projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("项目不存在或已被删除");
        }

        DashboardVO vo = new DashboardVO();
        vo.setProjectOverview(buildProjectOverview(project));
        vo.setTaskStats(buildTaskStats(projectId));
        vo.setDailyCompletion(buildDailyCompletion(projectId));
        vo.setMemberContributions(buildMemberContributions(projectId));
        vo.setKnowledgeStats(buildKnowledgeStats(projectId));
        vo.setDraftStats(buildDraftStats(projectId));
        return vo;
    }

    private ProjectOverviewVO buildProjectOverview(Project project) {
        ProjectOverviewVO vo = new ProjectOverviewVO();
        vo.setProjectName(project.getName());
        vo.setCompetitionType(project.getCompetitionType());
        vo.setProjectStatus(project.getStatus());

        Long memberCount = projectMemberMapper.selectCount(
                new LambdaQueryWrapper<ProjectMember>()
                        .eq(ProjectMember::getProjectId, project.getId())
        );
        vo.setMemberCount(memberCount);
        return vo;
    }

    private TaskStatsVO buildTaskStats(Long projectId) {
        List<DashboardMapper.StatusCount> statusCounts =
                dashboardMapper.countTasksByStatus(projectId);

        Map<Integer, Long> countMap = statusCounts.stream()
                .collect(Collectors.toMap(
                        DashboardMapper.StatusCount::getStatus,
                        DashboardMapper.StatusCount::getCnt
                ));

        TaskStatsVO vo = new TaskStatsVO();
        long todo = countMap.getOrDefault(0, 0L);
        long inProgress = countMap.getOrDefault(1, 0L);
        long review = countMap.getOrDefault(2, 0L);
        long done = countMap.getOrDefault(3, 0L);
        long total = todo + inProgress + review + done;

        vo.setTotal(total);
        vo.setTodo(todo);
        vo.setInProgress(inProgress);
        vo.setReview(review);
        vo.setDone(done);
        vo.setCompletionRate(total == 0 ? 0.0 :
                Math.round(done * 10000.0 / total) / 100.0);
        return vo;
    }

    private List<DailyCompletionVO> buildDailyCompletion(Long projectId) {
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(6).atStartOfDay();
        String sevenDaysAgoStr = sevenDaysAgo.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<DailyCompletionVO> dbResult =
                dashboardMapper.countDailyCompleted(projectId, sevenDaysAgoStr);

        Map<String, Long> dateCountMap = dbResult.stream()
                .collect(Collectors.toMap(
                        DailyCompletionVO::getDate,
                        DailyCompletionVO::getCount
                ));

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<DailyCompletionVO> result = new ArrayList<>(7);
        for (int i = 6; i >= 0; i--) {
            String dateStr = LocalDate.now().minusDays(i).format(dateFmt);
            DailyCompletionVO vo = new DailyCompletionVO();
            vo.setDate(dateStr);
            vo.setCount(dateCountMap.getOrDefault(dateStr, 0L));
            result.add(vo);
        }
        return result;
    }

    private List<MemberContributionVO> buildMemberContributions(Long projectId) {
        return dashboardMapper.getMemberContributions(projectId);
    }

    private KnowledgeStatsVO buildKnowledgeStats(Long projectId) {
        KnowledgeStatsVO vo = new KnowledgeStatsVO();

        Long totalDocuments = knowledgeDocumentMapper.selectCount(
                new LambdaQueryWrapper<KnowledgeDocument>()
                        .eq(KnowledgeDocument::getProjectId, projectId)
        );
        vo.setTotalDocuments(totalDocuments);

        Long parsedCount = knowledgeDocumentMapper.selectCount(
                new LambdaQueryWrapper<KnowledgeDocument>()
                        .eq(KnowledgeDocument::getProjectId, projectId)
                        .eq(KnowledgeDocument::getStatus, 2)
        );
        Long parsingCount = knowledgeDocumentMapper.selectCount(
                new LambdaQueryWrapper<KnowledgeDocument>()
                        .eq(KnowledgeDocument::getProjectId, projectId)
                        .eq(KnowledgeDocument::getStatus, 1)
        );
        Long failedCount = knowledgeDocumentMapper.selectCount(
                new LambdaQueryWrapper<KnowledgeDocument>()
                        .eq(KnowledgeDocument::getProjectId, projectId)
                        .eq(KnowledgeDocument::getStatus, 3)
        );

        vo.setParsedCount(parsedCount);
        vo.setParsingCount(parsingCount);
        vo.setFailedCount(failedCount);

        List<KnowledgeDocument> allDocs = knowledgeDocumentMapper.selectList(
                new LambdaQueryWrapper<KnowledgeDocument>()
                        .eq(KnowledgeDocument::getProjectId, projectId)
                        .select(KnowledgeDocument::getFileSize)
        );
        long totalFileSize = allDocs.stream()
                .mapToLong(doc -> doc.getFileSize() != null ? doc.getFileSize() : 0L)
                .sum();
        vo.setTotalFileSize(totalFileSize);

        return vo;
    }

    private DraftStatsVO buildDraftStats(Long projectId) {
        DraftStatsVO vo = new DraftStatsVO();

        Long totalDrafts = projectDraftMapper.selectCount(
                new LambdaQueryWrapper<ProjectDraft>()
                        .eq(ProjectDraft::getProjectId, projectId)
        );
        vo.setTotalDrafts(totalDrafts);

        List<ProjectDraft> latestDrafts = projectDraftMapper.selectList(
                new LambdaQueryWrapper<ProjectDraft>()
                        .eq(ProjectDraft::getProjectId, projectId)
                        .orderByDesc(ProjectDraft::getCreateTime)
                        .last("LIMIT 1")
        );
        vo.setLatestDraftTime(
                latestDrafts.isEmpty() ? null : latestDrafts.get(0).getCreateTime()
        );

        return vo;
    }
}
