package info.yywang.micro.sms.entity;

import lombok.Data;

/**
 * Created by xiuyuhang on 16/8/8.
 */
@Data
public class SendResult {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 信息
     */
    private String msg;

}
