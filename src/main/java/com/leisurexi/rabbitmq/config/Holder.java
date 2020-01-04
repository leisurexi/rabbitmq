package com.leisurexi.rabbitmq.config;

import lombok.Data;

/**
 * @author: leisurexi
 * @date: 2020-01-04 8:15 下午
 * @description:
 * @since JDK 1.8
 */
@Data
public class Holder<T> {

    private T data;

}
