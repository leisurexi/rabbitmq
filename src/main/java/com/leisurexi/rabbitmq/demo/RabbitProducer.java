package com.leisurexi.rabbitmq.demo;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author: leisurexi
 * @date: 2020-01-02 9:03 下午
 * @description: 生产者客户端示例代码
 * @since JDK 1.8
 */
public class RabbitProducer {

    private static final String EXCHANGE_NAME = "exchange_demo";
    private static final String ROUTING_KEY = "routingkey_demo";
    private static final String QUEUE_NAME = "queue_demo";
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername("guest");
        factory.setPassword("guest");
        try (
                //创建连接
                Connection connection = factory.newConnection();
                //创建通道
                Channel channel = connection.createChannel()
        ) {
            //创建一个持久化、非自动删除的交换器
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, null);
            //创建一个持久化、非独占、非自动删除的队列
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            //将交换器与队列通过路由键绑定
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
            //发送一条持久化消息
            String message = "hello, world!";
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        }
    }

}
