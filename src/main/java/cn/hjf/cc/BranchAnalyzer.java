package cn.hjf.cc;

import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.Value;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class BranchAnalyzer<V extends Value> extends Analyzer {

	private Map<Integer, LinkedList<Integer>> mListMap = new HashMap<>();

	/**
	 * Constructs a new {@link Analyzer}.
	 *
	 * @param interpreter the interpreter to use to symbolically interpret the bytecode instructions.
	 */
	public BranchAnalyzer(Interpreter interpreter) {
		super(interpreter);
	}

	@Override
	protected void newControlFlowEdge(int insnIndex, int successorIndex) {
		LinkedList<Integer> list = mListMap.get(insnIndex);

		if (list == null) {
			list = new LinkedList();
			mListMap.put(insnIndex, list);
		}

		list.add(successorIndex);
	}

	public Map<Integer, LinkedList<Integer>> getBranchMap() {
		Iterator<Map.Entry<Integer, LinkedList<Integer>>> iterator = mListMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, LinkedList<Integer>> e = iterator.next();
			if (e.getValue().size() <= 1) {
				iterator.remove();
			}
		}
		return mListMap;
	}
}
