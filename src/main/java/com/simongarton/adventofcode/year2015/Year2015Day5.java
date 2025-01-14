package com.simongarton.adventofcode.year2015;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Year2015Day5 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;

    @Override
    public String title() {
        return "Day 0: Template code";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2015, 5);
    }

    @Override
    public String part1(final String[] input) {

        return String.valueOf(Arrays.stream(input).filter(this::isNice).count());
    }

    private boolean isNice(final String s) {

        if (this.regexCountCharactersInString(s, "[aeiouAEIOU]") < 3) {
            return false;
        }
        if (this.regexFindDuplicates(s).isEmpty()) {
            return false;
        }
        return this.regexExcludeGroupsOfCharacters(s, "(?!.*(ab|cd|pq|xy)).*$");
    }

    @Override
    public String part2(final String[] input) {

        if (DEBUG) {
            for (final String s : input) {
                System.out.println(s + " " + this.scoringPairs(this.findRepeatingPairs(s)) + " " + this.findSandwichLetters(s));
            }
        }

        return String.valueOf(Arrays.stream(input).filter(this::isNice2).count());
    }

    private Map<String, Integer> scoringPairs(final Map<String, Integer> repeatingPairs) {

        final Map<String, Integer> pairs = new HashMap<>();
        for (final Map.Entry<String, Integer> entry : repeatingPairs.entrySet()) {
            if (entry.getValue() > 1) {
                pairs.put(entry.getKey(), entry.getValue());
            }
        }
        return pairs;
    }

    private boolean isNice2(final String s) {

        // my regex-fu is not worthy
        if (this.countRepeatingPairs(s) < 1) {
            return false;
        }
        return this.countSandwichLetters(s) > 0;
    }

    public long countRepeatingPairs(final String s) {

        final Map<String, Integer> map = this.findRepeatingPairs(s);
        return map.values().stream().filter(n -> n > 1).count();
    }

    public Map<String, Integer> findRepeatingPairs(final String s) {

        // aaaa is the nightmare.
        String workingLine = s;

        final Map<String, Integer> map = new HashMap<>();
        String lastKeyAdded = null;

        while (workingLine.length() > 1) {
            final String key = workingLine.substring(0, 2);
            // as long as they are not the same as the last key, I can add them
            if (!key.equalsIgnoreCase(lastKeyAdded)) {
                map.put(key, map.getOrDefault(key, 0) + 1);
                // if I add it, remember so I don't do it again immediately.
                lastKeyAdded = key;
            } else {
                // I'm not going to do it again immediately.
                lastKeyAdded = null;
            }
            workingLine = workingLine.substring(1);
        }
        return map;
    }

    private long countSandwichLetters(final String s) {

        final Map<String, Integer> map = this.findSandwichLetters(s);
        return map.size();
    }

    private Map<String, Integer> findSandwichLetters(final String s) {

        final Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < s.length() - 2; i++) {
            final String key = s.substring(i, i + 3);
            if (!key.substring(0, 1).equalsIgnoreCase(key.substring(2, 3))) {
                continue;
            }
            map.put(key, map.getOrDefault(key, 0) + 1);
        }
        return map;
    }
}
