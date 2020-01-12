package com.leisurexi.rabbitmq.cluster;

import com.leisurexi.rabbitmq.config.RabbitMQConfig;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: leisurexi
 * @date: 2020-01-12 11:21 下午
 * @description:
 * @since JDK 1.8
 */
@Slf4j
public class ClusterProducer {

    public static void main(String[] args) {
        String exchange = "exchange.cluster";
        String queue = "queue.cluster";
        String routingKey = "cluster";
        RabbitMQConfig.execute(channel -> {
            channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, true);
            channel.queueDeclare(queue, true, false, false, null);
            channel.queueBind(queue, exchange, routingKey);
            for (int i = 0; i < 100; i++) {
                String msg = "集群测试消息-" + i;
                channel.basicPublish(exchange, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
            }
        });
    }

}
