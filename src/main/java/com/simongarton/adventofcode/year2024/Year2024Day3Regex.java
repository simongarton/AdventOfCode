package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// redone after my first attempt, once I had learned how to do regex.

public class Year2024Day3Regex extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 3: Mull It Over (with regex)";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 3);
    }

    @Override
    public String part1(final String[] input) {

        final String completeLine = String.join("", input);

        final long total = this.sumAllCalculations(completeLine);

        return String.valueOf(total);
    }

    private Long calculate(final String line) {

        final String[] numbers = line.split(",");

        return Long.parseLong(numbers[0]) * Long.parseLong(numbers[1]);
    }

    private Long sumAllCalculations(final String line) {

        final List<Reference> references = this.findReferences(line);

        return references.stream().map(r -> r.calculation).mapToLong(this::calculate).sum();
    }

    private List<Reference> findReferences(final String line) {

        final String regex = "mul\\((\\d+,\\d+)\\)";
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        final List<Reference> matches = new ArrayList<>();
        int index = 0;
        while (matcher.find()) {
            matches.add(new Reference(matcher.group(1), index + matcher.start()));
            index = index + matcher.end();
            final String restOfLine = line.substring(index);
            matcher = pattern.matcher(restOfLine);
        }

        return matches;
    }

    private List<Long> findDos(final String line) {

        final String regex = "do\\(\\)";

        return this.findIndices(line, regex);
    }

    private List<Long> findDonts(final String line) {

        final String regex = "don't\\(\\)";

        return this.findIndices(line, regex);
    }

    private List<Long> findIndices(final String line, final String regex) {

        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        final List<Long> indices = new ArrayList<>();
        int index = 0;
        while (matcher.find()) {
            indices.add((long) index + matcher.start());
            index = index + matcher.end();
            final String restOfLine = line.substring(index);
            matcher = pattern.matcher(restOfLine);
        }

        return indices;
    }

    @Override
    public String part2(final String[] input) {

        final String completeLine = String.join("", input);

        final long total = this.sumAllValidCalculations(completeLine);

        return String.valueOf(total);
    }

    private Long sumAllValidCalculations(final String line) {

        final List<Reference> references = this.findReferences(line);
        final List<Long> dos = this.findDos(line);
        dos.add(0, -1L); // we start true
        final List<Long> donts = this.findDonts(line);
        final List<Reference> validReferences = this.removeInvalidCalculations(references, dos, donts);

        return validReferences.stream().map(r -> r.calculation).mapToLong(this::calculate).sum();
    }

    private List<Reference> removeInvalidCalculations(final List<Reference> references,
                                                      final List<Long> dos,
                                                      final List<Long> donts) {

        final List<Reference> validReferences = new ArrayList<>();

        for (final Reference reference : references) {
            final long lastDo = this.findLast(reference.index, dos);
            final long lastDont = this.findLast(reference.index, donts);
            if (lastDo > lastDont) {
                validReferences.add(reference);
            }
        }

        return validReferences;
    }

    private long findLast(final long index, final List<Long> indices) {
        final List<Long> matches = indices.stream().filter(i -> i < index).collect(Collectors.toList());
        if (matches.isEmpty()) {
            return -Long.MAX_VALUE;
        }
        return matches.get(matches.size() - 1);
    }

    static class Reference {

        private final String calculation;
        private final long index;

        public Reference(final String calculation, final long index) {
            this.calculation = calculation;
            this.index = index;
        }
    }
}
