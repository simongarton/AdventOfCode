package com.simongarton.adventofcode.year2025;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2025Day7 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 7: Laboratories";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2025, 7);
    }

    @Override
    public String part1(final String[] input) {

        this.loadChallengeMap(input);
        final int width = input[0].length();

        int splits = 0;
        for (int row = 0; row < input.length - 1; row++) {
            for (int col = 0; col < width; col++) {
                if (this.getChallengeMapSymbol(col, row).equalsIgnoreCase("S") ||
                        this.getChallengeMapSymbol(col, row).equalsIgnoreCase("|")
                ) {
                    final String nextOne = this.getChallengeMapSymbol(col, row + 1);
                    if (nextOne.equalsIgnoreCase("|")) {
                        continue;
                    }
                    if (nextOne.equalsIgnoreCase(".")) {
                        this.setChallengeMapLetter(col, row + 1, "|");
                        continue;
                    }
                    if (nextOne.equalsIgnoreCase("^")) {
                        this.setChallengeMapLetter(col - 1, row + 1, "|");
                        this.setChallengeMapLetter(col + 1, row + 1, "|");
                        splits++;
                        continue;
                    }
                    throw new RuntimeException(nextOne);
                }
            }
        }

        //this.drawChallengeMap();
        return String.valueOf(splits);
    }

    @Override
    public String part2(final String[] input) {

        /*

        I can calculate what will be on any given row.
        First row is special, 0 unless I'm an S in which case I'm 1
        Every other row:
        - start with the count above
        - if the symbol above is ^, reset to 0
        - if the symbol above and left is a ^, add the count above and left
        - if the symbol above and right is a ^, add the count above and right
        When I finish
        - add up everything that isn't a ^


        Sample works.

         */

        this.loadChallengeMap(input);
        final int width = input[0].length();

        final long[] thisRow = new long[width];
        final long[] nextRow = new long[width];

        for (int col = 0; col < width; col++) {
            final String line = input[0];
            if (line.charAt(col) == 'S') {
                thisRow[col] = 1L;
            }
        }

        for (int row = 1; row < input.length; row++) {
            final String line = input[row - 1];
            for (int col = 0; col < width; col++) {
                nextRow[col] = thisRow[col];
                if (line.charAt(col) == '^') {
                    nextRow[col] = 0L;
                }
                if (col > 0 && line.charAt(col - 1) == '^') {
                    nextRow[col] = nextRow[col] + thisRow[col - 1];
                }
                if (col < width - 1 && line.charAt(col + 1) == '^') {
                    nextRow[col] = nextRow[col] + thisRow[col + 1];
                }
            }
            for (int col = 0; col < width; col++) {
                thisRow[col] = nextRow[col];
            }
            //this.dumpRow(thisRow);
        }
        final String line = input[input.length - 1];
        for (int col = 0; col < width; col++) {
            if (line.charAt(col) == '^') {
                thisRow[col] = 0L;
            }
        }
        long timelines = 0L;
        for (int col = 0; col < width; col++) {
            timelines = timelines + thisRow[col];
        }

        return String.valueOf(timelines);
    }

    private void dumpRow(final long[] thisRow) {
        String line = "";
        for (int col = 0; col < thisRow.length; col++) {
            line = line + thisRow[col] + " ";
        }
        System.out.println(line.trim());
    }
}