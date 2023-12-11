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

    private Map<String, Cell> cells;
    private Cell start;
    private int width;
    private int height;

    private static final int MAP_TILE = 5;

    @Override
    public String title() {
        return "Day 10: Pipe Maze";
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

    private void paintPipeCells(final Graphics2D graphics2D) {
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                final Cell cell = this.getCell(col, row);
                this.paintPipeCell(graphics2D, cell);
            }
        }
    }

    private void paintPipeCell(final Graphics2D graphics2D, final Cell cell) {
        final int x0 = cell.getX() * MAP_TILE;
        final int y0 = cell.getY() * MAP_TILE;
        final int x1 = x0 + (MAP_TILE / 2);
        final int x2 = x0 + MAP_TILE;
        final int y1 = y0 + (MAP_TILE / 2);
        final int y2 = y0 + MAP_TILE;
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
                graphics2D.fillRect(x1, y1, 1, 1);
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

    private void clearBackground(final Graphics2D graphics2D) {

        graphics2D.setPaint(Color.BLACK);
        graphics2D.fillRect(0, 0, 800, 600);
    }

    private void drawMap() {

        for (int row = 0; row < this.height; row++) {
            final StringBuilder line = new StringBuilder();
            for (int col = 0; col < this.width; col++) {
                final Cell cell = this.getCell(col, row);
                if (cell.getContents().equalsIgnoreCase(".")) {
                    line.append(cell.getInsideOutside() == null ? "." : cell.getInsideOutside());
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
                line.append(draw);
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

        final int max = this.sortOutDistances();

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

        final int inside = this.driveAround();

        this.paintPipeMap("pipe-map.png");

//        this.drawMap();

        return String.valueOf(inside);
    }

    private int driveAround() {

        final Set<Cell> floodFillNeeded = new HashSet<>();

        PositionAndDirection driver = PositionAndDirection.builder()
                .cell(this.start)
                .direction("S")
                .build();
        do {
            driver = this.drive(driver);
            this.lookRight(driver, floodFillNeeded);
        } while (driver.getCell() != this.start);

        final int inside = this.floodFill(floodFillNeeded);

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

        // figure out the next cell I'm arriving at, and what direction I am turning in

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

        // I have just arrived at this cell. For the bends, I need to consider which direction I was
        // driving in when I arrived.

        // I think I'm getting confused here with travel direction and look direction
        // at this point I HAVE TURNED
        final String drivingDirection = driver.getDirection();

        final List<Cell> rightNeighbours = new ArrayList<>();
        switch (driver.getCell().getContents()) {
            default:
                throw new RuntimeException(driver.getCell().getContents());
            case ".":
                return;
            case "|":
                if (drivingDirection.equalsIgnoreCase("N")) {
                    rightNeighbours.addAll(this.sameRightNeighboursForDirection(List.of("E"), driver));
                } else {
                    rightNeighbours.addAll(this.sameRightNeighboursForDirection(List.of("W"), driver));
                }
                this.checkRightNeighbours(rightNeighbours, floodFillNeeded);
                break;
            case "-":
                if (drivingDirection.equalsIgnoreCase("E")) {
                    rightNeighbours.addAll(this.sameRightNeighboursForDirection(List.of("S"), driver));
                } else {
                    rightNeighbours.addAll(this.sameRightNeighboursForDirection(List.of("N"), driver));
                }
                this.checkRightNeighbours(rightNeighbours, floodFillNeeded);
                break;
            case "L":
                if (drivingDirection.equalsIgnoreCase("N")) {
                    rightNeighbours.addAll(this.sameRightNeighboursForDirection(List.of("N", "E"), driver));
                }
                if (drivingDirection.equalsIgnoreCase("E")) {
                    rightNeighbours.addAll(this.sameRightNeighboursForDirection(List.of("W", "S"), driver));
                }
                this.checkRightNeighbours(rightNeighbours, floodFillNeeded);
                break;
            case "F":
                if (drivingDirection.equalsIgnoreCase("E")) {
                    rightNeighbours.addAll(this.sameRightNeighboursForDirection(List.of("E", "S"), driver));
                }
                if (drivingDirection.equalsIgnoreCase("S")) {
                    rightNeighbours.addAll(this.sameRightNeighboursForDirection(List.of("N", "W"), driver));
                }
                this.checkRightNeighbours(rightNeighbours, floodFillNeeded);
                break;
            case "J":
                if (drivingDirection.equalsIgnoreCase("N")) {
                    rightNeighbours.addAll(this.sameRightNeighboursForDirection(List.of("S", "E"), driver));
                }
                if (drivingDirection.equalsIgnoreCase("W")) {
                    rightNeighbours.addAll(this.sameRightNeighboursForDirection(List.of("N", "W"), driver));
                }
                this.checkRightNeighbours(rightNeighbours, floodFillNeeded);
                break;
            case "7":
                if (drivingDirection.equalsIgnoreCase("S")) {
                    rightNeighbours.addAll(this.sameRightNeighboursForDirection(List.of("S", "W"), driver));
                }
                if (drivingDirection.equalsIgnoreCase("W")) {
                    rightNeighbours.addAll(this.sameRightNeighboursForDirection(List.of("N", "E"), driver));
                }
                this.checkRightNeighbours(rightNeighbours, floodFillNeeded);
                break;
        }
    }

    private List<Cell> sameRightNeighboursForDirection(final List<String> directions, final PositionAndDirection driver) {
        final List<Cell> rightNeighbours = new ArrayList<>();
        final Map<String, String> options = new HashMap<>();
        options.put("N", "E");
        options.put("E", "S");
        options.put("S", "W");
        options.put("W", "N");
        for (final String direction : directions) {
            //rightNeighbours.add(this.getNextCell(driver.getCell(), options.get(direction)));
            rightNeighbours.add(this.getNextCell(driver.getCell(), direction));
        }
        return rightNeighbours;
    }

    private void checkRightNeighbours(final List<Cell> rightNeighbours, final Set<Cell> floodFillNeeded) {
        for (final Cell rightNeighbour : rightNeighbours) {
            if (rightNeighbour.getContents().equalsIgnoreCase(".")) {
                floodFillNeeded.add(rightNeighbour);
            }
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
