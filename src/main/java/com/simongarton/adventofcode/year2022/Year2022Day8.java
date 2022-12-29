package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Year2022Day8 extends AdventOfCodeChallenge {

    private Tree[] grid;
    private int width;
    private int height;

    @Override
    public String title() {
        return "Day 8: Treetop Tree House";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2022, 8);
    }

    @Override
    public String part1(final String[] input) {
        this.loadGrid(input);
        this.updateFromNorth();
        this.updateFromEast();
        this.updateFromSouth();
        this.updateFromWest();
        return String.valueOf(this.countVisibleTrees());
    }

    @Override
    public String part2(final String[] input) {
        this.loadGrid(input);
        this.updateFromNorth();
        this.updateFromEast();
        this.updateFromSouth();
        this.updateFromWest();
        return String.valueOf(this.bestScenicScore());
    }

    protected void loadGrid(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        this.width = lines.get(0).length();
        this.height = lines.size();
        this.grid = new Tree[this.width * this.height];
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                final int index = this.getIndex(row, col);
                this.grid[index] = new Tree(Integer.parseInt(lines.get(row).substring(col, col + 1)));
            }
        }
    }

    protected int bestScenicScore() {
        int bestScenicScore = 0;
        String bestTree = "";
        for (int row = 1; row < this.height - 1; row++) {
            for (int col = 1; col < this.width - 1; col++) {
                if (!this.getTree(row, col).isVisible()) {
                    continue;
                }
                final int scenicScore = this.getScenicScore(row, col);
                if (scenicScore > bestScenicScore) {
                    bestScenicScore = scenicScore;
                    bestTree = String.format("%s,%s", row, col);
                }
            }
        }
        return bestScenicScore;
    }

    private int getScenicScore(
            final int row,
            final int col
    ) {
        return this.getViewingDistanceN(row, col) *
                this.getViewingDistanceE(row, col) *
                this.getViewingDistanceS(row, col) *
                this.getViewingDistanceW(row, col);
    }

    private int getViewingDistanceN(
            int row,
            final int col
    ) {
        int viewingDistance = 0;
        final int startHeight = this.getTreeHeight(row, col);
        while (row > 0) {
            viewingDistance++;
            row--;
            if (this.getTreeHeight(row, col) >= startHeight) {
                break;
            }
        }
        return viewingDistance;
    }

    private int getViewingDistanceS(
            int row,
            final int col
    ) {
        int viewingDistance = 0;
        final int startHeight = this.getTreeHeight(row, col);
        while (row < this.height - 1) {
            viewingDistance++;
            row++;
            if (this.getTreeHeight(row, col) >= startHeight) {
                break;
            }
        }
        return viewingDistance;
    }

    private int getViewingDistanceE(
            final int row,
            int col
    ) {
        int viewingDistance = 0;
        final int startHeight = this.getTreeHeight(row, col);
        while (col > 0) {
            viewingDistance++;
            col--;
            if (this.getTreeHeight(row, col) >= startHeight) {
                break;
            }
        }
        return viewingDistance;
    }

    private int getViewingDistanceW(
            final int row,
            int col
    ) {
        int viewingDistance = 0;
        final int startHeight = this.getTreeHeight(row, col);
        while (col < this.width - 1) {
            viewingDistance++;
            col++;
            if (this.getTreeHeight(row, col) >= startHeight) {
                break;
            }
        }
        return viewingDistance;
    }

    private int getIndex(
            final int row,
            final int col
    ) {
        return row * this.width + col;
    }

    private Tree getTree(
            final int row,
            final int col
    ) {
        final int index = this.getIndex(row, col);
        return this.grid[index];
    }

    private int getTreeHeight(
            final int row,
            final int col
    ) {
        final int index = this.getIndex(row, col);
        return this.grid[index].height;
    }

    protected void displayGrid() {
        for (int row = 0; row < this.height; row++) {
            final StringBuilder line = new StringBuilder();
            for (int col = 0; col < this.width; col++) {
                line.append(this.getTreeHeight(row, col));
            }
            System.out.println(line);
        }
    }

    protected int countVisibleTrees() {
        int visibleTrees = 0;
        final Set<String> done = new HashSet<>();
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                final String key = row + "," + col;
                if (done.contains(key)) {
                    continue;
                }
                if (this.getTree(row, col).isVisible()) {
                    visibleTrees++;
                }
                done.add(key);
            }
        }
        return visibleTrees;
    }

    protected void displayVisibleGrid() {
        for (int row = 0; row < this.height; row++) {
            final StringBuilder line = new StringBuilder();
            for (int col = 0; col < this.width; col++) {
                if (this.getTree(row, col).isVisible()) {
                    line.append(this.getTreeHeight(row, col));
                } else {
                    line.append(" ");
                }
            }
            System.out.println(line);
        }
    }

    protected void updateFromWest() {
        for (int row = 1; row < this.height - 1; row++) {
            int youMustBeTallerThanThisTree = this.getTreeHeight(row, 0);
            for (int col = 1; col < this.width - 1; col++) {
                final int thisHeight = this.getTreeHeight(row, col);
                if (thisHeight <= youMustBeTallerThanThisTree) {
                    this.getTree(row, col).visibleFromWest = false;
                }
                if (thisHeight > youMustBeTallerThanThisTree) {
                    youMustBeTallerThanThisTree = thisHeight;
                }
            }
        }
    }

    protected void updateFromEast() {
        for (int row = 1; row < this.height - 1; row++) {
            int youMustBeTallerThanThisTree = this.getTreeHeight(row, this.height - 1);
            for (int col = this.width - 2; col > 0; col--) {
                final int thisHeight = this.getTreeHeight(row, col);
                if (thisHeight <= youMustBeTallerThanThisTree) {
                    this.getTree(row, col).visibleFromEast = false;
                }
                if (thisHeight > youMustBeTallerThanThisTree) {
                    youMustBeTallerThanThisTree = thisHeight;
                }
            }
        }
    }

    protected void updateFromNorth() {
        for (int col = 1; col < this.width - 1; col++) {
            int youMustBeTallerThanThisTree = this.getTreeHeight(0, col);
            for (int row = 1; row < this.height - 1; row++) {
                final int thisHeight = this.getTreeHeight(row, col);
                if (thisHeight <= youMustBeTallerThanThisTree) {
                    this.getTree(row, col).visibleFromNorth = false;
                }
                if (thisHeight > youMustBeTallerThanThisTree) {
                    youMustBeTallerThanThisTree = thisHeight;
                }
            }
        }
    }

    protected void updateFromSouth() {
        for (int col = 1; col < this.width - 1; col++) {
            int youMustBeTallerThanThisTree = this.getTreeHeight(this.width - 1, col);
            for (int row = this.height - 2; row > 0; row--) {
                final int thisHeight = this.getTreeHeight(row, col);
                if (thisHeight <= youMustBeTallerThanThisTree) {
                    this.getTree(row, col).visibleFromSouth = false;
                }
                if (thisHeight > youMustBeTallerThanThisTree) {
                    youMustBeTallerThanThisTree = thisHeight;
                }
            }
        }
    }

    public static final class Tree {

        private final int height;
        private boolean visibleFromNorth = true;
        private boolean visibleFromEast = true;
        private boolean visibleFromSouth = true;
        private boolean visibleFromWest = true;

        public Tree(final int height) {
            this.height = height;
        }

        public boolean isVisible() {
            return this.visibleFromEast ||
                    this.visibleFromWest ||
                    this.visibleFromSouth ||
                    this.visibleFromNorth;
        }
    }
}
