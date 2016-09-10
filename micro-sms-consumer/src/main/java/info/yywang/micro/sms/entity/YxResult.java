package info.yywang.micro.sms.entity;

import lombok.Data;

/**
 * Created by xiuyuhang on 16/8/8.
 */
@Data
public class YxResult {

    /**
     * 返回码
     */
    private int code;

    /**
     * 返回的消息体
     */
    private String msg;

    /**
     * 如果成功返回的sendId,用来反查短信发送状态
     */
    private long obj;

}
