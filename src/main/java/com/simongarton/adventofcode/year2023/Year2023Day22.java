package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.*;

public class Year2023Day22 extends AdventOfCodeChallenge {

    private List<Brick> bricks;
    private int maxX;
    private int maxY;
    private int maxZ;

    private int width;
    private int depth;
    private int height;

    private Map<String, Set<String>> isSupportedBy;
    private Map<String, Set<String>> supports;

    @Override
    public String title() {
        return "Day 22: Sand Slabs";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 22);
    }

    @Override
    public String part1(final String[] input) {

        this.loadBricks(input);
//        this.drawXY();
        this.fallBricks();
//        this.drawXY();
        this.figureOutSupportedBy();
        return String.valueOf(this.countDisintegratable());
    }

    private int countDisintegratable() {

        int d = 0;
        for (final Brick brick : this.bricks) {
//            System.out.println(brick.getId());
//            System.out.println("  supports " + this.supports.get(brick.getId()));
//            System.out.println("  supportedBy " + this.isSupportedBy.get(brick.getId()));
            boolean allGood = true;
            if (this.supports.containsKey(brick.getId())) {
                for (final String needsSupport : this.supports.get(brick.getId())) {
                    if (this.isSupportedBy.get(needsSupport).size() == 1) {
                        allGood = false;
                    }
                }
            }
//            System.out.println(allGood);
            if (allGood) {
                d++;
            }
        }
        return d;
    }

    private void figureOutSupportedBy() {

        this.isSupportedBy = new HashMap<>();
        this.supports = new HashMap<>();
        for (final Brick top : this.bricks) {
            for (final Brick bottom : this.bricks) {
                if (top.getId().equalsIgnoreCase(bottom.getId())) {
                    continue;
                }
                for (final Coord3d topCoord : top.getCoords()) {
                    for (final Coord3d bottomCoord : bottom.getCoords()) {
                        if (this.underneath(bottomCoord, topCoord)) {
                            final Set<String> supportedBy = this.isSupportedBy.getOrDefault(top.getId(), new HashSet<>());
                            supportedBy.add(bottom.getId());
                            this.isSupportedBy.put(top.getId(), supportedBy);
                            final Set<String> supports = this.supports.getOrDefault(bottom.getId(), new HashSet<>());
                            supports.add(top.getId());
                            this.supports.put(bottom.getId(), supports);
                        }
                    }
                }

            }
        }

        System.out.println("digraph {");
        for (final Map.Entry<String, Set<String>> entry : this.isSupportedBy.entrySet()) {
            for (final String under : entry.getValue()) {
                System.out.println("  " + entry.getKey() + " -> " + under);
            }
        }
        System.out.println("}");
    }

    private void drawXY() {

        this.drawX();
        this.drawY();
    }

    private void drawY() {

        String map = (".".repeat(this.depth)).repeat(this.height);
        for (final Brick brick : this.bricks) {
            for (final Coord3d coord : brick.getCoords()) {
                final int x = coord.getY();
                final int y = coord.getZ();
                map = this.replaceCharacter(map, x, y, this.depth, String.valueOf(brick.getId()));
            }
        }

        for (int row = this.height - 1; row >= 0; row--) {
            final String line = map.substring(row * this.depth, (row + 1) * this.depth);
            System.out.println(line);
        }
        this.blankLine();
    }

    private void drawX() {

        String map = (".".repeat(this.width).repeat(this.height));
        for (final Brick brick : this.bricks) {
            for (final Coord3d coord : brick.getCoords()) {
                final int x = coord.getX();
                final int y = coord.getZ();
                map = this.replaceCharacter(map, x, y, this.width, String.valueOf(brick.getId()));
            }
        }

        for (int row = this.height - 1; row >= 0; row--) {
            final String line = map.substring(row * this.width, (row + 1) * this.width);
            System.out.println(line);
        }
        this.blankLine();
    }

    private void blankLine() {

        System.out.println();
    }

    private String replaceCharacter(final String original, final int x, final int y, final int width,
                                    final String replacement) {

        final int index = (y * width) + x;
        return original.substring(0, index) + replacement + original.substring(index + 1);
    }

    private void fallBricks() {

        int falls = 2;
        while (true) {
            int fell = 0;
            for (final Brick brick : this.bricks) {
                if (this.fall(brick)) {
                    fell++;
                }
            }
            if (fell == 0) {
                falls--;
            }
            if (falls == 0) {
                break;
            }
        }
    }

    private boolean fall(final Brick brick) {
        for (final Coord3d coord : brick.getCoords()) {
            if (coord.getZ() == 0) {
                return false;
            }
            if (this.otherBrickUnderneath(coord, brick.getId())) {
                return false;
            }
        }

        for (final Coord3d coord : brick.getCoords()) {
            coord.setZ(coord.getZ() - 1);
        }
        return true;
    }

    private boolean otherBrickUnderneath(final Coord3d brickCoord, final String id) {

        for (final Brick brick : this.bricks) {
            if (brick.getId().equalsIgnoreCase(id)) {
                continue;
            }
            for (final Coord3d coord : brick.getCoords()) {
                if (this.underneath(coord, brickCoord)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean underneath(final Coord3d other, final Coord3d me) {

        if (other.getX() != me.getX()) {
            return false;
        }
        if (other.getY() != me.getY()) {
            return false;
        }
        return other.getZ() == me.getZ() - 1;
    }

    private void loadBricks(final String[] input) {

        this.bricks = new ArrayList<>();
        for (final String line : input) {
            this.loadBrick(line);
        }

        this.width = this.maxX + 1;
        this.depth = this.maxY + 1;
        this.height = this.maxZ + 1;
    }

    private void loadBrick(final String line) {

        final String[] parts = line.split("~");
        final Coord3d start = this.buildCoord3d(parts[0]);
        final Coord3d end = this.buildCoord3d(parts[1]);
        final Brick brick = Brick.builder()
                .id(this.createId(this.bricks.size()))
                .coords(this.traceCoords(start, end))
                .build();
        this.bricks.add(brick);
    }

    private String createId(final int size) {

        return String.valueOf(size);
    }

    private List<Coord3d> traceCoords(final Coord3d start, final Coord3d end) {

        final int deltaX = start.getX() - end.getX();
        final int deltaY = start.getY() - end.getY();
        final int deltaZ = start.getZ() - end.getZ();

        if (deltaX == 0 && deltaY == 0 && deltaZ == 0) {
            return List.of(start);
        }

        if (deltaX != 0) {
            return this.coordRow(start, end, "x", deltaX < 0 ? 1 : -1);
        }
        if (deltaY != 0) {
            return this.coordRow(start, end, "y", deltaY < 0 ? 1 : -1);
        }
        return this.coordRow(start, end, "z", deltaZ < 0 ? 1 : -1);
    }

    private List<Coord3d> coordRow(final Coord3d start, final Coord3d end, final String which, final int delta) {

        final List<Coord3d> coords = new ArrayList<>();
        Coord3d coord = start;
        coords.add(coord);
        while (true) {
            final int newX = which.equalsIgnoreCase("x") ? coord.getX() + delta : coord.getX();
            final int newY = which.equalsIgnoreCase("y") ? coord.getY() + delta : coord.getY();
            final int newZ = which.equalsIgnoreCase("z") ? coord.getZ() + delta : coord.getZ();
            coord = Coord3d.builder()
                    .x(newX)
                    .y(newY)
                    .z(newZ)
                    .build();
            coords.add(coord);
            if (coord.equals(end)) {
                break;
            }
        }
        return coords;
    }

    private Coord3d buildCoord3d(final String part) {

        final String[] parts = part.split(",");
        final Coord3d coord = Coord3d.builder()
                .x(Integer.parseInt(parts[0]))
                .y(Integer.parseInt(parts[1]))
                .z(Integer.parseInt(parts[2]))
                .build();
        if (coord.getX() < 0 || coord.getY() < 0 || coord.getZ() < 0) {
            throw new RuntimeException("negatory");
        }
        this.maxX = Math.max(this.maxX, coord.getX());
        this.maxY = Math.max(this.maxY, coord.getY());
        this.maxZ = Math.max(this.maxZ, coord.getZ());
        return coord;
    }

    @Override
    public String part2(final String[] input) {

        return null;
    }

    @Data
    @Builder
    private static final class Brick {

        private String id;
        private List<Coord3d> coords;

    }

    @Data
    @Builder
    private static final class Coord3d {

        private int x;
        private int y;
        private int z;
    }
}
