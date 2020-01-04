package com.leisurexi.rabbitmq.config;

import java.io.IOException;

/**
 * @author: leisurexi
 * @date: 2020-01-04 12:41 下午
 * @description:
 * @since JDK 1.8
 */
@FunctionalInterface
public interface ChannelExecute {

    void execute(com.rabbitmq.client.Channel channel) throws IOException;

}
