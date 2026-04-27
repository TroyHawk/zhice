package com.zhice.config.interceptor;

import com.zhice.common.api.Result;
import com.zhice.common.context.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 核心鉴权拦截器：验证 Token 并组装 UserContext
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 Swagger 文档和登录注册接口
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/swagger-ui") || requestURI.contains("/v3/api-docs") || requestURI.contains("/login")) {
            return true;
        }

        // 从请求头获取 Authorization: Bearer <token>
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            returnErrorResponse(response, 401, "暂未登录或Token已过期");
            return false;
        }

        String token = authHeader.substring(7);
        try {
            // TODO: 这里调用 JwtUtils 解析 token (由于篇幅，JwtUtils封装可自行用jjwt实现)
            // 假设解析成功，拿到 userId = 101, role = 2 (指导老师)
            Long userId = 101L;
            Integer role = 2;

            // 将用户信息塞入上下文
            UserContext.setUserId(userId);
            UserContext.setUserRole(role);
            return true;

        } catch (Exception e) {
            log.error("Token 解析失败: {}", e.getMessage());
            returnErrorResponse(response, 401, "非法的Token");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完成，必须清空上下文，防止 Tomcat 线程池复用导致数据错乱！
        UserContext.clear();
    }

    private void returnErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(Result.error(code, message)));
    }
}