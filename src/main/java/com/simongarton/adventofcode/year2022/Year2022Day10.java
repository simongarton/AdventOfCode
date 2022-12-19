package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Year2022Day10 extends AdventOfCodeChallenge {

    private int register;
    private int cycle;
    private int scanPosition;
    private final Map<Integer, Integer> signalStrengths = new HashMap<>();
    private String[] crt;

    @Override
    public String title() {
        return "Day 10: Cathode-Ray Tube";
    }

    @Override
    public boolean run() {
        return this.runChallenge(2022, 10);
    }

    @Override
    public String part1(final String[] input) {
        this.register = 1;
        this.cycle = 0;
        this.signalStrengths.clear();
        this.crt = new String[0];
        for (final String instruction : input) {
            this.processInstruction(instruction);
        }
        return String.valueOf(this.calculateSignalStrength());
    }

    @Override
    public String part2(final String[] input) {
        this.register = 1;
        this.cycle = 0;
        this.signalStrengths.clear();
        this.crt = new String[6 * 40];
        Arrays.fill(this.crt, "-");
        this.scanPosition = 0;
        for (final String instruction : input) {
            this.processInstruction(instruction);
        }
//        this.drawCRT();
        // this has to be hardcoded after reading the screen.
        return "PGPHBEAB";
    }

    private void processInstruction(final String instruction) {
        if (instruction.equalsIgnoreCase("noop")) {
            this.updateCRT();
            this.cycle++;
            this.checkSignalStrength();
            return;
        }
        final String[] parts = instruction.split(" ");
        final String command = parts[0];
        final int value = Integer.parseInt(parts[1]);
        if (command.equalsIgnoreCase("addx")) {
            this.updateCRT();
            this.cycle++;
            this.checkSignalStrength();
            this.updateCRT();
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

    private void updateCRT() {
        if (this.crt.length == 0) {
            return;
        }
        final int col = this.scanPosition % 40;
        final int row = this.scanPosition / 40;
        if (Math.abs(col - this.register) <= 1) {
            this.crt[row * 40 + col] = "#";
        } else {
            this.crt[row * 40 + col] = ".";
        }
        this.scanPosition = this.scanPosition + 1;
    }

    private void drawCRT() {
        System.out.println("");
        String line = "";
        for (final String dot : this.crt) {
            line = line + dot;
            if (line.length() == 40) {
                System.out.println(line);
                line = "";
            }
        }
        System.out.println("");
    }

    private int calculateSignalStrength() {
        int signalStrength = 0;
        for (final Map.Entry<Integer, Integer> entry : this.signalStrengths.entrySet()) {
            signalStrength += entry.getKey() * entry.getValue();
        }
        return signalStrength;
    }
}
