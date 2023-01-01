package com.simongarton.adventofcode.year2020;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Year2020Day6 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;

    @Override
    public String title() {
        return "Day 6: Custom Customs";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2020, 6);
    }

    @Override
    public String part1(final String[] input) {
        int total = 0;
        final Set<String> answers = new HashSet<>();
        for (final String line : input) {
            if (line.length() == 0) {
                total += answers.size();
                answers.clear();
                continue;
            }
            for (int i = 0; i < line.length(); i++) {
                answers.add(line.substring(i, i + 1));
            }
        }
        total += answers.size();
        answers.clear();
        return String.valueOf(total);
    }

    @Override
    public String part2(final String[] input) {
        int total = 0;
        int people = 0;
        final Map<String, Integer> answers = new HashMap<>();
        for (final String line : input) {
            if (line.length() == 0) {
                int group = 0;
                for (final Map.Entry<String, Integer> entry : answers.entrySet()) {
                    if (entry.getValue().equals(people)) {
                        group += 1;
                    }
                }
                total += group;
                answers.clear();
                people = 0;
                continue;
            }
            for (int i = 0; i < line.length(); i++) {
                final String key = line.substring(i, i + 1);
                if (!answers.containsKey(key)) {
                    answers.put(key, 1);
                } else {
                    answers.put(key, answers.get(key) + 1);
                }
            }
            people = people + 1;
        }
        int group = 0;
        for (final Map.Entry<String, Integer> entry : answers.entrySet()) {
            if (entry.getValue().equals(people)) {
                group += 1;
            }
        }
        total += group;
        answers.clear();
        return String.valueOf(total);
    }
}
