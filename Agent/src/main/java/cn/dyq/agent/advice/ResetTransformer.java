package cn.dyq.agent.advice;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class ResetTransformer implements ClassFileTransformer {
    private final TimeCostTransformer timeCostTransformer;

    public ResetTransformer(TimeCostTransformer timeCostTransformer) {
        this.timeCostTransformer = timeCostTransformer;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return timeCostTransformer.originalClassBytesMap.getOrDefault(className, null);
    }
}
