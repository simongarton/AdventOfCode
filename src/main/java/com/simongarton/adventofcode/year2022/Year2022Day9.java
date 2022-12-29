package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.Coord;

import java.util.*;

// After a refactor, I had to change the answer.
// Attempted 2022.9.1 expected 5695 got 5663

public class Year2022Day9 extends AdventOfCodeChallenge {

    private Map<String, Cell> cells;

    @Override
    public String title() {
        return "Day 9: Rope Bridge";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2022, 9);
    }

    @Override
    public String part1(final String[] input) {
        return this.calculateMovesForRope(input, new Rope(2));
    }

    @Override
    public String part2(final String[] input) {
        return this.calculateMovesForRope(input, new Rope(10));
    }

    private String calculateMovesForRope(final String[] input, final Rope rope) {
        this.cells = new HashMap<>();
        this.getOrCreateCell(rope.getHead());

        for (final String move : input) {
            this.moveRope(rope, move);
        }

        int visits = 0;
        for (final Map.Entry<String, Cell> entry : this.cells.entrySet()) {
            if (entry.getValue().tailHasVisited) {
                visits++;
            }
        }

        return String.valueOf(visits);
    }

    private void moveRope(final Rope rope, final String move) {
        final String[] parts = move.split(" ");
        final String direction = parts[0];
        final int steps = Integer.parseInt(parts[1]);
        for (int step = 0; step < steps; step++) {
            rope.moveHead(rope.getHead(), direction, steps);
            rope.tailFollows();
            this.updateCells(rope.tail());
        }
    }

    private void updateCells(final Coord coord) {
        final Cell cell = this.getOrCreateCell(coord);
        cell.tailHasVisited = true;
    }

    private Cell getOrCreateCell(final Coord coord) {
        final String key = coord.toString();
        if (this.cells.containsKey(key)) {
            return this.cells.get(key);
        }
        final Cell cell = new Cell(coord);
        this.cells.put(key, cell);
        return cell;
    }

    public static class Rope {

        private final List<Coord> knots;

        public Rope(final int knotCount) {
            this.knots = new ArrayList<>();
            for (int i = 0; i < knotCount; i++) {
                this.knots.add(new Coord(0, 0));
            }
        }

        public Coord getHead() {
            return this.knots.get(0);
        }

        public void tailFollows() {
            // just the same, only I have to ripple down the rope
            for (int index = 0; index < this.knots.size() - 1; index++) {
                this.tailFollowsOne(this.knots.get(index), this.knots.get(index + 1));
            }
        }

        public void tailFollowsOne(final Coord head, final Coord tail) {
            // cardinal points and overlapping
            final int manhattanDistance = this.manhattanDistance(head, tail);
            if (manhattanDistance <= 1) {
                return;
            }
            // diagonals
            final double euclideanDistance = this.euclideanDistance(head, tail);
            if (String.valueOf(euclideanDistance).startsWith("1.414")) {
                return;
            }
            // ok, onto the moves
            if (Objects.equals(head.getX(), tail.getX())) {
                this.moveVertically(head, tail);
                return;
            }
            if (Objects.equals(head.getY(), tail.getY())) {
                this.moveHorizontally(head, tail);
                return;
            }
            this.moveDiagonally(head, tail);
        }

        public Coord tail() {
            return this.knots.get(this.knots.size() - 1);
        }

        public void moveHead(final Coord head, final String direction, final int steps) {
            switch (direction) {
                case "R":
                    this.move(head, 1, 0);
                    break;
                case "L":
                    this.move(head, -1, 0);
                    break;
                case "U":
                    this.move(head, 0, 1);
                    break;
                case "D":
                    this.move(head, 0, -1);
                    break;
                default:
                    throw new RuntimeException("Bad move " + direction);
            }
        }

        private void move(final Coord head, final int deltaX, final int deltaY) {
            head.setX(head.getX() + deltaX);
            head.setY(head.getY() + deltaY);
        }

        private void moveDiagonally(final Coord head, final Coord tail) {
            if (head.getY() > tail.getY()) {
                tail.setY(tail.getY() + 1);
            } else {
                tail.setY(tail.getY() - 1);
            }
            tail.setX(tail.getX() + ((head.getX() > tail.getX()) ? 1 : -1));
        }

        private void moveVertically(final Coord head, final Coord tail) {
            tail.setY(tail.getY() + ((head.getY() > tail.getY()) ? 1 : -1));
        }

        private void moveHorizontally(final Coord head, final Coord tail) {
            tail.setX(tail.getX() + ((head.getX() > tail.getX()) ? 1 : -1));
        }

        protected double euclideanDistance(final Coord first, final Coord second) {
            return Math.sqrt(
                    Math.pow(first.getX() - second.getX(), 2) +
                            Math.pow(first.getY() - second.getY(), 2)
            );
        }

        protected int manhattanDistance(final Coord first, final Coord second) {
            return Math.abs(first.getX() - second.getX()) + Math.abs(first.getY() - second.getY());
        }
    }

    public static class Cell {

        private final Coord coord;
        private boolean tailHasVisited;

        public Cell(final Coord coord) {
            this.coord = coord;
            this.tailHasVisited = false;
        }
    }
}
