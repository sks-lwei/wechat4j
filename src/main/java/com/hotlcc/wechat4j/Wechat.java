package com.hotlcc.wechat4j;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hotlcc.wechat4j.api.WebWeixinApi;
import com.hotlcc.wechat4j.enums.*;
import com.hotlcc.wechat4j.handler.ExitEventHandler;
import com.hotlcc.wechat4j.handler.ReceivedMsgHandler;
import com.hotlcc.wechat4j.model.BaseRequest;
import com.hotlcc.wechat4j.model.ReceivedMsg;
import com.hotlcc.wechat4j.model.UserInfo;
import com.hotlcc.wechat4j.model.WxMessage;
import com.hotlcc.wechat4j.util.*;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 微信客户端
 *
 * @author Allen
 */
@SuppressWarnings("Duplicates")
public class Wechat {
    private static Logger logger = LoggerFactory.getLogger(Wechat.class);

    private WebWeixinApi webWeixinApi;

    private CookieStore cookieStore;
    private HttpClient httpClient;

    //认证码
    private volatile String wxsid;
    private volatile String passTicket;
    private volatile String skey;
    private volatile String wxuin;
    //url版本号
    private volatile String urlVersion;
    //用户数据
    private volatile UserInfo loginUser;
    private final Lock loginUserLock = new ReentrantLock();
    private volatile JSONObject syncKey;
    private final Lock syncKeyLock = new ReentrantLock();
    private volatile List<UserInfo> contactList;
    private final Lock contactListLock = new ReentrantLock();
    //在线状态
    private volatile boolean isOnline = false;
    private final Lock isOnlineLock = new ReentrantLock();

    //同步监听器
    private volatile SyncMonitor syncMonitor;
    //退出事件处理器
    private List<ExitEventHandler> exitEventHandlers;
    //接收消息处理器
    private List<ReceivedMsgHandler> receivedMsgHandlers;

    public Wechat(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
        this.httpClient = buildHttpClient(cookieStore);
    }

    public Wechat() {
        this(new BasicCookieStore());
    }

    public void setWebWeixinApi(WebWeixinApi webWeixinApi) {
        this.webWeixinApi = webWeixinApi;
    }

    private Cookie getCookie(String name) {
        List<Cookie> cookies = cookieStore.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    public String getCookieValue(String name) {
        Cookie cookie = getCookie(name);
        if (cookie == null) {
            return null;
        }
        return cookie.getValue();
    }

    public void addExitEventHandler(ExitEventHandler handler) {
        if (handler == null) {
            return;
        }
        if (exitEventHandlers == null) {
            exitEventHandlers = new ArrayList<>();
        }

        exitEventHandlers.add(handler);
    }

    public void addExitEventHandler(Collection<ExitEventHandler> handlers) {
        if (handlers == null || handlers.isEmpty()) {
            return;
        }
        if (exitEventHandlers == null) {
            exitEventHandlers = new ArrayList<>();
        }
        for (ExitEventHandler handler : handlers) {
            addExitEventHandler(handler);
        }
    }

    public void addReceivedMsgHandler(ReceivedMsgHandler handler) {
        if (handler == null) {
            return;
        }
        if (receivedMsgHandlers == null) {
            receivedMsgHandlers = new ArrayList<>();
        }
        receivedMsgHandlers.add(handler);
    }

    public void addReceivedMsgHandler(Collection<ReceivedMsgHandler> handlers) {
        if (handlers == null || handlers.isEmpty()) {
            return;
        }
        if (receivedMsgHandlers == null) {
            receivedMsgHandlers = new ArrayList<>();
        }
        for (ReceivedMsgHandler handler : handlers) {
            addReceivedMsgHandler(handler);
        }
    }

    private HttpClient buildHttpClient(CookieStore cookieStore) {
        HttpRequestInterceptor interceptor = new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
                httpRequest.addHeader("User-Agent", PropertiesUtil.getProperty("wechat4j.userAgent"));
            }
        };
        HttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .addInterceptorFirst(interceptor)
                .build();
        return httpClient;
    }

    /**
     * 获取uuid（登录时）
     *
     * @param pw
     * @param time
     * @return
     */
    private String getWxUuid(PrintWriter pw, int time) {
        pw.print("尝试正常方式获取uuid...");
        pw.flush();

        for (int i = 0; i <= time; i++) {
            if (i > 0) {
                pw.print("\t第" + i + "次尝试...");
                pw.flush();
            }
            JSONObject result = webWeixinApi.getWxUuid(httpClient);

            if (result == null) {
                pw.println("\t失败：出现异常");
                pw.flush();
                return null;
            }

            String code = result.getString("code");
            String uuid = result.getString("uuid");
            if (!"200".equals(code)) {
                String msg = result.getString("msg");
                pw.println("\t失败：" + msg);
                pw.flush();
                return null;
            }

            if (StringUtil.isEmpty(uuid)) {
                pw.print("\t失败");
                if (i == 0 && time > 0) {
                    pw.print("，将重复尝试" + time + "次");
                }
                pw.println();
                pw.flush();
                continue;
            }

            pw.println("\t成功，值为：" + uuid);
            pw.flush();
            return uuid;
        }
        return null;
    }

    /**
     * 获取并显示qrcode（登录时）
     *
     * @return
     */
    private boolean getAndShowQRCode(PrintWriter pw, String uuid, int time) {
        pw.print("获取二维码...");
        pw.flush();

        for (int i = 0; i <= time; i++) {
            if (i > 0) {
                pw.print("\t第" + i + "次尝试...");
                pw.flush();
            }

            byte[] data = webWeixinApi.getQR(httpClient, uuid);

            if (data == null || data.length <= 0) {
                pw.print("\t失败");
                if (i == 0 && time > 0) {
                    pw.print("，将重新获取uuid并重复尝试" + time + "次");
                }
                pw.println();
                pw.flush();
                getWxUuid(pw, 0);

                CommonUtil.threadSleep(2000);
                continue;
            }

            pw.println("\t成功，请扫描二维码：");
            pw.flush();
            pw.println(QRCodeUtil.toCharMatrix(data));
            pw.flush();
            QRCodeUtil.openQRCodeImage(data);
            return true;
        }

        return false;
    }

    /**
     * 等待手机端确认登录（登录时）
     *
     * @return
     */
    private JSONObject waitForConfirm(PrintWriter pw, String uuid) {
        pw.print("等待手机端扫码...");
        pw.flush();

        boolean flag = false;
        while (true) {
            JSONObject result = webWeixinApi.getRedirectUri(httpClient, LoginTipEnum.TIP_0, uuid);
            if (result == null) {
                pw.println("\t失败：出现异常");
                pw.flush();
                return null;
            }

            String code = result.getString("code");
            if ("408".equals(code)) {
                pw.print(".");
                pw.flush();
                continue;
            } else if ("400".equals(code)) {
                pw.println("\t失败，二维码失效");
                pw.flush();
                return null;
            } else if ("201".equals(code)) {
                if (!flag) {
                    pw.println();
                    pw.print("请确认登录...");
                    pw.flush();
                    flag = true;
                }
                continue;
            } else if ("200".equals(code)) {
                pw.println("\t成功，认证完成");
                pw.flush();
                return result;
            } else {
                return null;
            }
        }
    }

    /**
     * 获取登录认证码（登录时）
     */
    private boolean getLoginCode(PrintWriter pw, String redirectUri) {
        pw.print("获取登录认证码...");
        pw.flush();

        JSONObject result = webWeixinApi.getLoginCode(httpClient, redirectUri);
        if (result == null) {
            pw.println("\t失败：出现异常");
            pw.flush();
            return false;
        }

        String ret = result.getString("ret");
        if (!"0".equals(ret)) {
            pw.println("\t失败：错误的返回码(" + ret + ")");
            pw.flush();
            return false;
        }

        wxsid = result.getString("wxsid");
        passTicket = result.getString("pass_ticket");
        skey = result.getString("skey");
        wxuin = result.getString("wxuin");

        pw.println("\t成功");
        pw.flush();

        return true;
    }

    /**
     * push方式获取uuid（登录时）
     *
     * @param pw
     * @param wxuin
     * @return
     */
    private String getWxUuid(PrintWriter pw, String wxuin) {
        pw.print("尝试push方式获取uuid...");
        pw.flush();

        JSONObject result = webWeixinApi.pushLogin(httpClient, urlVersion, wxuin);
        if (result == null) {
            pw.println("\t失败：出现异常");
            pw.flush();
            return null;
        }

        String ret = result.getString("ret");
        if (!"0".equals(ret)) {
            pw.println("\t失败：错误的返回码(" + ret + ")");
            pw.flush();
            return null;
        }

        String uuid = result.getString("uuid");
        if (StringUtil.isEmpty(uuid)) {
            pw.println("\t失败：空值");
            pw.flush();
            return null;
        }

        pw.println("\t成功，值为：" + uuid);
        pw.flush();

        return uuid;
    }

    /**
     * 微信数据初始化
     *
     * @return
     */
    private boolean wxInit() {
        JSONObject result = webWeixinApi.webWeixinInit(httpClient, urlVersion, passTicket, new BaseRequest(wxsid, skey, wxuin));
        if (result == null) {
            return false;
        }

        JSONObject BaseResponse = result.getJSONObject("BaseResponse");
        if (result == null) {
            return false;
        }

        int Ret = BaseResponse.getIntValue("Ret");
        if (Ret != 0) {
            return false;
        }

        loginUser = UserInfo.valueOf(result.getJSONObject("User"));
        syncKey = result.getJSONObject("SyncKey");

        return true;
    }

    /**
     * 微信数据初始化（登录时）
     *
     * @return
     */
    private boolean wxInitWithRetry(PrintWriter pw, int time) {
        pw.print("正在初始化数据...");
        pw.flush();

        for (int i = 0; i <= time; i++) {
            if (i > 0) {
                pw.print("\t第" + i + "次尝试...");
                pw.flush();
            }

            if (!wxInit()) {
                pw.print("\t失败");
                if (i == 0 && time > 0) {
                    pw.print("，将重复尝试" + time + "次");
                }
                pw.println();
                pw.flush();

                CommonUtil.threadSleep(2000);
                continue;
            }

            pw.println("\t成功");
            pw.flush();

            return true;
        }
        return false;
    }

    /**
     * 开启状态通知
     *
     * @param time
     * @return
     */
    private boolean statusNotify(int time) {
        for (int i = 0; i < time; i++) {
            JSONObject result = webWeixinApi.statusNotify(httpClient, urlVersion, passTicket, new BaseRequest(wxsid, skey, wxuin), getLoginUserName(false));
            if (result == null) {
                continue;
            }

            JSONObject BaseResponse = result.getJSONObject("BaseResponse");
            if (result == null) {
                continue;
            }

            int Ret = BaseResponse.getIntValue("Ret");
            if (Ret != 0) {
                continue;
            }

            return true;
        }

        return false;
    }

    /**
     * 自动登录
     */
    public boolean autoLogin(PrintWriter pw, boolean tryPushLogin) {
        // 0、获取消息打印流
        if (pw == null) {
            pw = new PrintWriter(System.out);
        }

        // 1、判断是否已经登录
        if (isOnline) {
            pw.println("当前已是登录状态，无需登录");
            return true;
        }

        JSONObject result = null;
        int time = PropertiesUtil.getIntValue("wechat4j.retry.time", 3);

        // 2、登录
        // 2.1、获取uuid
        String uuid = null;
        if (tryPushLogin && StringUtil.isNotEmpty(wxuin)) {
            uuid = getWxUuid(pw, wxuin);
        }
        if (StringUtil.isEmpty(uuid)) {
            uuid = getWxUuid(pw, time);
        }
        if (StringUtil.isEmpty(uuid)) {
            pw.println("无法获取uuid，登录不成功");
            pw.flush();
            return false;
        }
        // 2.2、获取并显示二维码
        if (!getAndShowQRCode(pw, uuid, time)) {
            pw.println("无法获取二维码，登录不成功");
            pw.flush();
            return false;
        }
        // 2.3、等待确认
        result = waitForConfirm(pw, uuid);
        if (result == null) {
            pw.println("手机端认证失败，登录不成功");
            pw.flush();
            return false;
        }
        urlVersion = result.getString("urlVersion");
        // 2.4、获取登录认证码
        if (!getLoginCode(pw, result.getString("redirectUri"))) {
            pw.println("无法获取登录认证码，登录不成功");
            pw.flush();
            return false;
        }

        // 3、初始化数据
        if (!wxInitWithRetry(pw, time)) {
            pw.println("初始化数据失败，请重新登录");
            pw.flush();
            return false;
        }
        pw.println("微信登录成功，欢迎你：" + getLoginUserNickName(false));
        pw.flush();

        try {
            isOnlineLock.lock();

            statusNotify(time);
            isOnline = true;
            syncMonitor = new SyncMonitor(this);
            syncMonitor.start();
        } finally {
            isOnlineLock.unlock();
        }

        return true;
    }

    /**
     * 自动登录
     *
     * @return
     */
    public boolean autoLogin() {
        return autoLogin(null, false);
    }

    /**
     * 退出登录
     */
    public void logout(boolean clearAllLoginInfo) {
        if (isOnline) {
            try {
                isOnlineLock.lock();

                if (isOnline) {
                    webWeixinApi.logout(httpClient, urlVersion, new BaseRequest(wxsid, skey, wxuin));
                    isOnline = false;

                    if (clearAllLoginInfo) {
                        clearAllLoginInfo();
                    }
                }
            } finally {
                isOnlineLock.unlock();
            }
        }
    }

    public void logout() {
        logout(true);
    }

    /**
     * 清除全部登录信息
     */
    private void clearAllLoginInfo() {
        try {
            loginUserLock.lock();
            syncKeyLock.lock();
            contactListLock.lock();

            wxsid = null;
            passTicket = null;
            skey = null;
            urlVersion = null;
            loginUser = null;
            syncKey = null;
            if (contactList != null) {
                contactList.clear();
                contactList = null;
            }
        } finally {
            loginUserLock.unlock();
            syncKeyLock.unlock();
            contactListLock.unlock();
        }
    }

    /**
     * 判断在线状态
     *
     * @return
     */
    public boolean isOnline() {
        return isOnline;
    }

    /**
     * 微信同步监听器（心跳）
     */
    private class SyncMonitor extends Thread {
        private Wechat wechat;

        public SyncMonitor(Wechat wechat) {
            this.wechat = wechat;
        }

        @Override
        public void run() {
            int time = PropertiesUtil.getIntValue("wechat4j.syncCheck.retry.time", 5);
            int i = 0;
            while (isOnline) {
                long start = System.currentTimeMillis();

                try {
                    JSONObject result = webWeixinApi.syncCheck(httpClient, urlVersion, new BaseRequest(wxsid, skey, wxuin), getSyncKeyList(false));
                    logger.info("微信同步监听心跳返回数据：{}", result);
                    if (result == null) {
                        throw new RuntimeException("微信API调用异常");
                    } else {
                        i = 0;
                    }

                    //人为退出
                    int retcode = result.getIntValue("retcode");
                    if (retcode != RetcodeEnum.RECODE_0.getCode()) {
                        logger.info("微信退出或从其它设备登录");
                        logout();
                        processExitEvent(ExitTypeEnum.REMOTE_EXIT, null);
                        return;
                    }

                    int selector = result.getIntValue("selector");
                    processSelector(selector);
                } catch (Exception e) {
                    logger.error("同步监听心跳异常", e);

                    if (i == 0) {
                        logger.info("同步监听请求失败，正在重试...");
                    } else if (i > 0) {
                        logger.info("第{}次重试失败" + i);
                    }

                    if (i >= time) {
                        logger.info("重复{}次仍然失败，退出微信", i);
                        logout();
                        processExitEvent(ExitTypeEnum.ERROR_EXIT, e);
                        return;
                    }

                    i++;
                }

                //如果时间太短则阻塞2秒
                long end = System.currentTimeMillis();
                if (end - start < 2000) {
                    CommonUtil.threadSleep(2000);
                }
            }

            processExitEvent(ExitTypeEnum.LOCAL_EXIT, null);
        }

        /**
         * 处理退出事件
         */
        private void processExitEvent(ExitTypeEnum type, Throwable t) {
            try {
                if (exitEventHandlers == null) {
                    return;
                }

                for (ExitEventHandler handler : exitEventHandlers) {
                    if (handler != null) {
                        processExitEvent(type, t, handler);
                    }
                }
            } catch (Exception e) {
                logger.error("Exit event process error.", e);
            }
        }

        private void processExitEvent(ExitTypeEnum type, Throwable t, ExitEventHandler handler) {
            try {
                switch (type) {
                    case ERROR_EXIT:
                        handler.handleErrorExitEvent(wechat);
                        break;
                    case REMOTE_EXIT:
                        handler.handleRemoteExitEvent(wechat);
                        break;
                    case LOCAL_EXIT:
                        handler.handleLocalExitEvent(wechat);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                logger.error("Exit event process error.", e);
            }

            try {
                handler.handleAllType(wechat, type, t);
            } catch (Exception e) {
                logger.error("Exit event process error.", e);
            }
        }

        /**
         * 处理selector值
         *
         * @param selector
         */
        private void processSelector(int selector) {
            try {
                SelectorEnum e = SelectorEnum.valueOf(selector);
                if (e == null) {
                    logger.warn("Cannot process unknow selector {}", selector);
                    return;
                }

                switch (e) {
                    case SELECTOR_0:
                        break;
                    case SELECTOR_2:
                        webWxSync();
                        break;
                    case SELECTOR_4:
                        break;
                    case SELECTOR_6:
                        break;
                    case SELECTOR_7:
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                logger.error("Execute processSelector error.", e);
            }
        }

        /**
         * 同步数据
         *
         * @return
         */
        private void webWxSync() {
            try {
                JSONObject result = webWeixinApi.webWxSync(httpClient, urlVersion, passTicket, new BaseRequest(wxsid, skey, wxuin), syncKey);
                if (result == null) {
                    logger.error("从服务端同步新数据异常");
                    return;
                }

                JSONObject BaseResponse = result.getJSONObject("BaseResponse");
                if (BaseResponse == null) {
                    logger.warn("同步接口返回数据格式错误");
                    return;
                }

                int Ret = BaseResponse.getIntValue("Ret");
                if (Ret != RetcodeEnum.RECODE_0.getCode()) {
                    logger.warn("同步接口返回错误代码:{}", Ret);
                    return;
                }

                //新消息处理
                JSONArray AddMsgList = result.getJSONArray("AddMsgList");
                processNewMsg(AddMsgList);

                //更新SyncKey
                try {
                    syncKeyLock.lock();
                    syncKey = result.getJSONObject("SyncKey");
                } finally {
                    syncKeyLock.unlock();
                }
            } catch (Exception e) {
                logger.error("Execute webWxSync error.", e);
            }
        }

        /**
         * 处理新消息
         *
         * @param AddMsgList
         */
        private void processNewMsg(JSONArray AddMsgList) {
            try {
                if (AddMsgList == null || AddMsgList.isEmpty()) {
                    return;
                }

                int len = AddMsgList.size();
                logger.debug("收到{}条新消息", len);

                if (receivedMsgHandlers == null || receivedMsgHandlers.isEmpty()) {
                    logger.warn("收到{}条新消息，但没有配置消息处理器", len);
                    return;
                }

                List<ReceivedMsg> receivedMsgList = ReceivedMsg.valueOf(AddMsgList);
                for (ReceivedMsg receivedMsg : receivedMsgList) {
                    for (ReceivedMsgHandler handler : receivedMsgHandlers) {
                        if (handler != null) {
                            processNewMsg(receivedMsg, handler);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Execute processNewMsg error.", e);
            }
        }

        private void processNewMsg(ReceivedMsg msg, ReceivedMsgHandler handler) {
            try {
                handler.handleAllType(wechat, msg);
            } catch (Exception e) {
                logger.error("Execute processNewMsg error.", e);
            }
        }
    }

    /**
     * 获取登录用户对象
     *
     * @return
     */
    public UserInfo getLoginUser(boolean update) {
        if (loginUser == null || update) {
            JSONObject result = webWeixinApi.webWeixinInit(httpClient, urlVersion, passTicket, new BaseRequest(wxsid, skey, wxuin));
            if (result == null) {
                return loginUser;
            }

            JSONObject BaseResponse = result.getJSONObject("BaseResponse");
            if (result == null) {
                return loginUser;
            }

            int Ret = BaseResponse.getIntValue("Ret");
            if (Ret != 0) {
                return loginUser;
            }

            try {
                loginUserLock.lock();
                if (loginUser == null || update) {
                    loginUser = UserInfo.valueOf(result.getJSONObject("User"));
                }
            } finally {
                loginUserLock.unlock();
            }

            return loginUser;
        }
        return loginUser;
    }

    public UserInfo getLoginUser() {
        return getLoginUser(false);
    }

    /**
     * 获取登录用户名
     *
     * @return
     */
    public String getLoginUserName(boolean update) {
        UserInfo loginUser = getLoginUser(update);
        if (loginUser == null) {
            return null;
        }
        return loginUser.getUserName();
    }

    public String getLoginUserName() {
        return getLoginUserName(false);
    }

    /**
     * 获取登录用户的昵称
     *
     * @return
     */
    public String getLoginUserNickName(boolean update) {
        UserInfo loginUser = getLoginUser(update);
        if (loginUser == null) {
            return null;
        }
        return loginUser.getNickName();
    }

    public String getLoginUserNickName() {
        return getLoginUserNickName(false);
    }

    /**
     * 获取SyncKey
     *
     * @param update
     * @return
     */
    private JSONObject getSyncKey(boolean update) {
        if (syncKey == null || update) {
            JSONObject result = webWeixinApi.webWeixinInit(httpClient, urlVersion, passTicket, new BaseRequest(wxsid, skey, wxuin));
            if (result == null) {
                return syncKey;
            }

            JSONObject BaseResponse = result.getJSONObject("BaseResponse");
            if (result == null) {
                return syncKey;
            }

            int Ret = BaseResponse.getIntValue("Ret");
            if (Ret != 0) {
                return syncKey;
            }

            try {
                syncKeyLock.lock();
                if (syncKey == null || update) {
                    syncKey = result.getJSONObject("SyncKey");
                }
            } finally {
                syncKeyLock.unlock();
            }

            return syncKey;
        }
        return syncKey;
    }

    /**
     * 获取SyncKey的List
     *
     * @param update
     * @return
     */
    private JSONArray getSyncKeyList(boolean update) {
        JSONObject SyncKey = getSyncKey(update);
        if (SyncKey == null) {
            return null;
        }
        return SyncKey.getJSONArray("List");
    }

    /**
     * 获取联系人列表
     *
     * @param update
     * @return
     */
    public List<UserInfo> getContactList(boolean update) {
        if (contactList == null || update) {
            JSONObject result = webWeixinApi.getContact(httpClient, urlVersion, passTicket, skey);
            if (result == null) {
                return contactList;
            }

            JSONObject BaseResponse = result.getJSONObject("BaseResponse");
            if (BaseResponse == null) {
                return contactList;
            }

            String Ret = BaseResponse.getString("Ret");
            if (!"0".equals(Ret)) {
                return contactList;
            }

            try {
                contactListLock.lock();
                if (contactList == null || update) {
                    contactList = UserInfo.valueOf(result.getJSONArray("MemberList"));
                }
            } finally {
                contactListLock.unlock();
            }

            return contactList;
        }
        return contactList;
    }

    public List<UserInfo> getContactList() {
        return getContactList(false);
    }

    /**
     * 根据UserName获取联系人信息
     *
     * @param update
     * @param userName
     * @return
     */
    public UserInfo getContactByUserName(boolean update, String userName) {
        if (StringUtil.isEmpty(userName)) {
            return null;
        }

        List<UserInfo> list = getContactList(update);
        if (list == null) {
            return null;
        }

        for (UserInfo userInfo : list) {
            if (userInfo == null) {
                continue;
            }

            if (userName.equals(userInfo.getUserName())) {
                return userInfo;
            }
        }

        return null;
    }

    public UserInfo getContactByUserName(String userName) {
        return getContactByUserName(false, userName);
    }

    /**
     * 根据NickName获取联系人信息
     *
     * @param update
     * @param nickName
     * @return
     */
    public UserInfo getContactByNickName(boolean update, String nickName) {
        if (StringUtil.isEmpty(nickName)) {
            return null;
        }

        List<UserInfo> list = getContactList(update);
        if (list == null) {
            return null;
        }

        for (UserInfo userInfo : list) {
            if (userInfo == null) {
                continue;
            }

            if (nickName.equals(userInfo.getNickName())) {
                return userInfo;
            }
        }

        return null;
    }

    public UserInfo getContactByNickName(String nickName) {
        return getContactByNickName(false, nickName);
    }

    /**
     * 根据RemarkName获取联系人信息
     *
     * @param update
     * @param remarkName
     * @return
     */
    public UserInfo getContactByRemarkName(boolean update, String remarkName) {
        if (StringUtil.isEmpty(remarkName)) {
            return null;
        }

        List<UserInfo> list = getContactList(update);
        if (list == null) {
            return null;
        }

        for (UserInfo userInfo : list) {
            if (userInfo == null) {
                continue;
            }

            if (remarkName.equals(userInfo.getRemarkName())) {
                return userInfo;
            }
        }

        return null;
    }

    public UserInfo getContactByRemarkName(String remarkName) {
        return getContactByRemarkName(false, remarkName);
    }

    /**
     * 发送文本消息
     *
     * @return
     */
    public JSONObject sendText(String content, String userName) {
        BaseRequest baseRequest = new BaseRequest(wxsid, skey, wxuin);

        String msgId = WechatUtil.createMsgId();
        String loginUserName = getLoginUserName(false);
        WxMessage message = new WxMessage();
        message.setClientMsgId(msgId);
        message.setContent(content);
        message.setFromUserName(loginUserName);
        message.setLocalID(msgId);
        if (StringUtil.isEmpty(userName)) {
            message.setToUserName(loginUserName);
        } else {
            message.setToUserName(userName);
        }
        message.setType(MsgTypeEnum.TEXT_MSG.getCode());

        JSONObject result = webWeixinApi.sendMsg(httpClient, urlVersion, passTicket, baseRequest, message);

        return result;
    }

    /**
     * 发送文本消息（根据昵称）
     *
     * @param content
     * @param nickName
     * @return
     */
    public JSONObject sendTextToNickName(String content, String nickName) {
        if (StringUtil.isEmpty(nickName)) {
            return sendText(content, null);
        }

        UserInfo userInfo = getContactByNickName(false, nickName);
        if (userInfo == null) {
            return null;
        }

        String userName = userInfo.getUserName();
        if (StringUtil.isEmpty(userName)) {
            return null;
        }

        return sendText(content, userName);
    }

    /**
     * 发送文本消息（根据备注名）
     *
     * @param content
     * @param remarkName
     * @return
     */
    public JSONObject sendTextToRemarkName(String content, String remarkName) {
        if (StringUtil.isEmpty(remarkName)) {
            return sendText(content, null);
        }

        UserInfo userInfo = getContactByRemarkName(false, remarkName);
        if (userInfo == null) {
            return null;
        }

        String userName = userInfo.getUserName();
        if (StringUtil.isEmpty(userName)) {
            return null;
        }

        return sendText(content, userName);
    }

    //TODO 待完成
    @Deprecated
    public JSONObject sendImage(File image, String userName) {
        String loginUserName = getLoginUserName(false);
        String toUserName = StringUtil.isEmpty(userName) ? loginUserName : userName;
        BaseRequest baseRequest = new BaseRequest(wxsid, skey, wxuin);
        String dataTicket = getCookieValue("webwx_data_ticket");

        JSONObject result = webWeixinApi.uploadMedia(httpClient, urlVersion, passTicket, baseRequest, loginUserName, toUserName, dataTicket, image);
        return result;
    }
}
