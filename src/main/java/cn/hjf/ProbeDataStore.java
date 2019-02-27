package cn.hjf;

import java.util.HashMap;
import java.util.Map;

public class ProbeDataStore {

	private static Map<String, boolean[]> sMap = new HashMap<>();

	public static boolean[] getProbeArray(String name, int probeCount) {
		System.out.println("ProbeDataStore getProbeArray : " + name + " , " + probeCount);
		boolean[] probeArray = sMap.get(name);
		if (probeArray == null) {
			probeArray = new boolean[probeCount];
			sMap.put(name, probeArray);
		}
		return probeArray;
	}
}
