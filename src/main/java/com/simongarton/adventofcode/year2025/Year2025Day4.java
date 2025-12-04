package com.simongarton.adventofcode.year2025;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.Coord;

import java.util.ArrayList;
import java.util.List;

public class Year2025Day4 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 4: Printing Department";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2025, 4);
    }

    @Override
    public String part1(final String[] input) {

        this.loadChallengeMap(input);

        int accessibleRolls = 0;
        for (int x = 0; x < this.mapWidth; x++) {
            for (int y = 0; y < this.mapHeight; y++) {
                accessibleRolls = accessibleRolls + (this.isAccessible(x, y) ? 1 : 0);
            }
        }

        return String.valueOf(accessibleRolls);
    }

    private void drawMap() {

        for (int row = 0; row < this.mapHeight; row++) {
            final StringBuilder line = new StringBuilder();
            for (int col = 0; col < this.mapHeight; col++) {
                final String symbol = this.getChallengeMapSymbol(col, row);
                if (symbol.equalsIgnoreCase(".")) {
                    line.append(symbol);
                    continue;
                }
                line.append(this.isAccessible(col, row) ? "@" : "x");
            }
            System.out.println(line);
        }
    }

    private boolean isAccessible(final int x, final int y) {

        final String symbol = this.getChallengeMapSymbol(x, y);
        if (symbol.equalsIgnoreCase(".")) {
            return false;
        }

        int adjacentRolls = 0;
        adjacentRolls += this.adjacentRoll(x - 1, y - 1);
        adjacentRolls += this.adjacentRoll(x, y - 1);
        adjacentRolls += this.adjacentRoll(x + 1, y - 1);

        adjacentRolls += this.adjacentRoll(x - 1, y);
        adjacentRolls += this.adjacentRoll(x + 1, y);

        adjacentRolls += this.adjacentRoll(x - 1, y + 1);
        adjacentRolls += this.adjacentRoll(x, y + 1);
        adjacentRolls += this.adjacentRoll(x + 1, y + 1);
        return adjacentRolls < 4;
    }

    private int adjacentRoll(final int x, final int y) {

        if (x < 0 || x >= this.mapWidth) {
            return 0;
        }
        if (y < 0 || y >= this.mapHeight) {
            return 0;
        }
        return this.getChallengeMapSymbol(x, y).equalsIgnoreCase("@") ? 1 : 0;
    }

    @Override
    public String part2(final String[] input) {

        this.loadChallengeMap(input);

        final int initial = this.countWhatsLeft();

        while (true) {

            final List<Coord> coordList = new ArrayList<>();

            int accessibleRolls = 0;
            for (int x = 0; x < this.mapWidth; x++) {
                for (int y = 0; y < this.mapHeight; y++) {
                    if (this.isAccessible(x, y)) {
                        accessibleRolls++;
                        coordList.add(new Coord(x, y));
                    }
                }
            }
            if (accessibleRolls == 0) {
                break;
            }
            this.removeAccessibleRolls(coordList);
        }

        return String.valueOf(initial - this.countWhatsLeft());
    }

    private void removeAccessibleRolls(final List<Coord> coordList) {

        for (final Coord coord : coordList) {
            this.setChallengeMapLetter(coord.getX(), coord.getY(), ".");
        }
    }

    private int countWhatsLeft() {

        int rolls = 0;
        for (int row = 0; row < this.mapHeight; row++) {
            for (int col = 0; col < this.mapHeight; col++) {
                final String symbol = this.getChallengeMapSymbol(col, row);
                if (symbol.equalsIgnoreCase("@")) {
                    rolls++;
                }
            }
        }
        return rolls;
    }
}