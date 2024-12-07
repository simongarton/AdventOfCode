package com.simongarton.adventofcode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Sample {

    public static void main(final String[] args) throws IOException {

        // which part to do with sample ?
        final boolean part1 = false;

        final Path path = Paths.get(String.format("src/main/resources/2024/2024-%s-sample-1.txt", Day.DAY));
        if (part1) {
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
