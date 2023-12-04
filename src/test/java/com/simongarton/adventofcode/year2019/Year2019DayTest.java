package com.simongarton.adventofcode.year2019;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class Year2019DayTest {

    @Test
    void part1() throws IOException {

        // given
        final Path path = Paths.get("src/main/resources/2019/sample.txt");
//        final Path path = Paths.get("src/main/resources/2019/2019-Day2-1.txt");
        final List<String> lines = Files.lines(path, StandardCharsets.UTF_8).collect(Collectors.toList());
        final Year2019Day2 year2019Day2 = new Year2019Day2();

        // when
        final String result = year2019Day2.part1(lines.toArray(new String[0]));

        // then
        assertNotNull(result);
        System.out.println(result);
    }

    @Test
    void part2() throws IOException {

        // given
//        final Path path = Paths.get("src/main/resources/2019/sample.txt");
        final Path path = Paths.get("src/main/resources/2019/2019-Day2-2.txt");
        final List<String> lines = Files.lines(path, StandardCharsets.UTF_8).collect(Collectors.toList());
        final Year2019Day2 year2019Day2 = new Year2019Day2();

        // when
        final String result = year2019Day2.part2(lines.toArray(new String[0]));

        // then
        assertNotNull(result);
        System.out.println(result);
    }
}