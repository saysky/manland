package com.example.sens.enums;

/**
 * 房屋置顶枚举
 */
public enum PostIsStickyEnum {

    /**
     * 真
     */
    TRUE(1),

    /**
     * 假
     */
    FALSE(0);

    private Integer value;

    PostIsStickyEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
