package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.Coord;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Year2022Day17 extends AdventOfCodeChallenge {

    private List<String> cave;

    private static final String ROCK = "#";
    private static final String BOTTOM = "-";
    private static final String FALLING_ROCK = "@";
    private static final String SPACE = ".";
    private static final String LEFT = "<";
    private static final String RIGHT = ">";
    private static final String WALL = "|";
    private static final int EMPTY = 3;
    private static final int WALL_WIDTH = 1;
    private static final int TWO = 2;

    // 1566272190007 is too high
    // 1566272190004 is also too high


    /*

    Part 1 - 2022 rocks - working. Part 2 is 1000000000000 : 1,000,000,000,000 1 trillion

    Is it possible to predict, in advance, where the rock will end up / how much the tower will grow ? I have a
    deterministic wind - which is very long though - and a deterministic sequence.

    This is the output

    After 2000 rocks, found a sequence at 209 matching that at 1900 with an offset of 1691
    After 3000 rocks, found a sequence at 1209 matching that at 2900 with an offset of 1691
    After 4000 rocks, found a sequence at 519 matching that at 3900 with an offset of 3381
    After 4000 rocks, found a sequence at 2209 matching that at 3900 with an offset of 1691
    After 5000 rocks, found a sequence at 1519 matching that at 4900 with an offset of 3381
    After 5000 rocks, found a sequence at 3209 matching that at 4900 with an offset of 1691

    The actual sequence length is 1690 - those offsets differ by 1690 after the first
    3381 - 1691 = 1690
    5071 - 3381 = 1690
    6761 - 5071 = 1690

    So what I want to do is build up a list of 1690 height changes, starting at height 209. Actually I should be basing this
    on rocks dropped. Those heights are based on dropped rocks though.

    wait

10: -14
11: 16

50: -12
51: 14

At 1909 I predicted 2980 and got 2964, a difference of 16
At 1910 I predicted 2966 and got 2980, a difference of -14

At 1949 I predicted 3044 and got 3030, a difference of 14
At 1950 I predicted 3032 and got 3044, a difference of -12

those are out of order ...

     */

    @Override
    public String title() {
        return "Day 17: Pyroclastic Flow";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2022, 17);
    }

    @Override
    public String part1(final String[] input) {
        final String windForecast = input[0];
        int iteration = 0;
        int windIndex = 0;
        this.buildCave();
        int rockIndex = 1;
        long rocksDropped = 0;
        Rock rock = new Rock(rockIndex);
        this.putRockInCave(rock);
        rocksDropped++;
        boolean running = true;
        String wind = windForecast.substring(windIndex, windIndex + 1);

        while (running) {
            boolean needToChangeRock = false;
            if (wind.equalsIgnoreCase(LEFT)) {
                if (!this.moveWouldCauseCollision(rock, -1, 0)) {
                    rock.moveLeft();
                }
            } else {
                if (!this.moveWouldCauseCollision(rock, 1, 0)) {
                    rock.moveRight();
                }
            }
            if (this.moveWouldCauseCollision(rock, 0, 1)) {
                needToChangeRock = true;
            } else {
                rock.moveDown();
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
        }
        return String.valueOf(this.findLevel());
    }

    @Override
    public String part2(final String[] input) {
        final String windForecast = input[0];
        int windIndex = 0;
        this.buildCave();
        int rockIndex = 1;
        long rocksDropped = 0;
        Rock rock = new Rock(rockIndex);
        this.putRockInCave(rock);
        rocksDropped++;
        boolean running = true;
        String wind = windForecast.substring(windIndex, windIndex + 1);
        final List<Integer> heights = new ArrayList<>();
        final List<Integer> rockDeltas = new ArrayList<>();
        long firstSequenceRockHeight = 0;
        // I should be able to get these from my method.
        final int firstRockToStartSequence = 209; // varying this +/- 1 same story, just different. also 1209
        final int sequenceLength = 1690;
        long lastRockHeight = 0;
        long rockSequenceHeight = 0;
        boolean haveDumpedDeltas = false;

        while (running) {
            boolean needToChangeRock = false;
            if (wind.equalsIgnoreCase(LEFT)) {
                if (!this.moveWouldCauseCollision(rock, -1, 0)) {
                    rock.moveLeft();
                }
            } else {
                if (!this.moveWouldCauseCollision(rock, 1, 0)) {
                    rock.moveRight();
                }
            }
            if (this.moveWouldCauseCollision(rock, 0, 1)) {
                needToChangeRock = true;
            } else {
                rock.moveDown();
            }
            if (needToChangeRock) {
                this.addRockToCave(rock);
                heights.add(rock.position.getY());
                if (heights.size() % 1000 == 0) {
                    this.lookForPatterns(heights, rocksDropped);
                }
                rockIndex++;
                if (rockIndex > 5) {
                    rockIndex = 1;
                }
                if (rocksDropped == firstRockToStartSequence) {
                    firstSequenceRockHeight = rock.position.getY();
                }
                if (rocksDropped > firstRockToStartSequence && rockDeltas.size() < sequenceLength) {
                    rockDeltas.add((int) (rock.position.getY() - lastRockHeight));
                    rockSequenceHeight = rockSequenceHeight + rockDeltas.get(rockDeltas.size() - 1);
                }
                if (rockDeltas.size() == sequenceLength && !haveDumpedDeltas) {
                    int index = 0;
                    for (final Integer rockDelta : rockDeltas) {
//                        System.out.println(index + ": " + rockDelta);
                        index++;
                    }
                    haveDumpedDeltas = true;
                }
                if (rockDeltas.size() == sequenceLength) {
                    final long numberOfRocksDroppedAfterSequenceStarted = rocksDropped - firstRockToStartSequence;
                    final long numberOfSequences = numberOfRocksDroppedAfterSequenceStarted / sequenceLength;
                    final long sequenceIndex = numberOfRocksDroppedAfterSequenceStarted % sequenceLength;
                    final long predictedHeight = firstSequenceRockHeight +
                            (rockSequenceHeight * numberOfSequences) +
                            this.cumulativeAddition(rockDeltas, sequenceIndex);
//                    System.out.println("At " + rocksDropped + " I predicted " + predictedHeight + " and got " + rock.position.getY() +
//                            ", a difference of " + (predictedHeight - rock.position.getY()));
                }

                lastRockHeight = rock.position.getY();
                rock = new Rock(rockIndex);
                this.putRockInCave(rock);
                rocksDropped++;
            }
            windIndex = windIndex + 1;
            if (windIndex == windForecast.length()) {
                windIndex = 0;
            }
            wind = windForecast.substring(windIndex, windIndex + 1);
            if (rocksDropped > 5000) {
                running = false;
            }
        }

        final long numberOfRocksDroppedAfterSequenceStarted = 1000000000000L - firstRockToStartSequence;
        final long numberOfSequences = numberOfRocksDroppedAfterSequenceStarted / sequenceLength;
        final long sequenceIndex = numberOfRocksDroppedAfterSequenceStarted % sequenceLength;
        final long predictedHeight = firstSequenceRockHeight +
                (rockSequenceHeight * numberOfSequences) +
                this.cumulativeAddition(rockDeltas, sequenceIndex);
        return String.valueOf(predictedHeight);
    }

    private long cumulativeAddition(final List<Integer> rockDeltas, final long sequenceIndex) {
        long total = 0;
        for (int i = 0; i < sequenceIndex; i++) {
            total += rockDeltas.get(i);
        }
        return total;
    }

    private void lookForPatterns(final List<Integer> heights, final long rocksDropped) {
        // get the last 10, 2 cycles of rocks
        final List<Integer> deltas = new ArrayList<>();
        // I don't think we can use the initial sequence, it has to slide
//        for (int i = 1; i < 101; i++) {
        final int sequenceLength = 100;
        final int sequenceStart = heights.size() - sequenceLength;
        final int sequenceEndExclusive = heights.size();
        for (int i = sequenceStart; i < sequenceEndExclusive; i++) {
            deltas.add(heights.get(i) - heights.get(i - 1));
        }
//        System.out.println("Got sequence of " + deltas.size() + " starting at " + (heights.size() - 11));
        for (int startPoint = 1; startPoint < heights.size() - 101; startPoint++) {
            boolean allMatch = true;
            final List<Integer> targets = new ArrayList<>();
            for (int i = 1; i < 101; i++) {
                targets.add(heights.get(i + startPoint) - heights.get(i + startPoint - 1));
            }
            for (int target = 0; target < targets.size(); target++) {
                if (!Objects.equals(deltas.get(target), targets.get(target))) {
                    allMatch = false;
                    break;
                }
            }
            if (!allMatch) {
                continue;
            }
            System.out.println("After " + rocksDropped + " rocks, found a sequence at " + startPoint + " matching that at " + sequenceStart + " with an offset of " + (sequenceStart - startPoint));
            for (int i = 0; i < deltas.size(); i++) {
//                System.out.println("  start " + (i + 1) + "=" + deltas.get(i) + " found again at " + (startPoint + i) + "=" + targets.get(i));
            }
        }
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

    private int putRockInCave(final Rock rock) {
        final int top = this.findLevel(); // 0 based
        int rowsAdded = 0;
        while (this.cave.size() <= (top + EMPTY + rock.getHeight())) {
            this.cave.add(WALL + SPACE.repeat(7) + WALL);
            rowsAdded++;
        }
        final int position = top + EMPTY + rock.getHeight();
        rock.position = new Coord(WALL_WIDTH + TWO, position);
//        System.out.printf("Adding rock %s top %s rowsAdded %s cave.size() %s\n",
//                rock.id,
//                top,
//                rowsAdded,
//                this.cave.size()
//        );
        return position;
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
