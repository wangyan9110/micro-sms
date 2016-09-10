package info.yywang.micro.sms.enums;

/**
 * @author yanyan.wang
 * @date 2016-08-04 01:10
 */
public enum MessageChannel {

    DaYu(1, "阿里大于"),

    YunXin(2, "网易云信");

    private int value;

    private String des;

    MessageChannel(int value, String des) {
        this.value = value;
        this.des = des;
    }

    public int getValue() {
        return value;
    }
}
