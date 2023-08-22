package org.dyq.boot.http;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.http.server.action.Action;
import org.dyq.boot.PrintUtil;
import org.dyq.common.Settings;

import java.io.IOException;

public class HeartBeatHandler implements Action {
    public static volatile long lastHeartBeatTime = -1;


    @Override
    public void doAction(HttpServerRequest request, HttpServerResponse response) throws IOException {
        long now = System.currentTimeMillis();
        if (lastHeartBeatTime != -1) {
            if (now - lastHeartBeatTime >= Settings.HEART_BEAT_INTERVAL) {
                PrintUtil.errorLine("agent jvm error,exit");
                System.exit(-1);
            }
        }
        lastHeartBeatTime = System.currentTimeMillis();
        response.send(200);
    }
}
