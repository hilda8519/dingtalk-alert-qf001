package com.qf.bigdata.azkaban.alert;

import azkaban.alert.Alerter;
import azkaban.executor.ExecutableFlow;
import azkaban.sla.SlaOption;
import azkaban.utils.Props;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;

import java.text.SimpleDateFormat;
import java.util.Map;

public class DingAlert  implements Alerter {
    private  Props props;
    private String dingServerUri="https://oapi.dingtalk.com/robot/send?access_token=";
    public DingAlert(Props props){
        this.props=props;
    }

    //判断是否触相应的事件处理
    private boolean isActivated(ExecutableFlow flow ,String type){
        Map<String,String> parameters=flow.getExecutionOptions().getFlowParameters();
        String propertyKey="ding.alert.on."+type;
        return Boolean.parseBoolean(
                parameters.getOrDefault(parameters.get(propertyKey),
                        props.getString(propertyKey,"false")));
    }

    //获取配置参数
    private String getProperty(ExecutableFlow flow,String propertyKey){
        Map<String,String> parameters=flow.getExecutionOptions().getFlowParameters();
        return parameters.getOrDefault(propertyKey,props.getString(propertyKey));
    }
    //发送消息
    private void send(ExecutableFlow flow,String title,String content){

        String token=this.getProperty(flow,"ding.token");
        String linkHost=this.getProperty(flow,"ding.link.azkaban.host");
        String linkAddress=linkHost+"/executor?execid="+flow.getExecutionId()+"#jobslist";
        String authWord=this.getProperty(flow,"ding.auth.word");
        DefaultDingTalkClient client=new DefaultDingTalkClient(dingServerUri+token);

        OapiRobotSendRequest request=new OapiRobotSendRequest();
        request.setMsgtype("link");
        OapiRobotSendRequest.Link link=new OapiRobotSendRequest.Link();
        link.setMessageUrl(linkAddress); //http://hadoop02:8081/executor?execid=3#jobslist
        link.setPicUrl("");
        link.setTitle(title);
        link.setText(authWord+":"+content);
        request.setLink(link);

        try{
            client.execute(request);
        }catch (Exception e){
            System.out.println(e.fillInStackTrace());
        }

    }

    public void alertOnSuccess(ExecutableFlow executableFlow) throws Exception {
        if(isActivated(executableFlow,"success")){
            String title="Flow "+executableFlow.getFlowId() +" has successded:\n";

            StringBuilder sb=new StringBuilder();
            sb.append("\t-Start:").append(
                    new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(executableFlow.getStartTime()))
                    .append("\n");
            sb.append("\t-End:").append(
                    new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(executableFlow.getEndTime()))
                    .append("\n");
            sb.append("\t- Duration:").append(executableFlow.getEndTime()-executableFlow.getStartTime()/1000).append("s");
            send(executableFlow,title,sb.toString());
        }
    }

    @Override
    public void alertOnError(ExecutableFlow executableFlow, String... strings) throws Exception {
        if(isActivated(executableFlow,"error")){
            String title="Flow "+executableFlow.getFlowId() +" has failed:\n";

            StringBuilder sb=new StringBuilder();
            sb.append("\t-Start:").append(
                    new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(executableFlow.getStartTime()))
                    .append("\n");
            sb.append("\t-End:").append(
                    new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(executableFlow.getEndTime()))
                    .append("\n");
            sb.append("\t- Duration:").append(executableFlow.getEndTime()-executableFlow.getStartTime()/1000).append("s");
            send(executableFlow,title,sb.toString());
        }

    }

    @Override
    public void alertOnFirstError(ExecutableFlow executableFlow) throws Exception {
        if(isActivated(executableFlow,"first.error")){
            String title="Flow "+executableFlow.getFlowId() +" has failed:\n";

            StringBuilder sb=new StringBuilder();
            sb.append("\t-Start:").append(
                    new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(executableFlow.getStartTime()))
                    .append("\n");
            sb.append("\t-End:").append(
                    new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(executableFlow.getEndTime()))
                    .append("\n");
            sb.append("\t- Duration:").append(executableFlow.getEndTime()-executableFlow.getStartTime()/1000).append("s");
            send(executableFlow,title,sb.toString());
        }
    }

    @Override
    public void alertOnSla(SlaOption slaOption, String s) throws Exception {

    }
}
