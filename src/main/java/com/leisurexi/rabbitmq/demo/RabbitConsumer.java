package com.leisurexi.rabbitmq.demo;

import com.leisurexi.rabbitmq.config.RabbitMQConfig;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author: leisurexi
 * @date: 2020-01-02 9:14 下午
 * @description: 消费者客户端示例
 * @since JDK 1.8
 */
@Slf4j
public class RabbitConsumer {

    public static void main(String[] args) {
        ConnectionFactory factory = RabbitMQConfig.createConnectionFactory();
        //创建连接
        Connection connection = null;
        //创建通道
        Channel channel = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            //设置客户端最多接收未被ack的消息的个数
            channel.basicQos(64);
            Channel _channel = channel;
            DefaultConsumer consumer = new DefaultConsumer(_channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    log.info("recv message: {}", new String(body));
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    _channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            channel.basicConsume("queue.dlx", consumer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                if (channel != null) {
                    channel.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
