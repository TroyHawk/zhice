package com.zhice.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("zc_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String realName;
    private String password; // 实际开发应为哈希值，测试阶段为方便先用明文

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}