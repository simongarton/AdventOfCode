package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;

public class Year2021Day15 extends AdventOfCodeChallenge {

    private List<Chiton> chitons;
    private int width;
    private int height;
    private Chiton startChiton;
    private Chiton endChiton;

    @Override
    public String title() {
        return "Day 15: Chiton";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2021, 15);
    }

    @Override
    public String part1(final String[] input) {
        this.width = input[0].length();
        this.height = input.length;
        this.loadChitons(input);
//        this.printChitons();
        this.startChiton = this.chitons.get(0);
        this.endChiton = this.chitons.get(-1 + this.width * this.height);
        final List<Chiton> path = this.aStar(this.startChiton, this.endChiton);
        final long result = this.score(path);
        return String.valueOf(result);
    }

    @Override
    public String part2(final String[] input) {
        this.width = input[0].length();
        this.height = input.length;
        this.loadChitons(input);
        this.expandChitons();
//        this.printChitons();
        this.startChiton = this.chitons.get(0);
        this.endChiton = this.chitons.get(-1 + (this.width) * (this.height));
        final List<Chiton> path = this.aStar(this.startChiton, this.endChiton);
        final long result = this.score(path);
        return String.valueOf(result);
    }

    private long score(final List<Chiton> path) {
        // path.forEach(c -> System.out.println(c + "=" + c.value));
        return path.stream().map(c -> c.value).mapToLong(Long::valueOf).sum() - this.startChiton.value;
    }

    private List<Chiton> aStar(final Chiton startChiton, final Chiton endChiton) {

        // I don't think this is right.
        // I adapted the algorithm from Wikipedia
        // https://en.wikipedia.org/wiki/A*_search_algorithm
        // but either I haven't done it right, or it's wrong.
        // I never try and pick the best node towards the end with costToGetToEnd()
        // and in their example, fScore is never used.

        // I think I'm just blundering around, and so my performance could be faster.

        // review https://stackabuse.com/graphs-in-java-a-star-algorithm/ and
        // https://www.baeldung.com/java-a-star-pathfinding

        final List<Chiton> openSet = new ArrayList<>();
        openSet.add(startChiton);

        final Map<Chiton, Chiton> cameFrom = new HashMap<>();
        final Map<Chiton, Long> costMap = new HashMap<>();
        for (final Chiton c : this.chitons) {
            costMap.put(c, Long.MAX_VALUE);
        }

        costMap.put(startChiton, 0L);

        while (!openSet.isEmpty()) {
            openSet.sort(Comparator.comparing(s -> s.score));
            final Chiton current = openSet.get(0);
            if (current.equals(endChiton)) {
                return this.buildPath(cameFrom, current);
            }
            openSet.remove(0);
            final List<Chiton> neighbours = this.getNeighbours(current);
            for (final Chiton neighbour : neighbours) {
                final long tentativeGscore = costMap.get(current) + neighbour.value;
                if (tentativeGscore < costMap.get(neighbour)) {
                    cameFrom.put(neighbour, current);
                    costMap.put(neighbour, tentativeGscore);
                    // this seems wrong, but is faster : 4s, not 6s
                    neighbour.score = tentativeGscore;
                    // this ... might ? ... be the right way
                    // neighbour.score = tentativeGscore + costToGetToEnd(neighbour);
                    if (!openSet.contains(neighbour)) {
                        openSet.add(neighbour);
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    private List<Chiton> getNeighbours(final Chiton current) {
        final List<Chiton> neighbours = new ArrayList<>();
        this.maybeAddCoord(neighbours, current.x + 1, current.y);
        this.maybeAddCoord(neighbours, current.x - 1, current.y);
        this.maybeAddCoord(neighbours, current.x, current.y + 1);
        this.maybeAddCoord(neighbours, current.x, current.y - 1);
        return neighbours;
    }

    private void maybeAddCoord(final List<Chiton> neighbours, final int x, final int y) {
        if (x < 0 || x >= this.width) {
            return;
        }
        if (y < 0 || y >= this.height) {
            return;
        }
        neighbours.add(this.chitons.get((y * this.width) + x));
    }

    private List<Chiton> buildPath(final Map<Chiton, Chiton> cameFrom, Chiton current) {
        final List<Chiton> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            final Chiton previous = cameFrom.get(current);
            path.add(0, previous);
            current = previous;
        }
        return path;
    }

    private long costToGetToEnd(final Chiton chiton) {
        final int c = Math.abs(chiton.x - this.endChiton.x);
        final int b = Math.abs(chiton.y - this.endChiton.y);
        return Math.round(Math.sqrt((c * c) + (b * b)));
    }

    private void loadChitons(final String[] lines) {
        this.chitons = new ArrayList<>();
        int row = 0;
        for (final String line : lines) {
            for (int col = 0; col < this.width; col++) {
                final Chiton chiton = new Chiton(col, row);
                chiton.value = Integer.parseInt(line.charAt(col) + "");
                this.chitons.add(chiton);
            }
            row++;
        }
    }

    private void expandChitons() {
        final List<Chiton> gridChitons = new ArrayList<>();
        final int expandedWidth = this.width * 5;
        final int expandedHeight = this.height * 5;
        for (int row = 0; row < expandedHeight; row++) {
            for (int col = 0; col < expandedWidth; col++) {
                final int tileX = col / this.width;
                final int tileY = row / this.height;
                final int originalCol = col % this.width;
                final int originalRow = row % this.height;
                final Chiton chiton = this.chitons.get((originalRow * this.width) + originalCol);
                final Chiton gridChiton = new Chiton(col, row);
                gridChiton.value = this.scale91(chiton.value, tileX, tileY);
                gridChitons.add(gridChiton);
            }
        }
        this.chitons.clear();
        this.chitons.addAll(gridChitons);
        this.width = expandedWidth;
        this.height = expandedHeight;
    }

    private int scale91(final int value, final int tileX, final int tileY) {
        final int newValue = value + tileX + tileY - 1;
        return (newValue % 9) + 1;
    }

    private void printChitons() {
        for (int row = 0; row < this.height; row++) {
            final StringBuilder line = new StringBuilder();
            for (int col = 0; col < this.width; col++) {
                final Chiton chiton = this.chitons.get((row * this.width) + col);
                line.append(chiton.value);
            }
            System.out.println(line);
        }
        System.out.println();
    }


    private static final class Chiton {
        final int x;
        final int y;
        int value;
        long score;

        public Chiton(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Chiton chiton = (Chiton) o;
            return this.x == chiton.x && this.y == chiton.y;
        }

        @Override
        public String toString() {
            return "(" + this.x + "," + this.y + ")";
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.x, this.y);
        }
    }
}
