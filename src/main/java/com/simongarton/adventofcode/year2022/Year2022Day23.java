package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.Coord;
import com.simongarton.adventofcode.common.InfiniteGrid;

import java.util.*;

public class Year2022Day23 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;
    private static final String ELF = "#";
    private static final String DOT = ".";
    private static final String VOID = " ";

    private InfiniteGrid grid;
    private List<Elf> elves;

    @Override
    public String title() {
        return "Day 23: Unstable Diffusion";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2022, 23);
    }

    @Override
    public String part1(final String[] input) {
        this.loadGridAndElves(input);
        final int rounds = this.moveElves(true);
        return String.valueOf(this.calculateElfBox());
    }

    @Override
    public String part2(final String[] input) {
        this.loadGridAndElves(input);
        final int rounds = this.moveElves(false);
        return String.valueOf(rounds);
    }

    private int calculateElfBox() {
        int minX = this.elves.get(0).original.getX();
        int maxX = this.elves.get(0).original.getX();
        int minY = this.elves.get(0).original.getY();
        int maxY = this.elves.get(0).original.getY();
        for (final Elf elf : this.elves) {
            if (elf.original.getX() < minX) {
                minX = elf.original.getX();
            }
            if (elf.original.getX() > maxX) {
                maxX = elf.original.getX();
            }
            if (elf.original.getY() < minY) {
                minY = elf.original.getY();
            }
            if (elf.original.getY() > maxY) {
                maxY = elf.original.getY();
            }
        }
        int blanks = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (!Objects.equals(this.grid.getXY(x, y), ELF)) {
                    blanks++;
                }
            }
        }
        return blanks;
    }

    private int moveElves(final boolean breakOn10) {
        int movesMade = 1;
        int roundIndex = 0;
        while (movesMade > 0) {
            final Map<String, Integer> moveCount = new HashMap<>();
            for (final Elf elf : this.elves) {
                elf.figureNextMove(this.grid, roundIndex);
                moveCount.put(elf.next.toString(),
                        moveCount.getOrDefault(elf.next.toString(), 0) + 1);
            }
            movesMade = 0;
            for (final Elf elf : this.elves) {
                final Coord next = elf.next;
                if (moveCount.getOrDefault(next.toString(), 0) == 1
                        && (!(elf.next.toString().equalsIgnoreCase(elf.original.toString())))) {
                    this.grid.putCoord(elf.original, DOT);
                    this.grid.putCoord(elf.next, ELF);
                    this.debugPrint("Elf " + elf.index + " moves to " + elf.next);
                    elf.move();
                    movesMade++;
                } else {
                    this.debugPrint("Elf " + elf.index + " stays at " + elf.original);
                    elf.stay();
                }
            }
            if (DEBUG) {
                this.grid.drawOnTerminal();
            }
            roundIndex++;
            if (breakOn10 && (roundIndex == 10)) {
                break;
            }
        }
        return roundIndex;
    }

    private void loadGridAndElves(final String[] input) {
        this.elves = new ArrayList<>();
        this.grid = new InfiniteGrid();
        this.grid.setDefaultResult(DOT);
        for (int row = 0; row < input.length; row++) {
            final String line = input[row];
            for (int col = 0; col < line.length(); col++) {
                final String m = line.charAt(col) + "";
                this.grid.putXY(col, row, m);
                if (!m.equalsIgnoreCase(ELF)) {
                    continue;
                }
                final Elf elf = new Elf(this.elves.size(), new Coord(col, row));
                this.elves.add(elf);
            }
        }
        if (DEBUG) {
            this.grid.drawOnTerminal();
        }
        this.grid.setDefaultResult(DOT);
    }

    public static final class Elf {

        int index;
        Coord original;
        Coord next;

        public Elf(final int index, final Coord start) {
            this.index = index;
            this.original = start;
        }

        public void figureNextMove(final InfiniteGrid grid, final int roundIndex) {
            if (!(
                    grid.getXY(this.original.getX() - 1, this.original.getY() - 1).equalsIgnoreCase(ELF) ||
                            grid.getXY(this.original.getX(), this.original.getY() - 1).equalsIgnoreCase(ELF) ||
                            grid.getXY(this.original.getX() + 1, this.original.getY() - 1).equalsIgnoreCase(ELF) ||
                            grid.getXY(this.original.getX() - 1, this.original.getY()).equalsIgnoreCase(ELF) ||
                            grid.getXY(this.original.getX() + 1, this.original.getY()).equalsIgnoreCase(ELF) ||
                            grid.getXY(this.original.getX() - 1, this.original.getY() + 1).equalsIgnoreCase(ELF) ||
                            grid.getXY(this.original.getX(), this.original.getY() + 1).equalsIgnoreCase(ELF) ||
                            grid.getXY(this.original.getX() + 1, this.original.getY() + 1).equalsIgnoreCase(ELF)

            )
            ) {
                this.next = this.original;
                return;
            }
            switch (roundIndex % 4) {
                case 0:
                    this.nswe(grid);
                    return;
                case 1:
                    this.swen(grid);
                    return;
                case 2:
                    this.wens(grid);
                    return;
                case 3:
                    this.ensw(grid);
                    return;
            }
        }

        private void nswe(final InfiniteGrid grid) {
            if (this.canMoveNorth(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move North.");
                this.next = new Coord(this.original.getX(), this.original.getY() - 1);
                return;
            }
            if (this.canMoveSouth(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move South.");
                this.next = new Coord(this.original.getX(), this.original.getY() + 1);
                return;
            }
            if (this.canMoveWest(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move West.");
                this.next = new Coord(this.original.getX() - 1, this.original.getY());
                return;
            }
            if (this.canMoveEast(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move East.");
                this.next = new Coord(this.original.getX() + 1, this.original.getY());
                return;
            }
            this.next = this.original;
        }

        private void swen(final InfiniteGrid grid) {
            if (this.canMoveSouth(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move South.");
                this.next = new Coord(this.original.getX(), this.original.getY() + 1);
                return;
            }
            if (this.canMoveWest(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move West.");
                this.next = new Coord(this.original.getX() - 1, this.original.getY());
                return;
            }
            if (this.canMoveEast(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move East.");
                this.next = new Coord(this.original.getX() + 1, this.original.getY());
                return;
            }
            if (this.canMoveNorth(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move North.");
                this.next = new Coord(this.original.getX(), this.original.getY() - 1);
                return;
            }
            this.next = this.original;
        }

        private void wens(final InfiniteGrid grid) {
            if (this.canMoveWest(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move West.");
                this.next = new Coord(this.original.getX() - 1, this.original.getY());
                return;
            }
            if (this.canMoveEast(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move East.");
                this.next = new Coord(this.original.getX() + 1, this.original.getY());
                return;
            }
            if (this.canMoveNorth(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move North.");
                this.next = new Coord(this.original.getX(), this.original.getY() - 1);
                return;
            }
            if (this.canMoveSouth(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move South.");
                this.next = new Coord(this.original.getX(), this.original.getY() + 1);
                return;
            }
            this.next = this.original;
        }

        private void ensw(final InfiniteGrid grid) {
            if (this.canMoveEast(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move East.");
                this.next = new Coord(this.original.getX() + 1, this.original.getY());
                return;
            }
            if (this.canMoveNorth(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move North.");
                this.next = new Coord(this.original.getX(), this.original.getY() - 1);
                return;
            }
            if (this.canMoveSouth(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move South.");
                this.next = new Coord(this.original.getX(), this.original.getY() + 1);
                return;
            }
            if (this.canMoveWest(grid)) {
                this.debugPrint("Elf " + this.index + " (" + this.original + ") can move West.");
                this.next = new Coord(this.original.getX() - 1, this.original.getY());
                return;
            }
            this.next = this.original;
        }

        private boolean canMoveNorth(final InfiniteGrid grid) {
            return (!(grid.getXY(this.original.getX() - 1, this.original.getY() - 1).equalsIgnoreCase(ELF) ||
                    grid.getXY(this.original.getX(), this.original.getY() - 1).equalsIgnoreCase(ELF) ||
                    grid.getXY(this.original.getX() + 1, this.original.getY() - 1).equalsIgnoreCase(ELF)));
        }

        private boolean canMoveSouth(final InfiniteGrid grid) {
            return (!(grid.getXY(this.original.getX() - 1, this.original.getY() + 1).equalsIgnoreCase(ELF) ||
                    grid.getXY(this.original.getX(), this.original.getY() + 1).equalsIgnoreCase(ELF) ||
                    grid.getXY(this.original.getX() + 1, this.original.getY() + 1).equalsIgnoreCase(ELF)));
        }

        private boolean canMoveWest(final InfiniteGrid grid) {
            return (!(grid.getXY(this.original.getX() - 1, this.original.getY() - 1).equalsIgnoreCase(ELF) ||
                    grid.getXY(this.original.getX() - 1, this.original.getY()).equalsIgnoreCase(ELF) ||
                    grid.getXY(this.original.getX() - 1, this.original.getY() + 1).equalsIgnoreCase(ELF)));
        }

        private boolean canMoveEast(final InfiniteGrid grid) {
            return (!(grid.getXY(this.original.getX() + 1, this.original.getY() - 1).equalsIgnoreCase(ELF) ||
                    grid.getXY(this.original.getX() + 1, this.original.getY()).equalsIgnoreCase(ELF) ||
                    grid.getXY(this.original.getX() + 1, this.original.getY() + 1).equalsIgnoreCase(ELF)));
        }

        public void move() {
            this.original = this.next;
        }

        public void stay() {
            this.next = this.original;
        }

        private void debugPrint(final String s) {
            if (DEBUG) {
                System.out.println("  " + s);
            }
        }
    }

    private void debugPrint(final String s) {
        if (DEBUG) {
            System.out.println("  " + s);
        }
    }
}
