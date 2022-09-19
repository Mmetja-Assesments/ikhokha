package com.ikhokha.techcheck;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Map<String, Integer> totalResults = new HashMap<>();

        Map<String, Integer> map = new ConcurrentHashMap<String, Integer>();

        File docPath = new File("docs");
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

        File[] commentFiles = docPath.listFiles((d, n) -> n.endsWith(".txt"));
        List<Map<String, Integer>> list = new ArrayList<>();

        for (File commentFile : commentFiles) {
            CommentAnalyzer commentAnalyzer = new CommentAnalyzer(commentFile);
            Future<Map<String, Integer>> future = executor.submit(commentAnalyzer);

            list.add(future.get());
        }

        executor.shutdown();
        System.out.println("RESULTS\n=======");

        for (Map<String, Integer> item : list) {
            addReportResults(item, totalResults);
        }
        totalResults.forEach((k, v) -> System.out.println(k + " : " + v));
    }

    /**
     * This method adds the result counts from a source map to the target map
     *
     * @param source the source map
     * @param target the target map
     */
    private static void addReportResults(Map<String, Integer> source, Map<String, Integer> target) {

        for (Map.Entry<String, Integer> entry : source.entrySet()) {
            target.putIfAbsent(entry.getKey(), 0);
            target.put(entry.getKey(), entry.getValue() + target.get(entry.getKey()));
        }
    }

}
