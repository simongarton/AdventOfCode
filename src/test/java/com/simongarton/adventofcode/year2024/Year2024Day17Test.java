package com.simongarton.adventofcode.year2024;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Year2024Day17Test {

    @Test
    void testFirstNumber() {

        // given
        final CompiledProgram compiledProgram = new CompiledProgram();
        final long a = 5;

        // when
        final String output = compiledProgram.run(a);

        // then
        assertEquals("0,", output);
    }

    @Test
    void testSecondNumber() {

        // given
        final CompiledProgram compiledProgram = new CompiledProgram();
        final long a = (5 << 3) + 6;

        // when
        final String output = compiledProgram.run(a);

        // then
        assertEquals("3,0,", output);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void testThirdNumber(final int number) {

        // given
        final CompiledProgram compiledProgram = new CompiledProgram();
        final long a = (46 << 3) + number;

        // when
        final String output = compiledProgram.run(a);

        // then
        assertEquals("5,3,0,", output);
    }

    @ParameterizedTest
    @MethodSource("valuesForHigherNumbers")
    void testHigherNumbers(final long previousNumber, final int number, final String expected) {

        // given
        final CompiledProgram compiledProgram = new CompiledProgram();
        final long a = (previousNumber << 3) + number;

        // when
        final String output = compiledProgram.run(a);

        // then
        assertEquals(expected, output);
    }

    public static List<Arguments> valuesForHigherNumbers() {

        return List.of(
                Arguments.of(368, 0, "5,5,3,0,"),
                Arguments.of(368, 1, "5,5,3,0,"),
                Arguments.of(368, 4, "5,5,3,0,"),
                Arguments.of(369, 1, "5,5,3,0,"),
                Arguments.of(369, 4, "5,5,3,0,"),

                Arguments.of(2944, 6, "3,5,5,3,0,"),
                Arguments.of(2945, 6, "3,5,5,3,0,"),
                Arguments.of(2948, 2, "3,5,5,3,0,"),
                Arguments.of(2948, 6, "3,5,5,3,0,"),
                Arguments.of(2953, 4, "3,5,5,3,0,"),
                Arguments.of(2953, 6, "3,5,5,3,0,"),
                Arguments.of(2956, 2, "3,5,5,3,0,"),
                Arguments.of(2956, 6, "3,5,5,3,0,"),

                Arguments.of(23558, 4, "0,3,5,5,3,0,"),
                Arguments.of(23590, 4, "0,3,5,5,3,0,"),
                Arguments.of(23654, 4, "0,3,5,5,3,0,"),

                Arguments.of(188468, 4, "4,0,3,5,5,3,0,"),
                Arguments.of(188468, 4, "4,0,3,5,5,3,0,"),
                Arguments.of(188468, 4, "4,0,3,5,5,3,0,"),

                Arguments.of(1507748, 6, "1,4,0,3,5,5,3,0,"),
                Arguments.of(1507748, 6, "1,4,0,3,5,5,3,0,"),
                Arguments.of(1507748, 6, "1,4,0,3,5,5,3,0,")
        );
    }

    @Test
    void testOneNumber() {

        // given
        final CompiledProgram compiledProgram = new CompiledProgram();
        final long a = (25295865633502L << 3) << 3;
        System.out.println(a);

        // when
        final String output = compiledProgram.run(a);

        // then
        System.out.println(output);
    }

    @Test
    void testCorrectInput() {

        // given
        final CompiledProgram compiledProgram = new CompiledProgram();
        final long a = 202991746427434L;
        final String expected = "2,4,1,1,7,5,4,4,1,4,0,3,5,5,3,0,";

        // when
        final String output = compiledProgram.run(a);

        // then
        assertEquals(expected, output);
    }

    @Test
    void testFullComputer() throws IOException {

        // given
        final Path path = Path.of("src", "main", "resources", "2024", "2024-Day17-1-quine.txt");
        final String[] input = Files.lines(path, StandardCharsets.UTF_8).toArray(String[]::new);
        final ChronospatialComputer computer = ChronospatialComputer.initializeFromLines(input);
        final String expected = "2,4,1,1,7,5,4,4,1,4,0,3,5,5,3,0";

        // when
        computer.run();

        // then
        assertEquals(expected, computer.getOutputString());
    }
}