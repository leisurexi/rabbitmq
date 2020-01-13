package com.leisurexi.rabbitmq.cluster;

import com.leisurexi.rabbitmq.config.RabbitMQConfig;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/**
 * @author: leisurexi
 * @date: 2020-01-12 11:21 下午
 * @description:
 * @since JDK 1.8
 */
@Slf4j
public class ClusterProducer {

    public static void main(String[] args) throws IOException, TimeoutException {
        String exchange = "exchange.cluster";
        String queue = "queue.cluster";
        String routingKey = "cluster";
        ConnectionFactory factory = RabbitMQConfig.createConnectionFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        try {
            channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, true);
            channel.queueDeclare(queue, true, false, false, null);
            channel.queueBind(queue, exchange, routingKey);
            for (int i = 0; i < 1000; i++) {
                String msg = "集群测试消息-" + i;
                channel.basicPublish(exchange, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
            }
        } finally {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

}
