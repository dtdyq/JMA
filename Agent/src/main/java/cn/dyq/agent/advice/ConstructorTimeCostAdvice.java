package cn.dyq.agent.advice;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Constructor;

public class ConstructorTimeCostAdvice {
    @Advice.OnMethodEnter
    static long onEnter(@Advice.Origin Constructor<?> method) {
        return System.currentTimeMillis();
    }

    @Advice.OnMethodExit
    static void onExit(@Advice.Enter long start, @Advice.Origin Constructor<?> method) {
        AdviceUtil.record(null,method,start,System.currentTimeMillis());
    }
}
