package info.yywang.micro.sms.listener;

import java.util.Map;

/**
 * 发送短信监听器
 *
 * @author yanyan.wang
 * @date 2016-08-04 00:54
 */
public interface SmsListener {

    /**
     * 发送基于模板短信
     *
     * @param phone
     * @param templateId
     * @param params
     */
    void sendMsg(String msgId, String phone, int templateId, Map<String, String> params);
}
