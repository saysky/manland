package com.example.sens.enums;

/**
 * <pre>
 *     角色枚举
 * </pre>
 *
 */
public enum RoleEnum {

    /**
     * 管理员
     */
    ADMIN("admin"),

    /**
     * 用户
     */
    USER("user");

    private String value;

    RoleEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
