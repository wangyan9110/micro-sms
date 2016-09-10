package info.yywang.micro.sms.producer;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author yanyan.wang
 * @date 2016-08-03 20:41
 */
public class SmsProducer {

    private final static DefaultMQProducer smsProducer = new DefaultMQProducer("SMSProducerGroup");

    private final static String CONFIG_PATH = "/data/appdatas/sms/config/smsProducer.properties";

    private final static String MQ_HOST = "192.168.0.119:9876";

    static {
        try {
            Properties smsProperties = new Properties();
            String path = CONFIG_PATH;
            if (System.getProperty("os.name").toLowerCase().contains("window")) {
                path = "C:" + path;
            }
            smsProperties.load(new FileInputStream(path));
            String namesrvAddr = smsProperties.getProperty("sms.namesrvaddr", MQ_HOST);
            String defaultTopicQueueNums = smsProperties.getProperty("sms.defaultTopicQueueNums", "4");
            String instanceName = smsProperties.getProperty("sms.instancename", "Producer");
            String sendMsgTimeout = smsProperties.getProperty("sms.sendMsgTimeout", "3000");
            String retryTimesWhenSendFailed = smsProperties.getProperty("sms.retryTimesWhenSendFailed", "2");

            smsProducer.setNamesrvAddr(namesrvAddr);
            smsProducer.setDefaultTopicQueueNums(Integer.parseInt(defaultTopicQueueNums));
            smsProducer.setInstanceName(instanceName);
            smsProducer.setSendMsgTimeout(Integer.parseInt(sendMsgTimeout));
            smsProducer.setRetryTimesWhenSendFailed(Integer.parseInt(retryTimesWhenSendFailed));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            smsProducer.start();
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                smsProducer.shutdown();
            }
        }));
    }

    public static boolean sendMsg(String tag, String body) {
        Message message = new Message(SmsConstant.TOPIC, tag, body.getBytes());
        try {
            //不抛异常即认为放成功
            SendResult sendResult = smsProducer.send(message);
            if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
                return true;
            } else {
                return false;
            }
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        } catch (RemotingException e) {
            throw new RuntimeException(e);
        } catch (MQBrokerException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
