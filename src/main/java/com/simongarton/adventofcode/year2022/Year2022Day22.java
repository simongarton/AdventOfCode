package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.Coord;

import java.util.ArrayList;
import java.util.List;

public class Year2022Day22 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;
    private static final String SPACE = " ";
    private static final String DOT = ".";
    private static final String WALL = "#";

    private List<String> map;
    private int width;
    private int height;
    private String instructionLine;
    private List<String> instructions;
    private Coord position;
    private Direction facing;

    public enum Direction {
        NORTH(3),
        EAST(0),
        SOUTH(1),
        WEST(2);

        private final int facing;

        Direction(final int facing) {
            this.facing = facing;
        }
    }

    @Override
    public String title() {
        return "Day 22: Monkey Map";
    }

    @Override
    public boolean run() {
        return this.runChallenge(2022, 22);
    }

    @Override
    public String part1(final String[] input) {
        this.loadMapAndInstructions(input);
        final int left = this.leftMost(0);
        this.position = new Coord(left, 0);
        this.facing = Direction.EAST;
        System.out.printf("%s x %s\n", this.width, this.height);
        for (final String mapLine : this.map) {
            System.out.printf("\t%s\n", mapLine.length());
        }
        System.out.printf("Starting at %s, facing %s\n", this.position, this.facing);
        for (final String instruction : this.instructions) {
            this.followInstruction(instruction);
            System.out.printf("Now at %s, facing %s\n", this.position, this.facing);
        }
        return String.valueOf(1000 * (this.position.getY() + 1) + 4 * (this.position.getX() + 1) + this.facing.facing);
    }

    private void followInstruction(final String instruction) {
        if (instruction.equalsIgnoreCase("R")) {
            this.facing = this.turnRight(this.facing);
            this.debugPrint("turned right to " + this.facing);
            return;
        }
        if (instruction.equalsIgnoreCase("L")) {
            this.facing = this.turnLeft(this.facing);
            this.debugPrint("turned left to " + this.facing);
            return;
        }
        final int steps = Integer.parseInt(instruction);
        for (int i = 0; i < steps; i++) {
            this.position = this.nextCoord(this.facing);
            this.debugPrint("moved to " + this.position + " on step " + i + " of " + steps);
        }
    }

    private void debugPrint(final String s) {
        if (DEBUG) {
            System.out.println("  " + s);
        }
    }

    private Coord nextCoord(final Direction facing) {
        switch (facing) {
            case NORTH:
                return this.goUp();
            case EAST:
                return this.goRight();
            case SOUTH:
                return this.goDown();
            case WEST:
                return this.goLeft();
            default:
                throw new RuntimeException("no default");
        }
    }

    private Coord goUp() {
        if (this.position.getY() == 0 ||
                this.getMap(this.position.getX(), this.position.getY() - 1).equalsIgnoreCase(SPACE)
        ) {
            return this.maybeTraceUpFromBottom();
        }
        final String spot = this.getMap(this.position.getX(), this.position.getY() - 1);
        if (spot.equalsIgnoreCase(DOT)) {
            return new Coord(this.position.getX(), this.position.getY() - 1);
        }
        if (spot.equalsIgnoreCase(WALL)) {
            return new Coord(this.position.getX(), this.position.getY());
        }
        throw new RuntimeException("goUp() didn't work");
    }

    private Coord maybeTraceUpFromBottom() {
        final int bottom = this.bottomMost(this.position.getX());
        final String spot = this.getMap(this.position.getX(), bottom);
        if (spot.equalsIgnoreCase(WALL)) {
            return new Coord(this.position.getX(), this.position.getY());
        }
        if (spot.equalsIgnoreCase(DOT)) {
            return new Coord(this.position.getX(), bottom);
        }
        throw new RuntimeException("maybeTraceUpFromBottom didn't work");
    }

    private Coord goDown() {
        if (this.position.getY() == (this.height - 1) ||
                this.getMap(this.position.getX(), this.position.getY() + 1).equalsIgnoreCase(SPACE)
        ) {
            return this.maybeTraceDownFromTop();
        }
        final String spot = this.getMap(this.position.getX(), this.position.getY() + 1);
        if (spot.equalsIgnoreCase(DOT)) {
            return new Coord(this.position.getX(), this.position.getY() + 1);
        }
        if (spot.equalsIgnoreCase(WALL)) {
            return new Coord(this.position.getX(), this.position.getY());
        }
        throw new RuntimeException("goDown() didn't work");
    }

    private Coord maybeTraceDownFromTop() {
        final int top = this.topMost(this.position.getX());
        final String spot = this.getMap(this.position.getX(), top);
        if (spot.equalsIgnoreCase(WALL)) {
            return new Coord(this.position.getX(), this.position.getY());
        }
        if (spot.equalsIgnoreCase(DOT)) {
            return new Coord(this.position.getX(), top);
        }
        throw new RuntimeException("maybeTraceUpFromBottom didn't work");
    }

    private Coord goRight() {
        if (this.position.getX() == (this.width - 1) ||
                this.getMap(this.position.getX() + 1, this.position.getY()).equalsIgnoreCase(SPACE)
        ) {
            return this.maybeTraceRightFromLeft();
        }
        final String spot = this.getMap(this.position.getX() + 1, this.position.getY());
        if (spot.equalsIgnoreCase(DOT)) {
            return new Coord(this.position.getX() + 1, this.position.getY());
        }
        if (spot.equalsIgnoreCase(WALL)) {
            return new Coord(this.position.getX(), this.position.getY());
        }
        throw new RuntimeException("goRight() didn't work");
    }

    private Coord maybeTraceRightFromLeft() {
        final int left = this.leftMost(this.position.getY());
        final String spot = this.getMap(left, this.position.getY());
        if (spot.equalsIgnoreCase(WALL)) {
            return new Coord(this.position.getX(), this.position.getY());
        }
        if (spot.equalsIgnoreCase(DOT)) {
            return new Coord(left, this.position.getY());
        }
        throw new RuntimeException("maybeTraceRightFromLeft didn't work");
    }

    private Coord goLeft() {
        if (this.position.getX() == 0 ||
                this.getMap(this.position.getX() - 1, this.position.getY()).equalsIgnoreCase(SPACE)
        ) {
            return this.maybeTraceLeftFromRight();
        }
        final String spot = this.getMap(this.position.getX() - 1, this.position.getY());
        if (spot.equalsIgnoreCase(DOT)) {
            return new Coord(this.position.getX() - 1, this.position.getY());
        }
        if (spot.equalsIgnoreCase(WALL)) {
            return new Coord(this.position.getX(), this.position.getY());
        }
        throw new RuntimeException("goLeft() didn't work");
    }

    private Coord maybeTraceLeftFromRight() {
        final int left = this.rightMost(this.position.getY());
        final String spot = this.getMap(left, this.position.getY());
        if (spot.equalsIgnoreCase(WALL)) {
            return new Coord(this.position.getX(), this.position.getY());
        }
        if (spot.equalsIgnoreCase(DOT)) {
            return new Coord(left, this.position.getY());
        }
        throw new RuntimeException("maybeTraceLeftFromRight didn't work");
    }

    private String getMap(final Integer col, final int row) {
        return "" + this.map.get(row).charAt(col);
    }

    private Direction turnRight(final Direction facing) {
        switch (facing) {
            case NORTH:
                return Direction.EAST;
            case EAST:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.WEST;
            case WEST:
                return Direction.NORTH;
            default:
                throw new RuntimeException("no default");
        }
    }

    private Direction turnLeft(final Direction facing) {
        switch (facing) {
            case NORTH:
                return Direction.WEST;
            case WEST:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.EAST;
            case EAST:
                return Direction.NORTH;
            default:
                throw new RuntimeException("no default");
        }
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    private void loadMapAndInstructions(final String[] input) {
        int longest = 0;
        for (int i = 0; i < input.length - 2; i++) {
            if (input[i].length() > longest) {
                longest = input[i].length();
            }
        }
        this.map = new ArrayList<>();
        for (int i = 0; i < input.length - 2; i++) {
            String base = input[i];
            if (base.length() < longest) {
                base += " ".repeat(longest - base.length());
            }
            this.map.add(base);
        }
        this.instructionLine = input[input.length - 1];
        this.buildInstructions();
        this.width = longest;
        this.height = this.map.size();
    }

    private void buildInstructions() {
        this.instructions = new ArrayList<>();
        String working = "";
        int i = 0;
        while (i < this.instructionLine.length()) {
            final char current = this.instructionLine.charAt(i);
            if (current == 'R' || current == 'L') {
                this.instructions.add(working);
                working = "";
                this.instructions.add("" + current);
            } else {
                working += current;
            }
            i++;
        }
        if (!working.isEmpty()) {
            this.instructions.add(working);
        }
    }

    private int leftMost(final int row) {
        final String mapLine = this.map.get(row);
        for (int i = 0; i < this.width; i++) {
            if (mapLine.charAt(i) != ' ') {
                return i;
            }
        }
        throw new RuntimeException("No leftMost on " + mapLine);
    }

    private int rightMost(final int row) {
        final String mapLine = this.map.get(row);
        int best = 0;
        for (int i = this.leftMost(row); i < this.width; i++) {
            try {
                if (mapLine.charAt(i) != ' ') {
                    best = i;
                }
            } catch (final StringIndexOutOfBoundsException e) {
                System.out.println(mapLine);
                System.out.println(mapLine.length());
                throw new RuntimeException("stop !");
            }
        }
        if (best == 0) {
            throw new RuntimeException("No rightMost on " + mapLine);
        }
        return best;
    }

    private int bottomMost(final int col) {
        for (int row = this.map.size() - 1; row >= 0; row--) {
            if (!this.getMap(col, row).equalsIgnoreCase(SPACE)) {
                return row;
            }
        }
        throw new RuntimeException("No bottomMost on " + col);
    }

    private int topMost(final int col) {
        for (int row = 0; row < this.map.size() - 1; row++) {
            if (!this.getMap(col, row).equalsIgnoreCase(SPACE)) {
                return row;
            }
        }
        throw new RuntimeException("No topMost on " + col);
    }
}
