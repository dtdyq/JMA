package cn.dyq.agent;

import cn.dyq.agent.advice.TimeCostTransformer;
import cn.dyq.agent.hierarchy.CMethodInfo;
import cn.dyq.agent.hierarchy.MethodHierarchyUtil;
import cn.dyq.agent.http.HttpClientThread;
import cn.dyq.agent.http.HttpTransUtil;
import cn.hutool.json.JSONUtil;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.dyq.common.AgentParams;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class AgentMain {
    private static long version = 0;

    public static void premain(String args, Instrumentation inst) {
        System.out.println("agent pre main run");
    }

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        //inst.appendToSystemClassLoaderSearch(new JarFile("D:\\code\\java\\JMA\\target\\JMA-1.0-SNAPSHOT.jar"));


        if (!preCheckAndConfig(inst, args)) {
            return;
        }
        HttpClientThread clientThread = new HttpClientThread();
        Thread thread = new Thread(clientThread);
        thread.setDaemon(true);
        thread.start();

        Set<CMethodInfo> methods = MethodHierarchyUtil.readClassMethodHierarchy(GlobalConfig.getInstance().getEnterClazzName(), GlobalConfig.getInstance().getEnterMethod());
        List<Class<?>> allLoadedClassed = Arrays.stream((Class<?>[]) inst.getAllLoadedClasses()).collect(Collectors.toList());

        List<CMethodInfo> finalTransClasses = new ArrayList<>();
        Set<Class<?>> finalClassSet = new LinkedHashSet<>();
        methods.forEach(cMethodInfo -> allLoadedClassed.forEach(aClass -> {
            if (aClass.getName().equals(cMethodInfo.cClassName.replaceAll("/", "."))) {
                finalTransClasses.add(cMethodInfo);
                finalClassSet.add(aClass);
            }
        }));
        TimeCostTransformer timeCostTransformer = new TimeCostTransformer(finalTransClasses);
        GlobalConfig.getInstance().setTimeCostTransformer(timeCostTransformer).setInstrument(inst).setTransClassSet(finalClassSet);
        inst.addTransformer(timeCostTransformer, true);

        inst.retransformClasses(finalClassSet.toArray(new Class[]{}));
        HttpTransUtil.sendSuccess("-----tran finish------");
    }

    private static boolean preCheckAndConfig(Instrumentation inst, String args) {
        if (args == null) {
            throw new RuntimeException("args error:" + args + " [clazzPath methodName]");
        }
        AgentParams params = JSONUtil.toBean(args, AgentParams.class);

        GlobalConfig.getInstance().setEnterClazzName(params.clazzName.replaceAll("\\.", "/"));
        GlobalConfig.getInstance().setEnterMethod(params.method);
        GlobalConfig.getInstance().setHttpPort(params.port);
        if (!HttpTransUtil.checkHttpConnection()) {
            return false;
        }
        Class<?>[] classes = inst.getAllLoadedClasses();
        boolean loaded = false;
        for (Class<?> c : classes) {
            if (c.getName().equals(GlobalConfig.getInstance().getEnterClassName())) {
                loaded = true;
                break;
            }
        }
        if (!loaded) {
            throw new RuntimeException("class[" + GlobalConfig.getInstance().getEnterClassName() + "] not loaded");
        }
        return true;
    }

    public static class MyAdvice {
        @Advice.OnMethodEnter
        static void onEnter(@Advice.Origin Method method) {
            if (!method.getDeclaringClass().isInterface()) {
            }
        }

        @Advice.OnMethodExit
        static void onExit(@Advice.Origin Method method) {
            if (!method.getDeclaringClass().isInterface()) {
            }
        }
    }

    static class Interceptor {
        @RuntimeType
        public static void intercept(@AllArguments Object[] args, @Origin Method method, @SuperCall Callable<?> callable) throws Exception {
        }
    }
}
