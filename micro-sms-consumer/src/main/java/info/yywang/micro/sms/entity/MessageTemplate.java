package info.yywang.micro.sms.entity;

import lombok.Data;

import java.util.Date;

/**
 * 短信模板
 *
 * @author yanyan.wang
 * @date 2016-08-04 00:30
 */
@Data
public class MessageTemplate {

    /**
     * the id
     */
    private int id;

    /**
     * 大于模板id
     */
    private String daYuTemplateId;

    /**
     * 云信模板id
     */
    private String yxTemplateId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

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
