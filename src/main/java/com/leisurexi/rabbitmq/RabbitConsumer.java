package com.leisurexi.rabbitmq;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author: leisurexi
 * @date: 2020-01-02 9:14 下午
 * @description: 消费者客户端示例
 * @since JDK 1.8
 */
@Slf4j
public class RabbitConsumer {

    private static final String QUEUE_NAME = "queue_demo";
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 5672;

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Address[] addresses = new Address[]{new Address(IP_ADDRESS, PORT)};
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        try (
                //创建连接
                Connection connection = factory.newConnection(addresses);
                //创建通道
                Channel channel = connection.createChannel()
        ) {
            //设置客户端最多接收未被ack的消息的个数
            channel.basicQos(64);
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    log.info("recv message: {}", new String(body));
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    channel.basicAck(envelope.getDeliveryTag(), false);
                    countDownLatch.countDown();
                }
            };
            channel.basicConsume(QUEUE_NAME, consumer);
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
