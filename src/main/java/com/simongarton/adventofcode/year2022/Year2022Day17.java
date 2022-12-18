package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;

public class Year2022Day17 extends AdventOfCodeChallenge {

    private List<String> cave;

    private static final String ROCK = "#";
    private static final String FALLING_ROCK = "@";
    private static final String SPACE = ".";
    private static final int EMPTY = 3;
    private static final int WALL = 1;
    private static final int TWO = 2;

    @Override
    public String title() {
        return "Day 17: Pyroclastic Flow";
    }

    @Override
    public boolean run() {
        return this.runChallenge(2022, 17);
    }

    @Override
    public String part1(final String[] input) {
        int iteration = 0;
        int windIndex = 0;
        this.buildCave();
        this.drawCave(null);
        int rockIndex = 0;
        Rock rock = new Rock(1);
        this.putRockInCave(rock);
        this.drawCave(rock);
        final boolean running = true;

        while (running) {
            final String wind = input[windIndex];
            if ("<".equalsIgnoreCase(input[windIndex])) {
                rock.moveLeft();
            } else {
                rock.moveRight();
            }
            if (rock.moveDown()) {
                rockIndex++;
                rock = new Rock((rockIndex % 5) + 1);
                this.putRockInCave(rock);
            }
            if (++windIndex == input.length) {
                windIndex = 0;
            }
            this.drawCave(rock);
            iteration++;
            if (iteration > 100) {
                break;
            }
        }

        return null;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    private void putRockInCave(final Rock rock) {
        final int top = this.findLevel();
        // top comes back as 0, rock is
        while (this.cave.size() <= (top + EMPTY + rock.getHeight())) {
            this.cave.add("|.......|");
        }
        rock.position = new Coord(WALL + TWO, this.cave.size() - 1);
    }

    public void drawCave(final Rock rock) {
        for (int level = this.cave.size() - 1; level >= 0; level--) {
            final String line = this.drawRockInCave(level, rock);
            System.out.println(line);
        }
        System.out.println("");
    }

    private String drawRockInCave(final int level, final Rock rock) {
        final String emptyRow = this.cave.get(level);
        if (rock == null || level > rock.position.getY()) {
            return emptyRow;
        }
        if (level < (rock.position.getY() - rock.getHeight())) {
            return emptyRow;
        }
        String filledRow = "|" + SPACE.repeat(rock.position.getX() - 1);
        // not sure about this
        final int rockRow = (level - rock.position.getY());
        for (int i = 0; i < 4; i++) {
            if (rock.hasLump(i, rockRow)) {
                filledRow = filledRow + FALLING_ROCK;
            } else {
                filledRow = filledRow + SPACE;
            }
        }
        filledRow = filledRow + SPACE.repeat(9 - WALL - (rock.position.getX() + 4));
        return filledRow + "|";
    }

    private int findLevel() {
        // 0 based index
        final int floor = 0;
        if (this.cave.size() == 1) {
            return floor;
        }
        for (int level = 1; level < this.cave.size(); level++) {
            if (this.cave.get(level).contains(ROCK)) {
                return level;
            }
        }
        return floor;
    }

    private void buildCave() {
        this.cave = new ArrayList<>();
        this.cave.add("+-------+");
    }

    public static final class Rock {

        private final int id;
        private Coord position;
        private final List<Coord> lumps;

        public Rock(final int id) {
            this.id = id;
            this.position = new Coord(0, 0);
            this.lumps = new ArrayList<>();
            switch (id) {
                case 1:
                    this.lumps.add(new Coord(0, 0));
                    this.lumps.add(new Coord(1, 0));
                    this.lumps.add(new Coord(2, 0));
                    this.lumps.add(new Coord(3, 0));
                    break;
                case 2:
                    this.lumps.add(new Coord(1, 0));
                    this.lumps.add(new Coord(0, 1));
                    this.lumps.add(new Coord(1, 1));
                    this.lumps.add(new Coord(2, 1));
                    this.lumps.add(new Coord(1, 2));
                    break;
                case 3:
                    this.lumps.add(new Coord(2, 0));
                    this.lumps.add(new Coord(2, 1));
                    this.lumps.add(new Coord(0, 2));
                    this.lumps.add(new Coord(1, 2));
                    this.lumps.add(new Coord(2, 2));
                    break;
                case 4:
                    this.lumps.add(new Coord(0, 0));
                    this.lumps.add(new Coord(0, 1));
                    this.lumps.add(new Coord(0, 2));
                    this.lumps.add(new Coord(0, 3));
                case 5:
                    this.lumps.add(new Coord(0, 0));
                    this.lumps.add(new Coord(1, 0));
                    this.lumps.add(new Coord(0, 1));
                    this.lumps.add(new Coord(1, 1));
                    break;
                default:
                    throw new RuntimeException("no rock");
            }
        }

        public int getHeight() {
            return 1 + this.lumps.stream().map(Coord::getY).mapToInt(Integer::intValue).max().orElse(0);
        }

        public int getWidth() {
            return 1 + this.lumps.stream().map(Coord::getX).mapToInt(Integer::intValue).max().orElse(0);
        }

        public boolean hasLump(final int i, final int rockRow) {
            return this.lumps.stream().anyMatch(c -> c.getX() == i && c.getY() == rockRow);
        }

        public void moveLeft() {
            if (this.position.getX() > 1) {
                this.position.setX(this.position.getX() - 1);
            }
        }

        public void moveRight() {
            if (this.position.getX() < (9 - WALL - this.getWidth())) {
                this.position.setX(this.position.getX() + 1);
            }
        }

        public boolean moveDown() {
            if (this.position.getY() > 0) {
                this.position.setY(this.position.getY() - 1);
                return false;
            }
            return true;
        }
    }
}
