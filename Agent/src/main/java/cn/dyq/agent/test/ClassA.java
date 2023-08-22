package cn.dyq.agent.test;

import java.util.function.Consumer;

public class ClassA {
    public void runA() {
        try {
            runA1();
        }catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void runA1() {
    }


    public void test() {
        Consumer<String> c = System.out::println;
    }
}
