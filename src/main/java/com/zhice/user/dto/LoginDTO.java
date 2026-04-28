package com.zhice.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户登录数据传输对象")
public class LoginDTO {
    @Schema(description = "用户名/学号", example = "2026001")
    private String username;

    @Schema(description = "密码", example = "123456")
    private String password;
}