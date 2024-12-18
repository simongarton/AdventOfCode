package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

public class Year2024Day17 extends AdventOfCodeChallenge {

    private final List<Integer> part2Program = List.of(2, 4, 1, 1, 7, 5, 4, 4, 1, 4, 0, 3, 5, 5, 3, 0);
    private final AtomicInteger bestDepthFound = new AtomicInteger(0);
    private long start;

    @Override
    public String title() {
        return "Day 17: Chronospatial Computer";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 17);
    }

    @Override
    public String part1(final String[] input) {

        final ChronospatialComputer computer = ChronospatialComputer.initializeFromLines(input);
        computer.run();

        return computer.getOutputString();
    }

    @Override
    public String part2(final String[] input) {

        this.start = System.currentTimeMillis();
        final ChronospatialComputer computer = ChronospatialComputer.initializeFromLines(input);
        long index = 1L;
        long validA = 0;
        this.bestDepthFound.set(0);
        final int checked = 0;
        while (true) {
            final Long a = this.evaluatePossibleAForAll(index);
            index++;
//            if (index % 10000000 == 0) {
//                System.out.println(index);
//            }
            if (a == null) {
                continue;
            }
            computer.reset();
            computer.setRegisterA(a);
            computer.run();
            if (computer.getOutputString().equalsIgnoreCase(computer.getProgramString())) {
                validA = a;
                break;
            }
            System.out.println(checked + " -> " + a + " p " + computer.getProgramString() + " -> " + computer.getOutputString());
        }

        return String.valueOf(validA);
    }

    public String part2Parallel(final String[] input) {

        // this was weird. Not thread safe ? Despite Atomics ?
        /*
        with index 1279022294173220864 bestDepthFound 0/16 =
        with index 270215977642231536 bestDepthFound 3/16 = 2,4,1,
        with index 1152921504606847024 bestDepthFound 1/16 = 2,
        with index 10794 bestDepthFound 4/16 = 2,4,1,1,
        ...
         */

        final ChronospatialComputer computer = ChronospatialComputer.initializeFromLines(input);

        final java.util.function.LongPredicate testFunction = value -> {
            return this.evaluatePossibleAForAll(value) != null;
        };

        LongStream.range(0, Long.MAX_VALUE)
                .parallel()
                .filter(testFunction)
                .forEach(value -> this.checkValue(computer, value)
                );

        return String.valueOf(null);
    }

    private void checkValue(final ChronospatialComputer computer, final long a) {

        computer.reset();
        computer.setRegisterA(a);
        computer.run();
        if (computer.getOutputString().equalsIgnoreCase(computer.getProgramString())) {
            System.out.println("a=" + a + " p " + computer.getProgramString() + " -> " + computer.getOutputString());
        }
    }

    private Integer evaluatePossibleA(final int index) {

        int a = index;
        int b = 0;
        int c = 0;

        b = a % 8;
        b = b ^ 1;
        c = Double.valueOf(Math.floor(1.0D * a / Math.pow(2, b))).intValue();
        b = b ^ c;
        b = b ^ 4;
        a = a / 8;
        if (b % 8 != 2) {
            return null;
        }
        return null;
    }

    private Long evaluatePossibleAForAll(final long index) {

        long a = index;
        long b = 0;
        long c = 0;

        final DecimalFormat formatter = new DecimalFormat("#,###");

        for (int iteration = 0; iteration < this.part2Program.size(); iteration++) {
            b = a % 8;
            b = b ^ 1;
            c = Double.valueOf(Math.floor(1.0D * a / Math.pow(2, b))).intValue();
            b = b ^ c;
            b = b ^ 4;
            a = a / 8;
            if (b % 8 != this.part2Program.get(iteration)) {
                final int currentBestDepth = this.bestDepthFound.get();
                if (iteration > currentBestDepth) {
                    this.bestDepthFound.set(iteration);
                    String best = "";
                    for (int i = 0; i < currentBestDepth; i++) {
                        best = best + this.part2Program.get(i) + ",";
                    }
                    System.out.println(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS) + "  (" + (System.currentTimeMillis() - this.start) / 1000 + " seconds) with index " + formatter.format(index) + " bestDepthFound " + currentBestDepth + "/" + this.part2Program.size() + " = " + best);
                }
                return null;
            }
        }
        return index;
    }
}
