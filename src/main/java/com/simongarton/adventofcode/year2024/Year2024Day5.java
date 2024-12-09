package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
        while (index < input.length) {
            final String update = input[index];
            final boolean valid = this.isUpdateValid(update);
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

        // this works just fine, but the resulting graph is HUGE - 5k x 30k pixels - and of little value.
        if (false) {
            this.buildGraph();
        }

        return index;
    }

    private void buildGraph() {

        final List<String> lines = new ArrayList<>();
        lines.add("digraph {");
        lines.add("  rankdir=\"LR\" ");
        for (final Map.Entry<String, Page> pageEntry : this.pages.entrySet()) {
            final String from = pageEntry.getKey();
            for (final String to : pageEntry.getValue().before) {
                lines.add(String.format("  %s -> %s", from, to));
            }
        }
        lines.add("}");

        try {
            final BufferedWriter br = new BufferedWriter(
                    new FileWriter(
                            String.format("src/graphs/%s.dot", this.getClass().getSimpleName())));
            for (final String str : lines) {
                br.write(str + System.lineSeparator());
            }
            br.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

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
//                    System.out.printf("  failed on %s:%s because %s must go before %s%n",
//                            i, j, otherPage.number, thisPage.number);
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
        while (index < input.length) {
            final String update = input[index];

            if (this.isUpdateValid(update)) {
                index++;
                continue;
            }

            final List<String> numbers = Arrays.asList(update.split(","));

            final List<String> workingLine = new ArrayList<>();
            for (final String number : numbers) {
                if (workingLine.isEmpty()) {
                    workingLine.add(number);
                    continue;
                }
                final int position = this.findValidPosition(number, workingLine);
                workingLine.add(position, number);
            }

            changedUpdates.add(String.join(",", workingLine));
            index++;
        }

        int total = 0;
        for (final String update : changedUpdates) {
            total += this.score(update);
        }

        return String.valueOf(total);
    }

    private int findValidPosition(final String number, final List<String> workingLine) {

        for (int position = 0; position <= workingLine.size(); position++) {
            final List<String> testLine = new ArrayList<>(workingLine);
            testLine.add(position, number);
            final String testUpdate = String.join(",", testLine);
            if (this.isUpdateValid(testUpdate)) {
                return position;
            }
        }
        throw new RuntimeException("Did not find a valid position.");
    }

    static class Page {

        String number;
        List<String> before;

        Page(final String number) {
            this.number = number;
            this.before = new ArrayList<>();
        }

        void mustGoBefore(final String other) {
            this.before.add(other);
        }

        boolean checkShouldBeBefore(final String other) {
            return this.before.contains(other);
        }

        @Override
        public String toString() {

            if (this.before.isEmpty()) {
                return this.number + " (can go anywhere)";
            }

            return this.number + " (must go before " + String.join(",", this.before) + ")";
        }
    }
}

