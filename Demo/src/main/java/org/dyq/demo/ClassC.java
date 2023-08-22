package org.dyq.demo;

public class ClassC {
    public void runC() {
        try {
            Thread.sleep(1000);
            System.out.println("run c");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
