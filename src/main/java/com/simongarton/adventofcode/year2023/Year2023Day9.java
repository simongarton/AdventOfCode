package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2023Day9 extends AdventOfCodeChallenge {

    List<List<Integer>> listOfLists = new ArrayList<>();

    @Override
    public String title() {
        return "Day 9: XXX";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 9);
    }

    @Override
    public String part1(final String[] input) {

        long total = 0;
        for (final String line : input) {
            total += this.solveNextValue(line);
        }

        return String.valueOf(total);
    }

    private long solveNextValue(final String line) {
        this.listOfLists.clear();
        this.listOfLists.add(this.convertLineToList(line));
        while (this.nonZeroList(this.listOfLists.get(this.listOfLists.size() - 1))) {
            this.listOfLists.add(this.listOfDifferences(this.listOfLists.get(this.listOfLists.size() - 1)));
        }
        for (int i = this.listOfLists.size() - 2; i > 0; i--) {
            final List<Integer> bottomList = this.listOfLists.get(i);
            final List<Integer> topList = this.listOfLists.get(i - 1);
            topList.add(bottomList.get(bottomList.size() - 1) + topList.get(topList.size() - 1));
            // do something with the difference in this line which will inform the difference in the previous one.
        }
        return this.listOfLists.get(0).get(this.listOfLists.get(0).size() - 1);
    }

    private List<Integer> listOfDifferences(final List<Integer> integers) {
        final List<Integer> nextList = new ArrayList<>();
        for (int i = 0; i < integers.size() - 1; i++) {
            nextList.add(integers.get(i + 1) - integers.get(i));
        }
        return nextList;
    }

    private boolean nonZeroList(final List<Integer> integers) {
        return integers.stream().anyMatch(i -> i != 0);
    }

    private List<Integer> convertLineToList(final String line) {
        return Arrays.stream(line.split(" ")).map(s -> Integer.valueOf(s)).collect(Collectors.toList());
    }

    @Override
    public String part2(final String[] input) {

        long total = 0;
        for (final String line : input) {
            total += this.solvePreviousValue(line);
        }

        return String.valueOf(total);
    }

    private long solvePreviousValue(final String line) {
        this.listOfLists.clear();
        this.listOfLists.add(this.convertLineToList(line));
        while (this.nonZeroList(this.listOfLists.get(this.listOfLists.size() - 1))) {
            this.listOfLists.add(this.listOfDifferences(this.listOfLists.get(this.listOfLists.size() - 1)));
        }
        // now work backwards
        for (int i = this.listOfLists.size() - 1; i > 0; i--) {
            final List<Integer> topList = this.listOfLists.get(i - 1);
            final List<Integer> bottomList = this.listOfLists.get(i);
            topList.add(0, topList.get(0) - bottomList.get(0));
            // do something with the difference in this line which will inform the difference in the previous one.
        }
        return this.listOfLists.get(0).get(0);
    }

}
