package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;

public class Year2024Day1 extends AdventOfCodeChallenge {

    // Copilot was off. IntelliJ full line completion was on for the first half of part 1 until
    // I remembered and turned it off (and it was annoying me.)

    private final List<Integer> one = new ArrayList<>();
    private final List<Integer> two = new ArrayList<>();

    @Override
    public String title() {
        return "Day 1: Historian Hysteria";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 1);
    }

    @Override
    public String part1(final String[] input) {

        this.readData(input);

        int total = 0;
        for (int i = 0; i < this.one.size(); i++) {
            final Integer first = this.one.get(i);
            final Integer second = this.two.get(i);
            final int diff = Math.abs(first - second);
            total += diff;
        }

        return String.valueOf(total);
    }

    private void readData(final String[] input) {

        this.one.clear();
        this.two.clear();

        for (final String line : input) {
            final String cleanedLine = this.cleanLine(line);
            final String[] tokens = cleanedLine.split(" ");
            this.one.add(Integer.parseInt(tokens[0]));
            this.two.add(Integer.parseInt(tokens[1]));
        }

        this.one.sort(Integer::compareTo);
        this.two.sort(Integer::compareTo);
    }

    // this may be overkill if they're all double spaces.
    private String cleanLine(final String line) {

        String newLine = line;
        while (newLine.contains("  ")) {
            newLine = newLine.replace("  ", " ");
        }
        return newLine;
    }

    @Override
    public String part2(final String[] input) {

        this.readData(input);

        int similarity = 0;
        for (final Integer first : this.one) {
            final int countSecond = (int) this.two.stream().filter(j -> j.equals(first)).count();
            similarity += (first * countSecond);
        }
        return String.valueOf(similarity);
    }
}
