package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Year2023Day10 extends AdventOfCodeChallenge {

    /*

    Start at S : that's 0.
    For all connected neighbours, mark as N + 1;
    For neighbours, I have to check if they ARE neighbours

    Part 2 :
    loop over rows, cols, so coming in from left
    keep track of if I'm inside a loop
    - a loop pipe is either the start, or distance > 0
    - if not a loop pipe : include me
    - if my current loop count is odd, i'm in

    No, that fails for the horizontal row of pipes, which is an odd number, so the far right cell thinks it's in.

    I note that the real map has very few dots. Can I check those individually ?
    Can I head out in all four directions, and the first one to the edge is the best indicator ?

    I don't think I can use any left-hand/right-hand rules as my pipes head off in two directions.





     */

    private Map<String, Cell> cells;
    private Cell start;
    private int width;
    private int height;

    @Override
    public String title() {
        return "Day 10: ";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 10);
    }

    @Override
    public String part1(final String[] input) {

        final int max = this.loadMap(input);

        return String.valueOf(max);
    }

    private void drawMap() {

        for (int row = 0; row < this.height; row++) {
            String line = "";
            for (int col = 0; col < this.width; col++) {
                final String key = col + "," + row;
                final Cell cell = this.cells.get(key);
                if (cell.getContents().equalsIgnoreCase(".")) {
                    line += cell.getInsideOutside() == null ? "." : cell.getInsideOutside();
                    continue;
                }
                String draw = "?";
                if (cell.getDistance() >= 0) {
                    if (cell.getDistance() < 10) {
                        draw = String.valueOf(cell.getDistance());
                    } else {
                        draw = "!";
                    }
                }
                line += draw;
            }
            System.out.println(line);
        }
    }

    private int loadMap(final String[] input) {

        this.height = input.length;
        this.width = input[0].length();

        this.cells = new HashMap<>();
        for (int row = 0; row < input.length; row++) {
            final String line = input[row];
            for (int col = 0; col < line.length(); col++) {
                final String key = col + "," + row;
                final Cell cell = this.figureOutCell(col, row, line.substring(col, col + 1));
                this.cells.put(key, cell);
                if (cell.start) {
                    this.start = cell;
                }
            }
        }

        this.sortOutStart();
        System.out.println("the start is a " + this.start.getContents());

        this.drawMap();

        final int max = this.sortOutDistances();
        System.out.println();

        this.drawMap();

        return max;
    }

    private int sortOutDistances() {

        int max = 0;

        final List<Cell> cellsToCheck = new ArrayList<>();
        cellsToCheck.add(this.start);

        while (!cellsToCheck.isEmpty()) {
            final Cell cellToCheck = cellsToCheck.get(0);
            cellsToCheck.remove(0);
            max = Math.max(max, cellToCheck.getDistance());
            this.maybeAddNeighbour(cellToCheck, cellsToCheck, cellToCheck.getX() - 1, cellToCheck.getY());
            this.maybeAddNeighbour(cellToCheck, cellsToCheck, cellToCheck.getX() + 1, cellToCheck.getY());
            this.maybeAddNeighbour(cellToCheck, cellsToCheck, cellToCheck.getX(), cellToCheck.getY() - 1);
            this.maybeAddNeighbour(cellToCheck, cellsToCheck, cellToCheck.getX(), cellToCheck.getY() + 1);

//            this.drawMap();
//            System.out.println();
        }

        return max;
    }

    private void maybeAddNeighbour(final Cell cellToCheck, final List<Cell> cellsToCheck, final int col, final int row) {
        final Cell neighbour = this.getCell(col, row);
        if (!this.canConnect(cellToCheck, neighbour)) {
            // can't connect
            return;
        }
        if (neighbour.getDistance() >= 0) {
            // already connected
            return;
        }
        neighbour.setDistance(cellToCheck.getDistance() + 1);
        cellsToCheck.add(neighbour);
    }

    private boolean canConnect(final Cell cellToCheck, final Cell neighbour) {
        if (cellToCheck == null || neighbour == null) {
            return false;
        }
        if (cellToCheck.outN() && neighbour.outS() && cellToCheck.getX() == neighbour.getX() && cellToCheck.getY() > neighbour.getY()) {
            return true;
        }
        if (cellToCheck.outS() && neighbour.outN() && cellToCheck.getX() == neighbour.getX() && cellToCheck.getY() < neighbour.getY()) {
            return true;
        }
        if (cellToCheck.outW() && neighbour.outE() && cellToCheck.getY() == neighbour.getY() && cellToCheck.getX() > neighbour.getX()) {
            return true;
        }
        if (cellToCheck.outE() && neighbour.outW() && cellToCheck.getY() == neighbour.getY() && cellToCheck.getX() < neighbour.getX()) {
            return true;
        }
        return false;
    }

    private void sortOutStart() {
        this.start.setDistance(0);

        // I'm NOT safe for edges
        Cell cell = this.getCell(this.start.getX() - 1, this.start.getY());
        final boolean needsW = cell != null && cell.outE();
        cell = this.getCell(this.start.getX() + 1, this.start.getY());
        final boolean needsE = cell != null && cell.outW();
        cell = this.getCell(this.start.getX(), this.start.getY() - 1);
        final boolean needsN = cell != null && cell.outS();
        cell = this.getCell(this.start.getX(), this.start.getY() + 1);
        final boolean needsS = cell != null && cell.outN();

        if (needsN && needsE) {
            this.start.setContents("L");
            return;
        }
        if (needsN && needsS) {
            this.start.setContents("|");
            return;
        }
        if (needsN && needsW) {
            this.start.setContents("J");
            return;
        }
        if (needsS && needsW) {
            this.start.setContents("7");
            return;
        }
        if (needsS && needsE) {
            this.start.setContents("F");
            return;
        }
        if (needsW && needsE) {
            this.start.setContents("-");
            return;
        }
        throw new RuntimeException("broken");
    }

    private Cell getCell(final int col, final int row) {
        final String key = col + "," + row;
        if (!this.cells.containsKey(key)) {
            return null;
        }
        return this.cells.get(key);
    }

    private Cell figureOutCell(final int col, final int row, final String substring) {
        return Cell.builder()
                .x(col)
                .y(row)
                .contents(substring)
                .distance(-1)
                .start(substring.equalsIgnoreCase("S"))
                .build();
    }

    @Override
    public String part2(final String[] input) {

        this.loadMap(input);

        final Cell testCell = this.getCell(5, 5);
        this.insideByAllDirections(testCell);

        final int inside = this.countInside4Ways();

        this.drawMap();

        return String.valueOf(inside);
    }

    private int countInside4Ways() {

        int inside = 0;
        for (int row = 0; row < this.height; row++) {
            final int insideCount = 0;
            for (int col = 0; col < this.width; col++) {
                final Cell cell = this.getCell(col, row);
                if (this.isJunk(cell)) {
                    inside++;
                    continue;
                }
                if (cell.getContents().equalsIgnoreCase(".")) {
                    if (this.insideByAllDirections(cell)) {
                        inside++;
                    }
                }
            }
        }

        return inside;
    }

    private boolean insideByAllDirections(final Cell cell) {
        int inside = 0;
        inside = inside + this.trackSaysImInside(cell, "N");
        inside = inside + this.trackSaysImInside(cell, "S");
        inside = inside + this.trackSaysImInside(cell, "E");
        inside = inside + this.trackSaysImInside(cell, "W");
        if (inside == 0) {
            return false;
        }
        if (inside == 4) {
            return true;
        }
        throw new RuntimeException("ambiguous");
    }

    private int trackSaysImInside(final Cell cell, final String direction) {
        int insideCount = 0;
        Cell workingCell = cell;

        while (true) {
            workingCell = this.getNextCell(workingCell, direction);
            if (workingCell == null) {
                break;
            }
            final Cell nextWorkingCell = this.getNextCell(workingCell, direction);
            if (this.isPipe(workingCell)) {
                if (List.of("N", "S").contains(direction) && nextWorkingCell != null && this.hasEWExit(nextWorkingCell)) {
                    continue;
                }
                if (List.of("E", "W").contains(direction) && nextWorkingCell != null && this.hasNSExit(nextWorkingCell)) {
                    continue;
                }
                insideCount = insideCount + 1;
            }
        }

        return insideCount % 2;
    }

    private boolean hasEWExit(final Cell workingCell) {
        return workingCell.outE() || workingCell.outW();
    }

    private boolean hasNSExit(final Cell workingCell) {
        return workingCell.outS() || workingCell.outN();
    }

    private Cell getNextCell(final Cell cell, final String direction) {
        switch (direction.toUpperCase()) {
            case "N":
                return this.getCell(cell.getX(), cell.getY() - 1);
            case "S":
                return this.getCell(cell.getX(), cell.getY() + 1);
            case "E":
                return this.getCell(cell.getX() + 1, cell.getY());
            case "W":
                return this.getCell(cell.getX() - 1, cell.getY());
            default:
                throw new RuntimeException(direction);
        }
    }

    private int countInsideFailed() {

        // this not valid for solid pipes

        int inside = 0;
        for (int row = 0; row < this.height; row++) {
            int insideCount = 0;
            for (int col = 0; col < this.width; col++) {
                final Cell cell = this.getCell(col, row);
                if (this.isJunk(cell)) {
                    inside++;
                    continue;
                }
                if (this.isPipe(cell)) {
                    insideCount++;
                    continue;
                }
                if (cell.getContents().equalsIgnoreCase(".")) {
                    if (insideCount % 2 == 1) {
                        inside++;
                        cell.setInsideOutside("I");
                    } else {
                        cell.setInsideOutside("O");
                    }
                    continue;
                }
                throw new RuntimeException("borked");
            }
        }
        return inside;
    }

    private boolean isPipe(final Cell cell) {
        return !cell.getContents().equalsIgnoreCase(".");
    }

    private boolean isJunk(final Cell cell) {
        if (cell.isStart() || cell.getDistance() > 0) {
            return false;
        }
        if (cell.getContents().equalsIgnoreCase(".")) {
            return false;
        }
        return true;
    }

    @Data
    @Builder
    private static final class Cell {

        private int x;
        private int y;
        private String contents;
        private String insideOutside;
        private int distance = -1;
        private boolean start = false;

        public boolean outN() {
            return List.of("|", "L", "J").contains(this.contents);
        }

        public boolean outE() {
            return List.of("-", "L", "F").contains(this.contents);
        }

        public boolean outS() {
            return List.of("|", "7", "F").contains(this.contents);
        }

        public boolean outW() {
            return List.of("-", "J", "7").contains(this.contents);
        }

    }
}
