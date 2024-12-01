package com.simongarton.adventofcode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Part1 {

    public static void main(final String[] args) throws IOException {

        final Path path = Paths.get("src/main/resources/2024/2024-Day1-1.txt");
        System.out.println(CurrentChallenge.getCurrentChallenge()
                .part1(Files.lines(path, StandardCharsets.UTF_8)
                        .toArray(String[]::new)));
    }
}
