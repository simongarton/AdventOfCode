package com.simongarton.adventofcode.year2015;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.ChallengeCoord;

import java.util.Arrays;

public class Year2015Day6 extends AdventOfCodeChallenge {

    private int[][] grid;

    @Override
    public String title() {
        return "Day 6: Probably a Fire Hazard";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2015, 6);
    }

    @Override
    public String part1(final String[] input) {

        this.grid = new int[1000][1000];
        Arrays.stream(input).forEach(this::handleInstruction);
        return String.valueOf(this.countLights());
    }

    private int countLights() {

        int total = 0;
        for (int x = 0; x < 1000; x++) {
            for (int y = 0; y < 1000; y++) {
                total += this.grid[x][y];
            }
        }
        return total;
    }

    private void handleInstruction(final String s) {

        if (s.startsWith("turn on")) {
            this.turnOn(s);
            return;
        }
        if (s.startsWith("turn off")) {
            this.turnOff(s);
            return;
        }
        if (s.startsWith("toggle")) {
            this.toggle(s);
            return;
        }
        throw new RuntimeException(s);
    }

    private void turnOn(final String s) {

        final String line = s.replace("turn on ", "");
        final String[] parts = line.split(" through ");
        final ChallengeCoord start = new ChallengeCoord(parts[0]);
        final ChallengeCoord end = new ChallengeCoord(parts[1]);

        for (int x = start.getX(); x <= end.getX(); x++) {
            for (int y = start.getY(); y <= end.getY(); y++) {
                this.grid[x][y] = 1;
            }
        }
    }

    private void turnOff(final String s) {

        final String line = s.replace("turn off ", "");
        final String[] parts = line.split(" through ");
        final ChallengeCoord start = new ChallengeCoord(parts[0]);
        final ChallengeCoord end = new ChallengeCoord(parts[1]);

        for (int x = start.getX(); x <= end.getX(); x++) {
            for (int y = start.getY(); y <= end.getY(); y++) {
                this.grid[x][y] = 0;
            }
        }
    }

    private void toggle(final String s) {

        final String line = s.replace("toggle ", "");
        final String[] parts = line.split(" through ");
        final ChallengeCoord start = new ChallengeCoord(parts[0]);
        final ChallengeCoord end = new ChallengeCoord(parts[1]);

        for (int x = start.getX(); x <= end.getX(); x++) {
            for (int y = start.getY(); y <= end.getY(); y++) {
                this.grid[x][y] = Math.abs(this.grid[x][y] - 1);
            }
        }
    }

    @Override
    public String part2(final String[] input) {

        this.grid = new int[1000][1000];
        Arrays.stream(input).forEach(this::handleSecondInstruction);
        return String.valueOf(this.countLights());
    }

    private void handleSecondInstruction(final String s) {

        if (s.startsWith("turn on")) {
            this.brighten(s, 1, "turn on ");
            return;
        }
        if (s.startsWith("turn off")) {
            this.dim(s);
            return;
        }
        if (s.startsWith("toggle")) {
            this.brighten(s, 2, "toggle ");
            return;
        }
        throw new RuntimeException(s);
    }

    private void dim(final String s) {

        final String line = s.replace("turn off ", "");
        final String[] parts = line.split(" through ");
        final ChallengeCoord start = new ChallengeCoord(parts[0]);
        final ChallengeCoord end = new ChallengeCoord(parts[1]);

        for (int x = start.getX(); x <= end.getX(); x++) {
            for (int y = start.getY(); y <= end.getY(); y++) {
                if (this.grid[x][y] > 0) {
                    this.grid[x][y] = this.grid[x][y] - 1;
                }
            }
        }
    }

    private void brighten(final String s, final int i, final String key) {

        final String line = s.replace(key, "");
        final String[] parts = line.split(" through ");
        final ChallengeCoord start = new ChallengeCoord(parts[0]);
        final ChallengeCoord end = new ChallengeCoord(parts[1]);

        for (int x = start.getX(); x <= end.getX(); x++) {
            for (int y = start.getY(); y <= end.getY(); y++) {
                this.grid[x][y] = this.grid[x][y] + i;
            }
        }
    }
}
