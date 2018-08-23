package com.hotlcc.wechat4j.enums;

/**
 * 微信退出类型
 */
public enum ExitTypeEnum {
    ERROR_EXIT("错误导致退出"),
    LOCAL_EXIT("本次手动退出"),
    REMOTE_EXIT("远程操作退出");

    private String desc;

    ExitTypeEnum(String desc) {
        this.desc = desc;
    }

}
