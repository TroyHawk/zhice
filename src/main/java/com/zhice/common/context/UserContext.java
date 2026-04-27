package com.zhice.common.context;

/**
 * 当前请求的用户上下文 (基于 ThreadLocal)
 */
public class UserContext {
    // 存储当前登录用户的 ID
    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();
    // 存储当前登录用户的系统角色 (如: 0-管理员, 1-学生, 2-教师)
    private static final ThreadLocal<Integer> CURRENT_USER_ROLE = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        CURRENT_USER_ID.set(userId);
    }

    public static Long getUserId() {
        return CURRENT_USER_ID.get();
    }

    public static void setUserRole(Integer role) {
        CURRENT_USER_ROLE.set(role);
    }

    public static Integer getUserRole() {
        return CURRENT_USER_ROLE.get();
    }

    /**
     * 防止内存泄漏，请求结束后必须清除
     */
    public static void clear() {
        CURRENT_USER_ID.remove();
        CURRENT_USER_ROLE.remove();
    }
}