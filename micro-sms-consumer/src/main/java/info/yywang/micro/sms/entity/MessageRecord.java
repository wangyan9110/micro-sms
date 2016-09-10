package info.yywang.micro.sms.entity;

import info.yywang.micro.sms.enums.MessageChannel;
import info.yywang.micro.sms.enums.MessageStatus;
import lombok.Data;

import java.util.Date;

/**
 * 短信记录
 *
 * @author yanyan.wang
 * @date 2016-08-04 00:18
 */
@Data
public class MessageRecord {

    /**
     * the id
     */
    private int id;

    /**
     * 唯一消息id
     */
    private String msgId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 短信内容
     */
    private String body;

    /**
     * 模板id
     */
    private String templateId;

    /**
     * 模板的参数
     */
    private String params;

    /**
     * 状态
     */
    private MessageStatus status;

    /**
     * 发送渠道
     */
    private MessageChannel channel;

    /**
     * 成功时间
     */
    private Date successTime;

    /**
     * 失败时间
     */
    private Date failedTime;

    /**
     * 反查短信状态的Id
     */
    private long sendId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否删除
     */
    private boolean isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
