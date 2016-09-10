package info.yywang.micro.sms.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageExt;
import info.yywang.micro.sms.listener.SmsListener;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author yanyan.wang
 * @date 2016-08-04 00:46
 */
public class SmsConsumer {

    private final static DefaultMQPushConsumer smsConsumer = new DefaultMQPushConsumer("SMSConsumerGroup");

    private final static String CONFIG_PATH = "/data/appdatas/sms/config/smsConsumer.properties";

    private final static Logger logger = Logger.getLogger(SmsConsumer.class);

    private final static String MQ_HOST = "192.168.0.119:9876";

    public static void init(final SmsListener smsListener) {
        try {
            Properties smsProperties = new Properties();
            String path = CONFIG_PATH;
            if (System.getProperty("os.name").toLowerCase().contains("window")) {
                path = "C:" + path;
            }
            smsProperties.load(new FileInputStream(path));
            String namesrvAddr = smsProperties.getProperty("sms.namesrvaddr", MQ_HOST);
            String consumeThreadMin = smsProperties.getProperty("sms.consumeThreadMin", "20");
            String consumeThreadMax = smsProperties.getProperty("sms.consumeThreadMax", "64");
            String adjustThreadPoolNumsThreshold = smsProperties.getProperty("sms.adjustThreadPoolNumsThreshold", "100000");
            String consumeConcurrentlyMaxSpan = smsProperties.getProperty("sms.consumeConcurrentlyMaxSpan", "2000");
            String pullThresholdForQueue = smsProperties.getProperty("sms.pullThresholdForQueue", "1000");
            String instanceName = smsProperties.getProperty("sms.instancename", "Consumer");
            String subscribeTags = smsProperties.getProperty("sms.subscribeTags", "*");

            smsConsumer.setInstanceName(instanceName);
            smsConsumer.setNamesrvAddr(namesrvAddr);
            smsConsumer.setConsumeThreadMax(Integer.parseInt(consumeThreadMax));
            smsConsumer.setConsumeThreadMin(Integer.parseInt(consumeThreadMin));
            smsConsumer.setAdjustThreadPoolNumsThreshold(Long.parseLong(adjustThreadPoolNumsThreshold));
            smsConsumer.setConsumeConcurrentlyMaxSpan(Integer.parseInt(consumeConcurrentlyMaxSpan));
            smsConsumer.setPullThresholdForQueue(Integer.parseInt(pullThresholdForQueue));

            smsConsumer.subscribe(SmsConstant.TOPIC, subscribeTags);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }

        smsConsumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt messageExt = msgs.get(0);
                if (messageExt.getTopic().equals(SmsConstant.TOPIC)) {
                    if (messageExt.getTags().equals(SmsConstant.TEMPLTAG)) {
                        Map<String, Object> body = JSON.parseObject(new String(messageExt.getBody()));
                        smsListener.sendMsg(messageExt.getMsgId(), body.get("phone") + "", (Integer) body.get("templateId"), (Map<String, String>) body.get("params"));
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        try {
            smsConsumer.start();
        } catch (MQClientException e) {
            logger.error(e.getErrorMessage(), e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                smsConsumer.shutdown();
            }
        }));
    }
}
