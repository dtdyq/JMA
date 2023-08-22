package cn.dyq.agent.advice;

import cn.dyq.agent.GlobalConfig;
import cn.dyq.agent.core.TimeCostManager;
import cn.dyq.agent.core.TimeCostRecord;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AdviceUtil {
    public static void record(Method method, Constructor<?> constructor, long start, long end) {
        TimeCostRecord record = new TimeCostRecord();
        if (method == null) {
            record.className = constructor.getDeclaringClass().getName();
            record.methodName = constructor.getName();
        } else {
            getCallStack(method);
            record.className = method.getDeclaringClass().getName();
            record.methodName = method.getName();
        }
        record.startTime = start;
        record.endTime = end;
        TimeCostManager.getInstance().addRecord(record);
    }

    public static void getCallStack(Method method) {
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
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
