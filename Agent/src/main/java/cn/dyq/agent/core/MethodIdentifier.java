package cn.dyq.agent.core;

import lombok.Data;

@Data
public class MethodIdentifier {
    public String className;
    public String methodName;
    public String descriptor;
    public String lineNumber;
}
