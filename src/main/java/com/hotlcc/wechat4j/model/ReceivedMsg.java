package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class ReceivedMsg {
    private ReceivedMsg() {
    }

    @JSONField(name = "SubMsgType")
    private Integer subMsgType;
    @JSONField(name = "VoiceLength")
    private Long voiceLength;
    @JSONField(name = "FileName")
    private String fileName;
    @JSONField(name = "ImgHeight")
    private Long imgHeight;
    @JSONField(name = "ToUserName")
    private String toUserName;
    @JSONField(name = "HasProductId")
    private Long hasProductId;
    @JSONField(name = "ImgStatus")
    private Integer imgStatus;
    @JSONField(name = "Url")
    private String url;
    @JSONField(name = "ImgWidth")
    private Integer imgWidth;
    @JSONField(name = "ForwardFlag")
    private Integer forwardFlag;
    @JSONField(name = "Status")
    private Integer status;
    @JSONField(name = "Ticket")
    private String ticket;
    @JSONField(name = "RecommendInfo")
    private RecommendInfo recommendInfo;
    @JSONField(name = "CreateTime")
    private Long createTime;
    @JSONField(name = "NewMsgId")
    private Long newMsgId;
    @JSONField(name = "MsgType")
    private Integer msgType;
    @JSONField(name = "EncryFileName")
    private String encryFileName;
    @JSONField(name = "MsgId")
    private String msgId;
    @JSONField(name = "StatusNotifyCode")
    private Integer statusNotifyCode;
    @JSONField(name = "AppInfo")
    private AppInfo appInfo;
    @JSONField(name = "AppMsgType")
    private Integer appMsgType;
    @JSONField(name = "PlayLength")
    private Long playLength;
    @JSONField(name = "MediaId")
    private String mediaId;
    @JSONField(name = "Content")
    private String content;
    @JSONField(name = "StatusNotifyUserName")
    private String statusNotifyUserName;
    @JSONField(name = "FromUserName")
    private String fromUserName;
    @JSONField(name = "OriContent")
    private String oriContent;
    @JSONField(name = "FileSize")
    private String fileSize;

    public static ReceivedMsg valueOf(JSONObject msg) {
        if (msg == null) {
            return null;
        }

        ReceivedMsg receivedMsg = new ReceivedMsg();

        receivedMsg.subMsgType = msg.getInteger("SubMsgType");
        receivedMsg.voiceLength = msg.getLong("VoiceLength");
        receivedMsg.fileName = msg.getString("FileName");
        receivedMsg.imgHeight = msg.getLong("ImgHeight");
        receivedMsg.toUserName = msg.getString("ToUserName");
        receivedMsg.hasProductId = msg.getLong("HasProductId");
        receivedMsg.imgStatus = msg.getInteger("ImgStatus");
        receivedMsg.url = msg.getString("Url");
        receivedMsg.imgWidth = msg.getInteger("ImgWidth");
        receivedMsg.forwardFlag = msg.getInteger("ForwardFlag");
        receivedMsg.status = msg.getInteger("Status");
        receivedMsg.ticket = msg.getString("Ticket");
        receivedMsg.recommendInfo = com.hotlcc.wechat4j.model.RecommendInfo.valueOf(msg.getJSONObject("RecommendInfo"));
        receivedMsg.createTime = msg.getLong("CreateTime");
        receivedMsg.newMsgId = msg.getLong("NewMsgId");
        receivedMsg.msgType = msg.getInteger("MsgType");
        receivedMsg.encryFileName = msg.getString("EncryFileName");
        receivedMsg.msgId = msg.getString("MsgId");
        receivedMsg.statusNotifyCode = msg.getInteger("StatusNotifyCode");
        receivedMsg.appInfo = com.hotlcc.wechat4j.model.AppInfo.valueOf(msg.getJSONObject("AppInfo"));
        receivedMsg.playLength = msg.getLong("PlayLength");
        receivedMsg.mediaId = msg.getString("MediaId");
        receivedMsg.content = msg.getString("Content");
        receivedMsg.statusNotifyUserName = msg.getString("StatusNotifyUserName");
        receivedMsg.fromUserName = msg.getString("FromUserName");
        receivedMsg.oriContent = msg.getString("OriContent");
        receivedMsg.fileSize = msg.getString("FileSize");

        return receivedMsg;
    }

    public static List<ReceivedMsg> valueOf(JSONArray msgs) {
        if (msgs == null) {
            return null;
        }

        List<ReceivedMsg> receivedMsgList = new ArrayList<>();
        for (int i = 0, len = msgs.size(); i < len; i++) {
            JSONObject info = msgs.getJSONObject(i);
            receivedMsgList.add(ReceivedMsg.valueOf(info));
        }
        return receivedMsgList;
    }
}
