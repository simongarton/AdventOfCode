package com.simongarton.adventofcode.year2024;

import com.googlecode.lanterna.TextColor;
import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2024Day6 extends AdventOfCodeChallenge {

    private static final String BREADCRUMB = "X";
    private static final String OBSTRUCTION = "#";
    private static final String NEW_OBSTRUCTION = "O";
    private static final String OFF_THE_BOARD = "*";
    private static final String EMPTY = ".";
    private static final String UP = "^";
    private static final String RIGHT = ">";
    private static final String DOWN = "v";
    private static final String LEFT = "<";

    private int guardX;
    private int guardY;
    private int guardDir; // 0 N, 1 E, 2 S, 3 W

    private int width;
    private int height;

    private final boolean DEBUG = true;

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

        if (this.DEBUG) {
            this.setupLanternaForDemo();
        }

        final int steps = this.solveMap();

        return String.valueOf(steps);
    }

    private void setupLanternaForDemo() {

        this.setUpLanterna(this.width + 1, this.height + 1);
    }

    private void setup(final String[] input) {

        this.width = input[0].length();
        this.height = input.length;
        this.map = String.join("", input);

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if (this.getMap(x, y).equalsIgnoreCase(UP)) {
                    this.guardX = x;
                    this.guardY = y;
                    this.guardDir = 0;
                    break;
                }
            }
        }
    }

    private int solveMap() {

        int timestamp = 0;

        int xs = 1; // I start here
        while (true) {
            timestamp = timestamp + 1;

            if (this.DEBUG) {
                this.drawCurrentMap();
                this.refreshAndSleep(100);
            }

            if (timestamp > 1000) {
                if (this.DEBUG) {
                    this.drawCurrentMap();
                    this.refreshAndSleep(1000);
                }
                return -1;
            }
            final String next = this.getNextMap(this.guardX, this.guardY, this.guardDir);

            if (next.equalsIgnoreCase(OFF_THE_BOARD)) {
                // off the board
                if (this.DEBUG) {
                    this.drawCurrentMap();
                    this.refreshAndSleep(1000);
                }
                return xs;
            }
            if (next.equalsIgnoreCase(OBSTRUCTION) || next.equalsIgnoreCase(NEW_OBSTRUCTION)) {
                this.guardDir = (this.guardDir + 1) % 4;
                continue;
            }
            if (next.equalsIgnoreCase(EMPTY)) {
                xs = xs + 1;
            }
            this.setMap(this.guardX, this.guardY, BREADCRUMB);
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
            this.setMap(this.guardX, this.guardY, this.guardCharacter(this.guardDir));
        }
    }

    private String guardCharacter(final int guardDir) {
        switch (guardDir) {
            case 0:
                return UP;
            case 1:
                return RIGHT;
            case 2:
                return DOWN;
            case 3:
                return LEFT;
            default:
                return "@";
        }
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

        if (x < 0 || x >= this.width) {
            return OFF_THE_BOARD;
        }
        if (y < 0 || y >= this.height) {
            return OFF_THE_BOARD;
        }
        final int index = (y * this.width) + x;
        if (index < 0 || index >= (this.width * this.height)) {
            return OFF_THE_BOARD;
        }
        return this.map.charAt(index) + "";
    }

    @Override
    public String part2(final String[] input) {

        this.setup(input);

        final int cols = this.width;
        final int rows = this.height;

        if (this.DEBUG) {
            this.setupLanternaForDemo();
        }

        if (this.DEBUG) {
            this.drawCurrentMap();
            this.refreshAndSleep(1000);
        }

        int blocks = 0;

        for (int y = 0; y < rows; y++) {
            System.out.println(y);
            for (int x = 0; x < cols; x++) {
                blocks += this.testVariation(x, y, input);
            }
        }

        return String.valueOf(blocks);
    }

    private int testVariation(final int x, final int y, final String[] input) {

        int blocks = 0;

        this.setup(input);

        if (this.getMap(x, y).equalsIgnoreCase("#")) {
            // already blocked
            return blocks;
        }

        if (x == this.guardX && y == this.guardY) {
            // can't block starting position
            return blocks;
        }
        this.setMap(x, y, "O");

        final int steps = this.solveMap();

        if (steps == -1) {
            if (this.DEBUG) {
                System.out.println("changed " + x + "," + y + "=" + steps);
                this.printMap();
                this.setup(input);
                this.setMap(x, y, NEW_OBSTRUCTION);
                this.printMap();
            }
            blocks++;
        }

        return blocks;
    }

    private void drawCurrentMap() {

        for (int i = 0; i < this.height; i++) {
            final String line = this.map.substring(i * this.width, (i + 1) * this.width);
            this.drawColoredString(line, 0, i, TextColor.ANSI.BLACK);
        }

        this.refreshAndSleep(0);
    }

    private void drawColoredString(final String s, final int x, final int y, final TextColor background) {

        for (int i = 0; i < s.length(); i++) {
            final TextColor foreground = this.textColor(s.charAt(i));
            this.drawChar(s.charAt(i), x + i, y, foreground, background);
        }
    }

    private TextColor textColor(final char c) {
        switch (c) {
            case '#':
                return TextColor.ANSI.GREEN;
            case 'O':
                return TextColor.ANSI.RED_BRIGHT;
            case 'X':
                return TextColor.ANSI.BLUE_BRIGHT;
            case '^':
            case '>':
            case 'v':
            case '<':
                return TextColor.ANSI.WHITE_BRIGHT;
            default:
                return TextColor.ANSI.BLACK_BRIGHT;
        }
    }
}
