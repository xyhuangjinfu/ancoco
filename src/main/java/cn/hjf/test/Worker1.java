package cn.hjf.test;

import cn.hjf.ProbeDataStore;

public class Worker1 {

	public static void main(String[] args) {
		new Worker1().foo();
	}

	public void foo() {
		boolean[] probeArray = ProbeDataStore.getProbeArray("hello", 28);
		int a = 13;
		int b = a * 2;
		System.out.println(b);
	}

}
