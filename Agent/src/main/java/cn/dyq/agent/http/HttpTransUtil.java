package cn.dyq.agent.http;

import cn.dyq.agent.GlobalConfig;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.dyq.common.Settings;
import org.dyq.common.TransferMessage;

public class HttpTransUtil {
    public static void sendInfo(Object body) {
        send("info", body.toString());
    }

    public static void sendWarn(Object body) {
        send("warn", body.toString());
    }

    public static void sendError(Object body) {
        send("error", body.toString());
    }

    public static void sendSuccess(Object body) {
        send("success", body.toString());
    }

    private static void send(String level, String body) {
        String msg = JSONUtil.toJsonStr(new TransferMessage(level, body));
        try {
            HttpUtil.post(String.format("http://%s:%d%s", Settings.HTTP_HOST, GlobalConfig.getInstance().getHttpPort(), "/message"), msg);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static boolean checkHttpConnection() {
        boolean success = true;
        for (int i = 0; i < 3; i++) {
            try {
                String resp = HttpUtil.get(String.format("http://%s:%d/%s", Settings.HTTP_HOST, GlobalConfig.getInstance().getHttpPort(), "/heartbeat"));
                success = resp != null;
            } catch (Throwable e) {
                success = false;
            }
            if (success) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //
            }
        }
        return success;
    }
}
