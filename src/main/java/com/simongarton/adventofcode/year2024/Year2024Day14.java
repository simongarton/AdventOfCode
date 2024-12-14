package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Year2024Day14 extends AdventOfCodeChallenge {

    private static final int BITMAP_SCALE = 4;

    private static final boolean DEBUG = false;

    List<List<Integer>> map;
    List<Robot> robots;

    int width = 101;
    int height = 103;

    @Override

    public String title() {
        return "Day 14: Restroom Redoubt";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 14);
    }

    @Override
    public String part1(final String[] input) {

        this.map = new ArrayList<>();
        for (int i = 0; i < this.height; i++) {
            final List<Integer> row = new ArrayList<>();
            for (int j = 0; j < this.width; j++) {
                row.add(0);
            }
            this.map.add(row);
        }

        this.loadRobots(input);
        if (DEBUG) {
            this.drawMap();
        }

        final int maxSeconds = 100;
        for (int second = 0; second < maxSeconds; second++) {
            this.moveRobots();
            if (DEBUG) {
                this.drawMap();
            }
        }

        return String.valueOf(this.countRobotsInQuadrant());
    }

    private int countRobotsInQuadrant() {

        final int[] counts = new int[4];
        for (final Robot robot : this.robots) {
            final Integer quadrant = this.getQuadrant(robot.position);
            if (quadrant == null) {
                continue;
            }
            counts[quadrant] = counts[quadrant] + 1;
        }
        return counts[0] * counts[1] * counts[2] * counts[3];
    }

    private Integer getQuadrant(final Coord position) {

        // individual quadrant doesn't matter
        final int middleRow = ((this.height + 1) / 2) - 1;
        final int middleCol = ((this.width + 1) / 2) - 1;
        if (position.y == middleRow || position.x == middleCol) {
            return null;
        }
        if (position.y < middleRow) {
            return position.x < middleCol ? 0 : 1;
        } else {
            return position.x < middleCol ? 2 : 3;
        }
    }

    private void moveRobots() {

        for (final Robot robot : this.robots) {
            this.removeRobotFromMap(robot.position);
            this.moveRobot(robot);
            this.addRobotToMap(robot.position);
        }
    }

    private void moveRobot(final Robot robot) {

        final int newX = (robot.position.x + robot.velocity.x + this.width) % this.width;
        final int newY = (robot.position.y + robot.velocity.y + this.height) % this.height;
        robot.position = new Coord(newX, newY);
    }

    private void drawMap() {

        for (int row = 0; row < this.height; row++) {
            final StringBuilder line = new StringBuilder();
            for (int col = 0; col < this.width; col++) {
                line.append(this.mapSymbol(this.getRobotCountOnMap(new Coord(col, row))));
            }
            System.out.println(line);
        }
        System.out.println();
    }

    private String mapSymbol(final int j) {
        if (j == 0) {
            return ".";
        }
        if (j >= 10) {
            return "*";
        }
        return String.valueOf(j);
    }

    private void loadRobots(final String[] input) {

        this.robots = new ArrayList<>();
        for (final String line : input) {
            this.loadRobot(line);
        }
    }

    private void loadRobot(final String line) {

        final String[] parts = line.split(" ");
        final Coord position = this.buildCoord(parts[0]);
        final Coord velocity = this.buildCoord(parts[1]);
        final Robot robot = new Robot(position, velocity);
        this.robots.add(robot);
        this.addRobotToMap(position);
    }

    private void addRobotToMap(final Coord position) {

        final List<Integer> row = this.map.get(position.y);
        row.set(position.x, row.get(position.x) + 1);
    }

    private void removeRobotFromMap(final Coord position) {

        final List<Integer> row = this.map.get(position.y);
        row.set(position.x, row.get(position.x) - 1);
        if (row.get(position.x) < 0) {
            throw new RuntimeException("oops");
        }
    }

    private int getSafeRobotCountOnMap(final Coord position) {
        try {
            return this.getRobotCountOnMap(position);
        } catch (final IndexOutOfBoundsException e) {
            return 0;
        }
    }

    private int getRobotCountOnMap(final Coord position) {

        final List<Integer> row = this.map.get(position.y);
        return row.get(position.x);
    }

    private Coord buildCoord(final String part) {

        final String rest = part.substring(2);
        final String[] coords = rest.split(",");
        return new Coord(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
    }

    @Override
    public String part2(final String[] input) {

        this.map = new ArrayList<>();
        for (int i = 0; i < this.height; i++) {
            final List<Integer> row = new ArrayList<>();
            for (int j = 0; j < this.width; j++) {
                row.add(0);
            }
            this.map.add(row);
        }

        this.loadRobots(input);

        int seconds = 0;
        while (true) {
            this.moveRobots();
            seconds++;
            if (this.mightBeThisOne()) {
                if (DEBUG) {
                    this.paintMap(seconds);
                }
                return String.valueOf(seconds);
            }
            if (seconds > 1000000) {
                break;
            }
            if ((seconds % 100000) == 0) {
                System.out.println(seconds);
            }
        }

        return null;
    }

    private boolean mightBeThisOne() {

        // assume they are clustered together - so everyone has a neighbour.
        final int robotTotal = this.robots.size();
        int robotsWithNeighbours = 0;

        for (final Robot robot : this.robots) {
            final int neighbours = this.getSafeRobotCountOnMap(new Coord(robot.position.x + 1, robot.position.y))
                    + this.getSafeRobotCountOnMap(new Coord(robot.position.x - 1, robot.position.y))
                    + this.getSafeRobotCountOnMap(new Coord(robot.position.x, robot.position.y + 1))
                    + this.getSafeRobotCountOnMap(new Coord(robot.position.x, robot.position.y - 1));
            if (neighbours > 1) {
                robotsWithNeighbours++;
            }
        }

        return robotsWithNeighbours >= (robotTotal / 2);
    }

    private void paintMap(final int seconds) {

        final String filename = "/Users/simongarton/projects/java/AdventOfCode/temp/robots-" + seconds + ".png";

        final BufferedImage bufferedImage = new BufferedImage(this.width * BITMAP_SCALE, this.height * BITMAP_SCALE, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);
        this.paintFloor(graphics2D);
        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
//            System.out.println(seconds + ":" + filename);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void paintFloor(final Graphics2D graphics2D) {
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                final int robotCount = this.getRobotCountOnMap(new Coord(col, row));
                if (robotCount > 0) {
                    graphics2D.setPaint(this.getColorForRobotCount(robotCount));
                    graphics2D.fillRect(col * BITMAP_SCALE, row * BITMAP_SCALE, BITMAP_SCALE, BITMAP_SCALE);
                }
            }
        }
    }

    private Paint getColorForRobotCount(final int robotCount) {
        if (robotCount == 0) {
            return Color.BLACK;
        }
        if (robotCount == 1) {
            return Color.GREEN;
        }
        return Color.RED;
    }

    private void clearBackground(final Graphics2D graphics2D) {

        graphics2D.setPaint(Color.BLACK);
        graphics2D.fillRect(0, 0, this.width * BITMAP_SCALE, this.height * BITMAP_SCALE);
    }

    static class Coord {

        public final int x;
        public final int y;

        public Coord(final int x, final int y) {

            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {

            return this.x + "," + this.y;
        }

        @Override
        public boolean equals(final Object o) {

            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Coord coord = (Coord) o;
            return this.x == coord.x && this.y == coord.y;
        }

        @Override
        public int hashCode() {

            return Objects.hash(this.x, this.y);
        }
    }

    static class Robot {

        Coord position;
        final Coord velocity;

        public Robot(final Coord position, final Coord velocity) {
            this.position = position;
            this.velocity = velocity;
        }
    }
}


