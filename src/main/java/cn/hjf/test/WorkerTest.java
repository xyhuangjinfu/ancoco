package cn.hjf.test;

public class WorkerTest {

    public static void main(String[] args) {
        Worker worker = new Worker();
        worker.foo();

        new Worker1().foo();
    }
}
