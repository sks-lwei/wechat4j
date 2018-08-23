package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class RecommendInfo {
    private RecommendInfo() {
    }

    @JSONField(name = "Ticket")
    private String ticket;
    @JSONField(name = "UserName")
    private String userName;
    @JSONField(name = "Sex")
    private Integer sex;
    @JSONField(name = "AttrStatus")
    private Integer attrStatus;
    @JSONField(name = "City")
    private String city;
    @JSONField(name = "NickName")
    private String nickName;
    @JSONField(name = "Scene")
    private Integer scene;
    @JSONField(name = "Province")
    private String province;
    @JSONField(name = "Content")
    private String content;
    @JSONField(name = "Alias")
    private String alias;
    @JSONField(name = "Signature")
    private String signature;
    @JSONField(name = "OpCode")
    private Integer opCode;
    @JSONField(name = "QQNum")
    private Long qqNum;
    @JSONField(name = "VerifyFlag")
    private Integer verifyFlag;

    public static RecommendInfo valueOf(JSONObject info) {
        if (info == null) {
            return null;
        }

        RecommendInfo recommendInfo = new RecommendInfo();

        recommendInfo.ticket = info.getString("Ticket");
        recommendInfo.userName = info.getString("UserName");
        recommendInfo.sex = info.getInteger("Sex");
        recommendInfo.attrStatus = info.getInteger("AttrStatus");
        recommendInfo.city = info.getString("City");
        recommendInfo.nickName = info.getString("NickName");
        recommendInfo.scene = info.getInteger("Scene");
        recommendInfo.province = info.getString("Province");
        recommendInfo.content = info.getString("Content");
        recommendInfo.alias = info.getString("Alias");
        recommendInfo.signature = info.getString("Signature");
        recommendInfo.opCode = info.getInteger("OpCode");
        recommendInfo.qqNum = info.getLong("QQNum");
        recommendInfo.verifyFlag = info.getInteger("VerifyFlag");

        return recommendInfo;
    }

    public static List<RecommendInfo> valueOf(JSONArray infos) {
        if (infos == null) {
            return null;
        }

        List<RecommendInfo> recommendInfos = new ArrayList<>();
        for (int i = 0, len = infos.size(); i < len; i++) {
            JSONObject info = infos.getJSONObject(i);
            recommendInfos.add(RecommendInfo.valueOf(info));
        }
        return recommendInfos;
    }
}
