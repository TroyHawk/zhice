package com.zhice.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhice.common.api.Result;
import com.zhice.common.utils.JwtUtils;
import com.zhice.user.dto.LoginDTO;
import com.zhice.user.entity.User;
import com.zhice.user.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "认证中心", description = "处理用户登录注册及Token颁发")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "验证账号密码并返回 JWT Token")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        // 根据用户名查询数据库
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginDTO.getUsername()));

        // 验证密码（生产环境应使用 BCrypt 等加密算法校验，这里仅作功能跑通）
        if (user == null || !user.getPassword().equals(loginDTO.getPassword())) {
            return Result.error(400, "用户名或密码错误");
        }

        // 签发 Token
        String token = JwtUtils.generateToken(user.getId());

        // 返回给前端
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("realName", user.getRealName());

        return Result.success(data);
    }
}