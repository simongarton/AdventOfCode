package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.Day;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Year2024Day3RegexTest {

    @Test
    void sample_1() throws IOException {

        // given
        final Path path = Paths.get(String.format("src/main/resources/2024/2024-%s-sample-1.txt", Day.DAY));
        final String[] input = (Files.lines(path, StandardCharsets.UTF_8).toArray(String[]::new));
        final Year2024Day3Regex year2024Day3Regex = new Year2024Day3Regex();

        // when
        final String result = year2024Day3Regex.part1(input);

        // then
        assertEquals("161", result); // sample
    }

    @Test
    void part1() throws IOException {

        // given
        final Path path = Paths.get(String.format("src/main/resources/2024/2024-%s-1.txt", Day.DAY));
        final String[] input = (Files.lines(path, StandardCharsets.UTF_8).toArray(String[]::new));
        final Year2024Day3Regex year2024Day3Regex = new Year2024Day3Regex();

        // when
        final String result = year2024Day3Regex.part1(input);

        // then
        assertEquals("187825547", result);
    }

    @Test
    void sample_2() throws IOException {

        // given
        final Path path = Paths.get(String.format("src/main/resources/2024/2024-%s-sample-2.txt", Day.DAY));
        final String[] input = (Files.lines(path, StandardCharsets.UTF_8).toArray(String[]::new));
        final Year2024Day3Regex year2024Day3Regex = new Year2024Day3Regex();

        // when
        final String result = year2024Day3Regex.part2(input);

        // then
        assertEquals("48", result);
    }

    @Test
    void sample_3() throws IOException {

        // given
        final Path path = Paths.get(String.format("src/main/resources/2024/2024-%s-sample-3.txt", Day.DAY));
        final String[] input = (Files.lines(path, StandardCharsets.UTF_8).toArray(String[]::new));
        final Year2024Day3Regex year2024Day3Regex = new Year2024Day3Regex();

        // when
        final String result = year2024Day3Regex.part2(input);

        // then
        assertEquals("35", result);
    }

    @Test
    void part2() throws IOException {

        // given
        final Path path = Paths.get(String.format("src/main/resources/2024/2024-%s-1.txt", Day.DAY));
        final String[] input = (Files.lines(path, StandardCharsets.UTF_8).toArray(String[]::new));
        final Year2024Day3Regex year2024Day3Regex = new Year2024Day3Regex();

        // when
        final String result = year2024Day3Regex.part2(input);

        // then
        assertEquals("85508223", result);
    }
}