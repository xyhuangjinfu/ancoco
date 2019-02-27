package cn.hjf.test;

import cn.hjf.ProbeDataStore;

public class Worker1 {

	public static void main(String[] args) {
		new Worker1().foo();
	}

	public void foo() {
		boolean[] probeArray = ProbeDataStore.getProbeArray("hello", 28);
		int a = 13;
		probeArray[0] = true;
		int b = a * 2;
		probeArray[1] = true;
		System.out.println(b);
		probeArray[2] = true;
	}

}
