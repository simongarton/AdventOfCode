package com.simongarton.adventofcode.year2025;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2025Day1 extends AdventOfCodeChallenge {

    // Copilot is off. IntelliJ full line completion is off.

    private int score;
    private int position;

    @Override
    public String title() {
        return "Day 1: Secret Entrance";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2025, 1);
    }

    @Override
    public String part1(final String[] input) {

        this.position = 50;
        this.score = 0;
        for (final String line : input) {
            final String dir = line.substring(0, 1);
            final int distance = Integer.parseInt(line.substring(1));
            if (dir.equalsIgnoreCase("L")) {
                this.position = this.position - distance;
            } else {
                this.position = this.position + distance;
            }
            while (this.position < 0) {
                this.position += 100;
            }
            this.position = this.position % 100;
            if (this.position == 0) {
                this.score++;
            }
        }

        return String.valueOf(this.score);
    }

    @Override
    public String part2(final String[] input) {

        this.score = 0;
        this.position = 50;
        for (final String line : input) {
            final String dir = line.substring(0, 1);
            final int distance = Integer.parseInt(line.substring(1));
            if (dir.equalsIgnoreCase("L")) {
                this.moveDial(-1, distance);
            } else {
                this.moveDial(1, distance);
            }
        }
        return String.valueOf(this.score);
    }

    private void moveDial(final int direction, final int distance) {

        for (int i = 0; i < distance; i++) {
            this.position = this.position + direction;
            if (this.position == 0) {
                this.score++;
            }
            if (this.position == 100) {
                this.position = 0;
                this.score++;
            }
            if (this.position == -1) {
                this.position = 99;
            }
        }
    }
}
