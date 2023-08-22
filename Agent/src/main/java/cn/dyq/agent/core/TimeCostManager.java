package cn.dyq.agent.core;

import cn.dyq.agent.http.HttpTransUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimeCostManager {
    private final Map<String, Map<Long, TimeCostChain>> backup = new HashMap<>();

    private ThreadLocal<TimeCostChain> localChain = new ThreadLocal<TimeCostChain>();
    private final Object printLock = new Object();

    private TimeCostManager() {

    }

    public static TimeCostManager getInstance() {
        return LH.INST;

    }

    public void addRecord(TimeCostRecord record) {
        TimeCostChain chain = localChain.get();
        if (chain == null) {
            localChain.set(new TimeCostChain());
            chain = localChain.get();
        }
        chain.recordList.add(record);
    }

    public void printAndRolling() {
        synchronized (printLock) {
            TimeCostChain chain = localChain.get();
            if (chain == null) {
                return;
            }
            String threadName = Thread.currentThread().getName();
            if (!backup.containsKey(threadName)) {
                backup.put(threadName, new HashMap<>());
            }
            TimeCostChain back = new TimeCostChain();
            back.recordList = chain.recordList;
            chain.recordList = new ArrayList<>();
            backup.get(threadName).put(System.currentTimeMillis(), back);
            HttpTransUtil.sendInfo("===========" + Thread.currentThread().getName() + "===========");
            back.recordList.forEach(HttpTransUtil::sendInfo);
            HttpTransUtil.sendInfo("===========chain end===============");
        }

    }

    private static final class LH {
        public static final TimeCostManager INST = new TimeCostManager();
    }
}
