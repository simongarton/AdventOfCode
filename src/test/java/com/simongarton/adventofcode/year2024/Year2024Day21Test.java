package com.simongarton.adventofcode.year2024;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Year2024Day21Test {

    @ParameterizedTest
    @MethodSource("part1SampleValues")
    void part1Sample(final String numericCode, final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final String sequence = year2024Day21.fullSequence(numericCode);

        // then
        assertEquals(expected, sequence);
    }

    @ParameterizedTest
    @MethodSource("numPadPathValues")
    void getNumPadPaths(final String from, final String to, final List<String> expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final List<String> paths = year2024Day21.getNumPadSequences(from, to);

        // then
        assertEquals(expected, paths);
    }

    @ParameterizedTest
    @MethodSource("numPadShortestPathValues")
    void getNumPadShortestPaths(final String from, final String to, final List<String> unsorted) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final List<String> expected = unsorted.stream().sorted(Comparator.naturalOrder()).toList();

        // when
        final List<String> paths = year2024Day21.getNumPadShortestSequences(from, to);

        // then
        assertEquals(expected, paths);
    }

    @ParameterizedTest
    @MethodSource("dirPadPathValues")
    void getDirPadPaths(final String from, final String to, final List<String> expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final List<String> paths = year2024Day21.getDirPadSequences(from, to);

        // then
        assertEquals(expected, paths);
    }

    static List<Arguments> part1SampleValues() {

        return List.of(
                Arguments.of("029A", "<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A"),
                Arguments.of("980A", "<v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A"),
                Arguments.of("179A", "<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A"),
                Arguments.of("456A", "<v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A"),
                Arguments.of("379A", "<v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A")
        );
    }

    static List<Arguments> numPadPathValues() {

        return List.of(
                Arguments.of("A", "3", List.of("^A", "<^>A", "<^^>vA"))
        );
    }

    static List<Arguments> numPadShortestPathValues() {

        return List.of(
                Arguments.of("A", "3", List.of("^A")),
                Arguments.of("A", "2", List.of("^<A", "<^A"))
        );
    }

    static List<Arguments> dirPadPathValues() {

        return List.of(
                Arguments.of("A", "^", List.of("<A", "v<^A")),
                Arguments.of("A", "<", List.of("<v<A", "v<<A"))
        );
    }
}