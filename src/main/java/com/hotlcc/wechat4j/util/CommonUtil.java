package com.hotlcc.wechat4j.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommonUtil {
    private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    private CommonUtil() {
    }

    public static void threadSleep(long millis, int nanos) {
        try {
            Thread.sleep(millis, nanos);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void threadSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
