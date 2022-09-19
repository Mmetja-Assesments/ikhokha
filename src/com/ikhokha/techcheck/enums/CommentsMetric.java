package com.ikhokha.techcheck.enums;

import java.util.Arrays;

public enum CommentsMetric {
    SHORTER_THAN_15("shorter"),
    MOVER_MENTIONS("mover"),
    SHAKER_MENTIONS("shaker"),
    QUESTION("?"),
    SPAM("spam");
    private final String metric;
    CommentsMetric(String metric) {
        this.metric = metric;
    }

    public String toString() {
        return this.metric;
    }
    public static CommentsMetric getByValue(String metric) {
        return Arrays.stream(CommentsMetric.values()).filter(v -> v.metric.equals(metric)).findAny().orElse(null);
    }

}
