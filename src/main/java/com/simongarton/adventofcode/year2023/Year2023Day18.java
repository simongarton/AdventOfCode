package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Year2023Day18 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;

    private List<Hole> diggings;
    private Map<String, Hole> diggingsMap;
    private int x;
    private int y;

    private String map;
    private int width;
    private int depth;
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;

    private int iteration;

    private final static Map<String, String> DIRECTIONS = new HashMap<>();

    static {
        DIRECTIONS.put("0", "R");
        DIRECTIONS.put("1", "D");
        DIRECTIONS.put("2", "L");
        DIRECTIONS.put("3", "U");
    }

    @Override
    public String title() {
        return "Day 18: Lavaduct Lagoon";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 18);
    }

    @Override
    public String part1(final String[] input) {

        // I need to find a start coordinate based on my map ... not sure how to.

        this.diggings = new ArrayList<>();
        this.diggingsMap = new HashMap<>();
        this.digHoles(input);
        this.buildMap();
        if (DEBUG) {
            this.paintMap("holey-moley-before.png");
        }

        // sample
//        this.floodFill(Coord.builder().x(2).y(1).build());
        // real
        this.floodFill(Coord.builder().x(1).y(-100).build());
        if (DEBUG) {
            this.paintMap("holey-moley-filled.png");
        }

        return String.valueOf(this.countRoom());
    }

    private void paintMap(final String filename) {

        final BufferedImage bufferedImage = new BufferedImage(this.width, this.depth, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);
        this.paintFloor(graphics2D);
        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void paintFloor(final Graphics2D graphics2D) {
        for (int row = 0; row < this.depth; row++) {
            for (int col = 0; col < this.width; col++) {
                final String floor = this.getRawFloor(col, row);
                if (floor.equalsIgnoreCase("#")) {
                    final Coord reverseCoord = this.reverseTranslateCoord(Coord.builder().x(col).y(row).build());
                    final Hole hole = this.diggingsMap.get(this.getCoordKey(reverseCoord));
                    if (hole == null) {
                        graphics2D.setPaint(Color.GREEN);
                    } else {
                        graphics2D.setPaint(Color.decode(hole.getColor()));
                    }
                    graphics2D.fillRect(col, row, 1, 1);
                }
            }
        }
    }

    private void paintFloorHole(final Graphics2D graphics2D) {
        for (final Hole hole : this.diggings) {
            final Coord coord = hole.getCoord();
            final Coord translatedCoord = this.translateCoord(coord);
            graphics2D.setPaint(Color.GREEN);
            graphics2D.fillRect(translatedCoord.getX(), translatedCoord.getY(), 1, 1);
        }
    }

    private void clearBackground(final Graphics2D graphics2D) {

        graphics2D.setPaint(Color.BLACK);
        graphics2D.fillRect(0, 0, this.width, this.depth);
    }

    private int countRoom() {
        int total = 0;
        for (int row = 0; row < this.depth; row++) {
            for (int col = 0; col < this.width; col++) {
                final String f = this.getRawFloor(col, row);
                if (f.equalsIgnoreCase("#")) {
                    total++;
                }
            }
        }
        return total;
    }

    private void floodFillRecursively(final int x, final int y) {
        if (this.getRawFloor(x, y).equalsIgnoreCase("#")) {
            return;
        }
        this.map = this.replaceCharacter(this.map, x, y, "#");
        if ((x - 1) >= 0) {
            this.floodFillRecursively(x - 1, y);
        }
        if ((x + 1) <= this.width) {
            this.floodFillRecursively(x + 1, y);
        }
        if ((y - 1) >= 0) {
            this.floodFillRecursively(x, y - 1);
        }
        if ((y + 1) <= this.depth) {
            this.floodFillRecursively(x, y + 1);
        }
        if (this.iteration++ % 10 == 0) {
            if (DEBUG) {
                this.paintMap("holey-moley-partial.png");
            }
        }
    }

    private void floodFill(final Coord coord) {
        final List<Coord> coordsToCheck = new ArrayList<>();
        coordsToCheck.add(coord);
        int iteration = 0;
        final Set<String> coordsToCheckKeys = new HashSet<>();
        while (!coordsToCheck.isEmpty()) {
            final Coord coordToCheck = coordsToCheck.get(0);
            coordsToCheck.remove(0);
            this.map = this.replaceCharacter(this.map, coordToCheck, "#");
            final List<Coord> neighbours = this.untouchedNeighbours(coordToCheck, coordsToCheckKeys);
            coordsToCheck.addAll(neighbours);
            coordsToCheckKeys.addAll(neighbours.stream().map(this::getCoordKey).collect(Collectors.toList()));
            iteration++;
//            if (iteration % 10000 == 0) {
//                this.paintMap("holey-moley-partial-" + iteration + ".png");
//            }
        }
    }

    private String getCoordKey(final Coord coordToCheck) {
        return coordToCheck.getX() + "," + coordToCheck.getY();
    }

    private List<Coord> untouchedNeighbours(final Coord coord, final Set<String> coordsToCheckKeys) {
        final List<Coord> neighbours = new ArrayList<>();
        final Coord up = this.neighbour(coord.getX(), coord.getY() - 1);
        final Coord down = this.neighbour(coord.getX(), coord.getY() + 1);
        final Coord left = this.neighbour(coord.getX() - 1, coord.getY());
        final Coord right = this.neighbour(coord.getX() + 1, coord.getY());
        this.maybeAdd(up, coordsToCheckKeys, neighbours);
        this.maybeAdd(left, coordsToCheckKeys, neighbours);
        this.maybeAdd(right, coordsToCheckKeys, neighbours);
        this.maybeAdd(down, coordsToCheckKeys, neighbours);
        return neighbours;
    }

    private void maybeAdd(final Coord up, final Set<String> coordsToCheckKeys, final List<Coord> neighbours) {
        if (!Objects.isNull(up)) {
            if (!coordsToCheckKeys.contains(this.getCoordKey(up))) {
                neighbours.add(up);
            }
        }
    }

    private Coord neighbour(final int x, final int y) {
        if (x < this.minX || x > this.maxX) {
            return null;
        }
        if (y < this.minY || y > this.maxY) {
            return null;
        }
        final String floor = this.getFloor(x, y);
        if (floor.equalsIgnoreCase("#")) {
            return null;
        }
        return Coord.builder().x(x).y(y).build();
    }

    private void digOutMiddle() {
        // ray-casting, but fooled by horizontal tunnels
        for (int row = 0; row < this.depth; row++) {
            boolean inside = false;
            for (int col = 0; col < this.width; col++) {
                final String f = this.getFloor(col, row);
                if (f.equalsIgnoreCase("#")) {
                    inside = !inside;
                    continue;
                }
                if (inside) {
                    final Coord coord = Coord.builder().x(col).y(row).build();
                    this.map = this.replaceCharacter(this.map, coord, "#");
                }
            }
        }
    }

    private String getFloor(final int x, final int y) {
        final Coord coord = Coord.builder().x(x).y(y).build();
        final Coord translatedCoord = this.translateCoord(coord);

        final int index = (translatedCoord.getY() * this.width) + translatedCoord.getX();
        try {
            return this.map.substring(index, index + 1);
        } catch (final StringIndexOutOfBoundsException e) {
            System.out.println(x + "," + y + " -> " + translatedCoord + " -> " + index + " (" + this.map.length() + ")");
            throw new RuntimeException("broke");
        }
    }

    private String getRawFloor(final int x, final int y) {
        final int index = (y * this.width) + x;
        return this.map.substring(index, index + 1);
    }

    private void buildMap() {
        final StringBuilder mapBuilder = new StringBuilder("");
        this.figureBounds();
        // 0 based
        this.width = 1 + this.maxX - this.minX;
        this.depth = 1 + this.maxY - this.minY;

        for (int row = 0; row < this.depth; row++) {
            mapBuilder.append(".".repeat(this.width));
        }

        this.map = mapBuilder.toString();

        for (final Hole hole : this.diggings) {
            this.map = this.replaceCharacter(this.map, hole.getCoord(), "#");
        }
    }

    private void debugMap() {

        for (int row = 0; row < this.depth; row++) {
            final String line = this.map.substring(row * this.width, (row + 1) * this.width);
            System.out.println(line);
        }
        this.blankLine();
    }

    private String replaceCharacter(final String map, final Coord coord, final String replacement) {

        final Coord translatedCoord = this.translateCoord(coord);
        return this.replaceCharacter(map, translatedCoord.getX(), translatedCoord.getY(), replacement);
    }

    private String replaceCharacter(final String map, final int x, final int y, final String replacement) {

        final int index = (y * this.width) + x;
        return this.replaceCharacter(map, index, replacement);
    }

    private Coord translateCoord(final Coord coord) {
        return Coord.builder()
                .x(coord.getX() - this.minX)
                .y(coord.getY() - this.minY)
                .build();
    }

    private Coord reverseTranslateCoord(final Coord coord) {
        return Coord.builder()
                .x(coord.getX() + this.minX)
                .y(coord.getY() + this.minY)
                .build();
    }

    private String replaceCharacter(final String original, final Integer index, final String replacement) {

        return original.substring(0, index) + replacement + original.substring(index + 1);
    }

    private void figureBounds() {
        for (final Hole hole : this.diggings) {
            this.minX = Math.min(this.minX, hole.getCoord().getX());
            this.maxX = Math.max(this.maxX, hole.getCoord().getX());
            this.minY = Math.min(this.minY, hole.getCoord().getY());
            this.maxY = Math.max(this.maxY, hole.getCoord().getY());
        }
    }

    private void digHoles(final String[] input) {
        Arrays.stream(input).forEach(this::digHole);
    }

    private void digHolesPart2(final String[] input) {
        Arrays.stream(input).forEach(this::digHolePart2);
    }

    private void digHole(final String h) {
        final String[] parts = h.split(" ");
        final String direction = parts[0];
        final int distance = Integer.parseInt(parts[1]);
        final String color = parts[2].replace("(", "").replace(")", "");
        for (int i = 0; i < distance; i++) {
            this.digOneHole(direction, color);
        }
    }

    private void digHolePart2(final String h) {
        final String[] parts = h.split(" ");
        final String color = parts[2].replace("(", "").replace(")", "");
        final String five = color.substring(1, 6);
        final String one = color.substring(6, 7);
        final int distance = Integer.parseInt(five, 16);
        final String direction = DIRECTIONS.get(one);
        for (int i = 0; i < distance; i++) {
            this.digOneHole(direction, color);
        }
    }

    private void digOneHole(final String direction, final String color) {
        final Coord coord = Coord.builder()
                .x(this.nextX(direction))
                .y(this.nextY(direction))
                .build();
        final Hole hole = Hole.builder()
                .coord(coord)
                .color(color)
                .build();
        this.diggings.add(hole);
        this.diggingsMap.put(this.getCoordKey(coord), hole);
        this.x = hole.getCoord().getX();
        this.y = hole.getCoord().getY();
    }

    private int nextX(final String direction) {
        switch (direction) {
            case "L":
                return this.x - 1;
            case "R":
                return this.x + 1;
            default:
                return this.x;
        }
    }

    private int nextY(final String direction) {
        switch (direction) {
            case "U":
                return this.y - 1;
            case "D":
                return this.y + 1;
            default:
                return this.y;
        }
    }

    @Override
    public String part2(final String[] input) {

        // I need to find a start coordinate based on my map ... not sure how to.

        /*
        minX = -2902613
        maxX = 11304609
        minY = -8817887
        maxY = 11393329
         */

        this.diggings = new ArrayList<>();
        this.diggingsMap = new HashMap<>();

        if (true) {
            // Will die with java.lang.OutOfMemoryError: Java heap space
            return String.valueOf(-1);
        }

        this.digHolesPart2(input);
        this.buildMap();
        if (DEBUG) {
            this.paintMap("holey-moley-before.png");
        }

        this.floodFill(Coord.builder().x(1).y(-100).build());
        if (DEBUG) {
            this.paintMap("holey-moley-filled.png");
        }

        return String.valueOf(this.countRoom());
    }

    @Data
    @Builder
    private static final class Hole {
        private Coord coord;
        private String color;
    }

    @Data
    @Builder
    private static final class Coord {
        private int x;
        private int y;
    }
}
