package com.simongarton.adventofcode.year2015;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2015Day8 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 8: Matchsticks";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2015, 8);
    }

    @Override
    public String part1(final String[] input) {

        int total = 0;
        for (String line : input) {
            total += score(line);
        }

        return String.valueOf(total);
    }

    private int score(String line) {

        return 0;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }
}
