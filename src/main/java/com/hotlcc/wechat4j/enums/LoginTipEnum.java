package com.hotlcc.wechat4j.enums;

/**
 * 等待确认登录的tip
 *
 * @author https://gitee.com/hotlcc
 */
public enum LoginTipEnum {
    TIP_0(0, "扫码登录"),
    TIP_1(1, "确认登录");

    private int code;
    private String desc;

    LoginTipEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + "";
    }
}
