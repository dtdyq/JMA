package org.dyq.demo;

public class ClassB extends ClassC {
    public void runB1() {
        System.out.println("run b1");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        runB2();
        runB2();
    }


    public void runB2() {
        System.out.println("run b2");
        runC();
        try {
            Thread.sleep(1000);
            test();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void runTT() {
        System.out.println("run tt");
        runC();
        runC();
        try {
            Thread.sleep(1000);
            test();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void test() {
        System.out.println("test");
    }
}
