package com.leisurexi.rabbitmq.encapsulation;

/**
 * @author: leisurexi
 * @date: 2020-01-06 10:39 下午
 * @description:
 * @since JDK 1.8
 */
@FunctionalInterface
public interface MsgCallBack {

    ConsumeStatus consumeMsg(Message message);

}
