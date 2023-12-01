package com.simongarton.adventofcode.year2023;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class Year2023Day1Test {

    @Test
    void part1() throws IOException {

        // given
//        final Path path = Paths.get("src/main/resources/2023/sample.txt");
        final Path path = Paths.get("src/main/resources/2023/2023-Day1-1.txt");
        final List<String> lines = Files.lines(path, StandardCharsets.UTF_8).collect(Collectors.toList());
        final Year2023Day1 year2023Day1 = new Year2023Day1();

        // when
        final String result = year2023Day1.part1(lines.toArray(new String[0]));

        // then
        assertNotNull(result);
        System.out.println(result);
    }


    @Test
    void part2() {
    }
}