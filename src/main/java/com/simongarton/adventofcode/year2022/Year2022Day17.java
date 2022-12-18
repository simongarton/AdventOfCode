package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;

public class Year2022Day17 extends AdventOfCodeChallenge {

    /*
    41 : adding 5 as the 10 rock

 23 |..@@...|
 22 |..@@...|
 21 |.......|
 20 |.......|
 19 |.......|
 18 |....#..|
 17 |....#..|
 16 |....#..|
 15 |....##.|
 14 |.....#.|
 13 |..####.|
 12 |.###...|
 11 |..#....|

 How did that not fall ?

     */

    private List<String> cave;

    private static final String ROCK = "#";
    private static final String BOTTOM = "-";
    private static final String CORNER = "+";
    private static final String FALLING_ROCK = "@";
    private static final String SPACE = ".";
    private static final String LEFT = "<";
    private static final String RIGHT = ">";
    private static final String WALL = "|";
    private static final int EMPTY = 3;
    private static final int WALL_WIDTH = 1;
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
//        this.debugRocks();
        final String windForecast = input[0];
        System.out.println(windForecast);
        int iteration = 0;
        int windIndex = 0;
        this.buildCave();
        int rockIndex = 1;
        int rocksDropped = 0;
        Rock rock = new Rock(rockIndex);
        this.putRockInCave(rock);
        rocksDropped++;
        // this is the first rock dropped
        this.drawCave(rock);
        boolean running = true;
        String wind = windForecast.substring(windIndex, windIndex + 1);

        while (running) {
            final boolean debug = (rocksDropped == 9);
            if (debug) {
                System.out.printf("%s : %s = %s %s\n\n", iteration, windIndex, wind, this.move(wind));
            }
            boolean needToChangeRock = false;
            if (wind.equalsIgnoreCase(LEFT)) {
                // not sure if I need to check banging against walls
                if (this.moveWouldCauseCollision(rock, -1, 0)) {
//                    needToChangeRock = true;
                } else {
                    rock.moveLeft();
                }
            } else {
                if (this.moveWouldCauseCollision(rock, 1, 0)) {
//                    needToChangeRock = true;
                } else {
                    rock.moveRight();
                }
            }
            if (debug) {
                this.drawCave(rock);
            }
            if (!needToChangeRock) {
                if (this.moveWouldCauseCollision(rock, 0, 1)) {
                    needToChangeRock = true;
                } else {
                    rock.moveDown();
                }
            }
            if (needToChangeRock) {
                this.addRockToCave(rock);
                rockIndex++;
                if (rockIndex > 5) {
                    rockIndex = 1;
                }
                rock = new Rock(rockIndex);
                this.putRockInCave(rock);
                rocksDropped++;
                System.out.printf("%s : adding %s as the %s rock\n\n", iteration, rock.id, rocksDropped);
            }
            windIndex = windIndex + 1;
            if (windIndex == windForecast.length()) {
                windIndex = 0;
            }
            wind = windForecast.substring(windIndex, windIndex + 1);
            iteration++;
            if (rocksDropped > 2022) {
                running = false;
            }
            if (debug) {
                this.drawCave(rock);
            }
        }
        this.drawCave(null);
        return String.valueOf(this.findLevel());
    }

    private void debugRocks() {
        for (int i = 1; i <= 5; i++) {
            final Rock rock = new Rock(i);
            System.out.printf("%s : %s, %s\n",
                    i,
                    rock.getWidth(),
                    rock.getHeight()
            );
        }
    }

    private String move(final String wind) {
        switch (wind) {
            case RIGHT:
                return "right";
            case LEFT:
                return "left";
            default:
                throw new RuntimeException(wind);
        }
    }

    private void debugRock(final Rock rock) {
        System.out.printf("Rock %s width %s height %s\n", rock.id, rock.getWidth(), rock.getHeight());
        for (int rockX = 0; rockX < 4; rockX++) {
            for (int rockY = 0; rockY < 4; rockY++) {
                System.out.printf("%s,%s = %s%n",
                        rockX,
                        rockY,
                        this.atCaveCoordinateForRock(rock, rockX, rockY));
            }
        }
    }

    private void debugCave() {
        for (int level = 0; level < this.cave.size(); level++) {
            for (int col = 0; col < 9; col++) {
                System.out.printf("%s,%s = %s%n",
                        col,
                        level,
                        this.atCaveCoordinate(col, level));
            }
        }
    }

    private void addRockToCave(final Rock rock) {
        final List<String> newCave = new ArrayList<>();
        for (int level = this.cave.size() - 1; level >= 1; level--) {
            String line = this.drawRockInCave(level, rock);
            line = line.replace(FALLING_ROCK, ROCK);
            newCave.add(0, line);
        }
        newCave.add(0, this.cave.get(0));
        this.cave.clear();
        this.cave.addAll(newCave);
    }

    private String atCaveCoordinate(final int x, final int y) {
        // doesn't include falling rocks
        return this.cave.get(y).substring(x, x + 1);
    }

    private String atCaveCoordinateForRock(final Rock rock, final int rockX, final int rockY) {
        final int caveX = rock.position.getX() + rockX;
        final int caveY = rock.position.getY() - rockY; // the rock coordinates increasing, go down to the floor
//        System.out.printf("%s, %s  for %s, %s converted to %s, %s\n",
//                rockX,
//                rockY,
//                rock.position.getX(),
//                rock.position.getY(),
//                caveX,
//                caveY);
        return this.atCaveCoordinate(caveX, caveY);
    }

    private boolean moveWouldCauseCollision(final Rock rock, final int deltaX, final int deltaY) {
        return this.rockAtNewPositionWillCauseCollision(rock, deltaX, deltaY);
    }

    private boolean rockAtNewPositionWillCauseCollision(final Rock rock, final int deltaX, final int deltaY) {
        for (int rockX = 0; rockX < 4; rockX++) {
            for (int rockY = 0; rockY < 4; rockY++) {
                if (!rock.hasLump(rockX, rockY)) {
                    continue;
                }
                if (this.atCaveCoordinateForRock(rock, rockX + deltaX, rockY + deltaY).equalsIgnoreCase(ROCK)) {
                    return true;
                }
                if (this.atCaveCoordinateForRock(rock, rockX + deltaX, rockY + deltaY).equalsIgnoreCase(BOTTOM)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public String part2(final String[] input) {
        return null;
    }

    private void putRockInCave(final Rock rock) {
        final int top = this.findLevel(); // 0 based
        int rowsAdded = 0;
        while (this.cave.size() <= (top + EMPTY + rock.getHeight())) {
            this.cave.add(WALL + SPACE.repeat(7) + WALL);
            rowsAdded++;
        }
//        rock.position = new Coord(WALL_WIDTH + TWO, this.cave.size() - 1);
        rock.position = new Coord(WALL_WIDTH + TWO, top + EMPTY + rock.getHeight());
//        System.out.printf("Adding rock %s top %s rowsAdded %s cave.size() %s\n",
//                rock.id,
//                top,
//                rowsAdded,
//                this.cave.size()
//        );
    }

    public void drawCave(final Rock rock) {
        for (int level = this.cave.size() - 1; level >= 1; level--) {
            final String line = this.drawRockInCave(level, rock);
            System.out.printf("%3d %s\n", level, line);
        }
        System.out.printf("%3d %s\n", 0, this.cave.get(0));
        System.out.printf("%3s %s\n", " ", "012345678");
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
        String filledRow = SPACE.repeat(rock.position.getX() - 1);
        final int rockRow = (rock.position.getY() - level);
        // now draw the 4 spaces which will contain the rock - for this level
        for (int i = 0; i < rock.getWidth(); i++) {
            if (rock.hasLump(i, rockRow)) {
                filledRow = filledRow + FALLING_ROCK;
            } else {
                filledRow = filledRow + SPACE;
            }
        }
        try {
            filledRow = filledRow + SPACE.repeat(7 - filledRow.length());
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(filledRow);
        }
        final String intermediate = WALL + filledRow + WALL;
        String finalLine = "";
        for (int i = 0; i < intermediate.length(); i++) {
            if (emptyRow.substring(i, i + 1).equalsIgnoreCase(ROCK)) {
                finalLine += ROCK;
            } else {
                finalLine += intermediate.substring(i, i + 1);
            }
        }
        return finalLine;
    }

    private int findLevel() {
        // 0 based index
        final int floor = 0;
        if (this.cave.size() == 1) {
            return floor;
        }
        for (int level = this.cave.size() - 1; level > 0; level--) {
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
                    break;
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
            if (this.position.getX() < (9 - WALL_WIDTH - this.getWidth())) {
                this.position.setX(this.position.getX() + 1);
            }
        }

        public void moveUp() {
            this.position.setY(this.position.getY() - 1);
        }

        public void moveDown() {
            this.position.setY(this.position.getY() - 1);
        }
    }
}
