package com.simongarton.adventofcode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Sample {

    public static void main(final String[] args) throws IOException {

        // it's always DayX-sample
        final Path path = Paths.get(String.format("../AdventOfCodeData/src/main/resources/%s/%s-Day%s-sample.txt",
                CurrentChallenge.YEAR,
                CurrentChallenge.YEAR,
                CurrentChallenge.DAY));

        if (false) {
            System.out.println(CurrentChallenge.getCurrentChallenge()
                    .part1(Files.lines(path, StandardCharsets.UTF_8)
                            .toArray(String[]::new)));
        } else {
            System.out.println(CurrentChallenge.getCurrentChallenge()
                    .part2(Files.lines(path, StandardCharsets.UTF_8)
                            .toArray(String[]::new)));
        }
    }
}
