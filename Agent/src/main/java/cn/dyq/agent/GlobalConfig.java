package cn.dyq.agent;

import cn.dyq.agent.advice.TimeCostTransformer;

import java.lang.instrument.Instrumentation;
import java.util.Set;

public class GlobalConfig {
    private String enterClazzName; // com/test/Clas
    private String enterMethod;
    private String enterClassName; // com.test.Clas
    private TimeCostTransformer tct;
    private Instrumentation inst;
    private Set<Class<?>> finalClassSet;
    private int httpPort;

    public String getEnterClazzName() {
        return enterClazzName;
    }

    public GlobalConfig setEnterClazzName(String enterClazzName) {
        this.enterClazzName = enterClazzName;
        enterClassName = enterClazzName.replaceAll("/", ".");
        return this;
    }

    public String getEnterMethod() {
        return enterMethod;
    }

    public GlobalConfig setEnterMethod(String enterMethod) {
        this.enterMethod = enterMethod;
        return this;
    }

    private GlobalConfig() {

    }

    public static GlobalConfig getInstance() {
        return LH.INST;
    }

    public String getEnterClassName() {
        return enterClassName;
    }

    public GlobalConfig setEnterClassName(String enterClassName) {
        this.enterClassName = enterClassName;
        return this;
    }

    public GlobalConfig setTimeCostTransformer(TimeCostTransformer timeCostTransformer) {
        this.tct = timeCostTransformer;
        return this;
    }

    public TimeCostTransformer getTimeCostTransformer() {
        return this.tct;
    }

    public GlobalConfig setInstrument(Instrumentation inst) {
        this.inst = inst;
        return this;
    }

    public Instrumentation getInstrument() {
        return this.inst;
    }

    public void setTransClassSet(Set<Class<?>> finalClassSet) {
        this.finalClassSet = finalClassSet;
    }

    public Set<Class<?>> getFinalClassSet() {
        return finalClassSet;
    }

    public GlobalConfig setHttpPort(int port) {
        this.httpPort = port;
        return this;
    }

    public int getHttpPort() {
        return httpPort;
    }


    private static final class LH {
        public static final GlobalConfig INST = new GlobalConfig();
    }
}
