package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.HashMap;
import java.util.Map;

public class Year2022Day6 extends AdventOfCodeChallenge {

    private static final int PACKET = 4;
    private static final int MESSAGE = 14;

    @Override
    public String title() {
        return "Day 6: Tuning Trouble";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2022, 6);
    }

    @Override
    public String part1(final String[] input) {
        return String.valueOf(this.findMarker(PACKET, input[0]));
    }

    @Override
    public String part2(final String[] input) {
        return String.valueOf(this.findMarker(MESSAGE, input[0]));
    }

    private Map<String, Integer> frequencyAnalysis(final String input) {

        final Map<String, Integer> frequency = new HashMap<>();
        for (int i = 0; i < input.length(); i++) {
            final String substring = input.substring(i, i + 1);
            if (frequency.containsKey(substring)) {
                frequency.put(substring, frequency.get(substring) + 1);
            } else {
                frequency.put(substring, 1);
            }
        }
        return frequency;
    }

    private int findMarker(final int length, final String line) {
        int marker = 0;
        for (int i = 0; i < line.length() - length; i++) {
            final String substring = line.substring(i, i + length);
            final Map<String, Integer> analysis = this.frequencyAnalysis(substring);
            if (analysis.size() == length) {
                marker = i + length;
                break;
            }
        }
        return marker;
    }
}
