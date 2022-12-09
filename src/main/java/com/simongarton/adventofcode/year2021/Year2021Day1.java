package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2021Day1 extends AdventOfCodeChallenge {

    @Override
    public boolean run() {
        return this.runChallenge(2021, 1);
    }

    @Override
    public String part1(final String[] input) {
        final List<Long> values = Arrays.stream(input).mapToLong(Long::parseLong).boxed().collect(Collectors.toList());
        return String.valueOf(this.countIncreases(values));
    }

    @Override
    public String part2(final String[] input) {
        final List<Long> values = Arrays.stream(input).mapToLong(Long::parseLong).boxed().collect(Collectors.toList());
        final List<Long> sumList = new ArrayList<>();
        for (int i = 2; i < values.size(); i++) {
            sumList.add(values.get(i - 2) + values.get(i - 1) + values.get(i));
        }
        return String.valueOf(this.countIncreases(sumList));
    }

    private int countIncreases(final List<Long> values) {
        long last = values.get(0);
        int index = 1;
        int increases = 0;
        while (index < values.size()) {
            final long current = values.get(index++);
            if (current > last) {
                increases++;
            }
            last = current;
        }
        return increases;
    }
}
