package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2024Day6 extends AdventOfCodeChallenge {

    private int guardX;
    private int guardY;
    private int guardDir; // 0 N, 1 E, 2 S, 3 W

    private int timestamp = 0;

    private int width;
    private int height;

    private String map;

    @Override
    public String title() {
        return "Day 6: Guard Gallivant";
    }

    @Override
    public Outcome run() {

        return this.runChallenge(2024, 6);
    }

    @Override
    public String part1(final String[] input) {

        this.setup(input);

        final int steps = this.solveMap();

        this.printMap();

        return String.valueOf(steps);
    }

    private void setup(final String[] input) {

        this.width = input[0].length();
        this.height = input.length;
        this.map = String.join("", input);

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if (this.getMap(x, y).equalsIgnoreCase("^")) {
                    this.guardX = x;
                    this.guardY = y;
                    this.guardDir = 0;
                    break;
                }
            }
        }
    }

    private int solveMap() {

        this.timestamp = 0;

        int xs = 1; // I start here
        while (true) {
            this.timestamp = this.timestamp + 1;
            if (this.timestamp > 10000) {
                return -1;
            }
            final String next = this.getNextMap(this.guardX, this.guardY, this.guardDir);
            if (next.equalsIgnoreCase("*")) {
                // off the board
                System.out.println("off the board at " + this.guardX + "," + this.guardY);
                this.setMap(this.guardX, this.guardY, "X");
                break;
            }
            if (next.equalsIgnoreCase("#") || next.equalsIgnoreCase("O")) {
                this.guardDir = (this.guardDir + 1) % 4;
                continue;
            }
            if (next.equalsIgnoreCase(".")) {
                xs = xs + 1;
            }
            this.setMap(this.guardX, this.guardY, "X");
//            System.out.printf("%n%s%n%n", xs);
            switch (this.guardDir) {
                case 0:
                    this.guardY = this.guardY - 1;
                    break;
                case 1:
                    this.guardX = this.guardX + 1;
                    break;
                case 2:
                    this.guardY = this.guardY + 1;
                    break;
                case 3:
                    this.guardX = this.guardX - 1;
                    break;
            }
            this.setMap(this.guardX, this.guardY, "^");
//            this.printMap();

        }
        return xs;
    }

    private void printMap() {
        for (int row = 0; row < this.height; row++) {
            final String line = this.map.substring((row * this.width), ((row + 1) * this.width));
            System.out.println(line);
        }

        System.out.println();
    }

    private void setMap(final int x, final int y, final String nextChar) {
        final int index = (y * this.width) + x;

        this.map = this.map.substring(0, index) + nextChar + this.map.substring(index + 1);
    }

    private String getNextMap(final int x, final int y, final int dir) {

        int nextX = x;
        int nextY = y;
        switch (dir) {
            case 0: {
                nextY = nextY - 1;
                break;
            }
            case 1: {
                nextX = nextX + 1;
                break;
            }
            case 2: {
                nextY = nextY + 1;
                break;
            }
            case 3: {
                nextX = nextX - 1;
                break;
            }
        }

        return this.getMap(nextX, nextY);
    }

    private String getMap(final int x, final int y) {

        final int index = (y * this.width) + x;
        if (index < 0 || index >= (this.width * this.height)) {
            return "*";
        }
        return this.map.charAt(index) + "";
    }

    @Override
    public String part2(final String[] input) {

        this.setup(input);

        final int cols = this.width;
        final int rows = this.height;

        int blocks = 0;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {

                this.setup(input);

                if (x == this.guardX && y == this.guardY) {
                    // can't block starting position
                    continue;
                }
                this.setMap(x, y, "O");

                final int steps = this.solveMap();

                if (steps == -1) {
                    System.out.println("changing " + x + "," + y + "=" + steps);
                    this.printMap();
                    this.setup(input);
                    this.setMap(x, y, "O");
                    this.printMap();
                    blocks++;
                }
            }
        }

        return String.valueOf(blocks);
    }
}
