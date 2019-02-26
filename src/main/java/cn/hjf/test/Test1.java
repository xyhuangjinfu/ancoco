package cn.hjf.test;

public class Test1 {

	public static void main(String[] args) {
		Test1 test1 = new Test1();
		test1.add(2, 4);
		System.out.println(test1.xx(1,2));
	}

	private void add(int a, long b) {
		int c = 10;

		int d = a + c;

		System.out.println(b + d);
	}

	private long xx(long a, long b) {
		return a + b;
	}
}
