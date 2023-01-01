package com.simongarton.adventofcode.year2020;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static com.simongarton.adventofcode.year2020.Year2020Day12.Direction.EAST;

public class Year2020Day12 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 12: Rain Risk";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2020, 12);
    }

    @Override
    public String part1(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        final Ship ship = new Ship(EAST, 0, 0);
        return String.valueOf(ship.followDirections(lines));
    }

    @Override
    public String part2(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        final Ship ship = new Ship(EAST, 0, 0);
        return String.valueOf(ship.followWaypointDirections(lines));

    }

    @Getter
    public enum Direction {

        NORTH(0),
        EAST(90),
        SOUTH(180),
        WEST(270);

        private final int bearing;

        Direction(final int bearing) {
            this.bearing = bearing;
        }

        public static Direction getFromBearing(final int bearing) {
            return Arrays.stream(values()).filter(b -> b.bearing == (bearing % 360)).findFirst().orElse(null);
        }
    }

    @Getter
    public static class Ship {

        private int east;
        private int north;
        private Direction direction;
        private int waypointEast;
        private int waypointNorth;

        public Ship(final Direction direction, final int east, final int north) {
            this.direction = direction;
            this.east = east;
            this.north = north;
            this.waypointEast = east + 10;
            this.waypointNorth = north + 1;
        }

        public long followDirections(final List<String> lines) {
            for (final String line : lines) {
                this.followDirection(line);
            }
            return Math.abs((long) this.east) + Math.abs((long) this.north);
        }

        public long followWaypointDirections(final List<String> lines) {
            for (final String line : lines) {
                this.followWaypointDirection(line);
            }
            return Math.abs((long) this.east) + Math.abs((long) this.north);
        }

        private void followDirection(final String line) {
            final String instruction = line.substring(0, 1);
            final int number = Integer.parseInt(line.substring(1));
            switch (instruction) {
                case "N":
                    this.north = this.north + number;
                    break;
                case "S":
                    this.north = this.north - number;
                    break;
                case "E":
                    this.east = this.east + number;
                    break;
                case "W":
                    this.east = this.east - number;
                    break;
                case "F":
                    switch (this.direction) {
                        case NORTH:
                            this.north = this.north + number;
                            break;
                        case SOUTH:
                            this.north = this.north - number;
                            break;
                        case EAST:
                            this.east = this.east + number;
                            break;
                        case WEST:
                            this.east = this.east - number;
                            break;
                    }
                    break;
                case "L":
                    this.direction = Direction.getFromBearing(360 + this.direction.getBearing() - number);
                    break;
                case "R":
                    this.direction = Direction.getFromBearing(this.direction.getBearing() + number);
                    break;
                default:
                    throw new RuntimeException("unrecognised instruction " + instruction);
            }
        }

        private void followWaypointDirection(final String line) {
            final String instruction = line.substring(0, 1);
            final int number = Integer.parseInt(line.substring(1));
            switch (instruction) {
                case "N":
                    this.waypointNorth = this.waypointNorth + number;
                    break;
                case "S":
                    this.waypointNorth = this.waypointNorth - number;
                    break;
                case "E":
                    this.waypointEast = this.waypointEast + number;
                    break;
                case "W":
                    this.waypointEast = this.waypointEast - number;
                    break;
                case "F":
                    final int deltaEast = this.waypointEast - this.east;
                    final int deltaNorth = this.waypointNorth - this.north;
                    this.north = this.north + (this.waypointNorth - this.north) * number;
                    this.east = this.east + (this.waypointEast - this.east) * number;
                    this.waypointNorth = this.north + deltaNorth;
                    this.waypointEast = this.east + deltaEast;
                    break;
                case "L":
                    this.rotateWaypoint(360 - number);
                    break;
                case "R":
                    this.rotateWaypoint(number);
                    break;
                default:
                    throw new RuntimeException("unrecognised instruction " + instruction);
            }
        }

        private void rotateWaypoint(final int angle) {
            final int deltaEast = this.waypointEast - this.east;
            final int deltaNorth = this.waypointNorth - this.north;
            switch (angle) {
                case 0:
                    break;
                case 90:
                    this.waypointEast = this.east + deltaNorth;
                    this.waypointNorth = this.north - deltaEast;
                    break;
                case 180:
                    this.waypointEast = this.east - deltaEast;
                    this.waypointNorth = this.north - deltaNorth;
                    break;
                case 270:
                    this.waypointEast = this.east - deltaNorth;
                    this.waypointNorth = this.north + deltaEast;
                    break;
                default:
                    throw new RuntimeException("Unhandled angle " + angle);
            }
        }

        private String getPosition() {
            return this.east + "," + this.north + " facing " + this.direction + " waypoint at " + this.waypointEast + "," + this.waypointNorth;
        }
    }
}
