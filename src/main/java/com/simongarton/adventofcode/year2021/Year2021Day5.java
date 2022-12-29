package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2021Day5 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 5: Hydrothermal Venture";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2021, 5);
    }

    @Override
    public String part1(final String[] input) {
        final Floor floor = new Floor(input, false);
        final int score = floor.overlap(2);
        return String.valueOf(score);
    }

    @Override
    public String part2(final String[] input) {
        final Floor floor = new Floor(input, true);
        final int score = floor.overlap(2);
        return String.valueOf(score);
    }

    private static final class Floor {

        private static final int MAX_DIM = 1000;
        int[] squares = new int[MAX_DIM * MAX_DIM];

        public Floor(final String[] lines, final boolean diagonals) {
            for (final String line : lines) {
                this.drawVent(line, diagonals);
            }
        }

        private void drawVent(final String line, final boolean diagonals) {
            final String[] coords = line.split("->");
            final Coord coord1 = new Coord(coords[0].trim());
            final Coord coord2 = new Coord(coords[1].trim());
            boolean done = false;
            if (coord1.x == coord2.x) {
                this.drawVerticalVent(coord1, coord2);
                done = true;
            }
            if (coord1.y == coord2.y) {
                this.drawHorizontalVent(coord1, coord2);
                done = true;
            }
            if (!done && diagonals) {
                this.drawDiagonalVent(coord1, coord2);
            }
        }

        private void drawVerticalVent(final Coord coord1, final Coord coord2) {
            final int delta = (coord1.y > coord2.y) ? -1 : +1;
            int startY = coord1.y;
            while (startY != coord2.y) {
                this.squares[(startY * MAX_DIM) + coord1.x] = this.squares[(startY * MAX_DIM) + coord1.x] + 1;
                startY = startY + delta;
            }
            this.squares[(startY * MAX_DIM) + coord1.x] = this.squares[(startY * MAX_DIM) + coord1.x] + 1;
        }

        private void drawHorizontalVent(final Coord coord1, final Coord coord2) {
            final int delta = (coord1.x > coord2.x) ? -1 : +1;
            int startX = coord1.x;
            while (startX != coord2.x) {
                this.squares[(coord1.y * MAX_DIM) + startX] = this.squares[(coord1.y * MAX_DIM) + startX] + 1;
                startX = startX + delta;
            }
            this.squares[(coord1.y * MAX_DIM) + startX] = this.squares[(coord1.y * MAX_DIM) + startX] + 1;
        }

        private void drawDiagonalVent(final Coord coord1, final Coord coord2) {
            final int deltaX = (coord1.x > coord2.x) ? -1 : +1;
            final int deltaY = (coord1.y > coord2.y) ? -1 : +1;
            int startX = coord1.x;
            int startY = coord1.y;
            while (startX != coord2.x) {
                this.squares[(startY * MAX_DIM) + startX] = this.squares[(startY * MAX_DIM) + startX] + 1;
                startX = startX + deltaX;
                startY = startY + deltaY;
            }
            this.squares[(startY * MAX_DIM) + startX] = this.squares[(startY * MAX_DIM) + startX] + 1;
        }

        public void printFloor() {
            for (int row = 0; row < MAX_DIM; row++) {
                final StringBuilder line = new StringBuilder();
                for (int col = 0; col < MAX_DIM; col++) {
                    if (this.squares[(row * MAX_DIM) + col] == 0) {
                        line.append(".");
                    } else {
                        line.append(this.squares[(row * MAX_DIM) + col]);
                    }
                }
                System.out.println(line);
            }
        }

        public int overlap(final int i) {
            int overlaps = 0;
            for (int row = 0; row < MAX_DIM; row++) {
                for (int col = 0; col < MAX_DIM; col++) {
                    if (this.squares[(row * MAX_DIM) + col] >= 2) {
                        overlaps++;
                    }
                }
            }
            return overlaps;
        }
    }

    private static final class Coord {
        int x;
        int y;

        public Coord(final String coordPair) {
            final String[] coords = coordPair.split(",");
            this.x = Integer.parseInt(coords[0]);
            this.y = Integer.parseInt(coords[1]);
        }
    }
}
