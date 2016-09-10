package info.yywang.micro.sms.dao;

import info.yywang.micro.sms.entity.MessageRecord;

/**
 * @author yanyan.wang
 * @date 2016-08-04 10:56
 */
public interface MessageRecordDao {

    int insert(MessageRecord messageRecord);

    void update(MessageRecord messageRecord);
}
