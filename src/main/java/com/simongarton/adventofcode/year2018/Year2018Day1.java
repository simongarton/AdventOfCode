package com.simongarton.adventofcode.year2018;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Year2018Day1 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 1: Chronal Calibration";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2018, 1);
    }

    @Override
    public String part1(final String[] input) {
        return String.valueOf(Arrays.stream(input).map(Integer::parseInt).reduce(0, Integer::sum));
    }

    @Override
    public String part2(final String[] input) {

        int frequency = 0;
        final Set<Integer> visited = new HashSet<>();
        int index = 0;
        while (true) {
            frequency = frequency + Integer.parseInt(input[index]);
            if (++index == input.length) {
                index = 0;
            }
            if (visited.contains(frequency)) {
                break;
            }
            visited.add(frequency);
        }
        return String.valueOf(frequency);
    }
}
