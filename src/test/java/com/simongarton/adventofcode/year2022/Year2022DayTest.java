package com.simongarton.adventofcode.year2022;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class Year2022DayTest {

    @Test
    void part1() throws IOException {

        // given
        final Path path = Paths.get("src/main/resources/2022/sample.txt");
        final List<String> lines = Files.lines(path, StandardCharsets.UTF_8).collect(Collectors.toList());
        final Year2022Day7 year2022Day7 = new Year2022Day7();

        // when
        final String result = year2022Day7.part1(lines.toArray(new String[0]));

        // then
        assertNotNull(result);
        System.out.println(result);
    }

    @Test
    void part2() throws IOException {

        // given
        final Path path = Paths.get("src/main/resources/2022/sample.txt");
        final List<String> lines = Files.lines(path, StandardCharsets.UTF_8).collect(Collectors.toList());
        final Year2022Day7 year2022Day7 = new Year2022Day7();

        // when
        final String result = year2022Day7.part2(lines.toArray(new String[0]));

        // then
        assertNotNull(result);
        System.out.println(result);
    }
}