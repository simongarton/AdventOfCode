package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Year2024Day14 extends AdventOfCodeChallenge {

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
        this.drawMap();

        final int maxSeconds = 100;
        for (int second = 0; second < maxSeconds; second++) {
            this.moveRobots();
            this.drawMap();
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
        return null;
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


