package com.leisurexi.rabbitmq.demo;

import com.leisurexi.rabbitmq.config.RabbitMQConfig;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

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

    @Test
    public void test1() {
        RabbitMQConfig.execute(channel -> {
            //设置客户端最多接收未被ack的消息的个数
            /**
             * RabbitMQ会保存一个消费者列表，每发送一条消息都会为对应的消费者计数，如果达到了所设定的上限，那么
             * RabbitMQ就不会向这个消费者再发送任何消息。直到消费者确认了某条消息之后，RabbitMQ将相应的计数减1，
             * 之后消费者可以继续接收消息，直到再次到达计数上限。这种机制可以类比于TCP/IP中的"滑动窗口"。
             */
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
            channel.basicConsume("queue.confirm", consumer);
        });
    }

}
