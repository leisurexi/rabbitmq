package com.leisurexi.rabbitmq.demo;

import com.leisurexi.rabbitmq.config.ChannelExecute;
import com.leisurexi.rabbitmq.config.RabbitMQConfig;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author: leisurexi
 * @date: 2020-01-02 9:03 下午
 * @description: 生产者客户端示例代码
 * @since JDK 1.8
 */
@Slf4j
public class RabbitProducer {

    private static final String EXCHANGE_NAME = "exchange.demo";
    private static final String ROUTING_KEY = "demo-routingKey";
    private static final String QUEUE_NAME = "queue.demo";

    /**
     * 设置好交换器和队列
     *
     * @param sendMessage
     */
    private static void execute(ChannelExecute sendMessage) {
        ConnectionFactory factory = RabbitMQConfig.createConnectionFactory();
        //创建连接
        Connection connection = null;
        //创建通道
        com.rabbitmq.client.Channel channel = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
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
//            String message = "hello, world!";
//            //deliveryMode设置为2，即消息会被持久化
//            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, false, false,
//                    MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            sendMessage.execute(channel);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                if (channel != null) {
                    channel.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 未设置交换器和队列
     *
     * @param sendMessage
     */
    private static void executeWithNoSetting(ChannelExecute sendMessage) {
        ConnectionFactory factory = RabbitMQConfig.createConnectionFactory();
        //创建连接
        Connection connection = null;
        //创建通道
        com.rabbitmq.client.Channel channel = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            sendMessage.execute(channel);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                if (channel != null) {
                    channel.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试mandatory参数
     */
    @Test
    public void test1() {
        execute(channel -> {
            //发送一条持久化消息
            String message = "mandatory test";
            /**
             * @param mandatory 设置为true时，交换器无法根据自身的类型和路由键找到一个符合条件的队列，那么RabbitMQ会调用
             *                  Basic.Return命令将消息返回给生产者。当mandatory参数为false时，出现上述情形，则消息直接被丢弃
             */
            channel.basicPublish(EXCHANGE_NAME, "", true,
                    MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());

            channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
                log.error("交换器无法根据自身类型找到符合条件的队列");
                log.info("replyCode: {}", replyCode);
                log.info("replyText: {}", replyText);
                log.info("exchange: {}", exchange);
                log.info("routingKey: {}", routingKey);
                log.info("properties: {}", properties.toString());
                log.info("body: {}", new String(body));
            });
        });
    }

    /**
     * 测试备份交换器
     * 备份交换器几种特殊情况:
     * 1. 如果设置的备份交换器不存在，客户端和RabbitMQ服务端都不会有异常出现，此时消息会丢失。
     * 2. 如果备份交换器没有绑定任何队列，客户端和RabbitMQ服务端都不会有异常出现，此时消息会丢失。
     * 3. 如果备份交换器没有任何匹配的队列，客户端和RabbitMQ服务端都不会有异常出现，此时消息会丢失。
     * 4. 如果备份交换器和mandatory参数一起使用，那么mandatory参数无效。
     */
    @Test
    public void test2() {
        executeWithNoSetting(channel -> {
            String myAeExchange = "myAeExchange";
            String myAeQueue = "myAeQueue";
            String normalExchange = "normalExchange";
            String normalQueue = "normalQueue";
            Map<String, Object> args = new HashMap<>(1);
            //通过声明交换器时声明一下参数来执行备份交换器
            args.put("alternate-exchange", myAeExchange);
            channel.exchangeDeclare(normalExchange, BuiltinExchangeType.DIRECT, true, false, args);
            channel.queueDeclare(normalQueue, true, false, false, null);
            channel.queueBind(normalQueue, normalExchange, "normalKey");
            //这里把备份交换器设置为 fanout 类型，因为消息被重新发送到备份交换器时的路由键和从生产者发出的路由键是一样的
            channel.exchangeDeclare(myAeExchange, BuiltinExchangeType.FANOUT, true, false, args);
            channel.queueDeclare(myAeQueue, true, false, false, null);
            channel.queueBind(myAeQueue, myAeExchange, "");
            //该消息会被正常发送至队列中
            channel.basicPublish(normalExchange, "normalKey",
                    MessageProperties.PERSISTENT_TEXT_PLAIN, "正常消息".getBytes());
            //因为routingKey不匹配，该消息会被丢入备份交换器中
            channel.basicPublish(normalExchange, "errorKey",
                    MessageProperties.PERSISTENT_TEXT_PLAIN, "routingKey错误消息".getBytes());
        });
    }

    /**
     * 测试设置队列和设置消息的过期时间
     */
    @Test
    public void test3() {
        executeWithNoSetting(channel -> {
            String exchange = "ttl.exchange";
            String queue = "ttl.queue";
            String routingKey = "ttl-routingKey";
            //设置队列中消息的过期时间
            Map<String, Object> args = new HashMap<>(1);
            args.put("x-message-ttl", 60000);
            channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, true);
            channel.queueDeclare(queue, true, false, false, args);
            channel.queueBind(queue, exchange, routingKey);
            //如果同时设置了消息的过期时间和队列中消息的过期时间，则以最短时间为准
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2) //持久化消息
                    .expiration("5000") //设置消息过期时间
                    .build();
            channel.basicPublish(exchange, routingKey, properties, "ttl-test-message".getBytes());
        });
    }

    /**
     * 测试死信队列
     * 消息变成死信一般是由于以下几种情况:
     * 1. 消息被拒绝(Basic.Reject/Basic.Nack)，并且设置requeue参数为false
     * 2. 消息过期
     * 3. 队列达到最大程度
     * <p>
     * 以下的示例不仅展示的是死信队列的用法，也是延迟队列的用法，对于queue.dlx这个死信队列来说，同样可以看做延迟队列。
     * 假设一个应用中需要将每条消息设置为10秒的延迟，生产者通过exchange.normal这个交换器将发送的消息存储在queue.normal
     * 这个队列中。消费者订阅的并非是queue.normal这个队列，而是queue.dlx这个队列。当消息从queue.normal这个队列中
     * 过期之后被存入queue.dlx这个队列中，消费者就恰巧消费到了延迟10秒的这条消息。
     */
    @Test
    public void test4() {
        String dlxExchange = "exchange.dlx";
        String dlxQueue = "queue.dlx";
        String normalExchange = "exchange.normal";
        String normalQueue = "queue.normal";
        String routingKey = "dlx-routingKey";
        Map<String, Object> args = new HashMap<>(3);
        args.put("x-message-ttl", 10000);
        args.put("x-dead-letter-exchange", dlxExchange);
        //加上以下参数配置可以为DLX指定路由键，如果没有特殊指定，则使用原队列的路由键
        args.put("x-dead-letter-routing-key", routingKey);
        executeWithNoSetting(channel -> {
            channel.exchangeDeclare(normalExchange, BuiltinExchangeType.FANOUT, true);
            channel.queueDeclare(normalQueue, true, false, false, args);
            channel.queueBind(normalQueue, normalExchange, "");

            channel.exchangeDeclare(dlxExchange, BuiltinExchangeType.DIRECT, true);
            channel.queueDeclare(dlxQueue, true, false, false, null);
            channel.queueBind(dlxQueue, dlxExchange, routingKey);

            /**
             * 生产者首先发送一条消息到exchange.normal交换器中，然后交换器顺利地存储到队列queue.normal中。由于队列queue.normal
             * 设置了过期时间为10s，在这10s内没有消费者消费这条消息，那么判定这条消息为过期。由于设置了DLX，过期之时，消息被丢给交换器
             * exchange.dlx中，这是找到与exchange.dlx匹配的队列queue.dlx，最后消息被存储在queue.dlx这个死信队列中。
             */
            channel.basicPublish(normalExchange, "",
                    MessageProperties.PERSISTENT_TEXT_PLAIN, "dlx-message".getBytes());
        });
    }

    /**
     * 测试优先级队列
     * 下面的代码中设置消息的优先级为5。默认为0，最该为队列设置的最大优先级。优先级高的消息可以被优先消费，这个也是有前提的:
     * 如果在消费者的消费速度大于生产者的速度且Broker中没有消息堆积的情况下，对发送消息设置优先级也就没有什么实际意义。因为
     * 生产者刚发送完一条消息就被消费者消费了，那么就相当于Broker中至多只有一条消息，对于单条消息来说优先级是没有什么意义的。
     */
    @Test
    public void test5() {
        String exchange = "exchange.priority";
        String queue = "queue.priority";
        executeWithNoSetting(channel -> {
            Map<String, Object> args = new HashMap<>(1);
            args.put("x-max-priority", 10);
            channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT, true);
            channel.queueDeclare(queue, true, false, false, args);
            channel.queueBind(queue, exchange, "");
            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder().deliveryMode(2);
            builder.priority(1);
            channel.basicPublish(exchange, "", builder.build(), "priority-message-1".getBytes());
            builder.priority(5);
            channel.basicPublish(exchange, "", builder.build(), "priority-message-5".getBytes());
        });
    }

    /**
     * 测试发送方确认
     */
    @Test
    public void test6() {
        String exchange = "exchange.confirm";
        String queue = "queue.confirm";
        RabbitMQConfig.execute(channel -> {
            channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, true);
            channel.queueDeclare(queue, true, false, false, null);
            channel.queueBind(queue, exchange, "");
            //将信道设置为 publisher confirm 模式
            channel.confirmSelect();
            channel.basicPublish(exchange, "", true,
                    MessageProperties.PERSISTENT_TEXT_PLAIN, "publisher confirm test".getBytes());
            channel.basicPublish(exchange, "adsad", true,
                    MessageProperties.PERSISTENT_TEXT_PLAIN, "publisher confirm test".getBytes());
            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    log.info("消息发送到交换器成功。deliveryTag: {}, multiple: {}", deliveryTag, multiple);
                }

                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    log.info("消息发送到交换器失败。deliveryTag: {}, multiple: {}", deliveryTag, multiple);
                }
            });
            channel.addReturnListener((replyCode, replyText, exchange1, routingKey, properties, body) -> {
                log.error("消息未成功发送到队列");
                log.info("replyCode: {}", replyCode);
                log.info("replyText: {}", replyText);
                log.info("exchange: {}", exchange);
                log.info("routingKey: {}", routingKey);
                log.info("properties: {}", properties.toString());
                log.info("body: {}", new String(body));
            });
        });
    }

}
