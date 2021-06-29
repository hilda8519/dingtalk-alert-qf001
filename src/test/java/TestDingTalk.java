import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;

public class TestDingTalk {

    public static void main(String[] args) {
        //向qf001机器人发送消息

        //https://oapi.dingtalk.com/robot/send?access_token=a876a4f92b474387deae1113b573df5fb76f0f666e4225a3e331235c0c61b13e
        String token="a876a4f92b474387deae1113b573df5fb76f0f666e4225a3e331235c0c61b13e";

        DefaultDingTalkClient client=new DefaultDingTalkClient(
                "https://oapi.dingtalk.com/robot/send?access_token="+token);

        OapiRobotSendRequest request=new OapiRobotSendRequest();
        request.setMsgtype("link");
        OapiRobotSendRequest.Link link=new OapiRobotSendRequest.Link();
        link.setMessageUrl("http://hadoop02:8081/");
        link.setPicUrl("");
        link.setTitle("ods news flow");
        link.setText("qf:hello world");
        request.setLink(link);

        try{
            client.execute(request);
        }catch (Exception e){
            System.out.println(e.fillInStackTrace());
        }

    }
}
