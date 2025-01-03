package com.simongarton.adventofcode.year2024;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class Year2024Day21Test {

    @ParameterizedTest
    @MethodSource("part1SampleValues")
    void part1Sample(final String numericCode, final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final String sequence = year2024Day21.shortestFullSequence(numericCode);

        // then
        assertEquals(expected, sequence);
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

    static List<Arguments> numPadPathValues() {

        return List.of(
                Arguments.of("A", "3", List.of("^A", "<^>A", "<^^>vA"))
        );
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

    static List<Arguments> numPadShortestPathValues() {

        return List.of(
                Arguments.of("A", "3", List.of("^A")),
                Arguments.of("A", "2", List.of("^<A", "<^A"))
        );
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

    static List<Arguments> dirPadPathValues() {

        return List.of(
                Arguments.of("A", "^", List.of("<A", "v<^A")),
                Arguments.of("A", "<", List.of("<v<A", "v<<A"))
        );
    }

    @Test
    void buildDirPadKeyPressesForSequence() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final List<String> expected = List.of("<Av<A>>^A", "<Av<A>^>A", "v<^Av<A>>^A", "v<^Av<A>^>A");

        // when
        final List<String> actual = year2024Day21.buildDirPadKeyPressesForSequence("^<A");

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("buildNodeForNumericSequenceValues")
    void buildNodeForNumericSequence(final String sequence, final int robotLevel, final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final Year2024Day21.Node actual = year2024Day21.buildNodeForNumericSequence(sequence, robotLevel);

        // then
        assertNotNull(actual);
    }

    static List<Arguments> buildNodeForNumericSequenceValues() {

        return List.of(
                Arguments.of("3", 1, "^A"),
                Arguments.of("2", 1, "<^A"),
                Arguments.of("1", 1, "<^<A"),

                Arguments.of("378A", 1, "^A<<^^A>A>vvvA")
        );
    }

    @ParameterizedTest
    @MethodSource("buildLevelsForNumericSequenceValues")
    void buildLevels(final String sequence,
                     final int robotLevel,
                     final int levels,
                     final Map<Integer, Integer> levelData,
                     final String expected) {
        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final Year2024Day21.Node node = year2024Day21.buildNodeForNumericSequence(sequence, robotLevel);
        final Map<Integer, List<Year2024Day21.Node>> actual = year2024Day21.buildLevels(node);

        // then
        assertNotNull(actual);
        assertEquals(levels, actual.size());
        for (final Map.Entry<Integer, Integer> entry : levelData.entrySet()) {
            assertEquals(entry.getValue(), actual.get(entry.getKey()).size());
        }
    }

    static List<Arguments> buildLevelsForNumericSequenceValues() {

        return List.of(
                Arguments.of("3", 1, 2, Map.of(0, 1, 1, 3), "^A"),
                Arguments.of("378A", 1, 2, Map.of(0, 1, 1, 216), "^A<<^^A>A>vvvA"),
                Arguments.of("2", 1, 2, Map.of(0, 1, 1, 4), "<^A")
        );
    }

    @ParameterizedTest
    @MethodSource("buildLevelsForNumericSequenceValues")
    void shortestKeypadSequence(final String sequence,
                                final int robotLevel,
                                final int levels,
                                final Map<Integer, Integer> levelData,
                                final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final Year2024Day21.Node node = year2024Day21.buildNodeForNumericSequence(sequence, robotLevel);
        final String actual = year2024Day21.shortestKeypadSequence(sequence, robotLevel);

        // then
        assertEquals(expected, actual);
    }

}
