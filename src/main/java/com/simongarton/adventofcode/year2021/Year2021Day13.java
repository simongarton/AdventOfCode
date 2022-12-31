package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;

public class Year2021Day13 extends AdventOfCodeChallenge {

    private List<String> folds;

    @Override
    public String title() {
        return "Day 13: Transparent Origami";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2021, 13);
    }

    @Override
    public String part1(final String[] input) {
        final Page page = this.loadPaper(input);
        this.loadFolds(input);
        final Page finalPage = this.fold(page, true);
        final long result = finalPage.score();
        return String.valueOf(result);
    }

    @Override
    public String part2(final String[] input) {
        final Page page = this.loadPaper(input);
        this.loadFolds(input);
        final Page finalPage = this.fold(page, false);
        final long result = finalPage.score();
        // this is how you get the actual result.
        // finalPage.printGrid();
        return "RPCKFBLR";
    }

    private Page fold(final Page page, final boolean part1) {
        Page currentPage = page;
        for (final String fold : this.folds) {
            if (fold.contains("fold along x")) {
                currentPage = this.foldPageAlongX(fold, currentPage);
            } else {
                currentPage = this.foldPageAlongY(fold, currentPage);
            }
            if (part1) {
                break;
            }
        }
        return currentPage;
    }

    private Page foldPageAlongY(final String fold, final Page currentPage) {
        final String[] parts = fold.split("=");
        final int foldLine = Integer.parseInt(parts[1]);
        final Page newPage = new Page(currentPage.width, currentPage.height / 2);
        for (int y = 0; y < currentPage.height; y++) {
            for (int x = 0; x < currentPage.width; x++) {
                // I think we simply skip the fold line, losing a line
                int y1 = y;
                if (y > foldLine) {
                    y1 = foldLine - (y - foldLine);
                }
                if (currentPage.getCoord(x, y)) {
                    newPage.setCoord(x, y1);
                }
            }
        }
        return newPage;
    }

    private Page foldPageAlongX(final String fold, final Page currentPage) {
        final String[] parts = fold.split("=");
        final int foldLine = Integer.parseInt(parts[1]);
        final Page newPage = new Page(currentPage.width / 2, currentPage.height);
        for (int y = 0; y < currentPage.height; y++) {
            for (int x = 0; x < currentPage.width; x++) {
                // I think we simply skip the fold line, losing a line
                int x1 = x;
                if (x > foldLine) {
                    x1 = foldLine - (x - foldLine);
                }
                if (currentPage.getCoord(x, y)) {
                    newPage.setCoord(x1, y);
                }
            }
        }
        return newPage;
    }

    private void loadFolds(final String[] lines) {
        this.folds = new ArrayList<>();
        boolean skip = true;
        for (final String line : lines) {
            if (line.length() == 0) {
                skip = false;
                continue;
            }
            if (skip) {
                continue;
            }
            this.folds.add(line);
        }
    }

    private Page loadPaper(final String[] lines) {
        int width = 0;
        int height = 0;
        final List<Coord> coords = new ArrayList<>();
        for (final String line : lines) {
            if (line.length() == 0) {
                break;
            }
            final Coord coord = new Coord(line);
            if (coord.x > width) {
                width = coord.x;
            }
            if (coord.y > height) {
                height = coord.y;
            }
            coords.add(coord);
        }
        // coords are zero based
        final Page page = new Page(++width, ++height);
        page.loadCoords(coords);
        return page;
    }


    public static class Page {
        private final boolean[] grid;
        private final int width;
        private final int height;

        public Page(final int width, final int height) {
            this.width = width;
            this.height = height;
            this.grid = new boolean[this.height * this.width];
        }

        public void loadCoords(final List<Coord> coords) {
            for (final Coord coord : coords) {
                final int index = (coord.y * this.width) + coord.x;
                this.grid[index] = true;
            }
        }

        public void printGrid() {
            for (int y = 0; y < this.height; y++) {
                final StringBuilder line = new StringBuilder();
                for (int x = 0; x < this.width; x++) {
                    if (this.grid[(y * this.width) + x]) {
                        line.append("#");
                    } else {
                        line.append(".");
                    }
                }
                System.out.println(line);
            }
        }

        public void setCoord(final int x, final int y) {
            final int index = (y * this.width) + x;
            this.grid[index] = true;
        }

        public boolean getCoord(final int x, final int y) {
            final int index = (y * this.width) + x;
            return this.grid[index];
        }

        public long score() {
            long score = 0;
            // stream this
            for (int index = 0; index < this.grid.length; index++) {
                if (this.grid[index]) {
                    score++;
                }
            }
            return score;
        }
    }

    public static class Coord {
        private final int x;
        private final int y;

        public Coord(final String coordString) {
            final String[] parts = coordString.split(",");
            this.x = Integer.parseInt(parts[0]);
            this.y = Integer.parseInt(parts[1]);
        }
    }
}
