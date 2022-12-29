package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.AllArgsConstructor;
import lombok.Data;

public class Year2021Day2 extends AdventOfCodeChallenge {

    @Override
    public Outcome run() {
        return this.runChallenge(2021, 2);
    }

    @Override
    public String part1(final String[] input) {
        final Coord c = new Coord(0, 0, 0);
        for (final String line : input) {
            this.move(line, c);
        }
        return String.valueOf(c.destination());
    }

    @Override
    public String part2(final String[] input) {
        final Coord c = new Coord(0, 0, 0);
        for (final String line : input) {
            this.moveWithAim(line, c);
        }
        return String.valueOf(c.destination());
    }

    @Data
    @AllArgsConstructor
    private static final class Coord {
        int x;
        int z;
        int aim;

        public int destination() {
            return this.x * this.z;
        }
    }

    private Coord move(final String line, final Coord c) {
        final String[] commands = line.split(" ");
        final String command = commands[0];
        final int delta = Integer.parseInt(commands[1]);
        switch (command) {
            case "forward":
                c.setX(c.getX() + delta);
                break;
            case "up":
                c.setZ(c.getZ() - delta);
                break;
            case "down":
                c.setZ(c.getZ() + delta);
                break;
            default:
                throw new RuntimeException("Unknown command " + command);
        }
        return c;
    }

    private Coord moveWithAim(final String line, final Coord c) {
        final String[] commands = line.split(" ");
        final String command = commands[0];
        final int delta = Integer.parseInt(commands[1]);
        switch (command) {
            case "forward":
                c.setX(c.getX() + delta);
                c.setZ(c.getZ() + (delta * c.getAim()));
                break;
            case "up":
                c.setAim(c.getAim() - delta);
                break;
            case "down":
                c.setAim(c.getAim() + delta);
                break;
            default:
                throw new RuntimeException("Unknown command " + command);
        }
        return c;
    }
}
