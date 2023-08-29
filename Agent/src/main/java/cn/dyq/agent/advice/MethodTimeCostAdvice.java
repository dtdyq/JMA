package cn.dyq.agent.advice;

import cn.dyq.agent.GlobalConfig;
import cn.dyq.agent.core.TimeCostManager;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class MethodTimeCostAdvice {
    @Advice.OnMethodEnter
    static long onEnter(@Advice.Origin Method method) {
        if (method.getName().equals(GlobalConfig.getInstance().getEnterMethod())
                && method.getDeclaringClass().getName().equals(GlobalConfig.getInstance().getEnterClassName())) {
            TimeCostManager.getInstance().canRecord(false);
        }
        return System.currentTimeMillis();
    }

    @Advice.OnMethodExit
    static void onExit(@Advice.Enter long start, @Advice.Origin Method method) {
        AdviceUtil.record(method, null, start, System.currentTimeMillis());
        if (method.getName().equals(GlobalConfig.getInstance().getEnterMethod())
                && method.getDeclaringClass().getName().equals(GlobalConfig.getInstance().getEnterClassName())) {
            TimeCostManager.getInstance().printAndRolling();
        }
    }
}
