package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2024Day8 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 8: Resonant Collinearity";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 8);
    }

    @Override
    public String part1(final String[] input) {

        this.loadChallengeMap(input);
        this.drawChallengeMap();
        return null;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }
}
