package com.hotlcc.wechat4j.handler;

import com.hotlcc.wechat4j.Wechat;
import com.hotlcc.wechat4j.model.ReceivedMsg;

/**
 * 接收消息处理器
 */
public interface ReceivedMsgHandler {
    /**
     * 处理所有类型的消息
     *
     * @param wechat
     * @param msg
     */
    void handleAllType(Wechat wechat, ReceivedMsg msg);
}
