package cn.dyq.agent.core;

import cn.dyq.agent.http.HttpTransUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class TimeCostManager {
    private final Map<String, Map<Long, TimeCostChain>> backup = new HashMap<>();

    private ConcurrentHashMap<String, TimeCostChain> costMap = new ConcurrentHashMap<>();
    private final Object printLock = new Object();

    private volatile boolean canRecord = false;
    private AtomicInteger seq = new AtomicInteger(0);

    private TimeCostManager() {

    }

    public static TimeCostManager getInstance() {
        return LH.INST;

    }

    public void canRecord(boolean b) {
        if (b) {
            seq.getAndAdd(1);
        }
        canRecord = b;
    }

    public synchronized void mergeRecord(TimeCostRecord record) {
        if (!canRecord) {
            return;
        }
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
                costRecord.count += record.count;
                merged = true;
                break;
            }
        }
        if (!merged) {
            chain.recordList.add(record);
        }
    }

    private boolean methodAndTraceEq(TimeCostRecord record, TimeCostRecord costRecord) {
        return record.method.equals(costRecord.method);
    }

    public synchronized void printAndRolling() {
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
                if (back.recordList.isEmpty()) {
                    return;
                }
                HttpTransUtil.sendInfo("=======" + Thread.currentThread().getName() + "||seq:" + seq.get() + "===========");
                List<StatisticInfo> infos = back.recordList.stream().map(r -> {
                    StatisticInfo info = new StatisticInfo();
                    info.record = r;
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
                }).sorted(Comparator.comparingLong((StatisticInfo o) -> o.totalCost).thenComparingLong(o -> o.avgCost)
                        .thenComparingLong(o -> o.maxCost).thenComparingLong(o -> o.minCost)).collect(Collectors.toList());
                Collections.reverse(infos);
                long allCost = 0;
                for (StatisticInfo info : infos) {
                    allCost += info.totalCost;
                }
                HttpTransUtil.sendInfo("TC(ms)\tAC(ms)\tMIN(ms)\tMAX(ms)\tCOUNT\tINFO");
                Set<String> infoSet = new HashSet<>();
                for (StatisticInfo record : infos) {
                    String cName = record.record.method.className;
                    String infoStr = cName.substring(cName.lastIndexOf(".") + 1) + "#" + record.record.method.methodName;
                    if (!infoSet.contains(infoStr)) {
                        infoSet.add(infoStr);
                    } else {
                        infoStr = cName + "#" + record.record.method.methodName;
                        infoSet.add(infoStr);
                    }
                    String statistic = String.format("%d\t%d\t%d\t%d\t%d\t%s", record.totalCost, record.avgCost
                            , record.minCost, record.maxCost, record.record.count, infoStr);
                    if ((float) record.totalCost / (float) allCost >= 0.1f) {
                        HttpTransUtil.sendWarn(statistic);
                    } else {
                        HttpTransUtil.sendInfo(statistic);
                    }
                }
                HttpTransUtil.sendInfo("===========statistic end===============");
            }
        });

    }

    private static final class LH {
        public static final TimeCostManager INST = new TimeCostManager();
    }
}
