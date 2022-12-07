package com.simongarton.adventofcode;

import com.simongarton.adventofcode.year2022.*;

import java.util.ArrayList;
import java.util.List;

public class AdventOfCode {

    private final List<AdventOfCodeChallenge> challenges;

    public static void main(final String[] args) {
        final AdventOfCode adventOfCode = new AdventOfCode();
        adventOfCode.load2022();
        adventOfCode.run();
    }

    private void run() {
        for (final AdventOfCodeChallenge codeChallenge : this.challenges) {
            codeChallenge.run();
        }
    }

    private void load2022() {
        this.challenges.add(new Year2022Day1());
        this.challenges.add(new Year2022Day2());
        this.challenges.add(new Year2022Day3());
        this.challenges.add(new Year2022Day4());
        this.challenges.add(new Year2022Day5());
        this.challenges.add(new Year2022Day6());
        this.challenges.add(new Year2022Day7());
    }

    public AdventOfCode() {
        this.challenges = new ArrayList<>();
    }
}
