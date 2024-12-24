package com.simongarton.adventofcode.year2016;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.ChallengeCoord;
import com.simongarton.adventofcode.common.CompassRose;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.simongarton.adventofcode.common.CompassRose.*;

public class Year2016Day1 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 1: No Time for a Taxicab";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2016, 1);
    }

    @Override
    public String part1(final String[] input) {

        int x = 0;
        int y = 0;
        CompassRose heading = CompassRose.NORTH;

        for (final String direction : input) {
            for (final String details : direction.split(", ")) {
                final String turn = details.substring(0, 1);
                final int steps = Integer.parseInt(details.substring(1));
                heading = this.turnIntoHeading(heading, turn);
                switch (heading) {
                    case NORTH -> {
                        y = y - steps;
                    }
                    case EAST -> {
                        x = x - steps;
                    }
                    case SOUTH -> {
                        y = y + steps;
                    }
                    case WEST -> {
                        x = x + steps;
                    }
                }
            }
        }
        return String.valueOf(Math.abs(y) + Math.abs(x));
    }

    private CompassRose turnIntoHeading(final CompassRose heading, final String turn) {

        final Map<CompassRose, CompassRose> turns;
        if (turn.equalsIgnoreCase("R")) {
            turns = Map.of(
                    NORTH, EAST,
                    EAST, SOUTH,
                    SOUTH, WEST,
                    WEST, NORTH
            );
        } else {
            turns = Map.of(
                    NORTH, WEST,
                    WEST, SOUTH,
                    SOUTH, EAST,
                    EAST, NORTH
            );
        }
        return turns.get(heading);
    }

    @Override
    public String part2(final String[] input) {

        int x = 0;
        int y = 0;
        CompassRose heading = CompassRose.NORTH;
        final Set<ChallengeCoord> visited = new HashSet<>();

        for (final String direction : input) {
            boolean foundHQ = false;
            for (final String details : direction.split(", ")) {
                final String turn = details.substring(0, 1);
                final int steps = Integer.parseInt(details.substring(1));
                heading = this.turnIntoHeading(heading, turn);
                final ChallengeCoord delta;
                switch (heading) {
                    case NORTH -> {
                        delta = ChallengeCoord.builder().x(0).y(-1).build();
                    }
                    case EAST -> {
                        delta = ChallengeCoord.builder().x(1).y(0).build();
                    }
                    case SOUTH -> {
                        delta = ChallengeCoord.builder().x(0).y(1).build();
                    }
                    case WEST -> {
                        delta = ChallengeCoord.builder().x(-1).y(0).build();
                    }
                    default -> throw new RuntimeException("oop");
                }
                for (int i = 0; i < steps; i++) {
                    x = x + delta.getX();
                    y = y + delta.getY();
                    final ChallengeCoord current = ChallengeCoord.builder().x(x).y(y).build();
                    if (visited.contains(current)) {
                        foundHQ = true;
                        break;
                    }
                    visited.add(current);
                }
                if (foundHQ) {
                    break;
                }
            }
        }
        return String.valueOf(Math.abs(y) + Math.abs(x));
    }
}
