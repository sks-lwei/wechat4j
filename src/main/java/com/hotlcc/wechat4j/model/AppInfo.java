package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class AppInfo {
    private AppInfo() {
    }

    @JSONField(name = "Type")
    private Integer type;
    @JSONField(name = "AppID")
    private String appID;

    public static AppInfo valueOf(JSONObject info) {
        if (info == null) {
            return null;
        }
        AppInfo appInfo = new AppInfo();
        appInfo.type = info.getInteger("Type");
        appInfo.appID = info.getString("AppID");
        return appInfo;
    }

    public static List<AppInfo> valueOf(JSONArray infos) {
        if (infos == null) {
            return null;
        }

        List<AppInfo> appInfos = new ArrayList<>();
        for (int i = 0, len = infos.size(); i < len; i++) {
            JSONObject info = infos.getJSONObject(i);
            appInfos.add(AppInfo.valueOf(info));
        }
        return appInfos;
    }
}
