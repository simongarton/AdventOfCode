package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2023Day21 extends AdventOfCodeChallenge {

    private String map;
    private String beenThere;
    private int width;
    private int height;
    private int start;

    @Override
    public String title() {
        return "Day 21: Step Counter";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 21);
    }

    @Override
    public String part1(final String[] input) {

        this.loadMap(input);
        this.width = input[0].length();
        this.height = input.length;
        this.buildBeenThere();

        this.map = this.map.replace("S", "O");
        for (int step = 0; step < 64; step++) {
            this.walkies();
//            this.debugMap();
//            this.debugBeenThere();
        }

        return String.valueOf(this.countGotSomewhere());
    }

    private long countPlaces() {
        return this.beenThere.chars().filter(ch -> ch == '1').count();
    }

    private long countGotSomewhere() {
        return this.map.chars().filter(ch -> ch == 'O').count() + 1;
    }

    private void walkies() {

        final String original = this.map;
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                if (this.getCell(original, row, col).equalsIgnoreCase("O")) {
                    this.walky(row, col);
                }
            }
        }
        this.map = this.map.substring(0, this.start) + "S" + this.map.substring(this.start + 1);
    }

    private void walky(final int row, final int col) {

        this.maybe(row - 1, col);
        this.maybe(row + 1, col);
        this.maybe(row, col - 1);
        this.maybe(row, col + 1);
        this.map = this.replaceCharacter(this.map, row, col, ".");
    }

    private void maybe(final int row, final int col) {

        if (row < 0 || row >= this.height || col < 0 || col >= this.width) {
            return;
        }
        final String old = this.getCell(this.map, row, col);
        if (old.equalsIgnoreCase("#")) {
            return;
        }
        this.map = this.replaceCharacter(this.map, row, col, "O");
        this.beenThere = this.replaceCharacter(this.beenThere, row, col, "1");
    }

    private String replaceCharacter(final String original, final int row, final int col, final String replacement) {

        final int index = (row * this.width) + col;
        return original.substring(0, index) + replacement + original.substring(index + 1);
    }

    private String getCell(final String original, final int row, final int col) {

        final int index = (row * this.width) + col;
        return original.substring(index, index + 1);
    }

    private void blankLine() {

        System.out.println();
    }

    private void debugMap() {

        this.debug(this.map);
    }

    private void debugBeenThere() {

        this.debug(this.beenThere);
    }

    private void debug(final String aMap) {
        for (int row = 0; row < this.height; row++) {
            final String line = aMap.substring(row * this.width, (row + 1) * this.width);
            System.out.println(line);
        }
        this.blankLine();
    }

    private void buildBeenThere() {

        this.beenThere = (" ".repeat(this.width)).repeat(this.height);

        this.start = this.map.indexOf("S");
        this.beenThere = this.beenThere.substring(0, this.start) + "1" + this.beenThere.substring(this.start + 1);
    }

    private void loadMap(final String[] input) {

        this.map = String.join("", input);
    }

    @Override
    public String part2(final String[] input) {

        return null;
    }
}
