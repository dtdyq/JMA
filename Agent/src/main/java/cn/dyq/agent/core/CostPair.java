package cn.dyq.agent.core;

public class CostPair {
    public long begin;
    public long end;

    public static CostPair of(long begin, long end) {
        CostPair pair = new CostPair();
        pair.begin = begin;
        pair.end = end;
        return pair;
    }
}
