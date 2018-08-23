package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信用户信息
 */
@Getter
public final class UserInfo {
    private UserInfo() {
    }

    @JSONField(name = "Uin")
    private Long uin;
    @JSONField(name = "NickName")
    private String nickName;
    @JSONField(name = "HeadImgUrl")
    private String headImgUrl;
    @JSONField(name = "ContactFlag")
    private Integer contactFlag;
    @JSONField(name = "MemberCount")
    private Integer memberCount;
    @JSONField(name = "MemberList")
    private List<UserInfo> memberList;
    @JSONField(name = "RemarkName")
    private String remarkName;
    @JSONField(name = "HideInputBarFlag")
    private Integer hideInputBarFlag;
    @JSONField(name = "Sex")
    private Integer sex;
    @JSONField(name = "Signature")
    private String signature;
    @JSONField(name = "VerifyFlag")
    private Integer verifyFlag;
    @JSONField(name = "OwnerUin")
    private Long ownerUin;
    @JSONField(name = "PYInitial")
    private String pyInitial;
    @JSONField(name = "PYQuanPin")
    private String pyQuanPin;
    @JSONField(name = "RemarkPYInitial")
    private String remarkPYInitial;
    @JSONField(name = "RemarkPYQuanPin")
    private String remarkPYQuanPin;
    @JSONField(name = "StarFriend")
    private Integer starFriend;
    @JSONField(name = "AppAccountFlag")
    private Integer appAccountFlag;
    @JSONField(name = "Statues")
    private Integer statues;
    @JSONField(name = "AttrStatus")
    private Integer attrStatus;
    @JSONField(name = "Province")
    private String province;
    @JSONField(name = "City")
    private String city;
    @JSONField(name = "Alias")
    private String alias;
    @JSONField(name = "SnsFlag")
    private Integer snsFlag;
    @JSONField(name = "UniFriend")
    private Integer uniFriend;
    @JSONField(name = "DisplayName")
    private String displayName;
    @JSONField(name = "ChatRoomId")
    private Long chatRoomId;
    @JSONField(name = "KeyWord")
    private String keyWord;
    @JSONField(name = "EncryChatRoomId")
    private String encryChatRoomId;
    @JSONField(name = "IsOwner")
    private Integer isOwner;
    @JSONField(name = "UserName")
    private String userName;

    public static UserInfo valueOf(JSONObject info) {
        if (info == null) {
            return null;
        }

        UserInfo userInfo = new UserInfo();

        userInfo.uin = info.getLong("Uin");
        userInfo.nickName = info.getString("NickName");
        userInfo.headImgUrl = info.getString("HeadImgUrl");
        userInfo.contactFlag = info.getInteger("ContactFlag");
        userInfo.memberCount = info.getInteger("MemberCount");
        userInfo.memberList = valueOf(info.getJSONArray("MemberList"));
        userInfo.remarkName = info.getString("RemarkName");
        userInfo.hideInputBarFlag = info.getInteger("HideInputBarFlag");
        userInfo.sex = info.getInteger("Sex");
        userInfo.signature = info.getString("Signature");
        userInfo.verifyFlag = info.getInteger("VerifyFlag");
        userInfo.ownerUin = info.getLong("OwnerUin");
        userInfo.pyInitial = info.getString("PYInitial");
        userInfo.pyQuanPin = info.getString("PYQuanPin");
        userInfo.remarkPYInitial = info.getString("RemarkPYInitial");
        userInfo.remarkPYQuanPin = info.getString("RemarkPYQuanPin");
        userInfo.starFriend = info.getInteger("StarFriend");
        userInfo.appAccountFlag = info.getInteger("AppAccountFlag");
        userInfo.statues = info.getInteger("Statues");
        userInfo.attrStatus = info.getInteger("AttrStatus");
        userInfo.province = info.getString("Province");
        userInfo.city = info.getString("City");
        userInfo.alias = info.getString("Alias");
        userInfo.snsFlag = info.getInteger("SnsFlag");
        userInfo.uniFriend = info.getInteger("UniFriend");
        userInfo.displayName = info.getString("DisplayName");
        userInfo.chatRoomId = info.getLong("ChatRoomId");
        userInfo.keyWord = info.getString("KeyWord");
        userInfo.encryChatRoomId = info.getString("EncryChatRoomId");
        userInfo.isOwner = info.getInteger("IsOwner");
        userInfo.userName = info.getString("UserName");

        return userInfo;
    }

    public static List<UserInfo> valueOf(JSONArray infos) {
        if (infos == null) {
            return null;
        }

        List<UserInfo> userList = new ArrayList<>();
        for (int i = 0, len = infos.size(); i < len; i++) {
            JSONObject info = infos.getJSONObject(i);
            userList.add(UserInfo.valueOf(info));
        }
        return userList;
    }
}
