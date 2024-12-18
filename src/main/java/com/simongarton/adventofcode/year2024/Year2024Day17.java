package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2024Day17 extends AdventOfCodeChallenge {

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
        int checked = 0;
        while (true) {
            final Integer a = this.evaluatePossibleA(index);
            if (a == null) {
                index++;
                continue;
            }
            computer.reset();
            computer.setRegisterA(a);
            computer.run();
            if (computer.getOutputString().equalsIgnoreCase(computer.getProgramString())) {
                validA = a;
                break;
            }
            if (++checked % 10000 == 0) {
                System.out.println(checked + " -> " + a + " p " + computer.getProgramString() + " -> " + computer.getOutputString());
            }
            index++;
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
        if (b % 8 == 2) {
            return index;
        }
        return null;
    }
}
