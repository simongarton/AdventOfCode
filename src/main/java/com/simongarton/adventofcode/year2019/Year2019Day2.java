package com.simongarton.adventofcode.year2019;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2019Day2 extends AdventOfCodeChallenge {

    List<Integer> memory;

    @Override
    public String title() {
        return "Day 2: 1202 Program Alarm";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2019, 2);
    }

    @Override
    public String part1(final String[] input) {
        this.loadProgram(input[0]);
        return String.valueOf(this.run1202());
    }

    @Override
    public String part2(final String[] input) {
        return String.valueOf(this.run19690720(input[0]));
    }

    private int run19690720(final String program) {
        for (int noun = 0; noun <= 99; noun++) {
            for (int verb = 0; verb <= 99; verb++) {
                this.loadProgram(program);
                this.setInitial(noun, verb);
                this.runProgram();
                if (this.memory.get(0) == 19690720) {
                    return (100 * noun) + verb;
                }
            }
        }
        throw new RuntimeException("No solution found.");
    }

    private int run1202() {
        this.setInitial(12, 2);
        this.runProgram();
        return this.memory.get(0);
    }

    private void loadProgram(final String program) {
        this.memory = Arrays.stream(program.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    private void runProgram() {
        int pointer = 0;
        int opCode = this.memory.get(pointer);
        int ref1, ref2, ref3;
        int val1, val2, result;
        while (opCode != 99) {
            if ((opCode != 1) && (opCode != 2)) {
                throw new RuntimeException("Bad opCode " + opCode);
            }
            ref1 = this.memory.get(pointer + 1);
            ref2 = this.memory.get(pointer + 2);
            ref3 = this.memory.get(pointer + 3);
            val1 = this.memory.get(ref1);
            val2 = this.memory.get(ref2);
            if (opCode == 1) {
                result = val1 + val2;
            } else {
                result = val1 * val2;
            }
            this.memory.set(ref3, result);
            pointer += 4;
            opCode = this.memory.get(pointer);
        }
    }

    private void setInitial(final int one, final int two) {
        this.memory.set(1, one);
        this.memory.set(2, two);
    }

}
