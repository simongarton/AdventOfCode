package com.simongarton.adventofcode.year2015;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.ChallengeCoord;

import java.util.HashMap;
import java.util.Map;

public class Year2015Day3 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 3: Perfectly Spherical Houses in a Vacuum";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2015, 3);
    }

    @Override
    public String part1(final String[] input) {

        long housesVisited = 0;
        for (final String line : input) {
            housesVisited += this.housesVisitedForDirections(line);
        }

        return String.valueOf(housesVisited);
    }

    private long housesVisitedForDirections(final String line) {

        final Map<ChallengeCoord, Integer> map = new HashMap<>();

        ChallengeCoord location = new ChallengeCoord(0, 0);
        map.put(location, map.getOrDefault(location, 0));

        for (int i = 0; i < line.length(); i++) {
            location = this.updateLocation(location, line.substring(i, i + 1));
            map.put(location, map.getOrDefault(location, 0));
        }

        return map.size();
    }

    private ChallengeCoord updateLocation(final ChallengeCoord location, final String substring) {
        if (substring.equalsIgnoreCase("<")) {
            return new ChallengeCoord(location.getX() - 1, location.getY());
        }
        if (substring.equalsIgnoreCase(">")) {
            return new ChallengeCoord(location.getX() + 1, location.getY());
        }
        if (substring.equalsIgnoreCase("^")) {
            return new ChallengeCoord(location.getX(), location.getY() - 1);
        }
        if (substring.equalsIgnoreCase("v")) {
            return new ChallengeCoord(location.getX(), location.getY() + 1);
        }
        throw new RuntimeException(substring);
    }

    @Override
    public String part2(final String[] input) {

        long housesVisited = 0;
        for (final String line : input) {
            housesVisited += this.housesVisitedByTwoForDirections(line);
        }

        return String.valueOf(housesVisited);
    }

    private long housesVisitedByTwoForDirections(final String line) {

        final Map<ChallengeCoord, Integer> map = new HashMap<>();

        ChallengeCoord santa = new ChallengeCoord(0, 0);
        ChallengeCoord roboSanta = new ChallengeCoord(0, 0);
        map.put(santa, map.getOrDefault(santa, 0));
        map.put(santa, map.getOrDefault(roboSanta, 0)); // hmm

        for (int i = 0; i < line.length(); i += 2) {
            santa = this.updateLocation(santa, line.substring(i, i + 1));
            map.put(santa, map.getOrDefault(santa, 0));
            roboSanta = this.updateLocation(roboSanta, line.substring(i + 1, i + 2));
            map.put(roboSanta, map.getOrDefault(roboSanta, 0));
        }

        return map.size();
    }
}
