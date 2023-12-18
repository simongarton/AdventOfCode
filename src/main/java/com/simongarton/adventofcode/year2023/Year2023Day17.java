package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.*;

public class Year2023Day17 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;

    private List<Cell> map;
    private int height;
    private int width;

    @Override
    public String title() {
        return "Day 17: Clumsy Crucible";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 17);
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
        return null;
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

    private Integer h(final Cell start, final Cell end) {
        return this.manhattan(start, end);
    }

    private Integer manhattan(final Cell start, final Cell end) {
        return Math.abs(start.x - end.x) + Math.abs(start.y - end.y);
    }

    private List<Cell> aStar() {

        final Cell start = this.getStart();
        return this.aStar(start);
    }

    private Cell getStart() {
        return this.getCell(0, 0);
    }

    private Cell getEnd() {
        return this.getCell(this.height - 1, this.width - 1);
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

    private Integer cost(final Cell current, final Cell neighbor) {
        return neighbor.getCost();
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

    private Cell buildCoord(final String square, final int row, final int col) {
        final Cell cell = Cell.builder()
                .value(square)
                .x(col)
                .y(row)
                .cost(Integer.parseInt(square))
                .build();
        return cell;
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
            cell.neighbours.add(neighbour);
        }
    }

    @Data
    @Builder
    private static final class Cell {

        private String value;
        private int cost;
        private int x;
        private int y;
        private List<Cell> neighbours;

        @Override
        public String toString() {
            String line = this.x + "," + this.y + " [" + this.cost + "] '" + this.value + "'";
            line = line + " (" + this.neighbours.size() + ")";
            return line;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Cell cell = (Cell) o;
            return this.x == cell.x && this.y == cell.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.x, this.y);
        }
    }
}
