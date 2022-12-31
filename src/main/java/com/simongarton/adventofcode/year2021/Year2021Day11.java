package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;

public class Year2021Day11 extends AdventOfCodeChallenge {

    private int[] octopi;
    private int width;
    private int height;

    @Override
    public String title() {
        return "Day 11: Dumbo Octopus";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2021, 11);
    }

    @Override
    public String part1(final String[] input) {
        this.width = input[0].length();
        this.height = input.length;
        this.octopi = new int[this.width * this.height];
        this.loadOctopi(input);
        long result = 0;
        for (int step = 0; step < 100; step++) {
            result += this.doOneStep();
        }
        return String.valueOf(result);
    }

    @Override
    public String part2(final String[] input) {
        this.width = input[0].length();
        this.height = input.length;
        this.octopi = new int[this.width * this.height];
        this.loadOctopi(input);
        long result = 0;
        int step = 0;
        while (true) {
            step++;
            final int changes = this.doOneStep();
            if (changes == 100) {
                result = step;
                break;
            }
        }

        return String.valueOf(result);
    }

    private void loadOctopi(final String[] lines) {
        int index = 0;
        for (final String line : lines) {
            for (int i = 0; i < this.width; i++) {
                this.octopi[index++] = Integer.parseInt(line.charAt(i) + "");
            }
        }
    }

    private int nines() {
        int flashes = 0;
        while (true) {
            final List<String> toBeDone = new ArrayList<>();
            for (int row = 0; row < this.height; row++) {
                for (int col = 0; col < this.width; col++) {
                    if (this.octopi[(row * this.width) + col] == 0) {
                        continue;
                    }
                    if (this.octopi[(row * this.width) + col] > 9) {
                        toBeDone.add(row + "," + col);
                    }
                }
            }
            if (toBeDone.isEmpty()) {
                break;
            }
            for (final String excited : toBeDone) {
                final String[] parts = excited.split(",");
                final int row = Integer.parseInt(parts[0]);
                final int col = Integer.parseInt(parts[1]);
                flashes++;
                this.octopi[(row * this.width) + col] = 0;
                this.boost(row + 1, col + 1);
                this.boost(row + 1, col + 0);
                this.boost(row + 1, col - 1);
                this.boost(row + 0, col + 1);
                this.boost(row + 0, col - 1);
                this.boost(row - 1, col + 1);
                this.boost(row - 1, col + 0);
                this.boost(row - 1, col - 1);
            }
        }
        return flashes;
    }

    private void boost(final int row, final int col) {
        if (row < 0 || row >= this.height) {
            return;
        }
        if (col < 0 || col >= this.width) {
            return;
        }
        if (this.octopi[(row * this.width) + col] == 0) {
            return;
        }
        this.octopi[(row * this.width) + col] = this.octopi[(row * this.width) + col] + 1;
    }

    private void printOctopi(final int step) {
        System.out.println("Step " + step + "\n");
        for (int row = 0; row < this.height; row++) {
            final StringBuilder line = new StringBuilder();
            for (int col = 0; col < this.width; col++) {
                final int val = this.octopi[(row * this.width) + col];
                if (val < 10) {
                    line.append(val);
                } else {
                    line.append("x");
                }
            }
            System.out.println(line);
        }
        System.out.println("");
    }

    private int doOneStep() {
        // first update all
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                this.octopi[(row * this.width) + col]++;
            }
        }
        return this.nines();
    }
}
