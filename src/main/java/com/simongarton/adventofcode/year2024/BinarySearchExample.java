package com.simongarton.adventofcode.year2024;

public class BinarySearchExample {

    public static void main(final String[] args) {

        final BinarySearchExample binarySearchExample = new BinarySearchExample();
        binarySearchExample.run();

    }

    double function(final double x) {
        return x * 2 + 1;
    }

    /**
     * Goal-seeking algorithm using binary search.
     *
     * @param target    The target value of the function.
     * @param low       The lower bound of the search range.
     * @param high      The upper bound of the search range.
     * @param tolerance The acceptable error margin for the result.
     * @return The value of x that makes f(x) close to the target.
     */
    public double goalSeek(final double target, double low, double high, final double tolerance) {
        double mid;
        while (high - low > tolerance) {
            mid = (low + high) / 2;
            final double fMid = this.function(mid);

            if (Math.abs(fMid - target) <= tolerance) {
                return mid; // Close enough to the target
            } else if (fMid < target) {
                low = mid; // Target is in the upper half
            } else {
                high = mid; // Target is in the lower half
            }
        }
        return (low + high) / 2; // Best approximation
    }

    private void run() {

        final double target = 0; // Solve f(x) = 0
        final double low = 0;
        final double high = 3;
        final double tolerance = 1e-12; // High precision

        final double result = this.goalSeek(target, low, high, tolerance);
        System.out.println("Result: " + result);
        System.out.println("f(" + result + ") = " + this.function(result));
    }
}
