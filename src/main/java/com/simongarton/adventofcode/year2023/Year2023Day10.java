package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;


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

    Flood fill won't work : I can close off entire unenclosed sections.

    OK. Pick a start cell - can do with code or just eyeball. Make it a vertical one on the left hand side.
    Now drive around the loop, following the corners - so I need to keep track of directions.
    For each horizontal and vertical (only), look to my right. If it's "." make it a "I" and add it to a list.
    Then floodfill the list.
    Then loop over and count.

    5187 too high

     */

    private Map<String, Cell> cells;
    private Cell start;
    private int width;
    private int height;

    private static final int MAP_TILE = 5;

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

    private void drawMap2() {

        for (int row = 0; row < this.height; row++) {
            final StringBuilder line = new StringBuilder();
            for (int col = 0; col < this.width; col++) {
                final Cell cell = this.getCell(col, row);
                if (cell.getContents().equalsIgnoreCase(".")) {
                    line.append(".");
                } else {
                    line.append("O");
                }
            }
            System.out.println(line);
        }
        System.out.println();
    }

    private void paintMap(final String filename) {

        final BufferedImage bufferedImage = new BufferedImage(this.width * MAP_TILE, this.height * MAP_TILE, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);
        this.paintCells(graphics2D);
        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void paintPipeMap(final String filename) {

        final BufferedImage bufferedImage = new BufferedImage(this.width * MAP_TILE, this.height * MAP_TILE, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);
        this.paintPipeCells(graphics2D);
        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void paintCells(final Graphics2D graphics2D) {
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                final Cell cell = this.getCell(col, row);
                this.paintCell(graphics2D, cell);
            }
        }
    }

    private void paintPipeCells(final Graphics2D graphics2D) {
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                final Cell cell = this.getCell(col, row);
                this.paintPipeCell(graphics2D, cell);
            }
        }
    }

    private void paintPipeCell(final Graphics2D graphics2D, final Cell cell) {
        final int x = cell.getX() * MAP_TILE;
        final int y = cell.getY() * MAP_TILE;
        final int x0 = x;
        final int x1 = x + (MAP_TILE / 2);
        final int x2 = x + MAP_TILE;
        final int y0 = y;
        final int y1 = y + (MAP_TILE / 2);
        final int y2 = y + MAP_TILE;
        graphics2D.setPaint(new Color(0, 200, 0));
        if (cell.isStart()) {
            graphics2D.setPaint(new Color(255, 255, 255));
        }
        switch (cell.getContents()) {
            case "O":
                graphics2D.setPaint(new Color(200, 0, 0));
                graphics2D.fillRect(x1 - 1, y1 - 1, 3, 3);
                break;
            case "I":
                graphics2D.setPaint(new Color(0, 0, 255));
                graphics2D.fillRect(x1 - 1, y1 - 1, 3, 3);
                break;
            default:
                graphics2D.setPaint(new Color(200, 0, 0));
                graphics2D.fillRect(x1 - 0, y1 - 0, 1, 1);
                break;
            case "|":
                graphics2D.drawLine(x1, y0, x1, y2);
                break;
            case "-":
                graphics2D.drawLine(x0, y1, x2, y1);
                break;
            case "L":
                graphics2D.drawLine(x1, y0, x1, y1);
                graphics2D.drawLine(x1, y1, x2, y1);
                break;
            case "J":
                graphics2D.drawLine(x1, y0, x1, y1);
                graphics2D.drawLine(x1, y1, x0, y1);
                break;
            case "7":
                graphics2D.drawLine(x0, y1, x1, y1);
                graphics2D.drawLine(x1, y1, x1, y2);
                break;
            case "F":
                graphics2D.drawLine(x1, y2, x1, y1);
                graphics2D.drawLine(x1, y1, x2, y1);
                break;
        }
    }

    private void paintCell(final Graphics2D graphics2D, final Cell cell) {

        final int left = cell.getX() * MAP_TILE;
        final int top = cell.getY() * MAP_TILE;
        if (cell.getContents().equalsIgnoreCase(".")) {
            graphics2D.setPaint(Color.BLACK);
            graphics2D.fillRect(left, top, MAP_TILE, MAP_TILE);
        } else {
            graphics2D.setPaint(new Color(0, 0, 200));
            graphics2D.fillRect(left, top, MAP_TILE, MAP_TILE);
        }
    }

    private void clearBackground(final Graphics2D graphics2D) {

        graphics2D.setPaint(Color.BLACK);
        graphics2D.fillRect(0, 0, 800, 600);
    }

    private void drawMap() {

        for (int row = 0; row < this.height; row++) {
            String line = "";
            for (int col = 0; col < this.width; col++) {
                final Cell cell = this.getCell(col, row);
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
                if (cell.getInsideOutside() != null) {
                    draw = cell.getInsideOutside();
                }
                line += draw;
            }
            System.out.println(line);
        }
        System.out.println();
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

        // Any tile that isn't part of the main loop can count as being enclosed by the loop.
        // is that all junk ? or just junk enclosed ?
        this.convertAllJunkToDots();

        this.drawMap2();
        this.paintMap("pipe-map-overview.png");
        this.paintPipeMap("pipe-map-before.png");

        final int inside = this.driveAround();

        this.paintPipeMap("pipe-map.png");

        this.drawMap();

        return String.valueOf(inside);
    }

    private int driveAround() {

        final Set<Cell> floodFillNeeded = new HashSet<>();

        PositionAndDirection driver = PositionAndDirection.builder()
                .cell(this.start)
                .direction("E")
                .build();
        do {
            driver = this.drive(driver);
            this.lookRight(driver, floodFillNeeded);
        } while (driver.getCell() != this.start);

        final int inside = this.staticFloodFill(floodFillNeeded);

        return inside;
    }

    private int staticFloodFill(final Set<Cell> floodFillNeeded) {

        int inside = 0;
        final List<Cell> cellsToCheck = new ArrayList<>(floodFillNeeded);
        while (!cellsToCheck.isEmpty()) {
            final Cell cellToCheck = cellsToCheck.get(0);
            cellsToCheck.remove(0);
            if (cellToCheck.getContents().equalsIgnoreCase(".")) {
                cellToCheck.setContents("I");
                inside++;
            }
        }

        return inside;
    }

    private int floodFill(final Set<Cell> floodFillNeeded) {

        int inside = 0;
        final List<Cell> cellsToCheck = new ArrayList<>(floodFillNeeded);
        while (!cellsToCheck.isEmpty()) {
            final Cell cellToCheck = cellsToCheck.get(0);
            cellsToCheck.remove(0);
            if (cellToCheck.getContents().equalsIgnoreCase(".")) {
                cellToCheck.setContents("I");
                inside++;
            }
            Cell cell = this.getCell(cellToCheck.getX() - 1, cellToCheck.getY());
            if (cell != null && cell.getContents().equalsIgnoreCase(".")) {
                if (!cellsToCheck.contains(cell)) {
                    cellsToCheck.add(cell);
                }
            }
            cell = this.getCell(cellToCheck.getX() + 1, cellToCheck.getY());
            if (cell != null && cell.getContents().equalsIgnoreCase(".")) {
                if (!cellsToCheck.contains(cell)) {
                    cellsToCheck.add(cell);
                }
            }
            cell = this.getCell(cellToCheck.getX(), cellToCheck.getY() - 1);
            if (cell != null && cell.getContents().equalsIgnoreCase(".")) {
                if (!cellsToCheck.contains(cell)) {
                    cellsToCheck.add(cell);
                }
            }
            cell = this.getCell(cellToCheck.getX(), cellToCheck.getY() + 1);
            if (cell != null && cell.getContents().equalsIgnoreCase(".")) {
                if (!cellsToCheck.contains(cell)) {
                    cellsToCheck.add(cell);
                }
            }
        }

        return inside;
    }

    private PositionAndDirection drive(final PositionAndDirection driver) {

        driver.setCell(this.getNextCell(driver.getCell(), driver.getDirection()));
        switch (driver.getCell().getContents()) {
            case "|":
                // nothing : I'm driving straight
                break;
            case "-":
                // nothing : I'm driving straight
                break;
            case "L": {
                if (driver.getDirection().equalsIgnoreCase("S")) {
                    driver.setDirection("E");
                    break;
                }
                if (driver.getDirection().equalsIgnoreCase("W")) {
                    driver.setDirection("N");
                    break;
                }
                throw new RuntimeException("crash");
            }
            case "J": {
                if (driver.getDirection().equalsIgnoreCase("S")) {
                    driver.setDirection("W");
                    break;
                }
                if (driver.getDirection().equalsIgnoreCase("E")) {
                    driver.setDirection("N");
                    break;
                }
                throw new RuntimeException("crash");
            }
            case "7": {
                if (driver.getDirection().equalsIgnoreCase("N")) {
                    driver.setDirection("W");
                    break;
                }
                if (driver.getDirection().equalsIgnoreCase("E")) {
                    driver.setDirection("S");
                    break;
                }
                throw new RuntimeException("crash");
            }
            case "F": {
                if (driver.getDirection().equalsIgnoreCase("N")) {
                    driver.setDirection("E");
                    break;

                }
                if (driver.getDirection().equalsIgnoreCase("W")) {
                    driver.setDirection("S");
                    break;
                }
                throw new RuntimeException("crash");
            }
            default: {
                throw new RuntimeException("out");
            }
        }
        return driver;
    }

    private void lookRight(final PositionAndDirection driver, final Set<Cell> floodFillNeeded) {

        final Cell rightNeighbour;
        switch (driver.getCell().getContents()) {
            default:
                // nothing doing
                return;
            case "|":
                // assuming I can only be going N/S
                if (driver.getDirection().equalsIgnoreCase("N")) {
                    rightNeighbour = this.getNextCell(driver.getCell(), "E");
                } else {
                    rightNeighbour = this.getNextCell(driver.getCell(), "W");
                }
                if (rightNeighbour.getContents().equalsIgnoreCase(".")) {
                    floodFillNeeded.add(rightNeighbour);
                }
                break;
            case "-":
                if (driver.getDirection().equalsIgnoreCase("E")) {
                    rightNeighbour = this.getNextCell(driver.getCell(), "S");
                } else {
                    rightNeighbour = this.getNextCell(driver.getCell(), "N");
                }
                if (rightNeighbour.getContents().equalsIgnoreCase(".")) {
                    floodFillNeeded.add(rightNeighbour);
                }
                break;
        }
    }

    private void convertAllJunkToDots() {
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                final Cell cell = this.getCell(col, row);
                if (this.isJunk(cell)) {
                    cell.setContents(".");
                }
            }
        }
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
    private static final class PositionAndDirection {

        private Cell cell;
        private String direction;

        private String key() {
            return this.cell.getX() + "," + this.cell.getY();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final PositionAndDirection that = (PositionAndDirection) o;
            return this.key().equalsIgnoreCase(that.key());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.key());
        }
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
