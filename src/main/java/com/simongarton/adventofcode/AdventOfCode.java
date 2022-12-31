package com.simongarton.adventofcode;

import com.simongarton.adventofcode.year2019.Year2019Day1;
import com.simongarton.adventofcode.year2019.Year2019Day2;
import com.simongarton.adventofcode.year2019.Year2019Day3;
import com.simongarton.adventofcode.year2019.Year2019Day4;
import com.simongarton.adventofcode.year2020.*;
import com.simongarton.adventofcode.year2021.*;
import com.simongarton.adventofcode.year2022.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdventOfCode {

    private final List<AdventOfCodeChallenge> challenges;
    private final Map<Integer, Map<Integer, AdventOfCodeChallenge.Outcome>> complete = new HashMap<>();

    public static void main(final String[] args) {
        final AdventOfCode adventOfCode = new AdventOfCode();
        adventOfCode.load2019();
        adventOfCode.load2020();
        adventOfCode.load2021();
//        adventOfCode.load2022();
        adventOfCode.run();
    }

    private void run() {
        this.complete.clear();
        for (final AdventOfCodeChallenge codeChallenge : this.challenges) {
            final AdventOfCodeChallenge.Outcome outcome = codeChallenge.run();
            final int year = codeChallenge.getYear();
            final int day = codeChallenge.getDay();
            if (!this.complete.containsKey(year)) {
                this.complete.put(year, new HashMap<>());
            }
            this.complete.get(year).put(day, outcome);
        }

        this.displayResults();
    }

    private void displayResults() {

        System.out.println("");
        System.out.println("                1111111111222222");
        System.out.println("       1234567890123456789012345");
        for (int year = 2019; year <= 2022; year++) {
            String line = year + " : ";
            for (int day = 1; day <= 25; day++) {
                if (this.complete.containsKey(year)) {
                    if (this.complete.get(year).containsKey(day)) {
                        if (this.complete.get(year).get(day).both()) {
                            line += "✓";
                        } else {
                            if (this.complete.get(year).get(day).part1) {
                                line += ".";
                            } else {
                                line += " ";
                            }
                        }
                    } else {
                        line += " ";
                    }
                } else {
                    line += " ";
                }
            }
            System.out.println(line);
        }
        System.out.println("       1234567890123456789012345");
        System.out.println("                1111111111222222");
    }

    private void load2019() {
        this.challenges.add(new Year2019Day1());
        this.challenges.add(new Year2019Day2());
        this.challenges.add(new Year2019Day3());
        this.challenges.add(new Year2019Day4());
    }

    private void load2020() {
        this.challenges.add(new Year2020Day1());
        this.challenges.add(new Year2020Day2());
        this.challenges.add(new Year2020Day3());
        this.challenges.add(new Year2020Day4());
        this.challenges.add(new Year2020Day5());
    }

    private void load2021() {
        this.challenges.add(new Year2021Day1());
        this.challenges.add(new Year2021Day2());
        this.challenges.add(new Year2021Day3());
        this.challenges.add(new Year2021Day4());
        this.challenges.add(new Year2021Day5());
        this.challenges.add(new Year2021Day6());
        this.challenges.add(new Year2021Day7());
        this.challenges.add(new Year2021Day8());
        this.challenges.add(new Year2021Day9());
        this.challenges.add(new Year2021Day10());
        this.challenges.add(new Year2021Day11());
        this.challenges.add(new Year2021Day12());
        this.challenges.add(new Year2021Day13());
        this.challenges.add(new Year2021Day14());
        this.challenges.add(new Year2021Day15());
        this.challenges.add(new Year2021Day16());
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
        this.challenges.add(new Year2022Day10());
        this.challenges.add(new Year2022Day11());
        this.challenges.add(new Year2022Day12());
        this.challenges.add(new Year2022Day13());
        this.challenges.add(new Year2022Day14());
        this.challenges.add(new Year2022Day15());
        this.challenges.add(new Year2022Day16());
        this.challenges.add(new Year2022Day17());
        this.challenges.add(new Year2022Day18());
        this.challenges.add(new Year2022Day19());
        this.challenges.add(new Year2022Day20());
        this.challenges.add(new Year2022Day21());
        this.challenges.add(new Year2022Day22());
        this.challenges.add(new Year2022Day23());
        this.challenges.add(new Year2022Day24());
        this.challenges.add(new Year2022Day25());
    }

    public AdventOfCode() {
        this.challenges = new ArrayList<>();
    }
}
