package info.yywang.micro.sms.enums;

/**
 * 短信状态
 *
 * @author yanyan.wang
 * @date 2016-08-04 00:25
 */
public enum MessageStatus {

    Sending(1, "发送中"),

    Success(2, "发送成功"),

    Failed(3, "发送失败");

    private int value;

    private String des;

    MessageStatus(int value, String des) {
        this.value = value;
        this.des = des;
    }

    public int getValue(){
        return value;
    }
}
