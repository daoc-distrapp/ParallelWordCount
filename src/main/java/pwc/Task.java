package pwc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class Task implements Callable<Map<String, Integer>> {
	private String line;
	
	public Task(String line) {
		this.line = line;
	}
	
	@Override
	public Map<String, Integer> call() throws Exception {
		String[] words = Main.splitTrimLine(line);
		return Main.wordsArrayIntoMap(new HashMap<String, Integer>(), words);
	}

}
