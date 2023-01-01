package com.simongarton.adventofcode.year2020;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Year2020Day11 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 11: Seating System";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2020, 11);
    }

    @Override
    public String part1(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        return String.valueOf(this.runToSolution(lines, 1));
    }

    @Override
    public String part2(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        return String.valueOf(this.runToSolution(lines, 2));
    }


    private int runToSolution(final List<String> lines, final int part) {
        List<String> working = new ArrayList<>(lines);
        while (true) {
            List<String> updated = new ArrayList<>(working);
            updated = part == 1 ? this.update1(updated) : this.update2(updated);
            if (this.noChange(working, updated)) {
                break;
            }
            working = new ArrayList<>(updated);
        }
        return this.countOccupied(working);
    }

    private int countOccupied(final List<String> current) {
        int occupied = 0;
        for (final String s : current) {
            for (int col = 0; col < s.length(); col++) {
                if (s.substring(col, col + 1).equalsIgnoreCase("#")) {
                    occupied++;
                }
            }
        }
        return occupied;
    }

    private void print(final int iterations, final List<String> working) {
        System.out.println("iteration " + iterations + "\n");
        working.forEach(System.out::println);
        System.out.println("");
    }

    private List<String> update1(final List<String> current) {
        final List<String> updated = new ArrayList<>();
        for (int row = 0; row < current.size(); row++) {
            final StringBuilder newRow = new StringBuilder();
            for (int col = 0; col < current.get(row).length(); col++) {
                final String currentSeat = current.get(row).substring(col, col + 1);
                String replaceSeat = ".";
                final int adjacent = this.countAdjacent(current, row, col);
                if (currentSeat.equalsIgnoreCase("L")) {
                    replaceSeat = adjacent == 0 ? "#" : "L";
                }
                if (currentSeat.equalsIgnoreCase("#")) {
                    replaceSeat = adjacent >= 4 ? "L" : "#";
                }
                newRow.append(replaceSeat);
            }
            updated.add(newRow.toString());
        }
        return updated;
    }

    private List<String> update2(final List<String> current) {
        final List<String> updated = new ArrayList<>();
        for (int row = 0; row < current.size(); row++) {
            final StringBuilder newRow = new StringBuilder();
            for (int col = 0; col < current.get(row).length(); col++) {
                final String currentSeat = current.get(row).substring(col, col + 1);
                String replaceSeat = ".";
                final int adjacent = this.countVisible(current, row, col);
                if (currentSeat.equalsIgnoreCase("L")) {
                    replaceSeat = adjacent == 0 ? "#" : "L";
                }
                if (currentSeat.equalsIgnoreCase("#")) {
                    replaceSeat = adjacent >= 5 ? "L" : "#";
                }
                newRow.append(replaceSeat);
            }
            updated.add(newRow.toString());
        }
        return updated;
    }

    private int countAdjacent(final List<String> current, final int row, final int col) {
        int neighbours = 0;
        neighbours += this.countNeighbour(current, row + 1, col + 1);
        neighbours += this.countNeighbour(current, row + 1, col + 0);
        neighbours += this.countNeighbour(current, row + 1, col - 1);
        neighbours += this.countNeighbour(current, row + 0, col + 1);
        neighbours += this.countNeighbour(current, row + 0, col - 1);
        neighbours += this.countNeighbour(current, row - 1, col + 1);
        neighbours += this.countNeighbour(current, row - 1, col + 0);
        neighbours += this.countNeighbour(current, row - 1, col - 1);
        return neighbours;
    }

    private int countVisible(final List<String> current, final int row, final int col) {
        int neighbours = 0;
        neighbours += this.countVisibleNeighbour(current, row, col, 1, 1);
        neighbours += this.countVisibleNeighbour(current, row, col, 1, 0);
        neighbours += this.countVisibleNeighbour(current, row, col, 1, -1);
        neighbours += this.countVisibleNeighbour(current, row, col, 0, 1);
        neighbours += this.countVisibleNeighbour(current, row, col, 0, -1);
        neighbours += this.countVisibleNeighbour(current, row, col, -1, 1);
        neighbours += this.countVisibleNeighbour(current, row, col, -1, 0);
        neighbours += this.countVisibleNeighbour(current, row, col, -1, -1);
        return neighbours;
    }

    private int countVisibleNeighbour(final List<String> current, int row, int col, final int rowDelta, final int colDelta) {
        while (true) {
            row = row + rowDelta;
            col = col + colDelta;
            if (row < 0 || row >= current.size()) {
                return 0;
            }
            final String line = current.get(row);
            if (col < 0 || col >= line.length()) {
                return 0;
            }
            final String seat = line.substring(col, col + 1);
            if (seat.equalsIgnoreCase(".")) {
                continue;
            }
            return seat.equalsIgnoreCase("#") ? 1 : 0;
        }
    }

    private int countNeighbour(final List<String> current, final int row, final int col) {
        if (row < 0 || row >= current.size()) {
            return 0;
        }
        final String line = current.get(row);
        if (col < 0 || col >= line.length()) {
            return 0;
        }
        return line.substring(col, col + 1).equalsIgnoreCase("#") ? 1 : 0;
    }

    private boolean noChange(final List<String> working, final List<String> updated) {
        if (working.size() != updated.size()) {
            throw new RuntimeException("Wrong number of rows");
        }
        for (int i = 0; i < working.size(); i++) {
            if (working.get(i).length() != updated.get(i).length()) {
                throw new RuntimeException("Wrong number of cols at line " + i);
            }
            if (!working.get(i).equalsIgnoreCase(updated.get(i))) {
                return false;
            }
        }
        return true;
    }
}
