package com.dfire.soa.mq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author gantang
 * @Date 2017/6/3
 */
public class MqTest {

    private static final String TOPIC_TEST = "cash";
    private static final String TAG_TEST = "tag1";
    public static void main(String[] args) throws MQClientException {
        System.setProperty(MixAll.NAMESRV_ADDR_PROPERTY, "mq101.2dfire-daily.com:9876;mq102.2dfire-daily.com:9876");
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("S_fundmng_demo_producer");
        consumer.setAwaitTerminationWhenShutdown(3000);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.subscribe(TOPIC_TEST, TAG_TEST);

        final AtomicLong lastReceivedMills = new AtomicLong(System.currentTimeMillis());

        final AtomicLong consumeTimes = new AtomicLong(0);

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            public ConsumeConcurrentlyStatus consumeMessage(final List<MessageExt> msgs,
                                                            final ConsumeConcurrentlyContext context) {
                System.out.println("Received" + msgs.get(0).getTopic() + "messages !");

                lastReceivedMills.set(System.currentTimeMillis());

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        DefaultMQPushConsumer consumer2 = new DefaultMQPushConsumer("S_fundmng_demo_produce2222r");
        consumer2.subscribe("brand_config_down",// topic
                TAG_TEST);
        consumer2.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.println("Received" + msgs.get(0).getTopic()+ "messages !");

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer2.start();
        consumer.start();
    }
}
