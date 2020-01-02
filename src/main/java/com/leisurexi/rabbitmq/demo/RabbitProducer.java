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
            /**
             * @param exchange   交换器名称
             * @param type       交换器类型
             * @param durable    是否开启持久化
             * @param autoDelete 当所有绑定队列都不在使用时，是否自动删除交换器 true：删除，false：不删除
             * @param internal   是否开启内置。如果设置为true，则表示是内置的交换器，客户端程序无法直接发送消息到这个
             *                   交换器中，只能通过交换器路由到交换器的这种方式
             * @param argument   其他一些结构化参数
             */
            //创建一个持久化、非自动删除的交换器
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, false, null);
            /**
             * @param queue      队列的名称
             * @param durable    设置是否持久化。为true则设置队列为持久化。持久化的队列会存盘，在服务器重启的时候可以保证消息不丢失
             * @param exclusive  设置是否排他。为true则设置队列为排他的。如果一个队列被声明为排他队列，该排他队列仅对首次声明他的连
             *                   接(Connection)可见的，同一个连接的不同通道(Channel)是可以同时访问同一连接创建的排他队列；"首次"
             *                   是指如果一个连接已经声明了一个排他队列，其他连接时不允许建立同名的排他队列的，这个与普通队列不同；
             *                   即使该队列是持久化的，一旦连接关闭或者客户端退出，该排他队列会被自动删除，这种队列适用于一个客户端同
             *                   时发送和读取消息的应用场景
             * @param autoDelete 设置是否自动删除。为true则设置队列为自动删除。自动删除的前提是: 至少有一个消费者连接到这个队列，之后
             *                   所有与这个队列连接的消费者都都断开时，才会自动删除。不能把这个参数错误地理解为: 当连接到此队列的所有
             *                   客户端断开时，这个队列自动删除，因为生产者客户端创建这个队列，或者没有消费者客户端与这个队列连接时，都
             *                   不会自动删除这个队列
             * @param arguments  设置队列的其他一些参数
             *
             * 注意: 生产者和消费者都能够使用queueDeclare来声明一个队列，但是如果消费者在同一个channel上订阅了另一个队列，就无法声明队列
             * 了。必须先取消订阅，然后将channel置为"传输"模式，之后才能声明队列
             */
            //创建一个持久化、非排他、非自动删除的队列
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            //将交换器与队列通过路由键绑定
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
            //发送一条持久化消息
            String message = "hello, world!";
            //deliveryMode设置为2，即消息会被持久化
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        }
    }

}
