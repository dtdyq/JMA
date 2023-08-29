package cn.dyq.agent.core;

import cn.dyq.agent.http.HttpTransUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class TimeCostManager {
    private final Map<String, Map<Long, TimeCostChain>> backup = new HashMap<>();

    private ConcurrentHashMap<String, TimeCostChain> costMap = new ConcurrentHashMap<>();
    private final Object printLock = new Object();

    private TimeCostManager() {

    }

    public static TimeCostManager getInstance() {
        return LH.INST;

    }

    public void mergeRecord(TimeCostRecord record) {
        String key = Thread.currentThread().getName();
        TimeCostChain chain = costMap.getOrDefault(key, null);
        if (chain == null) {
            costMap.put(key, new TimeCostChain());
            chain = costMap.get(key);
        }
        boolean merged = false;
        for (TimeCostRecord costRecord : chain.recordList) {
            if (methodAndTraceEq(record, costRecord)) {
                costRecord.costs.addAll(record.costs);
                costRecord.count += 1;
                merged = true;
                break;
            }
        }
        if (!merged) {
            chain.recordList.add(record);
        }
    }

    private boolean methodAndTraceEq(TimeCostRecord record, TimeCostRecord costRecord) {
        if (!record.method.equals(costRecord.method)) {
            return false;
        }
        if (record.callTree.size() != costRecord.callTree.size()) {
            return false;
        }
        for (int i = 0; i < record.callTree.size(); i++) {
            if (!record.callTree.get(i).equals(costRecord.callTree.get(i))) {
                return false;
            }
        }
        return true;
    }

    public void printAndRolling() {
        synchronized (printLock) {
            costMap.forEach(new BiConsumer<String, TimeCostChain>() {
                @Override
                public void accept(String threadName, TimeCostChain timeCostChain) {
                    if (!backup.containsKey(threadName)) {
                        backup.put(threadName, new HashMap<>());
                    }
                    TimeCostChain back = new TimeCostChain();
                    back.recordList = timeCostChain.recordList;
                    timeCostChain.recordList = new ArrayList<>();
                    backup.get(threadName).put(System.currentTimeMillis(), back);
                    HttpTransUtil.sendInfo("===========" + Thread.currentThread().getName() + "===========");
                    List<StatisticInfo> infos = back.recordList.stream().map(r -> {
                        StatisticInfo info = new StatisticInfo();
                        for (CostPair cost : r.costs) {
                            long c = (cost.end - cost.begin);
                            info.totalCost += c;
                            if (c > info.maxCost) {
                                info.maxCost = c;
                            }
                            if (c < info.minCost) {
                                info.minCost = c;
                            }
                        }
                        info.avgCost = info.totalCost / r.count;
                        return info;
                    }).sorted(new Comparator<StatisticInfo>() {
                        @Override
                        public int compare(StatisticInfo o1, StatisticInfo o2) {
                            int c = Long.compare(o1.totalCost, o2.totalCost);
                            if (c != 0) {
                                return c;
                            }
                            c = Long.compare(o1.avgCost, o2.avgCost);
                            if (c != 0) {
                                return c;
                            }
                            c = Long.compare(o1.maxCost, o2.maxCost);
                            if (c != 0) {
                                return c;
                            }
                            return Long.compare(o1.minCost, o2.minCost);
                        }
                    }).collect(Collectors.toList());

                    long allCost = 0;
                    for (StatisticInfo info : infos) {
                        allCost += info.totalCost;
                    }
                    HttpTransUtil.sendInfo("TC\tAC\tMIN\tMAX\tCOUNT\tINFO");
                    for (StatisticInfo record : infos) {
                        String statistic = String.format("%d\t%d\t%d\t%d\t%d\t%s", record.totalCost, record.avgCost
                                , record.minCost, record.maxCost, record.record.count, record.record.method.className + "#" + record.record.method.methodName);
                        if ((float) record.totalCost / (float) allCost >= 0.4f) {
                            HttpTransUtil.sendWarn(statistic);
                        } else {
                            HttpTransUtil.sendInfo(statistic);
                        }
                    }
                    HttpTransUtil.sendInfo("===========statistic end===============");
                }
            });
        }

    }

    private static final class LH {
        public static final TimeCostManager INST = new TimeCostManager();
    }
}
