package com.hotlcc.wechat4j.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;

public final class WechatUtil {
    private WechatUtil() {
    }

    private static String STRING_CHARS_1 = "123456789";
    private static String STRING_CHARS_2 = "1234567890";

    /**
     * 创建一个设备ID
     *
     * @return
     */
    public static String createDeviceID() {
        return "e" + RandomStringUtils.random(15, STRING_CHARS_1);
    }

    /**
     * 创建一个消息ID
     *
     * @return
     */
    public static String createMsgId() {
        return System.currentTimeMillis() + RandomStringUtils.random(4, STRING_CHARS_2);
    }

    /**
     * 把SyncKeyList转为字符串格式
     *
     * @param SyncKeyList
     * @return
     */
    public static String syncKeyListToString(JSONArray SyncKeyList) {
        if (SyncKeyList == null) {
            return null;
        }
        StringBuffer synckey = new StringBuffer();
        for (int i = 0, len = SyncKeyList.size(); i < len; i++) {
            JSONObject json = SyncKeyList.getJSONObject(i);
            if (i > 0) {
                synckey.append("|");
            }
            synckey.append(json.getString("Key"))
                    .append("_")
                    .append(json.getString("Val"));
        }
        return synckey.toString();
    }

    /**
     * 根据ContentType得到微信上传所需的mediatype
     *
     * @param contentType
     * @return
     */
    public static String getMediatype(String contentType) {
        if (contentType == null) {
            return "doc";
        }
        if (contentType.indexOf("image") >= 0) {
            return "pic";
        } else {
            return "doc";
        }
    }
}
