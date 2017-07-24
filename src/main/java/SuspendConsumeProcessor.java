//
//
//import com.twodfire.async.message.client.consumer.ConsumerListenerForRm;import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author gantang
// * @Date 2017/5/22
// */
//public class SuspendConsumeProcessor {
//    private static final Logger logger = LoggerFactory.getLogger(SuspendConsumeProcessor.class);
//
//    private static ArrayBlockingQueue<ConsumerListenerForRm> consumerQueue = new ArrayBlockingQueue<ConsumerListenerForRm>(30);
//
//    private static ScheduledExecutorService executorService;
//
//    public static void appendSuspendQueue(ConsumerListenerForRm consumerListenerForRm) {
//        consumerQueue.add(consumerListenerForRm);
//        if (executorService == null) {
//            synchronized (SuspendConsumeProcessor.class) {
//                if (executorService == null) {
//                    executorService = Executors.newSingleThreadScheduledExecutor();
//                    if (consumerQueue.size() > 0) {
//                        executorService.scheduleAtFixedRate(new Runnable() {
//                            @Override
//                            public void run() {
//                                Boolean resume = StringUtils.equals(Boolean.FALSE.toString(), consumerQueue.iterator().next().getNamesrvSuspendConsumeVal());
//                                if (resume) {
//                                    while (!consumerQueue.isEmpty()) {
//                                        ConsumerListenerForRm consumer = consumerQueue.poll();
//                                        try {
//                                            consumer.start();
//                                            logger.info(consumer.getConsumerGroup() + " resume success!");
//                                        } catch (Exception e) {
//                                            logger.error("RocketMQ Consumer resume failed!" + consumer.getConsumerGroup());
//                                        }
//                                    }
//                                    consumerQueue.clear();
//                                    executorService.shutdown();
//                                    executorService = null;
//                                }
//                            }
//                        }, 2, 2, TimeUnit.MINUTES);
//                    }
//                }
//            }
//        }
//    }
//}
//private void checkSuspendConsume() {
//        if (suspend) {
//        consumer.shutdown();
//        } else {
//        try {
//        String suspendConsume = getNamesrvSuspendConsumeVal();
//        if (StringUtils.equals(Boolean.TRUE.toString(), suspendConsume)) {
//        suspend = true;
//        consumer.shutdown();
//        logger.info(consumer.getClientIP() + " suspend consume success!");
//        }
//        } catch (Exception e) {
//        logger.error("get suspend consume failed" + e);
//        }
//        }
//        }
//
//private static AtomicLong lastAcquireTime = new AtomicLong(System.currentTimeMillis() - MessageConfig.Interval);
//
//
//        String getNamesrvSuspendConsumeVal() {
//        String suspendConsume = null;
//        try {
//        if (System.currentTimeMillis() - lastAcquireTime.get() < MessageConfig.Interval) {
//        return Boolean.FALSE.toString();
//        }
//        String clientIp = consumer.getClientIP();
//        ClientConfig clientConfig = new ClientConfig();
//        clientConfig.setNamesrvAddr(namesrvAddr);
//        clientConfig.setInstanceName(String.valueOf(System.currentTimeMillis()));
//        String clientId = clientConfig.buildMQClientId();
//        MQClientInstance mqClientInstance = new MQClientInstance(clientConfig, new Long(System.currentTimeMillis() / 1000).intValue(), clientId);
//        mqClientInstance.start();
//        MQClientAPIImpl mqClientAPI = mqClientInstance.getMQClientAPIImpl();
//        suspendConsume = mqClientAPI.getKVConfigValue(MessageConfig.SUSPEND_CONSUME_NAMESPACE, clientIp, 3000);
//        mqClientInstance.shutdown();
//        } catch (Exception e) {
//        if (!(e instanceof MQClientException && ((MQClientException) e).getResponseCode() == 22)) {
//        logger.error("get suspend consume failed,reason:" + e);
//        }
//        } finally {
//        lastAcquireTime.set(System.currentTimeMillis());
//        }
//        return suspendConsume;
//        }