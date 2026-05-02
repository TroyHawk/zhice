package com.zhice.document.service.impl;

import com.deepoove.poi.XWPFTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhice.document.entity.ProjectDraft;
import com.zhice.document.mapper.ProjectDraftMapper;
import com.zhice.document.service.DocumentService;
import com.zhice.project.entity.Project;
import com.zhice.project.mapper.ProjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectDraftMapper draftMapper;

    @Autowired
    private VectorStore vectorStore;

    private final ChatClient chatClient;

    public DocumentServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String generateInitialContent(Long projectId) {
        // 1. 召回智库片段
        List<Document> allDocs = vectorStore.similaritySearch(SearchRequest.defaults().withTopK(30));
        String context = allDocs.stream()
                .filter(doc -> {
                    Object docProjectId = doc.getMetadata().get("projectId");
                    return docProjectId != null && docProjectId.toString().equals(projectId.toString());
                })
                .limit(10)
                .map(Document::getContent)
                .collect(Collectors.joining("\n\n"));

        if (context.isEmpty()) {
            return "{\"error\": \"当前项目未上传背景资料，无法生成。\"}";
        }

      // 2. 强制 JSON 输出的 Prompt (去掉容易引起 Spring AI 解析错误的大括号 {})
        String systemPrompt = """
                你是一个经验丰富的大学生学科竞赛指导老师。请根据提供的【项目资料】，提取并生成《竞赛申报书》的核心模块。
                
                【严格输出要求】：
                你必须且只能输出一个合法的 JSON 对象，不要输出任何 Markdown 标记（如 ```json），不要包含任何解释性前言或后语。
                你要输出的 JSON 对象必须严格包含以下 4 个 Key：
                "background": "项目的研发背景和痛点分析（约150字）"
                "innovation": "项目的3条核心创新点（分点作答，约150字）"
                "architecture": "项目的技术架构或实现方案简述（约150字）"
                "market": "市场前景与应用价值分析（约150字）"
                
                【项目资料】：
                """ + context;

        try {
            String aiResponse = chatClient.prompt().system(systemPrompt).call().content();
            // 清理可能带有的 Markdown 标记，确保是纯 JSON
            return aiResponse.replaceAll("```json", "").replaceAll("```", "").trim();
        } catch (Exception e) {
            log.error("AI 初次生成失败", e);
            throw new RuntimeException("AI 生成初稿失败");
        }
    }

    @Override
    public String iterateContent(Long projectId, String oldContent, String userPrompt) {
        // 局部微调 Prompt：严格要求只输出纯文本
        String systemPrompt = "你是一个竞赛专家。请根据用户的反馈意见，仅对提供的这一个特定模块的内容进行重写和润色。\n" +
                "【严格限制】：1. 只需输出修改后的纯文本。2. 绝对不要输出 JSON 格式，也不要有任何解释。";
        String combinedUserPrompt = "【该模块现有内容】：\n" + oldContent + "\n\n【用户的修改意见】：\n" + userPrompt;

        return chatClient.prompt().system(systemPrompt).user(combinedUserPrompt).call().content().trim();
    }

    @Override
    public void saveDraft(ProjectDraft draft) {
        draftMapper.insert(draft);
    }

    @Override
    public void exportDraftToWord(Long draftId, OutputStream outputStream) {
        ProjectDraft draft = draftMapper.selectById(draftId);
        if (draft == null) throw new IllegalArgumentException("草稿不存在");

        Project project = projectMapper.selectById(draft.getProjectId());

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("projectName", project.getName());
        dataModel.put("projectDescription", project.getDescription() != null ? project.getDescription() : "暂无简介");

        // 核心改造：将存入数据库的 JSON 字符串解析为 Map 并合并进模板数据中
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> aiContentMap = objectMapper.readValue(draft.getContent(), new TypeReference<Map<String, String>>() {});
            dataModel.putAll(aiContentMap); // 自动塞入 background, innovation 等键值对
        } catch (Exception e) {
            log.error("解析草稿 JSON 失败", e);
            dataModel.put("rawContent", draft.getContent()); // 解析失败的兜底
        }

        try {
            XWPFTemplate template = XWPFTemplate.compile(new ClassPathResource("templates/4c_template.docx").getInputStream())
                    .render(dataModel);
            template.write(outputStream);
            template.close();
        } catch (Exception e) {
            log.error("生成文档失败", e);
            throw new RuntimeException("Word 模板渲染失败");
        }
    }
}