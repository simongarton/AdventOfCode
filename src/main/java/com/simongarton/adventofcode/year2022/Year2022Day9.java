package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Year2022Day9 extends AdventOfCodeChallenge {

    private Map<String, Cell> cells;

    @Override
    public boolean run() {
        return this.runChallenge(2022, 9);
    }

    @Override
    public String part1(final String[] input) {
        return calculateMovesForRope(input, new Rope(2));
    }

    @Override
    public String part2(final String[] input) {
        return calculateMovesForRope(input, new Rope(10));
    }

    private String calculateMovesForRope(String[] input, Rope rope) {
        this.cells = new HashMap<>();
        getOrCreateCell(rope.getHead());

        for (String move : input) {
            moveRope(rope, move);
        }

        int visits = 0;
        for (Map.Entry<String, Cell> entry :cells.entrySet()) {
            if (entry.getValue().tailHasVisited) {
                visits ++;
            }
        }

        return String.valueOf(visits);
    }

    private void moveRope(Rope rope, String move) {
        String[] parts = move.split(" ");
        String direction = parts[0];
        int steps = Integer.parseInt(parts[1]);
        for (int step = 0; step < steps; step ++) {
            rope.moveHead(rope.getHead(), direction, steps);
            rope.tailFollows();
            updateCells(rope.tail());
        }
    }

    private void updateCells(Coord coord) {
        Cell cell = getOrCreateCell(coord);
        cell.tailHasVisited = true;
    }

    private Cell getOrCreateCell(Coord coord) {
        String key = coord.getKey();
        if (this.cells.containsKey(key)) {
            return this.cells.get(key);
        }
        Cell cell = new Cell(coord);
        this.cells.put(key, cell);
        return cell;
    }

    public static class Coord {
        private int x;
        private int y;

        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public String getKey() {
            return x + "," + y;
        }
    }

    public static class Rope {
        
        private List<Coord> knots;

        public Rope(int knotCount) {
            knots = new ArrayList<>();
            for (int i = 0; i < knotCount; i++) {
                knots.add(new Coord(0, 0));
            }
        }

        public Coord getHead() {
            return knots.get(0);
        }

        public void tailFollows() {
            // just the same, only I have to ripple down the rope
            for (int index = 0; index < knots.size() - 1; index ++) {
                tailFollowsOne(knots.get(index), knots.get(index + 1));
            }
        }

        public void tailFollowsOne(Coord head, Coord tail) {
            // cardinal points and overlapping
            int manhattanDistance = manhattanDistance(head, tail);
            if (manhattanDistance <= 1) {
                return;
            }
            // diagonals
            double euclideanDistance = euclideanDistance(head, tail);
            if (String.valueOf(euclideanDistance).startsWith("1.414")) {
                return;
            }
            // ok, onto the moves
            if (head.x == tail.x) {
                moveVertically(head, tail);
                return;
            }
            if (head.y == tail.y) {
                moveHorizontally(head, tail);
                return;
            }
            moveDiagonally(head, tail);
        }

        public Coord tail() {
            return knots.get(knots.size() -1);
        }

        public void moveHead(Coord head, String direction, int steps) {
            switch (direction) {
                case "R":
                    move(head, 1, 0);
                    break;
                case "L":
                    move(head, -1, 0);
                    break;
                case "U":
                    move(head, 0, 1);
                    break;
                case "D":
                    move(head, 0, -1);
                    break;
                default:
                    throw new RuntimeException("Bad move " + direction);
            }
        }

        private void move(Coord head, int deltaX, int deltaY) {
            head.x = head.x + deltaX;
            head.y = head.y + deltaY;
        }

        private void moveDiagonally(Coord head, Coord tail) {
            if (head.y > tail.y) {
                tail.y = tail.y + 1;
            } else {
                tail.y = tail.y - 1;
            }
            tail.x = tail.x +  ((head.x > tail.x) ? 1 : -1);
        }

        private void moveVertically(Coord head, Coord tail) {
            tail.y = tail.y +  ((head.y > tail.y) ? 1 : -1);
        }

        private void moveHorizontally(Coord head, Coord tail) {
            tail.x = tail.x +  ((head.x > tail.x) ? 1 : -1);
        }

        protected double euclideanDistance(Coord first, Coord second) {
            return Math.sqrt(
                    Math.pow(first.x - second.x, 2) +
                            Math.pow(first.y - second.y, 2)
            );
        }

        protected int manhattanDistance(Coord first, Coord second) {
            return Math.abs(first.x - second.x) + Math.abs(first.y - second.y);
        }
    }

    public static class Cell {

        private Coord coord;
        private boolean tailHasVisited;

        public Cell(Coord coord) {
            this.coord = coord;
            this.tailHasVisited = false;
        }
    }

}
