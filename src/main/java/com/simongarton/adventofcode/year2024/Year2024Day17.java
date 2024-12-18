package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.List;

public class Year2024Day17 extends AdventOfCodeChallenge {

    private final List<Integer> part2Program = List.of(2, 4, 1, 1, 7, 5, 4, 4, 1, 4, 0, 3, 5, 5, 3, 0);
    private int bestDepthFound;

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

        final ChronospatialComputer computer = ChronospatialComputer.initializeFromLines(input);
        int index = 1;
        int validA = 0;
        this.bestDepthFound = 0;
        final int checked = 0;
        while (true) {
            final Integer a = this.evaluatePossibleAForAll(index);
            index++;
            if (index % 10000000 == 0) {
                System.out.println(index);
            }
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
//            if (++checked % 10000 == 0) {
            System.out.println(checked + " -> " + a + " p " + computer.getProgramString() + " -> " + computer.getOutputString());
//            }
        }

        return String.valueOf(validA);
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

    private Integer evaluatePossibleAForAll(final int index) {

        int a = index;
        int b = 0;
        int c = 0;

        for (int iteration = 0; iteration < this.part2Program.size(); iteration++) {
            b = a % 8;
            b = b ^ 1;
            c = Double.valueOf(Math.floor(1.0D * a / Math.pow(2, b))).intValue();
            b = b ^ c;
            b = b ^ 4;
            a = a / 8;
            if (b % 8 != this.part2Program.get(iteration)) {
                if (iteration > this.bestDepthFound) {
                    this.bestDepthFound = iteration;
                    String best = "";
                    for (int i = 0; i < this.bestDepthFound; i++) {
                        best = best + this.part2Program.get(i) + ",";
                    }
                    System.out.println("bestDepthFound " + this.bestDepthFound + "/" + this.part2Program.size() + " = " + best);
                }
                return null;
            }
        }
        return index;
    }
}
