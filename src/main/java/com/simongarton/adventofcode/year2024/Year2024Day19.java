package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Year2024Day19 extends AdventOfCodeChallenge {

    private List<String> towels = new ArrayList<>();
    private List<String> carpets = new ArrayList<>();

    /*

    towels: 447
    carpets: 400 max length 60

    Had a couple of ideas.

    Recursion and memoisation.
    Learn how a Trie works.

    222 is too low

     */

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

        this.towels = Arrays.asList(input[0].split(", "));

        System.out.println("towels: " + this.towels.size());

        this.carpets = new ArrayList<>();
        int maxLength = 0;

        for (int design = 2; design < input.length; design++) {
            final String carpet = input[design];
            this.carpets.add(carpet);
            if (carpet.length() > maxLength) {
                maxLength = carpet.length();
            }
        }

        System.out.println("carpets: " + this.carpets.size() + " max length " + maxLength);

        final int validCarpets = this.countValidCarpets();

        return String.valueOf(validCarpets);
    }

    private int countValidCarpets() {

        int valid = 0;
        for (final String carpet : this.carpets) {
            final boolean wasValid = this.validCarpet(carpet);
            if (wasValid) {
                valid++;
            }
            System.out.println(carpet + " : " + wasValid);
        }
        return valid;
    }

    private boolean validCarpet(final String carpet) {

        final List<Node> available = new ArrayList<>();
        final List<Node> visited = new ArrayList<>();

        available.add(new Node(carpet));

        while (!available.isEmpty()) {
            final int index = this.bestToUse(available);
            final Node current = available.remove(index);
//            System.out.println(current.patternLeft);

            visited.add(current);
            if (current.patternLeft.isEmpty()) {
                return true;
            }

            boolean usedATowel = false;
            for (final String towel : this.towels) {
                if (current.couldUseTowel(towel)) {
                    final Node next = current.useTowel(towel);
                    available.add(next);
                    usedATowel = true;
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
        return null;
    }

    static class Node {

        String originalPattern;
        List<String> towelsUsed;
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
