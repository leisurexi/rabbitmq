package com.leisurexi.rabbitmq.cluster;

import com.leisurexi.rabbitmq.config.RabbitMQConfig;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @Author: leisurexi
 * @Description:
 * @Date: 2020/1/13 09:29
 */
@Slf4j
public class ClusterConsumer {

    public static void main(String[] args) {
        RabbitMQConfig.execute(channel -> {
            //设置客户端最多接收未被ack的消息的个数
            channel.basicQos(64);
            Channel _channel = channel;
            DefaultConsumer consumer = new DefaultConsumer(_channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    log.info("recv message: {}", new String(body));
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    _channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            channel.basicConsume("queue.cluster", consumer);
        });
    }

}
