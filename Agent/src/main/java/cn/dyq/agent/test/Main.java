package cn.dyq.agent.test;

import cn.dyq.agent.advice.AdviceUtil;

public class Main {
    public static void main(String[] args) {
        method1();
    }

    public static void method1() {
        method2();
    }

    public static void method2() {
        printStackTrace();
    }

    public static void printStackTrace() {
        new TestIn();
    }
    static class TestIn {
        public TestIn() {
            try {
                AdviceUtil.getCallStack(null,TestIn.class.getConstructor()).forEach(System.out::println);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}