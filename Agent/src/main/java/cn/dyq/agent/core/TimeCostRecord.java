package cn.dyq.agent.core;

import cn.dyq.agent.DateUtil;

public class TimeCostRecord {
    public String className;
    public String methodName;
    public long startTime;
    public long endTime;

    @Override
    public String toString() {
        return String.format("record[%s:%s] callAt:%s cost:%sms", className, methodName, DateUtil.fromTimeToStandardStr(startTime), String.valueOf(endTime - startTime));
    }
}
