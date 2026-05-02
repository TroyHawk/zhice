package com.zhice.document.service;

import com.zhice.document.entity.ProjectDraft;

import java.io.OutputStream;
import java.util.List;

public interface DocumentService {


    // 1. 获取系统生成的初始版本内容
    String generateInitialContent(Long projectId);

    // 2. 基于旧内容和用户Prompt迭代新内容
    String iterateContent(Long projectId, String oldContent, String userPrompt);

    // 3. 保存草稿到数据库
    void saveDraft(ProjectDraft draft);

//    // 4. 获取项目的所有草稿
//    List<ProjectDraft> listDrafts(Long projectId);

    // 5. 将指定的草稿内容导出为 Word (修改原有导出方法)
    void exportDraftToWord(Long draftId, OutputStream outputStream);
}