package info.yywang.micro.sms.entity;

import lombok.Data;

/**
 * Created by xiuyuhang on 16/8/8.
 */
@Data
public class DaYuSendResult extends SendResult {

    public DaYuSendResult(){}

    public DaYuSendResult(boolean success, String msg) {
        super.setSuccess(success);
        super.setMsg(msg);
    }

    public DaYuSendResult(boolean success, String msg, String errorCode) {
        super.setSuccess(success);
        super.setMsg(msg);
        this.errorCode = errorCode;
    }

    /**
     * 错误码
     */
    private String errorCode;
}
