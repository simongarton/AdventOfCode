package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;

public class Year2024Day5 extends AdventOfCodeChallenge {

    Map<String, Page> pages = new HashMap<>();

    @Override
    public String title() {
        return "Day 5: Print Queue";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 5);
    }

    @Override
    public String part1(final String[] input) {

        int index = this.setupPages(input);

        index++;
        int total = 0;
        System.out.printf("found %s pages%n%n", this.pages.size());
        while (index < input.length) {
            final String update = input[index];
            final boolean valid = this.isUpdateValid(update);
            System.out.printf("update %s valid ? %s%n", update, valid);
            index++;
            if (valid) {
                total += this.score(update);
            }
        }

        return String.valueOf(total);
    }

    private int setupPages(final String[] input) {

        this.pages = new HashMap<>();

        int index = 0;
        while (true) {
            final String line = input[index];
            if (line.isEmpty()) {
                break;
            }
            this.processRule(line);
            index++;
        }

        return index;
    }

    private int score(final String update) {
        final String[] numbers = update.split(",");
        final int mid = numbers.length / 2;
        return Integer.parseInt(numbers[mid]);
    }

    private boolean isUpdateValid(final String update) {

        boolean validUpdate = true;
        final String[] numbers = update.split(",");
        for (int i = 0; i < numbers.length; i++) {
            final Page thisPage = this.pages.get(numbers[i]);
            for (int j = i + 1; j < numbers.length; j++) {
                if (!this.pages.containsKey(numbers[j])) {
                    continue;
                }
                final Page otherPage = this.pages.get(numbers[j]);
//                System.out.printf("%s %s %s %s%n", update, i, j, otherPage);
                if (otherPage.checkShouldBeBefore(thisPage.number)) {
                    System.out.printf("  failed on %s:%s because %s must go before %s%n",
                            i, j, otherPage.number, thisPage.number);
                    validUpdate = false;
                }
            }
        }
        return validUpdate;
    }

    private void processRule(final String line) {

        final String[] parts = line.split("\\|");
        final String first = parts[0];
        final String second = parts[1];

        final Page page = this.getOrCreate(first);
        this.getOrCreate(second);
        page.mustGoBefore(second);
    }

    private Page getOrCreate(final String first) {

        if (this.pages.containsKey(first)) {
            return this.pages.get(first);
        }
        final Page page = new Page(first);
        this.pages.put(first, page);
        return page;
    }

    @Override
    public String part2(final String[] input) {

        int index = this.setupPages(input);

        final List<String> changedUpdates = new ArrayList<>();

        index++;
        System.out.printf("found %s pages%n%n", this.pages.size());
        while (index < input.length) {
            final String update = input[index];

            if (this.isUpdateValid(update)) {
                System.out.printf("update %s is already valid%n", update);
                index++;
                continue;
            }

            final String[] numbers = update.split(",");
            final List<List<String>> permutations = new ArrayList<>();
            this.generatePermutations(numbers, 0, permutations);

            boolean found = false;
            for (final List<String> permutationList : permutations) {
                final String permutation = String.join(",", permutationList);
                final boolean valid = this.isUpdateValid(permutation);
                if (valid) {
                    System.out.printf("update %s should be %s%n", update, permutation);
                    found = true;
                    changedUpdates.add(permutation);
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("broken");
            }
            index++;
        }

        int total = 0;
        for (final String update : changedUpdates) {
            total += this.score(update);
        }

        return String.valueOf(total);
    }

    void generatePermutations(final String[] array, final int start, final List<List<String>> result) {
        if (start == array.length) {
            final List<String> permutation = new ArrayList<>(Arrays.asList(array));
            result.add(permutation);
        } else {
            for (int i = start; i < array.length; i++) {
                this.swap(array, start, i);
                this.generatePermutations(array, start + 1, result);
                this.swap(array, start, i);
            }
        }
    }

    void swap(final String[] array, final int i, final int j) {
        final String temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }


    static class Page {

        String number;
        List<String> before;
        List<String> after;

        Page(final String number) {
            this.number = number;
            this.before = new ArrayList<>();
            this.after = new ArrayList<>();
        }

        void mustGoBefore(final String other) {
            this.before.add(other);
        }

        void mustGoAfter(final String other) {
            this.after.add(other);
        }

        boolean checkShouldBeBefore(final String other) {
            return this.before.contains(other);
        }
    }
}

