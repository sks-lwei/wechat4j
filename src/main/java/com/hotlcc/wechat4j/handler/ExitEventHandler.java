package com.hotlcc.wechat4j.handler;

import com.hotlcc.wechat4j.Wechat;
import com.hotlcc.wechat4j.enums.ExitTypeEnum;

/**
 * 退出事件处理器
 */
public interface ExitEventHandler {
    /**
     * 针对所有类型的退出事件
     *
     * @param wechat
     * @param type
     * @param t
     */
    void handleAllType(Wechat wechat, ExitTypeEnum type, Throwable t);

    /**
     * 针对错误导致的退出事件
     *
     * @param wechat
     */
    void handleErrorExitEvent(Wechat wechat);

    /**
     * 针对远程人为导致的退出事件
     *
     * @param wechat
     */
    void handleRemoteExitEvent(Wechat wechat);

    /**
     * 针对本地任务导致的退出事件
     *
     * @param wechat
     */
    void handleLocalExitEvent(Wechat wechat);
}
