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

    public String title() {
        return null;
    }

    public abstract boolean run();

    public int getYear() {
        return this.year;
    }

    public int getDay() {
        return this.day;
    }

    protected int year;
    protected int day;

    public abstract String part1(final String[] input);

    public abstract String part2(final String[] input);

    public boolean runChallenge(final int year, final int day) {
        this.year = year;
        this.day = day;
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
            if (this.title() == null) {
                System.out.printf("Attempted %s.%02d.%s and got correct answer in %s ms : %15s%n",
                        year,
                        day,
                        part,
                        this.leftPad("" + (System.currentTimeMillis() - start), 8),
                        actual
                );
            } else {
                System.out.printf("Attempted %s.%02d.%s and got correct answer in %s ms : %15s (%s part %s)%n",
                        year,
                        day,
                        part,
                        this.leftPad("" + (System.currentTimeMillis() - start), 8),
                        actual,
                        this.title(),
                        part
                );
            }
        }
        return true;
    }

    private String leftPad(final String s, final int length) {
        if (s.length() == length) {
            return s;
        }
        if (s.length() > length) {
            return "*".repeat(length);
        }
        return " ".repeat(length - s.length()) + s;
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
