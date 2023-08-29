package cn.dyq.agent.advice;

import cn.dyq.agent.GlobalConfig;
import cn.dyq.agent.core.CostPair;
import cn.dyq.agent.core.MethodIdentifier;
import cn.dyq.agent.core.TimeCostManager;
import cn.dyq.agent.core.TimeCostRecord;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AdviceUtil {
    public static void record(Method method, Constructor<?> constructor, long start, long end) {
        MethodIdentifier identifier = new MethodIdentifier();
        List<StackTraceElement> callChain = new ArrayList<>();
        if (method == null) {
            callChain = getCallStack(null, constructor);
            identifier.className = constructor.getDeclaringClass().getName();
            identifier.methodName = constructor.getName();
            identifier.descriptor = Type.getConstructorDescriptor(constructor);
        } else {
            callChain = getCallStack(method, null);
            identifier.className = method.getDeclaringClass().getName();
            identifier.methodName = method.getName();
            identifier.descriptor = Type.getMethodDescriptor(method);
        }
        TimeCostRecord record = new TimeCostRecord();
        record.method = identifier;
        record.costs.add(CostPair.of(start, end));
        record.callTree = callChain;
        TimeCostManager.getInstance().mergeRecord(record);
    }

    public static List<StackTraceElement> getCallStack(Method method, Constructor<?> constructor) {
        String className = method != null ? method.getDeclaringClass().getName() : constructor.getDeclaringClass().getName();
        String methodName = method != null ? method.getName() : "<init>";
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        List<StackTraceElement> stack = new ArrayList<>();
        boolean enqueue = false;
        for (StackTraceElement element : stackTraceElements) {
            if (element.getClassName().equals(className) && element.getMethodName().equals(methodName)) {
                enqueue = true;
            }
            if (enqueue) {
                stack.add(0, element);
            }
            if (element.getClassName().equals(GlobalConfig.getInstance().getEnterClassName()) && element.getMethodName().equals(GlobalConfig.getInstance().getEnterMethod())) {
                break;
            }
        }
//        Collections.reverse(stack);
        return stack;
    }

    public static List<Class<?>> getAllSuperClasses(Class<?> clazz) {
        List<Class<?>> superClasses = new ArrayList<>();
        while (clazz.getSuperclass() != null) {
            clazz = clazz.getSuperclass();
            if (clazz.isInterface()) {
                continue;
            }
            superClasses.add(clazz);
        }
        return superClasses;
    }
}
