package org.dyq.boot.http;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.http.server.action.Action;
import cn.hutool.json.JSONUtil;
import org.dyq.boot.PrintUtil;
import org.dyq.common.TransferMessage;

import java.io.IOException;

public class ClientMessageHandler implements Action {

    @Override
    public void doAction(HttpServerRequest request, HttpServerResponse response) throws IOException {
        String body = request.getBody();
        TransferMessage message = JSONUtil.toBean(body, TransferMessage.class);
        switch (message.level.toLowerCase()) {
            case "info":
                PrintUtil.infoLine(message.body);
                break;
            case "warn":
                PrintUtil.warnLine(message.body);
                break;
            case "error":
                PrintUtil.errorLine(message.body);
                break;
            case "success":
                PrintUtil.successLine(message.body);
                break;
        }
        response.send(200);
    }
}
