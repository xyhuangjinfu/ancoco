package cn.hjf.test;

public class Worker {

//    public int add(int a, int b) {
//        if (a > 0) {
//            System.out.println("a>0");
//        } else {
//            System.out.println("a<0");
//        }
//        return a + b;
//    }

    public static void main(String[] args) {
        new Worker().foo();
    }

    public void foo() {
        int a = 2;
        int b = a + 3;
        System.out.println(b);
    }




}
