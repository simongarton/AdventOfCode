package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;

public class Year2024Day19 extends AdventOfCodeChallenge {

    private List<String> towels = new ArrayList<>();
    private List<String> carpets = new ArrayList<>();

    private final Map<String, Long> towelCache = new HashMap<>();

    @Override
    public String title() {
        return "Day 19: Linen Layout";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 19);
    }

    @Override
    public String part1(final String[] input) {

        this.setup(input);

        final long validCarpets = this.countValidCarpets();

        return String.valueOf(validCarpets);
    }

    private void setup(final String[] input) {

        this.towels = Arrays.asList(input[0].split(", "));

        this.carpets = new ArrayList<>();
        int maxLength = 0;

        for (int design = 2; design < input.length; design++) {
            final String carpet = input[design];
            this.carpets.add(carpet);
            if (carpet.length() > maxLength) {
                maxLength = carpet.length();
            }
        }
    }

    private long countValidCarpets() {

        int valid = 0;
        for (final String carpet : this.carpets) {
            final boolean wasValid = this.validCarpet(carpet);
            if (wasValid) {
                valid++;
            }
        }
        return valid;
    }

    private boolean validCarpet(final String carpet) {

        final List<Node> available = new ArrayList<>();

        available.add(new Node(carpet));

        while (!available.isEmpty()) {
            final int index = this.bestToUse(available);
            final Node current = available.remove(index);

            if (current.patternLeft.isEmpty()) {
                return true;
            }

            for (final String towel : this.towels) {
                if (current.couldUseTowel(towel)) {
                    final Node next = current.useTowel(towel);
                    available.add(next);
                }
            }
        }
        return false;
    }

    private int bestToUse(final List<Node> available) {

        int minLength = Integer.MAX_VALUE;
        int minLengthId = 0;
        for (int i = 0; i < available.size(); i++) {
            final Node node = available.get(i);
            if (node.lengthLeft() < minLength) {
                minLength = node.lengthLeft();
                minLengthId = i;
            }
        }
        return minLengthId;
    }

    @Override
    public String part2(final String[] input) {

        this.setup(input);

        final long validCarpetCombos = this.countValidCarpetCombos();

        return String.valueOf(validCarpetCombos);
    }

    private long countValidCarpetCombos() {

        long valid = 0;
        for (final String carpet : this.carpets) {
            final boolean wasValid = this.validCarpet(carpet);
            if (wasValid) {
                final long combos = this.recursiveValidCarpetCombos(carpet);
                valid += combos;
            }
        }
        return valid;
    }

    private long recursiveValidCarpetCombos(final String carpet) {

        if (carpet.isEmpty()) {
            return 1;
        }

        if (this.towelCache.containsKey(carpet)) {
            return this.towelCache.get(carpet);
        }

        long combos = 0;
        for (final String towel : this.towels) {
            if (carpet.length() >= towel.length()) {
                if (carpet.substring(0, towel.length()).equalsIgnoreCase(towel)) {
                    combos = combos + this.recursiveValidCarpetCombos(carpet.substring(towel.length()));
                }
            }
        }

        this.towelCache.put(carpet, combos);
        return combos;
    }

    static class Node {

        final String originalPattern;
        final List<String> towelsUsed;
        String patternLeft;

        public Node(final String originalPattern) {
            this.originalPattern = originalPattern;
            this.patternLeft = originalPattern;
            this.towelsUsed = new ArrayList<>();
        }

        public boolean couldUseTowel(final String towel) {

            if (towel.length() > this.patternLeft.length()) {
                return false;
            }

            return this.patternLeft.substring(0, towel.length()).equalsIgnoreCase(towel);
        }

        public int lengthLeft() {
            return this.patternLeft.length();
        }

        public Node useTowel(final String towel) {

            final Node node = new Node(this.originalPattern);
            node.towelsUsed.addAll(this.towelsUsed);
            node.towelsUsed.add(towel);
            node.patternLeft = this.patternLeft.substring(towel.length());
            return node;
        }
    }
}
