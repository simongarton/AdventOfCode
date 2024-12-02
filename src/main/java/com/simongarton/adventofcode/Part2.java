package com.simongarton.adventofcode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Part2 {

    public static void main(final String[] args) throws IOException {

        final Path path = Paths.get(String.format("src/main/resources/2024/2024-%s-1.txt", Day.DAY));
        System.out.println(CurrentChallenge.getCurrentChallenge()
                .part2(Files.lines(path, StandardCharsets.UTF_8)
                        .toArray(String[]::new)));
    }
}
