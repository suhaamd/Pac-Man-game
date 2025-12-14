package test.java.pacman.tests;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

final class TestProgressTracker {
    private static final Map<String, Boolean> RESULTS = new LinkedHashMap<>();
    private static final Map<String, Double> POINTS = new LinkedHashMap<>();
    private static final Map<String, Double> METRICS = new HashMap<>();
    private static final Map<String, Object> OBJECTS = new HashMap<>();

    private TestProgressTracker() {
    }

    static void record(String label, boolean pass, double points) {
        RESULTS.put(label, pass);
        POINTS.put(label, points);
    }

    static void storeMetric(String key, double value) {
        METRICS.put(key, value);
    }

    static double getMetric(String key) {
        return METRICS.getOrDefault(key, Double.NaN);
    }

    static void storeObject(String key, Object value) {
        OBJECTS.put(key, value);
    }

    static Object getObject(String key) {
        return OBJECTS.get(key);
    }

    static long passedCount() {
        return RESULTS.values().stream().filter(Boolean::booleanValue).count();
    }

    static double totalPoints() {
        return POINTS.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    static double pointsEarned() {
        return RESULTS.entrySet().stream()
                .filter(entry -> entry.getValue())
                .mapToDouble(entry -> POINTS.getOrDefault(entry.getKey(), 0.0))
                .sum();
    }

    static void singleDivider() {
        System.out.println("--------------------------------------------------");
    }

    static void doubleDivider() {
        System.out.println("==================================================");
    }
}
