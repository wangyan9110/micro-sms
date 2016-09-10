package info.yywang.micro.sms.entity;

import lombok.Data;

/**
 * Created by xiuyuhang on 16/8/8.
 */
@Data
public class YunXinSendResult extends SendResult {

    public YunXinSendResult() {
    }

    public YunXinSendResult(boolean success, String msg) {
        super.setMsg(msg);
        super.setSuccess(success);
    }

    public YunXinSendResult(boolean success, String msg, int code) {
        super.setMsg(msg);
        super.setSuccess(success);
        this.code = code;
    }

    public YunXinSendResult(boolean success, String msg, int code, long sendId) {
        super.setMsg(msg);
        super.setSuccess(success);
        this.code = code;
        this.sendId = sendId;
    }

    /**
     * 状态码
     */
    private int code;

    /**
     * 发送信息的Id用来反查
     */
    private long sendId;
}
