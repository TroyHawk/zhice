package com.zhice.document.service;

import com.deepoove.poi.XWPFTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 智策文档生成核心服务
 */
@Slf4j
@Service
public class DocumentService {

    /**
     * 根据项目信息和 AI 生成的内容，渲染 4C 大赛作品说明书
     * @param projectId 项目唯一ID
     * @return 渲染后的 Word 二进制字节数组
     */
    public byte[] generate4cDocument(Long projectId) {
        // 1. TODO: 从数据库中查询 project 基础信息 (如项目名称、团队成员等)
        // 2. TODO: 调用 AI 智库 (RAG) 接口，传入项目资料，获取 AI 提炼和总结的内容

        // 模拟 AI 提取和生成的结构化数据（对应 Word 模板里的 {{projectName}}, {{innovation}} 等标签）
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("projectName", "智策 - 大学生竞赛团队文档协作平台");
        templateData.put("teamName", "TopCoder 战队");
        templateData.put("background", "当前大学生竞赛团队在准备过程中，往往重研发、轻文档，导致后期撰写申报书耗时耗力...");
        templateData.put("innovation", "首创结合 RAG 技术的智能文档生成方案，基于 poi-tl 引擎实现官方模板的像素级还原。");

        // 3. 读取 resources 目录下的模板文件进行渲染
        // 注意：你需要在 src/main/resources/templates 目录下放入一个 4c_template.docx 文件，并在里面挖好 {{变量名}} 的坑位
        try (XWPFTemplate template = XWPFTemplate.compile("src/main/resources/templates/4c_template.docx").render(templateData);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 将渲染后的文档写入输出流
            template.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            log.error("文档模板渲染失败，项目ID: {}", projectId, e);
            throw new RuntimeException("模板渲染异常，一键生成文档失败");
        }
    }
}