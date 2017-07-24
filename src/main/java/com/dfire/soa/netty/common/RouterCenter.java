package com.dfire.soa.netty.common;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author gantang
 * @Date 2017/7/22
 */
public class RouterCenter {

    private static Map<String, Channel> channelTable = new ConcurrentHashMap<>();

    public static List<Channel> getAllChannel() {
        return channelTable.entrySet().stream().map(Map.Entry::getValue).collect(toList());
    }

    public static Channel getSingle(String channelId) {
        return channelTable.get(channelId);
    }

    public static void put(String id, Channel channel) {
        channelTable.put(id, channel);
    }

    public static void remove(String id) {
        channelTable.remove(id);
    }
}
