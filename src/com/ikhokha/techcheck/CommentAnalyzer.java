package com.ikhokha.techcheck;

import com.ikhokha.techcheck.enums.CommentsMetric;
import com.ikhokha.techcheck.service.CommentAnalyzerService;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentAnalyzer implements Callable<Map<String, Integer>>, CommentAnalyzerService {

    private final File file;
    private final List<String> searchWords = Arrays.asList("shaker", "mover", "?", "url");
    private final String urlRegex = "(http://|https://){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+";

    public CommentAnalyzer(File file) {
        this.file = file;
    }

    /**
     * This method increments a counter by 1 for a match type on the countMap. Uninitialized keys will be set to 1
     *
     * @param countMap the map that keeps track of counts
     * @param key      the key for the value to increment
     */
    private void inOccurrence(Map<String, Integer> countMap, String key) {
        countMap.putIfAbsent(key, 0);
        countMap.put(key, countMap.get(key) + 1);
    }

    @Override
    public Map<String, Integer> call() throws Exception {

        Map<String, Integer> resultsMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                String finalLine = line;
                CompletableFuture.runAsync(() -> analyze(finalLine, resultsMap));
                CompletableFuture.runAsync(() -> analyze(urlRegex, finalLine, resultsMap));
                CompletableFuture.runAsync(() -> analyze(15, finalLine, resultsMap));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file.getAbsolutePath());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO Error processing file: " + file.getAbsolutePath());
            e.printStackTrace();
        }

        return resultsMap;
    }

    /**
     * This checks if a word exists in a specific string
     *
     * @param line       string value that is to be checked for a url
     * @param resultsMap a map in which the results will be stored
     */
    public void analyze(String line, Map<String, Integer> resultsMap) {
        for (String metric : searchWords) {
            if (line.toUpperCase().trim().contains(metric.toUpperCase().trim())) {
                inOccurrence(resultsMap, CommentsMetric.getByValue(metric).name());
            }
        }
    }

    /**
     * This checks if a string has less that a specific number of characters
     *
     * @param line       string value that is to be checked for a url
     * @param resultsMap a map in which the results will be stored
     * @param comparator the number which the string length will be checked against
     */
    public void analyze(Integer comparator, String line, Map<String, Integer> resultsMap) {
        if (line.length() < comparator) {
            inOccurrence(resultsMap, CommentsMetric.SHORTER_THAN_15.name());
        }
    }

    /**
     * This checks if a string contains a URL
     *
     * @param regex      string value of a regular expression
     * @param line       string value that is to be checked for a url
     * @param resultsMap a map in which the results will be stored
     */
    public void analyze(String regex, String line, Map<String, Integer> resultsMap) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            inOccurrence(resultsMap, CommentsMetric.SPAM.name());
        }
    }
}
