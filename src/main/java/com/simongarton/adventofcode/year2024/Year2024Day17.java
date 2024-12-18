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
        int a = 0;
        while (true) {
            computer.reset();
            computer.setRegisterA(a);
            computer.run();
            if (computer.getOutputString().equalsIgnoreCase(computer.getProgramString())) {
                break;
            }
            if (a % 10000 == 0) {
                System.out.println(a + " p " + computer.getProgramString() + " -> " + computer.getOutputString());
            }
            a++;
        }

        return String.valueOf(a);
    }

}
