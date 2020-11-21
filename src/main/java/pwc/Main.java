package pwc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Main {

	public static void main(String[] args) throws Exception {
		File f = new File("file.txt");
		Map<String, Integer> wordCount = new HashMap<String, Integer>();
		
		int numProc = Runtime.getRuntime().availableProcessors();
		ExecutorService es = Executors.newFixedThreadPool(numProc * 2);
		ExecutorCompletionService<Map<String, Integer>> ecs = new ExecutorCompletionService<Map<String, Integer>>(es);

		long taskCounter = Files.lines(f.toPath()).map(line -> ecs.submit(new Task(line))).count(); 
		es.shutdown();
		
		LongStream.rangeClosed(1, taskCounter).forEach(x -> {
			try {
				wordsMapIntoMap(wordCount, ecs.take().get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			} 
		});

		wordCount.entrySet().stream().map(e -> e.toString()).sorted().forEach(System.out::println);
		int count = wordCount.values().stream().reduce(Integer::sum).get();
		System.out.println("Total de palabras en archivo: " + count);
	}
	
	public static String[] splitTrimLine(String line) {
		return line.split("\\W+");
	}
	
	public static Map<String, Integer> wordsArrayIntoMap(Map<String, Integer> total, String[] words) {
		Stream.of(words).forEach(w -> total.merge(w, 1, Integer::sum));
		return total;
	}
	
	public static Map<String, Integer> wordsMapIntoMap(Map<String, Integer> total, Map<String, Integer> partial) {
		partial.keySet().stream().forEach(w -> total.merge(w, partial.get(w), Integer::sum));
		return total;
	}	
}
