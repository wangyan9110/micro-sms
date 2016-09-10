package info.yywang.micro.sms.dao;

import info.yywang.micro.sms.entity.MessageTemplate;

/**
 * @author yanyan.wang
 * @date 2016-08-04 10:57
 */
public interface MessageTemplateDao {

    /**
     * 通过模板Id获取
     *
     * @return
     */
    MessageTemplate find(int id);

}
