package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;

public class Year2022Day3 extends AdventOfCodeChallenge {

    @Override
    public boolean run() {
        return this.runChallenge(2022, 3);
    }

    @Override
    public String part1(final String[] input) {

        int totalPriority = 0;
        for (final String line : input) {
            totalPriority += this.analyseRucksack(line);
        }
        return String.valueOf(totalPriority);
    }

    @Override
    public String part2(final String[] input) {

        int totalPriority = 0;
        final List<String> groupOf3 = new ArrayList<>();
        for (final String line : input) {
            groupOf3.add(line);
            if (groupOf3.size() == 3) {
                totalPriority += this.findAndPrioritizeBadge(groupOf3);
                groupOf3.clear();
            }
        }
        return String.valueOf(totalPriority);
    }

    private int findAndPrioritizeBadge(final List<String> groupOf3) {
        final StringBuilder commonItems12 = new StringBuilder();
        final String group1 = groupOf3.get(0);
        final String group2 = groupOf3.get(1);
        final String group3 = groupOf3.get(2);
        for (int i = 0; i < group1.length(); i++) {
            final String item = group1.substring(i, i + 1);
            if (group2.contains(item)) {
                commonItems12.append(item);
            }
        }
        String commonItem123 = null;
        for (int i = 0; i < commonItems12.length(); i++) {
            final String item = commonItems12.substring(i, i + 1);
            if (group3.contains(item)) {
                commonItem123 = item;
                break;
            }
        }
        if (commonItem123 == null) {
            throw new RuntimeException("Not found.");
        }
        return this.priority(commonItem123);
    }

    private int priority(final String commonItem) {
        if (commonItem.toLowerCase().equals(commonItem)) {
            return (int) commonItem.charAt(0) - 96;
        }
        return (int) commonItem.charAt(0) - 64 + 26;
    }

    private int analyseRucksack(final String line) {
        final String compartment1 = line.substring(0, line.length() / 2);
        final String compartment2 = line.substring(line.length() / 2);
        String commonItem = null;
        for (int i = 0; i < compartment1.length(); i++) {
            final String item = compartment1.substring(i, i + 1);
            if (compartment2.contains(item)) {
                commonItem = item;
                break;
            }
        }
        if (commonItem == null) {
            throw new RuntimeException("Not found.");
        }
        return this.priority(commonItem);
    }
}
