package com.ikhokha.techcheck.service;


import java.util.Map;

public interface CommentAnalyzerService {
    void analyze(String line, Map<String, Integer> resultsMap);
}
