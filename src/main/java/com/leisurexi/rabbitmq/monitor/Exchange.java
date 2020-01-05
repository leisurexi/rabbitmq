package com.leisurexi.rabbitmq.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author: leisurexi
 * @date: 2020-01-05 8:41 下午
 * @description:
 * @since JDK 1.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exchange {

    /**
     * 数据流入的速率
     */
    private double publishInRate;
    /**
     * 数据流入的总量
     */
    private long publishIn;
    /**
     * 数据流出的速率
     */
    private double publishOutRate;
    /**
     * 数据流出的总量
     */
    private long publishOut;
    private String name;
    private String vhost;
    private String type;
    private boolean durable;
    private boolean autoDelete;
    private boolean internal;
    private Map<String, Object> arguments;

}
