package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * 要发送的消息
 */
@Getter
@Setter
public class WxMessage {
    @JSONField(name = "ClientMsgId")
    private String clientMsgId;
    @JSONField(name = "Content")
    private String content;
    @JSONField(name = "FromUserName")
    private String fromUserName;
    @JSONField(name = "LocalID")
    private String localID;
    @JSONField(name = "ToUserName")
    private String toUserName;
    @JSONField(name = "Type")
    private Integer type;
}
