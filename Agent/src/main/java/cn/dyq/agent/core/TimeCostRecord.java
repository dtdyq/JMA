package cn.dyq.agent.core;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TimeCostRecord {
    public MethodIdentifier method;
    public List<StackTraceElement> callTree = new ArrayList<>();
    public long count;//调用次数
    public List<CostPair> costs = new ArrayList<>();

}
