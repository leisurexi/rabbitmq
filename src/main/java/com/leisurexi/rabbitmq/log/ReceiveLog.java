package com.leisurexi.rabbitmq.log;

import com.leisurexi.rabbitmq.config.RabbitMQConfig;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author: leisurexi
 * @date: 2020-01-05 3:14 下午
 * @description: 接收RabbitMQ服务日志示例程序
 * @since JDK 1.8
 */
@Slf4j
public class ReceiveLog {

    public static void main(String[] args) {
        RabbitMQConfig.executeConnection(connection -> {
            Channel channelDebug = connection.createChannel();
            Channel channelInfo = connection.createChannel();
            Channel channelWarn = connection.createChannel();
            Channel channelError = connection.createChannel();

            channelDebug.basicConsume("queue.debug", false,
                    "DEBUG", new ConsumerThread(channelDebug));
            channelInfo.basicConsume("queue.info", false,
                    "INFO", new ConsumerThread(channelDebug));
            channelWarn.basicConsume("queue.warn", false,
                    "WARN", new ConsumerThread(channelDebug));
            channelError.basicConsume("queue.error", false,
                    "ERROR", new ConsumerThread(channelDebug));
        });
    }

    @Slf4j
    public static class ConsumerThread extends DefaultConsumer {

        public ConsumerThread(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            String logContent = new String(body);
            log.info("consumerTag: {}, 日志内容: {}", consumerTag, logContent);
            getChannel().basicAck(envelope.getDeliveryTag(), false);
        }
    }


}
