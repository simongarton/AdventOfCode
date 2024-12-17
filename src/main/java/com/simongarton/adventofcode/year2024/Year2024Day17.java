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

        // 7,6,7,7,4,7,0,3,7 was wrong

        final ChronospatialComputer computer = this.initializeComputer(input);
        computer.run();

        return computer.getOutputString();
    }

    private ChronospatialComputer initializeComputer(final String[] input) {

        final int a = Integer.parseInt(input[0].replace("Register A: ", ""));
        final int b = Integer.parseInt(input[1].replace("Register B: ", ""));
        final int c = Integer.parseInt(input[2].replace("Register C: ", ""));

        final String program = input[4].replace("Program: ", "");

        final ChronospatialComputer computer = new ChronospatialComputer(program);
        computer.setRegisterA(a);
        computer.setRegisterB(b);
        computer.setRegisterC(c);

        return computer;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

}
