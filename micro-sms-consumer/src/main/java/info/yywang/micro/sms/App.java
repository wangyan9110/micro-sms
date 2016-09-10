package info.yywang.micro.sms;

import info.yywang.micro.common.exceptions.BizException;
import info.yywang.micro.sms.consumer.SmsConsumer;
import info.yywang.micro.sms.listener.SmsListener;
import info.yywang.micro.sms.utils.SpringContextUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author yanyan.wang
 * @date 2016-08-03 21:52
 */
public class App {

    public static void main(String[] args) {

        if (args.length > 0) {
            if ("start".equals(args[0])) {
                new ClassPathXmlApplicationContext("classpath*:config/spring/application-*.xml");
                SmsListener smsListener = (SmsListener) SpringContextUtils.getBean("smsListener");
                SmsConsumer.init(smsListener);
                try {
                    Object lock = new Object();
                    synchronized (lock) {
                        while (true) {
                            lock.wait();
                        }
                    }
                } catch (InterruptedException ex) {
                    throw new BizException(ex);
                }
            } else {
                System.exit(0);
            }
        }
    }

}
