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

    // this is now timing out, as do other things on level 3

    @Test
    void singleSampleValueTest() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String numericCode = "029A";
        final String expected = "<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A";

        // when
        final String sequence = year2024Day21.shortestFullSequence(numericCode);

        // then
        assertEquals(expected, sequence);
    }

    @Test
    void singleDigitValueTest() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String numericCode = "0";
        final String expected = "<vA<AA>>^AvAA<^A>A"; // this matches the beginning of the sample

        // when
        final String sequence = year2024Day21.shortestFullSequence(numericCode);

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
        final List<String> actual = year2024Day21.buildDirPadKeyPressesForSequence("^<A", 3);

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
        final String shortestSequence = year2024Day21.shortestKeypadSequenceForNode(actual, robotLevel);

        // then
        assertNotNull(actual);
        assertEquals(expected, shortestSequence);
    }

    static List<Arguments> buildNodeForNumericSequenceValues() {

        return List.of(
                Arguments.of("3", 1, "^A"),
                Arguments.of("2", 1, "<^A"),
                Arguments.of("1", 1, "<^<A"),

                Arguments.of("378A", 1, "^A<<^^A>A>vvvA"),

                Arguments.of("3", 2, "<A>A"),
                Arguments.of("3", 3, "<v<A>>^AvA^A"), // this looks really good, so I'm happy here
                Arguments.of("32", 1, "^A<A"),
                Arguments.of("32", 2, "<A>A<v<A>>^A")
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
        for (final Map.Entry<Integer, List<Year2024Day21.Node>> entry : actual.entrySet()) {
            assertEquals(entry.getValue().size(), levelData.get(entry.getKey()));
        }
    }

    static List<Arguments> buildLevelsForNumericSequenceValues() {

        return List.of(
                Arguments.of("3", 1, 2, Map.of(0, 1, 1, 1), "^A"),
                Arguments.of("378A", 1, 2, Map.of(0, 1, 1, 24), "^A<<^^A>A>vvvA"),
                Arguments.of("2", 1, 2, Map.of(0, 1, 1, 2), "<^A"),
                Arguments.of("3", 2, 3, Map.of(0, 1, 1, 1, 2, 4), "<A>A"),
                Arguments.of("3", 3, 4, Map.of(0, 1, 1, 1, 2, 4, 3, 240), "<v<A>>^AvA^A")
        );
    }

    @ParameterizedTest
    @MethodSource("shortestKeypadSequenceValues")
    void shortestKeypadSequence(final String sequence,
                                final int robotLevel,
                                final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final String actual = year2024Day21.shortestKeypadSequence(sequence, robotLevel);

        // then
        assertEquals(expected, actual);
    }

    static List<Arguments> shortestKeypadSequenceValues() {

        return List.of(
                Arguments.of("3", 1, "^A"),
                Arguments.of("378A", 1, "^A<<^^A>A>vvvA"),
                Arguments.of("2", 1, "<^A"),
                Arguments.of("3", 2, "<A>A"),
                Arguments.of("3", 3, "<v<A>>^AvA^A"),
                Arguments.of("7", 1, "<^<^^A"),
                Arguments.of("7", 2, "<AAAv<AA>>^A"),
//                Arguments.of("7", 3, "^A"),
                Arguments.of("0", 1, "<A"),
                Arguments.of("0", 2, "<v<A>>^A")
//                Arguments.of("0", 3, "^A")
        );
    }

}
