package com.leisurexi.rabbitmq.encapsulation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: leisurexi
 * @date: 2020-01-06 9:57 下午
 * @description:
 * @since JDK 1.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message implements Serializable {

    private static final long serialVersionUID = 153573246437431186L;

    private long msgSeq;
    private String msgBody;
    private long deliveryTag;

}
