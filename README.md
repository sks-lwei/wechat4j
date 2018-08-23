# wechat4j

> 一个基于Web微信API的Java版微信客户端


[![gitee](https://img.shields.io/badge/gitee-%40hotlcc-green.svg)](https://gitee.com/hotlcc)
[![gitee](https://img.shields.io/badge/github-%40hotlcc-blank.svg)](https://github.com/hotlcc)
[![toutiao](https://img.shields.io/badge/toutiao-%40Allen-red.svg)](https://www.toutiao.com/c/user/3341863552/#mid=51655113888)
[![@Allen on weibo](https://img.shields.io/badge/weibo-%40Allen-orange.svg)](https://weibo.com/hotloveu?is_hot=1)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

## Web微信API文档

详见：[Web微信API文档](doc/web-weixin-api.md)

## 简单使用

```java
WebWeixinApi api = new WebWeixinApi();
// 实例化微信客户端
Wechat wechat = new Wechat();
wechat.setWebWeixinApi(api);
// 自动登录
wechat.autoLogin();
```

## 发送消息

### 文本消息

```java
// 通过userName发送文本消息
public JSONObject sendText(String content, String userName);
// 通过昵称发送消息
public JSONObject sendTextToNickName(String content, String nickName);
// 通过备注名发送消息
public JSONObject sendTextToRemarkName(String content, String remarkName);
```

> 目前仅支持发送文本消息，更多消息类型支持尽请期待。

## 消息处理器

> 通过在实例化时添加消息处理器来处理接收到的消息<br>
> 消息处理器需要实现`ReceivedMsgHandler`接口

```java
wechat.addReceivedMsgHandler(new ReceivedMsgHandler() {
    @Override
    public void handleAllType(Wechat wechat, ReceivedMsg msg) {
        UserInfo contact = wechat.getContactByUserName(false, msg.getFromUserName());
        String name = StringUtil.isEmpty(contact.getRemarkName()) ? contact.getNickName() : contact.getRemarkName();
        System.out.println(name + ": " + msg.getContent());
    }
});
```

