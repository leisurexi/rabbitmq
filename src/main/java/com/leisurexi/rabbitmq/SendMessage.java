package com.leisurexi.rabbitmq;

import com.rabbitmq.client.Channel;

import java.io.IOException;

/**
 * @author: leisurexi
 * @date: 2020-01-04 12:41 下午
 * @description:
 * @since JDK 1.8
 */
@FunctionalInterface
public interface SendMessage {

    void execute(Channel channel) throws IOException;

}
