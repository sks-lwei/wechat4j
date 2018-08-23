package com.hotlcc.wechat4j.enums;

public enum RetcodeEnum {
    RECODE_0(0, "正常"),
    RECODE_1100(1100, "失败/登出微信"),
    RECODE_1101(1101, "从其它设备登录微信");

    private int code;
    private String desc;

    RetcodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public static RetcodeEnum valueOf(int code) {
        RetcodeEnum[] es = values();
        for (RetcodeEnum e : es) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
