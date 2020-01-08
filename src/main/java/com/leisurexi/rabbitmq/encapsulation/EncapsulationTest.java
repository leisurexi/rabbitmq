package com.leisurexi.rabbitmq.encapsulation;

import com.leisurexi.rabbitmq.config.RabbitMQConfig;
import com.leisurexi.rabbitmq.util.IOUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author: leisurexi
 * @date: 2020-01-06 10:12 下午
 * @description:
 * @since JDK 1.8
 */
@Slf4j
public class EncapsulationTest {

    private static final String exchange = "exchange.encapsulation";
    private static final String queue = "queue.encapsulation";
    private static final String routingKey = "rk";

    @Test
    public void product() {
        RabbitMQConfig.executeConnection(connection -> {
            RmqEncapsulation rmqEncapsulation = new RmqEncapsulation(4);
            Channel channel = connection.createChannel();
            rmqEncapsulation.exchangeDeclare(channel, exchange, BuiltinExchangeType.DIRECT,
                    true, false, null);
            rmqEncapsulation.queueDeclare(channel, queue, true, false, false, null);
            rmqEncapsulation.queueBind(channel, queue, exchange, routingKey, null);
            for (int i = 0; i < 100; i++) {
                Message message = Message.builder()
                        .msgSeq(i)
                        .msgBody("rabbitmq encapsulation")
                        .build();
                byte[] body = IOUtil.getBytesFromObject(message);
                rmqEncapsulation.basicPublish(channel, exchange, routingKey, false,
                        MessageProperties.PERSISTENT_TEXT_PLAIN, body);
            }
        });
    }

    @Test
    public void consume() {
        RabbitMQConfig.executeConnection(connection -> {
            RmqEncapsulation rmqEncapsulation = new RmqEncapsulation(4);
            Channel channel = connection.createChannel();
            channel.basicQos(64);
            rmqEncapsulation.basicConsume(channel, queue, false, "consumer_leisurexi", rmqEncapsulation.getBlockingQueue(),
                    message -> {
                        if (message != null) {
                            log.info(message.toString());
                            return ConsumeStatus.SUCCESS;
                        }
                        return ConsumeStatus.FALL;
                    });
        });
    }

}
