package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.HashMap;
import java.util.Map;

public class Year2022Day10 extends AdventOfCodeChallenge {

    private int register;
    private int cycle;
    private final Map<Integer, Integer> signalStrengths = new HashMap<>();

    @Override
    public boolean run() {
        return this.runChallenge(2022, 10);
    }

    @Override
    public String part1(final String[] input) {
        this.register = 1;
        this.cycle = 0;
        this.signalStrengths.clear();
        for (final String instruction : input) {
            this.processInstruction(instruction);
        }
        return String.valueOf(this.calculateSignalStrength());
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    private void processInstruction(final String instruction) {
        if (instruction.equalsIgnoreCase("noop")) {
            this.cycle++;
            this.checkSignalStrength();
            return;
        }
        final String[] parts = instruction.split(" ");
        final String command = parts[0];
        final int value = Integer.parseInt(parts[1]);
        if (command.equalsIgnoreCase("addx")) {
            this.cycle++;
            this.checkSignalStrength();
            this.cycle++;
            this.checkSignalStrength();
            this.register += value;
            return;
        }
        throw new RuntimeException("Unhandled command " + command);
    }

    private void checkSignalStrength() {
        if (this.cycle == 20 || (this.cycle - 20) % 40 == 0) {
            this.signalStrengths.put(this.cycle, this.register);
        }
    }

    private int calculateSignalStrength() {
        int signalStrength = 0;
        for (final Map.Entry<Integer, Integer> entry : this.signalStrengths.entrySet()) {
            signalStrength += entry.getKey() * entry.getValue();
        }
        return signalStrength;
    }
}
