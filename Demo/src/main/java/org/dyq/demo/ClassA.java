package org.dyq.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassA {
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    public void runA() {
        runA1();
        ClassB classB = new ClassB();
        classB.runB1();
        classB.runB2();
        classB.runB1();
        classB.runB2();
        classB.runB1();
        classB.runB1();
    }

    public void runA1() {
        System.out.println("run a1");
    }


}
