package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.HashMap;
import java.util.Map;

public class Year2022Day9 extends AdventOfCodeChallenge {

    private Map<String, Cell> cells;
    private Rope rope;

    @Override
    public boolean run() {
        return this.runChallenge(2022, 9);
    }

    @Override
    public String part1(final String[] input) {
        this.rope = new Rope();
        this.cells = new HashMap<>();
        getOrCreateCell(rope.head);

        for (String move : input) {
            moveRope(move);
            System.out.println(rope.position());
        }

        int visits = 0;
        for (Map.Entry<String, Cell> entry :cells.entrySet()) {
            if (entry.getValue().tailHasVisited) {
                visits ++;
            }
        }

        return String.valueOf(visits);
    }

    private void moveRope(String move) {
        String[] parts = move.split(" ");
        String direction = parts[0];
        int steps = Integer.parseInt(parts[1]);
        for (int step = 0; step < steps; step ++) {
            rope.moveHead(direction, steps);
            rope.tailFollows();
            updateCells(rope.tail);
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

    @Override
    public String part2(final String[] input) {
        this.rope = new Rope();
        this.cells = new HashMap<>();

        return null;
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
        private Coord head;
        private Coord tail;

        public Rope() {
            this.head = new Coord(0, 0);
            this.tail = new Coord(0, 0);
        }

        public void moveHead(String direction, int steps) {
            switch (direction) {
                case "R":
                    move(1, 0);
                    break;
                case "L":
                    move(-1, 0);
                    break;
                case "U":
                    move(0, 1);
                    break;
                case "D":
                    move(0, -1);
                    break;
                default:
                    throw new RuntimeException("Bad move " + direction);
            }
        }

        private void move(int deltaX, int deltaY) {
            this.head.x = this.head.x + deltaX;
            this.head.y = this.head.y + deltaY;

        }

        public void tailFollows() {
            // oh boy
            int manhattanDistance = manhattanDistance();
            // cardinal points and overlapping
            if (manhattanDistance <= 1) {
                return;
            }
            // diagonals
            double euclideanDistance = euclideanDistance();
            if (String.valueOf(euclideanDistance).startsWith("1.414")) {
                return;
            }
            // ok, onto the moves
            if (head.x == tail.x) {
                moveVertically();
                return;
            }
            if (head.y == tail.y) {
                moveHorizontally();
                return;
            }
            moveDiagonally();
        }

        private void moveDiagonally() {
            if (head.y > tail.y) {
                tail.y = tail.y + 1;
            } else {
                tail.y = tail.y - 1;
            }
            tail.x = tail.x +  ((head.x > tail.x) ? 1 : -1);
        }

        private void moveVertically() {
            tail.y = tail.y +  ((head.y > tail.y) ? 1 : -1);
        }

        private void moveHorizontally() {
            tail.x = tail.x +  ((head.x > tail.x) ? 1 : -1);
        }

        private double euclideanDistance() {
            return Math.sqrt(
                    Math.pow(head.x - tail.x, 2) +
                            Math.pow(head.y - tail.y, 2)
            );
        }

        private int manhattanDistance() {
            return Math.abs(head.x - tail.x) + Math.abs(head.y - tail.y);
        }

        public String position() {
            return head.x + "," + head.y + " <- " + tail.x + "," + tail.y;
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
