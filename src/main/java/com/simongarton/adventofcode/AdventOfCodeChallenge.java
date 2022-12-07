package com.simongarton.adventofcode;

import com.simongarton.adventofcode.exceptions.InvalidSetupException;
import com.simongarton.adventofcode.exceptions.WrongAnswerException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AdventOfCodeChallenge {

    public abstract boolean run();

    public abstract String part1(final String[] input);

    public abstract String part2(final String[] input);

    public boolean runChallenge(final int year, final int day) {
        for (int part = 1; part <= 2; part++) {
            final long start = System.currentTimeMillis();
            final String[] input = this.loadStrings(year, day, part);
            final String expected = this.loadAnswer(year, day, part);
            final String actual = part == 1 ? this.part1(input) : this.part2(input);
            if (!expected.equals(actual)) {
                throw new WrongAnswerException(year,
                        day,
                        part,
                        expected,
                        actual);
            }
            System.out.printf("Attempted %s.%s.%s expected %s and got %s in %sms%n",
                    year,
                    day,
                    part,
                    expected,
                    actual,
                    System.currentTimeMillis() - start);
        }
        return true;
    }

    private String[] loadStrings(final int year, final int day, final int part) {
        final Path path = Paths.get(String.format("src/main/resources/%s/%s-Day%s-%s.txt",
                year,
                year,
                day,
                part));
        try {
            return Files.lines(path, StandardCharsets.UTF_8).toArray(String[]::new);
        } catch (final IOException e) {
            e.printStackTrace();
            throw new InvalidSetupException(e.getMessage());
        }
    }

    private String loadAnswer(final int year, final int day, final int part) {
        final Path path = Paths.get(String.format("src/main/resources/%s/%s-Day%s-%s-answer.txt",
                year,
                year,
                day,
                part));
        try {
            final List<String> lines = Files.lines(path, StandardCharsets.UTF_8)
                    .collect(Collectors.toList());
            return lines.get(0);
        } catch (final IOException e) {
            e.printStackTrace();
            throw new InvalidSetupException(e.getMessage());
        }
    }
}
