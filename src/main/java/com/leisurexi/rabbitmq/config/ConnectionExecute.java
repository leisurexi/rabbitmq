package com.leisurexi.rabbitmq.config;

import com.rabbitmq.client.Connection;

import java.io.IOException;

/**
 * @author: leisurexi
 * @date: 2020-01-05 3:16 下午
 * @description:
 * @since JDK 1.8
 */
@FunctionalInterface
public interface ConnectionExecute {

    void execute(Connection connection) throws IOException;

}
