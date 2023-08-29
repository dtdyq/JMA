package org.dyq.boot.http;

import cn.hutool.http.HttpUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class HttpSvrUtil {
    public static void startSvr(int port) {
        PrintStream tmp = System.out;
        try {
            System.setOut(new PrintStream(new OutputStream() {
                @Override
                public void write(int b) {
                    // drop
                }
            }));
            HttpUtil.createServer(port)
                    .addAction("/heartbeat", new HeartBeatHandler())
                    .addAction("/message", new ClientMessageHandler())
                    .start();
        } finally {
            System.setOut(tmp);
        }

    }
}
