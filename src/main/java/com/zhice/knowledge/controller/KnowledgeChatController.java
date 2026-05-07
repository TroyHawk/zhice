package com.zhice.knowledge.controller;

import com.zhice.common.api.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 智库智能问答控制器
 */
@RestController
@RequestMapping("/api/v1/knowledge")
@Tag(name = "AI 智库中心", description = "基于阿里云百炼的 RAG 智能问答")
public class KnowledgeChatController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Autowired
    public KnowledgeChatController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        // 使用 Spring AI 提供的 Builder 实例化 ChatClient
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    @GetMapping("/{projectId}/chat")
    @Operation(summary = "项目资料智能问答", description = "针对项目已上传的资料进行 RAG 上下文检索与对话")
    public Result<String> chat(
            @Parameter(description = "项目唯一ID", required = true) @PathVariable Long projectId,
            @Parameter(description = "用户的提问", required = true) @RequestParam String question) {

        // 1. 向量检索：由于 SimpleVectorStore 不支持原生 Filter，我们先扩大召回数量 (TopK 设为 30)
        List<Document> allSimilarDocuments = vectorStore.similaritySearch(
                SearchRequest.query(question).withTopK(30)
        );

        // 2. 内存过滤：使用 Java Stream 手动筛出当前项目的数据，实现【项目级数据隔离】
        List<Document> similarDocuments = allSimilarDocuments.stream()
                .filter(doc -> {
                    // 从元数据中取出 projectId。注意：从 JSON 反序列化后可能是 Integer 或 String，统转为 String 比较最安全
                    Object docProjectId = doc.getMetadata().get("projectId");
                    return docProjectId != null && docProjectId.toString().equals(projectId.toString());
                })
                .limit(5) // 取过滤后最相关的 5 条片段
                .collect(Collectors.toList());

        // 如果该项目尚未上传任何资料，直接返回提示
        if (similarDocuments.isEmpty()) {
            return Result.success("当前项目知识库中未能检索到相关资料，请先上传竞赛背景文档。");
        }

        // 2. 组装上下文：将检索到的片段内容拼接成完整的字符串
        String context = similarDocuments.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n\n"));

        // 3. 构建 RAG 专属系统级 Prompt
        // 这个 Prompt 负责严格“框住”大模型的行为，防止它产生幻觉胡编乱造
        String systemPrompt = "你是一个专业、严谨的大学生竞赛辅助AI助手。请严格根据以下提供的【项目参考资料】来回答用户的问题。\n" +
                "规则1：如果资料中没有相关信息，请明确回答“根据现有资料无法得出结论”，绝不能凭空捏造。\n" +
                "规则2：回答要条理清晰，适合作为竞赛申报书或答辩PPT的参考。\n" +
                "规则3：回答不能带有markdown格式。\n\n" +
                "【项目参考资料】:\n" + context;

        // 4. 远程调用阿里云百炼的模型进行解答
        String answer = chatClient.prompt()
                .system(systemPrompt)
                .user(question)
                .call()
                .content();

        // 5. 返回组装好的 AI 答案
        return Result.success(answer);
    }
}