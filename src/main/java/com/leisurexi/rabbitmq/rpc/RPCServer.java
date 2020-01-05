package com.leisurexi.rabbitmq.rpc;

import com.leisurexi.rabbitmq.config.RabbitMQConfig;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author: leisurexi
 * @date: 2020-01-04 7:53 下午
 * @description: 用RabbitMQ实现RPC调用，服务端示例
 * @since JDK 1.8
 */
@Slf4j
public class RPCServer {

    private static final String RPC_QUEUE_NAME = "queue.rpc";
    private static final String REPLY_QUEUE_NAME = "queue.reply";

    public static void main(String[] args) {
        RabbitMQConfig.execute(channel -> {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.queueDeclare(REPLY_QUEUE_NAME, false, false, false, null);
            //该消费者在接收到队列里的消息但没有返回确认结果之前，队列不会将新的消息分发给该消费者
            channel.basicQos(1);
            log.info("Awaiting RPC requests");
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties().builder()
                            .correlationId(properties.getCorrelationId())
                            .build();
                    String response = "";
                    try {
                        String message = new String(body);
                        log.info("客户端发来的信息: {}", message);
                        response = "SUCCESS";
                    } finally {
                        try {
                            channel.basicPublish("", "reply.queue", replyProps, response.getBytes());
                            channel.basicAck(envelope.getDeliveryTag(), false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
        });
    }

}
