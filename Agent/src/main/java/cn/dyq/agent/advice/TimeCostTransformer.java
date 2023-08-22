package cn.dyq.agent.advice;


import cn.dyq.agent.hierarchy.CMethodInfo;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TimeCostTransformer implements ClassFileTransformer {

    public Map<String, byte[]> originalClassBytesMap = new HashMap<>();

    private List<CMethodInfo> methods = new ArrayList<>();

    public TimeCostTransformer(List<CMethodInfo> methods) {
        this.methods = methods;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!methods.stream().map(c -> c.cClassName).collect(Collectors.toList()).contains(className)) {
            return null;
        }

        try {
            if (className.startsWith("java") || className.startsWith("com/sun")) {
                return null;
            }
            List<String> toBeTransMethodNames = methods.stream().filter(c -> c.cClassName.equals(className)).map(c -> c.methodName).collect(Collectors.toList());

            try (DynamicType.Unloaded<?> i = new ByteBuddy().redefine(classBeingRedefined).visit(Advice.to(MethodTimeCostAdvice.class).on(target -> {
                String name = target.getName();
                return toBeTransMethodNames.contains(name) && !target.isNative();
            })).make()) {
                originalClassBytesMap.put(className, classfileBuffer);
                return i.getBytes();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
