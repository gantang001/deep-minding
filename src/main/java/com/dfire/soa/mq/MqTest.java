package com.dfire.soa.mq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.UUID;
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
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("c_test_spark_streaming");
        consumer.setAwaitTerminationWhenShutdown(3000);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(TOPIC_TEST, TAG_TEST);
        new Thread(() -> {
            DefaultMQProducer producer = new DefaultMQProducer(UUID.randomUUID().toString());
            try {
                producer.start();
                for (; ; ) {
                    Message message = new Message(TOPIC_TEST, TAG_TEST, UUID.randomUUID().toString().getBytes());
                    producer.send(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        final AtomicLong lastReceivedMills = new AtomicLong(System.currentTimeMillis());

        //final AtomicLong consumeTimes = new AtomicLong(0);

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            public ConsumeConcurrentlyStatus consumeMessage(final List<MessageExt> msgs,
                                                            final ConsumeConcurrentlyContext context) {
                System.out.println("Received" + msgs.get(0).getTags() + "messages !");

                lastReceivedMills.set(System.currentTimeMillis());

                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
//        DefaultMQPushConsumer consumer2 = new DefaultMQPushConsumer("S_fundmng_demo_produce2222r");
//        consumer2.subscribe("brand_config_down",// topic
//                TAG_TEST);
//        consumer2.registerMessageListener(new MessageListenerConcurrently() {
//            @Override
//            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//                System.out.println("Received" + msgs.get(0).getTopic()+ "messages !");
//
//                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//            }
//        });
//        consumer2.start();
        consumer.start();
    }
}
