package org.dyq.common;

public class AgentParams {
    public int port;
    public String method;
    public String clazzName;

    public AgentParams() {
    }

    public AgentParams(int port, String method, String clazzName) {
        this.port = port;
        this.method = method;
        this.clazzName = clazzName;
    }

    public int getPort() {
        return port;
    }

    public AgentParams setPort(int port) {
        this.port = port;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public AgentParams setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getClazzName() {
        return clazzName;
    }

    public AgentParams setClazzName(String clazzName) {
        this.clazzName = clazzName;
        return this;
    }
}
