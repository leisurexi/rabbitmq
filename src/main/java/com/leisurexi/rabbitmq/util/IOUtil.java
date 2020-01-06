package com.leisurexi.rabbitmq.util;

import com.alibaba.fastjson.JSON;
import com.leisurexi.rabbitmq.encapsulation.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author: leisurexi
 * @date: 2020-01-06 10:02 下午
 * @description:
 * @since JDK 1.8
 */
@Slf4j
public class IOUtil {

    /**
     * 对象转换为字节数组
     *
     * @param object
     * @return
     */
    public static byte[] getBytesFromObject(Object object) {
        if (object == null) {
            return null;
        }
        try (
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                ObjectOutputStream oo = new ObjectOutputStream(bo)
        ) {
            oo.writeObject(object);
            return bo.toByteArray();
        } catch (IOException e) {
            log.error("对象转换为字节数组失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 字节数组转换为对象
     * @param data
     * @return
     */
    public static Object getObjectFromBytes(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        try (
                ByteArrayInputStream bi = new ByteArrayInputStream(data);
                ObjectInputStream inputStream = new ObjectInputStream(bi)
        ) {
            return inputStream.readObject();
        } catch (Exception e) {
            log.error("字节数组转换为对象失败: {}", e.getMessage());
            return null;
        }
    }

}
