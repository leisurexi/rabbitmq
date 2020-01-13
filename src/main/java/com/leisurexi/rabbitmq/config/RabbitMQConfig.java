package com.leisurexi.rabbitmq.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author: leisurexi
 * @date: 2020-01-04 7:55 下午
 * @description:
 * @since JDK 1.8
 */
public class RabbitMQConfig {

    public static final String IP_ADDRESS = "127.0.0.1";
    public static final int PORT = 5672;

    public static ConnectionFactory createConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername("user");
        factory.setPassword("bitnami");
        factory.setVirtualHost("leisurexi");
        return factory;
    }

    public static void execute(ChannelExecute channelExecute) {
        ConnectionFactory factory = createConnectionFactory();
        Connection connection = null;
        Channel channel = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channelExecute.execute(channel);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
//        finally {
//            try {
//                TimeUnit.SECONDS.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            try {
//                if (channel != null) {
//                    channel.close();
//                }
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    public static void executeConnection(ConnectionExecute connectionExecute) {
        ConnectionFactory factory = createConnectionFactory();
        Connection connection = null;
        try {
            connection = factory.newConnection();
            connectionExecute.execute(connection);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
