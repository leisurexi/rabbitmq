package com.leisurexi.rabbitmq.rpc;

import com.leisurexi.rabbitmq.config.RabbitMQConfig;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

/**
 * @author: leisurexi
 * @date: 2020-01-04 8:26 下午
 * @description: 用RabbitMQ实现RPC调用，客户端示例
 * @since JDK 1.8
 */
@Slf4j
public class RPCClient {

    private static final String RPC_QUEUE_NAME = "queue.rpc";
    private static final String REPLY_QUEUE_NAME = "queue.reply";

    public static void main(String[] args) {
        RabbitMQConfig.execute(channel -> {
            String message = "30";
            String corrId = UUID.randomUUID().toString();
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .correlationId(corrId)
                    .replyTo(channel.queueDeclare().getQueue())
                    .build();
            channel.basicPublish("", RPC_QUEUE_NAME, properties, message.getBytes());
            log.info(channel.queueDeclare().getQueue());
            channel.basicConsume(REPLY_QUEUE_NAME, true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    if (properties.getCorrelationId().equals(corrId)) {
                        String response = new String(body);
                        log.info("来自服务端的响应: {}", response);
                    }
                }
            });
        });
    }

}
