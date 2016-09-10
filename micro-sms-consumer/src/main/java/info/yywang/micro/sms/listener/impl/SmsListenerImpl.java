package info.yywang.micro.sms.listener.impl;

import info.yywang.micro.common.time.DateUtils;
import info.yywang.micro.common.utils.JSONUtils;
import info.yywang.micro.sms.dao.MessageRecordDao;
import info.yywang.micro.sms.dao.MessageTemplateDao;
import info.yywang.micro.sms.entity.DaYuSendResult;
import info.yywang.micro.sms.entity.MessageRecord;
import info.yywang.micro.sms.entity.MessageTemplate;
import info.yywang.micro.sms.entity.SendResult;
import info.yywang.micro.sms.entity.YunXinSendResult;
import info.yywang.micro.sms.enums.MessageChannel;
import info.yywang.micro.sms.enums.MessageStatus;
import info.yywang.micro.sms.listener.SmsListener;
import info.yywang.micro.sms.service.MessageService;
import info.yywang.micro.sms.utils.AnalysisUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by xiuyuhang on 16/8/4.
 */
@Service("smsListener")
public class SmsListenerImpl implements SmsListener {

    private final static Logger logger = Logger.getLogger(SmsListenerImpl.class);

    @Resource
    private MessageRecordDao messageRecordDao;

    @Resource
    private MessageTemplateDao messageTemplateDao;

    @Resource
    private MessageService messageService;

    /**
     * 配置阿里大于和网易云信的发送比例
     */
    @Value("#{messagePropertyReader['proportion']}")
    private String proportion;

    @Override
    public void sendMsg(String msgId, String phone, int templateId, Map<String, String> params) {

        MessageTemplate messageTemplate = messageTemplateDao.find(templateId);

        String[] pro = proportion.split(":");

        int rand = new Random().nextInt(Integer.parseInt(pro[1]));
        MessageRecord messageRecord = getMessageRecord(msgId, phone, messageTemplate, params, rand, pro);

        //msgId是数据库的唯一键  利用mysql特性(ON DUPLICATE KEY UPDATE 去重)
        messageRecordDao.insert(messageRecord);

        //如果消息不存在,插入之后将生成的主键值赋值给id,则id>0  如果小于等于0说明该消息已经被消费了
        if (messageRecord.getId() <= 0) {
            //消息已经被消费过了
            return;
        }

        MessageRecord finalMessageRecord;
        if (rand > Integer.parseInt(pro[0]) - 1) {
            //先使用阿里大于
            finalMessageRecord = userDayuFirst(messageRecord, messageTemplate, params);
        } else {
            //先使用网易云信
            finalMessageRecord = userYunXinFirst(messageRecord, messageTemplate, params);
        }

        messageRecordDao.update(finalMessageRecord);
    }

    /**
     * 先使用大于 如果失败使用云信再发一次
     *
     * @param params
     * @param messageTemplate
     * @param messageRecord
     * @return
     */
    private MessageRecord userDayuFirst(MessageRecord messageRecord, MessageTemplate messageTemplate, Map<String, String> params) {
        if (StringUtils.isEmpty(messageTemplate.getDaYuTemplateId())) {
            //如果选用大于时,没有
            YunXinSendResult yunXinSendResult = (YunXinSendResult) sendByYunXin(messageRecord, messageTemplate, params);
            dealYunXinResult(messageRecord, messageTemplate, yunXinSendResult);
            return messageRecord;
        }
        //先用大于发
        DaYuSendResult daYuSendResult = (DaYuSendResult) sendByDayu(messageRecord, messageTemplate, params);

        if (daYuSendResult.getSuccess()) {
            //如果第一次发送成功
            messageRecord.setSuccessTime(DateUtils.now());
            messageRecord.setChannel(MessageChannel.DaYu);
            messageRecord.setStatus(MessageStatus.Success);
            messageRecord.setTemplateId(messageTemplate.getDaYuTemplateId());
        } else {
            YunXinSendResult yunXinSendResult = (YunXinSendResult) sendByYunXin(messageRecord, messageTemplate, params);
            dealYunXinResult(messageRecord, messageTemplate, yunXinSendResult);
        }
        return messageRecord;
    }

    /**
     * 处理云信的结果 如果失败使用大于再发一次
     *
     * @param messageRecord
     * @param messageTemplate
     * @param yunXinSendResult
     */
    private void dealYunXinResult(MessageRecord messageRecord, MessageTemplate messageTemplate, YunXinSendResult yunXinSendResult) {
        Date now = DateUtils.now();
        if (yunXinSendResult.getSuccess()) {
            //切换云信发送成功
            messageRecord.setSuccessTime(now);
            messageRecord.setStatus(MessageStatus.Success);
            messageRecord.setSendId(yunXinSendResult.getSendId());
        } else {
            //切换云信发送也失败了
            messageRecord.setFailedTime(now);
            messageRecord.setStatus(MessageStatus.Failed);
            messageRecord.setRemark("错误信息: " + yunXinSendResult.getMsg() + ",错误码: " + yunXinSendResult.getCode());
        }
        messageRecord.setChannel(MessageChannel.YunXin);
        messageRecord.setTemplateId(messageTemplate.getYxTemplateId());
    }

    /**
     * 先使用云信
     *
     * @param messageTemplate 消息模板
     * @param messageRecord   消息记录
     * @param params          参数
     * @return
     */
    private MessageRecord userYunXinFirst(MessageRecord messageRecord, MessageTemplate messageTemplate, Map<String, String> params) {

        if (StringUtils.isEmpty(messageTemplate.getYxTemplateId())) {
            //如果云信的templateId没有,直接调用大于,然后返回
            DaYuSendResult daYuSendResult = (DaYuSendResult) sendByDayu(messageRecord, messageTemplate, params);
            dealDayuReult(messageRecord, messageTemplate, daYuSendResult);
            return messageRecord;
        }

        YunXinSendResult yunXinSendResult = (YunXinSendResult) sendByYunXin(messageRecord, messageTemplate, params);

        if (yunXinSendResult.getSuccess()) {
            //如果第一次发送成功
            messageRecord.setSuccessTime(DateUtils.now());
            messageRecord.setStatus(MessageStatus.Success);
            messageRecord.setChannel(MessageChannel.YunXin);
            messageRecord.setSendId(yunXinSendResult.getSendId());
            messageRecord.setTemplateId(messageTemplate.getYxTemplateId());
        } else {
            //切换大于发
            DaYuSendResult daYuSendResult = (DaYuSendResult) sendByDayu(messageRecord, messageTemplate, params);
            dealDayuReult(messageRecord, messageTemplate, daYuSendResult);
        }
        return messageRecord;
    }

    /**
     * 处理大于的返回结果
     *
     * @param messageRecord
     * @param messageTemplate
     * @param daYuSendResult
     */
    private void dealDayuReult(MessageRecord messageRecord, MessageTemplate messageTemplate, DaYuSendResult daYuSendResult) {
        Date now = DateUtils.now();
        if (daYuSendResult.getSuccess()) {
            //切换大于发送成功
            messageRecord.setSuccessTime(now);
            messageRecord.setStatus(MessageStatus.Success);
        } else {
            //切换大于仍然发送失败
            messageRecord.setFailedTime(now);
            messageRecord.setStatus(MessageStatus.Failed);
            messageRecord.setRemark("错误信息: " + daYuSendResult.getMsg() + ",错误码: " + daYuSendResult.getErrorCode());
        }
        messageRecord.setChannel(MessageChannel.DaYu);
        messageRecord.setTemplateId(messageTemplate.getDaYuTemplateId());
    }

    /**
     * 使用阿里大于发短信
     *
     * @param params        参数
     * @param messageRecord 记录
     * @return
     */
    private SendResult sendByDayu(MessageRecord messageRecord, MessageTemplate messageTemplate, Map<String, String> params) {
        DaYuSendResult daYuSendResult;
        try {
            daYuSendResult = (DaYuSendResult) messageService.sendByDayu(messageRecord.getPhone(), messageTemplate.getDaYuTemplateId(), params);
        } catch (Exception e) {
            daYuSendResult = new DaYuSendResult(false, e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return daYuSendResult;
    }

    /**
     * 使用网易云信发
     *
     * @param params        参数
     * @param messageRecord 记录
     * @return
     */
    private SendResult sendByYunXin(MessageRecord messageRecord, MessageTemplate messageTemplate, Map<String, String> params) {

        List<String> sortParams = AnalysisUtils.analysisAndGetParams(messageTemplate.getContent(), params);

        YunXinSendResult yunXinSendResult;
        try {
            yunXinSendResult = (YunXinSendResult) messageService.sendByYunXin(messageRecord.getPhone(), Integer.parseInt(messageTemplate.getYxTemplateId()), sortParams);
        } catch (Exception e) {
            yunXinSendResult = new YunXinSendResult(false, e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return yunXinSendResult;
    }

    private MessageRecord getMessageRecord(String msgId, String phone, MessageTemplate messageTemplate, Map<String, String> params, int rand, String[] pro) {
        String selectTemplateId;
        MessageChannel messageChannel;

        //进行计算,按比例选取发送方
        if (rand > Integer.parseInt(pro[0]) - 1) {
            //选用阿里大于
            selectTemplateId = messageTemplate.getDaYuTemplateId();
            messageChannel = MessageChannel.DaYu;
        } else {
            //选网易云信
            selectTemplateId = messageTemplate.getYxTemplateId();
            messageChannel = MessageChannel.YunXin;
        }

        MessageRecord messageRecord = new MessageRecord();
        messageRecord.setMsgId(msgId);
        messageRecord.setPhone(phone);
        messageRecord.setTemplateId(selectTemplateId);
        messageRecord.setStatus(MessageStatus.Sending);
        messageRecord.setChannel(messageChannel);
        messageRecord.setParams(JSONUtils.toJSONString(params));
        messageRecord.setDeleted(false);
        messageRecord.setCreateTime(DateUtils.now());
        return messageRecord;
    }
}
