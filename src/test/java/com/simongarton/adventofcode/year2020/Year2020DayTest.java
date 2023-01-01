package com.simongarton.adventofcode.year2020;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class Year2020DayTest {

    @Test
    void part1() throws IOException {

        // given
//        final Path path = Paths.get("src/main/resources/2020/sample.txt");
        final Path path = Paths.get("src/main/resources/2020/2020-Day3-1.txt");
        final List<String> lines = Files.lines(path, StandardCharsets.UTF_8).collect(Collectors.toList());
        final Year2020Day3 year2020Day3 = new Year2020Day3();

        // when
        final String result = year2020Day3.part1(lines.toArray(new String[0]));

        // then
        assertNotNull(result);
        System.out.println(result);
    }

    @Test
    void part2() throws IOException {

        // given
//        final Path path = Paths.get("src/main/resources/2020/sample.txt");
        final Path path = Paths.get("src/main/resources/2020/2020-Day3-1.txt");
        final List<String> lines = Files.lines(path, StandardCharsets.UTF_8).collect(Collectors.toList());
        final Year2020Day3 year2020Day3 = new Year2020Day3();

        // when
        final String result = year2020Day3.part2(lines.toArray(new String[0]));

        // then
        assertNotNull(result);
        System.out.println(result);
    }
}