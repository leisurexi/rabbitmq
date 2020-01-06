package com.leisurexi.rabbitmq.encapsulation;

import com.leisurexi.rabbitmq.util.IOUtil;
import com.rabbitmq.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

/**
 * @author: leisurexi
 * @date: 2020-01-06 9:38 下午
 * @description:
 * @since JDK 1.8
 */
@Slf4j
@Data
public class RmqEncapsulation {

    /**
     * 分片数，表示一个逻辑队列背后的实际队列数
     */
    private int subdivisionNum;
    private ConcurrentLinkedDeque<Message> blockingQueue;

    public RmqEncapsulation(int subdivisionNum) {
        this.subdivisionNum = subdivisionNum;
        this.blockingQueue = new ConcurrentLinkedDeque<>();
    }

    /**
     * 声明交换器
     */
    public void exchangeDeclare(Channel channel, String exchange, BuiltinExchangeType type, boolean durable, boolean autoDelete,
                                Map<String, Object> arguments) throws IOException {
        channel.exchangeDeclare(exchange, type, durable, autoDelete, arguments);
    }

    /**
     * 声明队列
     */
    public void queueDeclare(Channel channel, String queue, boolean durable, boolean exclusive, boolean autoDelete,
                             Map<String, Object> arguments) throws IOException {
        for (int i = 0; i < subdivisionNum; i++) {
            String queueName = queue + "_" + i;
            channel.queueDeclare(queueName, durable, exclusive, autoDelete, arguments);
        }
    }

    /**
     * 创建绑定关系
     */
    public void queueBind(Channel channel, String queue, String exchange, String routingKey, Map<String, Object> arguments) throws IOException {
        for (int i = 0; i < subdivisionNum; i++) {
            String rkName = routingKey + "_" + i;
            String queueName = queue + "_" + i;
            channel.queueBind(queueName, exchange, rkName, arguments);
        }
    }

    /**
     * 生产者发送消息封装
     */
    public void basicPublish(Channel channel, String exchange, String routingKey, boolean mandatory,
                             AMQP.BasicProperties props, byte[] body) throws IOException {
        //随机挑一个队列发送
        Random random = new Random();
        int index = random.nextInt(subdivisionNum);
        String rkName = routingKey + "_" + index;
        channel.basicPublish(exchange, rkName, mandatory, props, body);
    }

    /**
     * 指定要消费的队列
     */
    private void startConsume(Channel channel, String queue, boolean autoAck, String consumerTag,
                              ConcurrentLinkedDeque<Message> newBlockingQueue) throws IOException {
        for (int i = 0; i < subdivisionNum; i++) {
            String queueName = queue + "_" + i;
            channel.basicConsume(queueName, autoAck, consumerTag + i, new NewConsumer(channel, newBlockingQueue));
        }
    }

    /**
     * 消费者推模式消费实现
     */
    public void basicConsume(Channel channel, String queue, boolean autoAck, String consumerTag,
                             ConcurrentLinkedDeque<Message> newBlockingQueue, MsgCallBack msgCallBack) throws IOException {
        startConsume(channel, queue, autoAck, consumerTag, newBlockingQueue);
        while (true) {
            Message message = newBlockingQueue.peekFirst();
            if (message != null) {
                ConsumeStatus consumeStatus = msgCallBack.consumeMsg(message);
                newBlockingQueue.removeFirst();
                if (consumeStatus == ConsumeStatus.SUCCESS) {
                    channel.basicAck(message.getDeliveryTag(), false);
                } else {
                    channel.basicReject(message.getDeliveryTag(), false);
                }
            } else {
                try {
                    TimeUnit.MICROSECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class NewConsumer extends DefaultConsumer {
        private ConcurrentLinkedDeque<Message> newBlockingQueue;

        public NewConsumer(Channel channel, ConcurrentLinkedDeque<Message> newBlockingQueue) {
            super(channel);
            this.newBlockingQueue = newBlockingQueue;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            Message message = (Message) IOUtil.getObjectFromBytes(body);
            message.setDeliveryTag(envelope.getDeliveryTag());
//            log.info("consumerTag: {}", consumerTag);
//            log.info("envelope: {}", envelope);
//            log.info("properties: {}", properties);
//            log.info("message: {}", message);
            newBlockingQueue.addLast(message);
        }
    }

}
