package com.leisurexi.rabbitmq.monitor;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: leisurexi
 * @date: 2020-01-05 8:13 下午
 * @description: RabbitMQ节点监控示例
 * @since JDK 1.8
 */
@Slf4j
public class RabbitMQMonitor {

    private static final String URL = "http://127.0.0.1:15672/";

    public static List<ClusterNode> getClusterData() {
        HttpResponse response = HttpRequest.get(URL + "api/nodes")
                .basicAuth("guest", "guest")
                .timeout(5000)
                .execute();
        String result = response.body();
        return parseClusters(result);
    }

    private static List<ClusterNode> parseClusters(String data) {
        JSONArray jsonArray = JSON.parseArray(data);
        List<ClusterNode> list = new ArrayList<>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ClusterNode clusterNode = ClusterNode.builder()
                    .diskFree(jsonObject.getLongValue("disk_free"))
                    .diskFreeLimit(jsonObject.getLongValue("disk_free_limit"))
                    .fdUsed(jsonObject.getLongValue("fd_used"))
                    .fdTotal(jsonObject.getLongValue("fd_total"))
                    .socketsUsed(jsonObject.getLongValue("sockets_used"))
                    .socketsTotal(jsonObject.getLongValue("sockets_total"))
                    .memoryUsed(jsonObject.getLongValue("memory_used"))
                    .memoryLimit(jsonObject.getLongValue("memory_limit"))
                    .procUsed(jsonObject.getLongValue("proc_used"))
                    .procTotal(jsonObject.getLongValue("proc_total"))
                    .build();
            list.add(clusterNode);
        }
        return list;
    }

    /**
     * 获取交换器信息
     *
     * @param vhost    命名空间
     * @param exchange 交换器名称
     * @return
     */
    public static Exchange getExchangeData(String vhost, String exchange) throws UnsupportedEncodingException {
        String url = URL + "api/exchanges/" + URLUtil.encode(vhost) + "/" + URLUtil.encode(exchange);
        HttpResponse response = HttpRequest.get(url)
                .basicAuth("guest", "guest")
                .timeout(5000)
                .execute();
        String result = response.body();
        return parseExchange(result);
    }

    private static Exchange parseExchange(String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        Exchange exchange = Exchange.builder()
                .publishInRate(jsonObject.getDoubleValue("publish_in_rate"))
                .publishIn(jsonObject.getLongValue("publish_in"))
                .publishOutRate(jsonObject.getDoubleValue("publish_out_rate"))
                .publishOut(jsonObject.getLongValue("publish_out"))
                .name(jsonObject.getString("name"))
                .vhost(jsonObject.getString("vhost"))
                .type(jsonObject.getString("type"))
                .durable(jsonObject.getBooleanValue("durable"))
                .autoDelete(jsonObject.getBooleanValue("auto_delete"))
                .internal(jsonObject.getBooleanValue("internal"))
                .arguments(jsonObject.getJSONObject("arguments").getInnerMap())
                .build();
        return exchange;
    }

    @Test
    public void test1() {
        List<ClusterNode> clusterData = getClusterData();
        log.info("节点信息: {}", clusterData);
    }

    @Test
    public void test2() throws UnsupportedEncodingException {
        Exchange exchange = getExchangeData("leisurexi", "exchange.normal");
        log.info("交换器信息: {}", exchange);
    }


}
