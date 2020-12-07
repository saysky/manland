package com.example.sens.enums;

/**
 * <pre>
 *     订单状态enum
 * </pre>
 */
public enum OrderStatusEnum {

    /**
     * 待支付
     */
    NOT_PAY(0),

    /**
     * 已支付
     */
    HAS_PAY(1),

    /**
     * 已完结
     */
    FINISHED(2),

    /**
     * 已关闭,已取消
     */
    CLOSED(3),

    /**
     * 押金退回失败
     */
    DEPOSIT_RETURN_FAIL(4)

    ;



    private Integer code;

    OrderStatusEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}
