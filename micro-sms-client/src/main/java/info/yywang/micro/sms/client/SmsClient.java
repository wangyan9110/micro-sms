package info.yywang.micro.sms.client;

import com.alibaba.fastjson.JSON;
import info.yywang.micro.sms.producer.SmsConstant;
import info.yywang.micro.sms.producer.SmsProducer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yanyan.wang
 * @date 2016-08-03 20:28
 */
public class SmsClient {

    /**
     * 发送短信 支持模板方式
     *
     * @param phone      手机号
     * @param templateId 模板id
     * @param params     参数
     * @return 是否发送成功
     */
    public static boolean sendMsg(String phone, int templateId, Map<String, String> params) {

        if (templateId == 0) {
            return false;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("phone", phone);
        body.put("templateId", templateId);
        body.put("params", params);
        return SmsProducer.sendMsg(SmsConstant.TEMPLTAG, JSON.toJSONString(body));
    }

    /**
     * 发送短信
     *
     * @param phone      手机号
     * @param signId     签名id
     * @param templateId 模板id
     * @param params     参数
     * @return
     */
    public static boolean sendMsg(String phone, String signId, String templateId, Map<String, String> params) {
        return false;
    }


    /**
     * 发送短信 支持模板方式
     * <p>
     * params依次填充变量
     * </p>
     *
     * @param phone
     * @param templateId
     * @param params
     * @return
     */
    public static boolean sendMsg(String phone, int templateId, String... params) {
        return false;
    }

//    /**
//     * 发送短信
//     *
//     * @param phone 手机号
//     * @param msg   消息内容
//     * @return 是否发送成功
//     */
//    public static boolean sendMsg(String phone, String msg) {
//        Map<String, Object> body = new HashMap<>();
//        body.put("phone", phone);
//        body.put("msg", msg);
//        return SmsProducer.sendMsg(SmsConstant.MSGTAG, JSON.toJSONString(body));
//    }

}
