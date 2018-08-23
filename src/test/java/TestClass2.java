import com.hotlcc.wechat4j.Wechat;
import com.hotlcc.wechat4j.api.WebWeixinApi;
import com.hotlcc.wechat4j.handler.ReceivedMsgHandler;
import com.hotlcc.wechat4j.model.ReceivedMsg;
import com.hotlcc.wechat4j.model.UserInfo;
import com.hotlcc.wechat4j.util.CommonUtil;
import com.hotlcc.wechat4j.util.StringUtil;

public class TestClass2 {
    public static void main(String[] args) {
        WebWeixinApi api = new WebWeixinApi();
        Wechat wechat = new Wechat();
        wechat.setWebWeixinApi(api);
        wechat.addReceivedMsgHandler(new ReceivedMsgHandler() {
            @Override
            public void handleAllType(Wechat wechat, ReceivedMsg msg) {
                UserInfo contact = wechat.getContactByUserName(false, msg.getFromUserName());
                String name = StringUtil.isEmpty(contact.getRemarkName()) ? contact.getNickName() : contact.getRemarkName();
                System.out.println(name + ": " + msg.getContent());
            }
        });
        wechat.autoLogin();
        CommonUtil.threadSleep(60000);
    }
}
