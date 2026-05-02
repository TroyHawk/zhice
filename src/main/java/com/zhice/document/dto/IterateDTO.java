package com.zhice.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "申报书局部对话微调参数")
public class IterateDTO {

    @Schema(description = "正在微调的模块Key (如: innovation)", example = "innovation")
    private String sectionKey;

    @Schema(description = "该模块现有的内容", example = "本项目使用了大模型技术。")
    private String oldContent;

    @Schema(description = "修改意见/Prompt", example = "请扩写为100字左右，强调低延迟。")
    private String userPrompt;
}