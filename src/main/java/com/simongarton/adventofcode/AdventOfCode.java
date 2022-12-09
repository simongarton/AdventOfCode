package com.simongarton.adventofcode;

import com.simongarton.adventofcode.year2021.*;
import com.simongarton.adventofcode.year2022.*;

import java.util.ArrayList;
import java.util.List;

public class AdventOfCode {

    private final List<AdventOfCodeChallenge> challenges;

    public static void main(final String[] args) {
        final AdventOfCode adventOfCode = new AdventOfCode();
        adventOfCode.load2021();
        adventOfCode.load2022();
        adventOfCode.run();
    }

    private void run() {
        for (final AdventOfCodeChallenge codeChallenge : this.challenges) {
            codeChallenge.run();
        }
    }

    private void load2021() {
        this.challenges.add(new Year2021Day1());
        this.challenges.add(new Year2021Day2());
        this.challenges.add(new Year2021Day3());
        this.challenges.add(new Year2021Day4());
        this.challenges.add(new Year2021Day5());
//        this.challenges.add(new Year2021Day6());
//        this.challenges.add(new Year2021Day7());
//        this.challenges.add(new Year2021Day8());
//        this.challenges.add(new Year2021Day9());
//        this.challenges.add(new Year2021Day10());
//        this.challenges.add(new Year2021Day11());
//        this.challenges.add(new Year2021Day12());
//        this.challenges.add(new Year2021Day13());
//        this.challenges.add(new Year2021Day14());
//        this.challenges.add(new Year2021Day15());
//        this.challenges.add(new Year2021Day16());
//        this.challenges.add(new Year2021Day17());
//        this.challenges.add(new Year2021Day18());
//        this.challenges.add(new Year2021Day19());
//        this.challenges.add(new Year2021Day20());
//        this.challenges.add(new Year2021Day21());
//        this.challenges.add(new Year2021Day22());
//        this.challenges.add(new Year2021Day23());
//        this.challenges.add(new Year2021Day24());
//        this.challenges.add(new Year2021Day25());
    }

    private void load2022() {
        this.challenges.add(new Year2022Day1());
        this.challenges.add(new Year2022Day2());
        this.challenges.add(new Year2022Day3());
        this.challenges.add(new Year2022Day4());
        this.challenges.add(new Year2022Day5());
        this.challenges.add(new Year2022Day6());
        this.challenges.add(new Year2022Day7());
        this.challenges.add(new Year2022Day8());
        this.challenges.add(new Year2022Day9());
    }

    public AdventOfCode() {
        this.challenges = new ArrayList<>();
    }
}
