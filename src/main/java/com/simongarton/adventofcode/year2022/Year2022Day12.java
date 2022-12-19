package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;
import java.util.stream.Collectors;

public class Year2022Day12 extends AdventOfCodeChallenge {

    private static final String START = "start";
    private static final String END = "end";
    private static final boolean DEBUG = false;

    private List<Cell> map;
    private int height;
    private int width;

    @Override
    public String title() {
        return "Day 12: Hill Climbing Algorithm";
    }

    @Override
    public boolean run() {
        return this.runChallenge(2022, 12);
    }

    @Override
    public String part1(final String[] input) {
        this.loadMap(input);
        this.buildNeighbours();
        final List<Cell> path = this.aStar();
        if (DEBUG) {
            for (final Cell cell : path) {
                System.out.println(cell);
            }
        }
        return String.valueOf(path.size() - 1);
    }

    @Override
    public String part2(final String[] input) {
        this.loadMap(input);
        this.buildNeighbours();
        this.resetStart();
        final List<Cell> starts = this.findStarts();
        int bestPath = 0;
        for (final Cell start : starts) {
            List<Cell> path = null;
            try {
                path = this.aStar(start);
            } catch (final RuntimeException e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
                continue;
            }
            final int score = path.size() - 1;
            if (bestPath == 0 || bestPath > score) {
                bestPath = score;
            }
        }
        return String.valueOf(bestPath);
    }

    private List<Cell> findStarts() {
        return this.map.stream().filter(c -> c.height == 0).collect(Collectors.toList());
    }

    private void resetStart() {
        for (final Cell cell : this.map) {
            if (cell.value.equalsIgnoreCase("S")) {
                cell.value = "a";
            }
        }
    }

    private List<Cell> aStar() {

        final Cell start = this.getStart();
        return this.aStar(start);
    }

    private List<Cell> aStar(final Cell start) {
        final boolean debug = false;

        final Cell end = this.getEnd();

        final Set<Cell> openSet = new HashSet<>(Collections.singleton(start));
        final Map<Cell, Cell> cameFrom = new HashMap<>();
        final Map<Cell, Integer> gScore = new HashMap<>();
        gScore.put(start, 0);

        final Map<Cell, Integer> fScore = new HashMap<>();
        fScore.put(start, this.h(start, start));

        while (!openSet.isEmpty()) {
            final Cell current = this.bestOpenSetWithFScoreValue(openSet, fScore);
            if (current == end) {
                return this.reconstructPath(cameFrom, current);
            }
            this.debugPrint(debug, "working on / removing current " + current.toString() + " with openSet.size()=" + openSet.size());
            openSet.remove(current);
            for (final Cell neighbor : current.neighbours) {
                // d(current,neighbor) is the weight of the edge from current to neighbor
                // tentative_gScore is the distance from start to the neighbor through current
                final int tentative_gScore = gScore.get(current) + this.cost(current, neighbor);
                this.debugPrint(debug, "  checking neighbour " + neighbor.toString() + " tentative_gScore=" + tentative_gScore);
                if (tentative_gScore < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    // This path to neighbor is better than any previous one. Record it!
                    this.debugPrint(debug, "     using neighbour " + neighbor);
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentative_gScore);
                    fScore.put(neighbor, tentative_gScore + this.h(start, neighbor));
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                } else {
                    this.debugPrint(debug, "     ignoring neighbour " + neighbor);
                }
            }
        }

        // Open set is empty but goal was never reached
        throw new RuntimeException("AStar failed.");
    }

    private void debugPrint(final boolean debug, final String s) {
        if (debug) {
            System.out.println(s);
        }
    }

    private Cell bestOpenSetWithFScoreValue(final Set<Cell> openSet, final Map<Cell, Integer> fScore) {
        Cell best = null;
        int bestValue = 0;
        for (final Cell cell : openSet) {
            final int cost = fScore.get(cell); // may not be there ?
            if (best == null || bestValue > cost) {
                best = cell;
                bestValue = cost;
            }
        }
        return best;
    }

    private Integer h(final Cell start, final Cell end) {
        return this.manhattan(start, end);
    }

    private Integer manhattan(final Cell start, final Cell end) {
        return Math.abs(start.x - end.x) + Math.abs(start.y - end.y);
    }

    private Integer cost(final Cell current, final Cell neighbor) {
        return 1;
    }

    private List<Cell> reconstructPath(final Map<Cell, Cell> cameFrom, final Cell end) {
        Cell current = end;
        final List<Cell> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        return path;
    }

    private Cell getEnd() {
        return this.map.stream().filter(c -> END.equalsIgnoreCase(c.type)).findFirst().orElseThrow(() -> new RuntimeException("No " + END));
    }

    private Cell getStart() {
        return this.map.stream().filter(c -> START.equalsIgnoreCase(c.type)).findFirst().orElseThrow(() -> new RuntimeException("No " + START));
    }

    private Cell getCell(final int row, final int col) {
        if (row < 0 || row >= this.height) {
            return null;
        }
        if (col < 0 || col >= this.width) {
            return null;
        }
        final int address = (row * this.width) + col;
        return this.map.get(address);
    }

    private void loadMap(final String[] input) {
        this.map = new ArrayList<>();
        int row = 0;
        for (final String line : input) {
            for (int col = 0; col < line.length(); col++) {
                final String square = line.substring(col, col + 1);
                final Cell cell = this.buildCoord(square, row, col);
                this.map.add(cell);
            }
            row++;
        }
        this.width = input[0].length();
        this.height = input.length;
    }

    private Cell buildCoord(final String square, final int row, final int col) {
        final Cell cell = new Cell();
        cell.value = square;
        cell.type = this.figureCoordType(square);
        cell.x = col;
        cell.y = row;
        cell.height = this.figureHeight(square);
        return cell;
    }

    private int figureHeight(final String square) {
        if (square.equals("E")) {
            return 26;
        }
        if (square.equals("S")) {
            return 0;
        }
        return (int) square.charAt(0) - 97;
    }

    private String figureCoordType(final String square) {
        switch (square) {
            case "S":
                return START;
            case "E":
                return END;
            default:
                return null;
        }
    }

    private void buildNeighbours() {
        for (int i = 0; i < this.map.size(); i++) {
            this.buildNeighbours(i);
        }
    }

    private void buildNeighbours(final int i) {
        final Cell cell = this.map.get(i);
        cell.neighbours = new ArrayList<>();
        this.addNeighbour(cell, -1, 0);
        this.addNeighbour(cell, 1, 0);
        this.addNeighbour(cell, 0, -1);
        this.addNeighbour(cell, 0, 1);
    }

    private void addNeighbour(final Cell cell, final int row, final int col) {
        final Cell neighbour = this.getCell(cell.y + row, cell.x + col);
        if (neighbour != null) {
            final int deltaCost = neighbour.height - cell.height;
            if (deltaCost <= 1) {
                cell.neighbours.add(neighbour);
            }
        }
    }

    private int figureRow(final int i) {
        return i / this.height;
    }

    private int figureCol(final int i) {
        return i % this.width;
    }

    public static final class Cell {

        private String type;
        private String value;
        private int height;
        private int x;
        private int y;
        private List<Cell> neighbours;

        @Override
        public String toString() {
            String line = this.x + "," + this.y + " [" + this.height + "] '" + this.value + "'";
            if (this.type != null) {
                line = line + " {" + this.type + "}";
            }
            line = line + " (" + this.neighbours.size() + ")";
            return line;
        }
    }
}
