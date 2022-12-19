package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Year2022Day1 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 1: Calorie Counting";
    }

    @Override
    public boolean run() {
        return this.runChallenge(2022, 1);
    }

    @Override
    public String part1(final String[] input) {

        return String.valueOf(this.countCalories(input).get(0));
    }

    @Override
    public String part2(final String[] input) {

        final List<Integer> calories = this.countCalories(input);
        return String.valueOf(calories.get(0) + calories.get(1) + calories.get(2));
    }

    private List<Integer> countCalories(final String[] input) {

        final List<Integer> calories = new ArrayList<>();

        int runningTotal = 0;

        for (final String line : input) {
            if (line.length() == 0) {
                calories.add(runningTotal);
                runningTotal = 0;
            } else {
                runningTotal += Integer.parseInt(line);
            }
        }

        calories.sort(Comparator.comparing(Integer::intValue).reversed());

        return calories;
    }

    /*

    An alternative option. Use

        return String.valueOf(Common.getTopCalories(input, 3));

    where the second parameter is the top N you want to sum up.
    Although more complicated, it manages input of unlimited size -
    the trivial solution above will build up a list of all results
    and then sorts it, and if we have 2 billion elves, we're in trouble;
    this just keeps track of the values and discards any elf that wouldn't
    make the cut.

     */

    private Integer getTopCalories(
            final String[] input,
            final int max
    ) {

        final int[] topCalories = new int[max];

        int runningTotal = 0;

        for (final String line : input) {
            if (line.length() == 0) {
                this.updateTopCalories(topCalories, runningTotal);
                runningTotal = 0;
            } else {
                runningTotal += Integer.parseInt(line);
            }
        }

        return Arrays.stream(topCalories).sum();
    }

    private void updateTopCalories(
            final int[] topCalories,
            final int newTotal
    ) {
        // the array will always be sorted, highest first. work along it, swapping the incoming newTotal
        // it is more than the array value, and then use that for the next, until you get to the end or
        // don't have to swap.

        int currentValue = newTotal;

        for (int i = 0; i < topCalories.length; i++) {
            if (currentValue == 0) {
                break;
            }
            final int existingValue = topCalories[i];
            if (existingValue < currentValue) {
                topCalories[i] = currentValue;
                currentValue = existingValue;
            }
        }
    }
}
