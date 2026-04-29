package com.zhice.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 团队邀请参数传输对象
 */
@Data
@Schema(description = "邀请成员参数对象")
public class MemberInviteDTO {

    @Schema(description = "被邀请人的学号/教工号/用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026002")
    private String username;

    @Schema(description = "授予的角色：2-核心成员, 3-指导老师", example = "2")
    private Integer role;
}