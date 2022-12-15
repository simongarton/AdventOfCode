package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2022Day14 extends AdventOfCodeChallenge {

    private char[] map;
    private Bounds bounds;

    private static final char WATER = 46;
    private static final char ROCK = 35;
    private static final char SAND = 111;

    @Override
    public String title() {
        return "Day 14: Regolith Reservoir";
    }

    @Override
    public boolean run() {
        return this.runChallenge(2022, 14);
    }

    @Override
    public String part1(final String[] input) {
        this.loadMap(input, false);
        final int result = this.sinkSand();
        this.saveMap();
        this.saveImage("2022-14.1.png");
        return String.valueOf(result);
    }

    @Override
    public String part2(final String[] input) {
        this.loadMap(input, true);
        final int result = this.sinkSand();
        this.saveMap();
        this.saveImage("2022-14.2.png");
        return String.valueOf(result);
    }

    private void loadMap(final String[] input, final boolean extendMap) {
        this.bounds = new Bounds();
        final List<List<Coord>> coordLines = new ArrayList<>();
        for (final String line : input) {
            final String[] coords = line.split(" -> ");
            final List<Coord> coordLine = Arrays.stream(coords).map(Coord::new).collect(Collectors.toList());
            this.updateBounds(coordLine);
            coordLines.add(coordLine);
        }
        // silly hack for map
        this.bounds.minY = 0;
        if (extendMap) {
            this.bounds.maxY = this.bounds.maxY + 2;
            this.bounds.minX = this.bounds.minX - this.bounds.maxY;
            this.bounds.maxX = this.bounds.maxX + this.bounds.maxY;
        }
        this.map = new char[this.bounds.getWidth() * this.bounds.getHeight()];
        for (int x = this.bounds.minX; x <= this.bounds.maxX; x++) {
            for (int y = this.bounds.minY; y <= this.bounds.maxY; y++) {
                this.setMap(WATER, x, y);
            }
        }
        if (extendMap) {
            for (int x = this.bounds.minX; x <= this.bounds.maxX; x++) {
                this.setMap(ROCK, x, this.bounds.maxY);
            }
        }
        // now draw the rocks
        for (final List<Coord> coordLine : coordLines) {
            this.addRocks(coordLine);
        }
    }

    private int sinkSand() {
        final Coord sand = new Coord("500,0");
        int sunkSand = 0;
        while (true) {
            if (sand.getY() >= this.bounds.maxY) {
                // this feels hacky, but worked.
                break;
            }
            if (this.getMap(sand.getX(), sand.getY() + 1) == WATER) {
                sand.setY(sand.getY() + 1);
                continue;
            }
            // try down left
            int newX = sand.getX() - 1;
            final int newY = sand.getY() + 1;
            if (newX >= this.bounds.minX) {
                if (this.getMap(newX, newY) == WATER) {
                    sand.setX(newX);
                    sand.setY(newY);
                    continue;
                }
            } else {
                break;
            }
            // try down right
            newX = sand.getX() + 1;
            if (newX <= this.bounds.maxX) {
                if (this.getMap(newX, newY) == WATER) {
                    sand.setX(newX);
                    sand.setY(newY);
                    continue;
                }
            } else {
                break;
            }
            this.setMap(SAND, sand.getX(), sand.getY());
            sunkSand++;
            if (sand.getY() == 0) {
                break;
            }
            sand.setX(500);
            sand.setY(0);
//            this.displayMap();
        }
        return sunkSand;
    }

    private void setMap(final char code, final int x, final int y) {
        // this should be real, not 0 based
        final int mapX = x - this.bounds.minX;
        final int mapY = y - this.bounds.minY;
        final int index = mapY * this.bounds.getWidth() + mapX;
        this.map[index] = code;
    }

    private char getMap(final int x, final int y) {
        // this should be real, not 0 based
        final int mapX = x - this.bounds.minX;
        final int mapY = y - this.bounds.minY;
        final int index = mapY * this.bounds.getWidth() + mapX;
        return this.map[index];
    }

    private void addRocks(final List<Coord> coordLine) {
        for (int index = 0; index < coordLine.size() - 1; index++) {
            final Coord from = coordLine.get(index);
            final Coord to = coordLine.get(index + 1);
            this.drawRocks(from, to);
        }
    }

    private void drawRocks(final Coord from, final Coord to) {
        int deltaX = 0;
        int deltaY = 0;
        if (to.getX() > from.getX()) {
            deltaX = 1;
        }
        if (to.getX() < from.getX()) {
            deltaX = -1;
        }
        if (to.getY() > from.getY()) {
            deltaY = 1;
        }
        if (to.getY() < from.getY()) {
            deltaY = -1;
        }
        final Coord working = new Coord(from.getX() + "," + from.getY());
        this.setMap(ROCK, working.getX(), working.getY());
        while (!(working.getX().equals(to.getX()) && working.getY().equals(to.getY()))) {
            working.setX(working.getX() + deltaX);
            working.setY(working.getY() + deltaY);
            this.setMap(ROCK, working.getX(), working.getY());
        }
    }

    private void displayMap() {
        for (int y = this.bounds.minY; y <= this.bounds.maxY; y++) {
            String line = "";
            for (int x = this.bounds.minX; x <= this.bounds.maxX; x++) {
                final char contents = this.getMap(x, y);
                switch (contents) {
                    case WATER:
                        line += ".";
                        break;
                    case ROCK:
                        line += "#";
                        break;
                    case SAND:
                        line += "o";
                        break;
                    default:
                        throw new RuntimeException("Something else");
                }
            }
            System.out.println(line);
        }
        System.out.println("");
    }

    private void saveMap() {
        final List<String> lines = new ArrayList<>();
        for (int y = this.bounds.minY; y <= this.bounds.maxY; y++) {
            String line = "";
            for (int x = this.bounds.minX; x <= this.bounds.maxX; x++) {
                final char contents = this.getMap(x, y);
                switch (contents) {
                    case WATER:
                        line += ".";
                        break;
                    case ROCK:
                        line += "#";
                        break;
                    case SAND:
                        line += "o";
                        break;
                    default:
                        throw new RuntimeException("Something else");
                }
            }
            lines.add(line);
        }
        try {
            Files.writeString(Path.of("map.txt"), String.join("\n", lines));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void saveImage(final String filename) {
        final BufferedImage img = new BufferedImage(this.bounds.getWidth(), this.bounds.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = this.bounds.minY; y <= this.bounds.maxY; y++) {
            for (int x = this.bounds.minX; x <= this.bounds.maxX; x++) {
                final char contents = this.getMap(x, y);
                switch (contents) {
                    case WATER:
                        img.setRGB(x - this.bounds.minX, y - this.bounds.minY, Color.CYAN.getRGB());
                        break;
                    case ROCK:
                        img.setRGB(x - this.bounds.minX, y - this.bounds.minY, Color.GRAY.getRGB());
                        break;
                    case SAND:
                        img.setRGB(x - this.bounds.minX, y - this.bounds.minY, Color.YELLOW.getRGB());
                        break;
                    default:
                        throw new RuntimeException("Something else");
                }
            }
        }
        try {
            final File outputfile = new File(filename);
            ImageIO.write(img, "png", outputfile);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void updateBounds(final List<Coord> coordLine) {
        for (final Coord coord : coordLine) {
            if (this.bounds.minX == null || coord.getX() < this.bounds.minX) {
                this.bounds.minX = coord.getX();
            }
            if (this.bounds.minY == null || coord.getY() < this.bounds.minY) {
                this.bounds.minY = coord.getY();
            }
            if (this.bounds.maxX == null || coord.getX() > this.bounds.maxX) {
                this.bounds.maxX = coord.getX();
            }
            if (this.bounds.maxY == null || coord.getY() > this.bounds.maxY) {
                this.bounds.maxY = coord.getY();
            }
        }
    }

    public static final class Bounds {
        private Integer minX;
        private Integer minY;
        private Integer maxX;
        private Integer maxY;

        @Override
        public String toString() {
            return this.minX + "," + this.minY + " -> " + this.maxX + "," + this.maxY;
        }

        public int getWidth() {
            return 1 + this.maxX - this.minX;
        }

        public int getHeight() {
            return 1 + this.maxY - this.minY;
        }
    }
}

