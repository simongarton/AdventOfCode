package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;

public class Year2024Day3 extends AdventOfCodeChallenge {

    private boolean currentlyEnabled = true;

    @Override
    public String title() {
        return "Day 3: Mull It Over";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 3);
    }

    @Override
    public String part1(final String[] input) {

        long total = 0;
        for (final String mainLine : input) {
            final List<String> cleanLines = this.findCalculations(mainLine, false);
            total += cleanLines.stream().map(this::calculate).mapToLong(Long::valueOf).sum();
        }

        return String.valueOf(total);
    }

    private Integer calculate(final String line) {
        final String[] parts = line.split("\\(");
        final String[] parts2 = parts[1].split("\\)");
        final String[] numbers = parts2[0].split(",");
        return Integer.parseInt(numbers[0]) * Integer.parseInt(numbers[1]);
    }

    private List<String> findCalculations(final String s, final boolean specialRules) {

        // I should be able to do this with a regex. If I could do it with a regex.

        final List<String> valid = List.of("m", "u", "l", "(", ")", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ",");

        final List<String> calculations = new ArrayList<>();
        StringBuilder currentCalculation = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {

            if (specialRules) {
                if (i < (s.length() - 4) && s.substring(i, i + 4).equalsIgnoreCase("do()")) {
                    this.currentlyEnabled = true;
                    i += 3;
                    continue;
                }
                if (i < (s.length() - 7) && s.substring(i, i + 7).equalsIgnoreCase("don't()")) {
                    this.currentlyEnabled = false;
                    i += 6;
                    continue;
                }
            }

            final String letter = "" + s.charAt(i);
            final String lastLetter = currentCalculation.length() > 0 ?
                    "" + currentCalculation.charAt(currentCalculation.length() - 1) :
                    "";

            if (!valid.contains(letter)) {
                currentCalculation = new StringBuilder();
                continue;
            }
            if (letter.equalsIgnoreCase("m")) {
                if (currentCalculation.toString().equalsIgnoreCase("")) {
                    currentCalculation.append(letter);
                }
                continue;
            }
            if (currentCalculation.length() == 0) {
                continue;
            }
            if (letter.equalsIgnoreCase("u")) {
                if (currentCalculation.toString().equalsIgnoreCase("m")) {
                    currentCalculation.append(letter);
                }
                continue;
            }
            if (letter.equalsIgnoreCase("l")) {
                if (currentCalculation.toString().equalsIgnoreCase("mu")) {
                    currentCalculation.append(letter);
                }
                continue;
            }
            if (letter.equalsIgnoreCase("(")) {
                if (currentCalculation.toString().equalsIgnoreCase("mul")) {
                    currentCalculation.append(letter);
                }
                continue;
            }
            if (this.isNumeric(letter)) {
                if (currentCalculation.toString().equalsIgnoreCase("mul(")) {
                    currentCalculation.append(letter);
                    continue;
                }
                if (this.isNumeric(lastLetter)) {
                    currentCalculation.append(letter);
                    continue;
                }
                if (lastLetter.equalsIgnoreCase(",")) {
                    currentCalculation.append(letter);
                }
                continue;
            }
            if (letter.equalsIgnoreCase(",")) {
                if (this.isNumeric(lastLetter)) {
                    currentCalculation.append(letter);
                }
                continue;
            }
            if (letter.equalsIgnoreCase(")")) {
                if (this.isNumeric(lastLetter)) {
                    currentCalculation.append(letter);
                    if (this.currentlyEnabled) {
                        calculations.add(currentCalculation.toString());
                    }

                }
                currentCalculation = new StringBuilder();
            }
            // and if I'm overrunning
            if (currentCalculation.length() > 0) {
                if (!this.isNumeric(letter)) {
                    currentCalculation = new StringBuilder();
                }
            }
        }

        return calculations;
    }

    @Override
    public String part2(final String[] input) {

        // not 86865767
        // not 88880807
        long total = 0;
        for (final String mainLine : input) {
            final List<String> cleanLines = this.findCalculations(mainLine, true);
            total += cleanLines.stream().map(this::calculate).mapToLong(Long::valueOf).sum();
        }

        return String.valueOf(total);
    }
}
