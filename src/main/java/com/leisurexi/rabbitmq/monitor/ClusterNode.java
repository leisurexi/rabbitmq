package com.leisurexi.rabbitmq.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: leisurexi
 * @date: 2020-01-05 8:15 下午
 * @description:
 * @since JDK 1.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClusterNode {

    /**
     * 磁盘空闲
     */
    private long diskFree;
    private long diskFreeLimit;
    /**
     * 句柄使用数
     */
    private long fdUsed;
    private long fdTotal;
    /**
     * Socket使用数
     */
    private long socketsUsed;
    private long socketsTotal;
    /**
     * 内存使用值
     */
    private long memoryUsed;
    private long memoryLimit;
    /**
     * Erlang进程使用数
     */
    private long procUsed;
    private long procTotal;

}
