package com.simongarton.adventofcode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Part1 {

    public static void main(final String[] args) throws IOException {

        final Path path = Paths.get(String.format("../AdventOfCodeData/src/main/resources/%s/%s-Day%s-1.txt",
                CurrentChallenge.YEAR,
                CurrentChallenge.YEAR,
                CurrentChallenge.DAY));
        System.out.println(CurrentChallenge.getCurrentChallenge()
                .part1(Files.lines(path, StandardCharsets.UTF_8)
                        .toArray(String[]::new)));
    }
}
