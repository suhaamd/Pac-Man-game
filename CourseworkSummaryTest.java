package test.java.pacman.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Order(100)
class CourseworkSummaryTest {

    @Test
    @DisplayName("Coursework summary output")
    void printSummary() {
        long passed = TestProgressTracker.passedCount();
        double earned = TestProgressTracker.pointsEarned();
        System.out.printf("%d tests passed, %.0f/20 CW2 obtained (3 points for reports).%n",
                passed, earned);
    }
}
