package com.hotlcc.wechat4j.enums;

public enum SelectorEnum {
    SELECTOR_0(0, "正常"),
    SELECTOR_2(2, "有新消息"),
    SELECTOR_4(4, "目前发现修改了联系人备注会出现"),
    SELECTOR_6(6, "目前不知道代表什么"),
    SELECTOR_7(7, "手机操作了微信");

    private int code;
    private String desc;

    SelectorEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public static SelectorEnum valueOf(int code) {
        SelectorEnum[] es = values();
        for (SelectorEnum e : es) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
