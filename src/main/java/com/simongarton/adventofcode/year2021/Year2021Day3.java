package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2021Day3 extends AdventOfCodeChallenge {

    @Override
    public Outcome run() {
        return this.runChallenge(2021, 3);
    }

    @Override
    public String part1(final String[] input) {
        final int messageLength = input[0].length();
        final int messageCount = input.length;
        final List<Long> counts = new ArrayList<>();
        for (int index = 0; index < messageLength; index++) {
            counts.add(this.countOnesAtIndex(index, Arrays.asList(input)));
        }
        // these are inversed - could I just create epsilon from gamma afterwards ?
        final StringBuilder gamma = new StringBuilder();
        final StringBuilder epsilon = new StringBuilder();
        final int half = messageCount / 2;
        for (int index = 0; index < messageLength; index++) {
            if (counts.get(index) > half) {
                gamma.append("1");
                epsilon.append("0");
            } else {
                gamma.append("0");
                epsilon.append("1");
            }
        }
        final Integer gammaValue = Integer.parseInt(gamma.toString(), 2);
        final Integer epsilonValue = Integer.parseInt(epsilon.toString(), 2);
        return String.valueOf(gammaValue * epsilonValue);
    }

    @Override
    public String part2(final String[] input) {
        List<String> currentLines = new ArrayList<>(Arrays.asList(input));
        while (currentLines.size() > 1) {
            currentLines = this.reduceList(currentLines, 0, '1', '0');
        }
        final long oxygen = Integer.parseInt(currentLines.get(0), 2);
        currentLines = new ArrayList<>(Arrays.asList(input));
        while (currentLines.size() > 1) {
            currentLines = this.reduceList(currentLines, 0, '0', '1');
        }
        final long co2 = Integer.parseInt(currentLines.get(0), 2);
        return String.valueOf(oxygen * co2);
    }

    private long countOnesAtIndex(final int index, final List<String> lines) {
        return lines.stream().filter(l -> l.charAt(index) == '1').count();
    }

    private List<String> reduceList(final List<String> currentLines, final int index, final char v1, final char v2) {
        if (currentLines.size() == 1) {
            return currentLines;
        }
        final long ones = this.countOnesAtIndex(index, currentLines);
        final char match = ones >= (currentLines.size() / 2.0) ? v1 : v2;
        final List<String> newLines = currentLines.stream().filter(l -> l.charAt(index) == match).collect(Collectors.toList());
        return this.reduceList(newLines, index + 1, v1, v2);
    }
}
