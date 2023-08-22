package org.dyq.demo;

public class TestMain {
    public static void main(String[] args) throws ClassNotFoundException {

        Thread.currentThread().setName("thread-111");

//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                Thread.currentThread().setName("thread-222");
//                while (true) {
//                    org.dyq.demo.ClassA classA = new org.dyq.demo.ClassA();
//                    classA.runA();
//                    System.out.println("------------");
//                }
//            }
//        }).start();
        while (true) {
            ClassA classA = new ClassA();
            classA.runA();
            System.out.println("------------");
        }
    }
}
