package com.hotlcc.wechat4j.enums;

/**
 * 操作系统enum
 *
 * @author https://gitee.com/hotlcc
 */
public enum OperatingSystemEnum {
    DARWIN("darwin"),
    WINDOWS("windows"),
    LINUX("linux"),
    MAC_OS("mac"),
    OTHER("other");

    private String value;

    public String getValue() {
        return value;
    }

    OperatingSystemEnum(String value) {
        this.value = value;
    }

    public static OperatingSystemEnum currentOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.indexOf(OperatingSystemEnum.DARWIN.getValue()) >= 0) {
            return OperatingSystemEnum.DARWIN;
        } else if (osName.indexOf(OperatingSystemEnum.WINDOWS.getValue()) >= 0) {
            return OperatingSystemEnum.WINDOWS;
        } else if (osName.indexOf(OperatingSystemEnum.LINUX.getValue()) >= 0) {
            return OperatingSystemEnum.LINUX;
        } else if (osName.indexOf(OperatingSystemEnum.MAC_OS.getValue()) >= 0) {
            return OperatingSystemEnum.MAC_OS;
        }
        return OperatingSystemEnum.OTHER;
    }
}
