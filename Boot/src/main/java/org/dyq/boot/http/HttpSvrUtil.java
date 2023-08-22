package org.dyq.boot.http;

import cn.hutool.http.HttpUtil;

public class HttpSvrUtil {
    public static void startSvr(int port) {
        HttpUtil.createServer(port)
                .addAction("/heartbeat", new HeartBeatHandler())
                .addAction("/message", new ClientMessageHandler())
                .start();
    }
}
