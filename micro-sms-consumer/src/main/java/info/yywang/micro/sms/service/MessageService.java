package info.yywang.micro.sms.service;

import info.yywang.micro.sms.entity.SendResult;

import java.util.List;
import java.util.Map;

/**
 * Created by xiuyuhang on 16/8/5.
 */
public interface MessageService {

    /**
     * 通过阿里大于发送短信
     *
     * @param phone
     * @param templateId
     * @param params
     * @return
     */
    SendResult sendByDayu(String phone, String templateId, Map<String, String> params);

    /**
     * 通过网易云信发送短信
     *
     * @return
     */
    SendResult sendByYunXin(String phones, int templateId, List<String> params);
}
