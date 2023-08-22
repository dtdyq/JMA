package cn.dyq.agent.http;

import cn.dyq.agent.GlobalConfig;
import cn.dyq.agent.advice.ResetTransformer;
import cn.dyq.agent.advice.TimeCostTransformer;
import cn.hutool.http.HttpUtil;
import org.dyq.common.Settings;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class HttpClientThread implements Runnable {

    private volatile boolean succeed = false;

    public boolean succeed() {
        return succeed;
    }

    @Override
    public void run() {
        try {
            int flip = Settings.HEART_BEAT_INTERVAL / 1000 - 1;
            int count = flip;
            while (true) {
                Thread.sleep(1000);
                if (count == flip) {
                    count = 0;
                    if (checkBootExit()) {
                        break;
                    }
                }
                count++;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private boolean checkBootExit() throws UnmodifiableClassException {
        boolean exitThread;
        try {
            String resp = HttpUtil.get(String.format("http://%s:%d%s", Settings.HTTP_HOST, GlobalConfig.getInstance().getHttpPort(), "/heartbeat"));
            System.out.println(" res:" + resp);
            exitThread = resp == null;
        } catch (Throwable e) {
            exitThread = true;
        }
        if (exitThread) {
            Instrumentation instrumentation = GlobalConfig.getInstance().getInstrument();
            if (instrumentation != null) {
                TimeCostTransformer timeCostTransformer = GlobalConfig.getInstance().getTimeCostTransformer();
                if (timeCostTransformer != null) {
                    instrumentation.removeTransformer(timeCostTransformer);
                }
                if (GlobalConfig.getInstance().getFinalClassSet() != null) {
                    ResetTransformer resetTransformer = new ResetTransformer(timeCostTransformer);
                    instrumentation.addTransformer(resetTransformer, true);
                    instrumentation.retransformClasses(GlobalConfig.getInstance().getFinalClassSet().toArray(new Class[]{}));
                    instrumentation.removeTransformer(resetTransformer);
                }
            }
        }
        return exitThread;
    }

}
