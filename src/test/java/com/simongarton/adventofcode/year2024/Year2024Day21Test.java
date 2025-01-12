package com.simongarton.adventofcode.year2024;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Year2024Day21Test {

    @ParameterizedTest
    @MethodSource("part1SampleValues")
    void testPart1SampleLengthOnly(final String numericCode, final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final String sequence = year2024Day21.shortestFullSequence(numericCode, 3);

        // then
        assertEquals(expected.length(), sequence.length());
    }

    @ParameterizedTest
    @MethodSource("part1SampleValues")
    void testPart1SampleExact(final String numericCode, final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final String sequence = year2024Day21.shortestFullSequence(numericCode, 3);

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

    @Test
    void testSingleSampleValue() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String numericCode = "029A";
        final String expected = "<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A";

        // when
        final String sequence = year2024Day21.shortestFullSequence(numericCode, 3);

        /*

        Expected :<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A
        Actual   :<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<v<A>>^AA<vA>A^A<A>A<v<A>A>^AAA<A>vA^A

         */

        // then
        assertEquals(expected.length(), sequence.length());
    }

    @Test
    void testSingleDigitValue() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String numericCode = "0";
        final String expected = "<vA<AA>>^AvAA<^A>A"; // this matches the beginning of the sample

        // when
        final String sequence = year2024Day21.shortestFullSequence(numericCode, 3);

        // then
        assertEquals(expected, sequence);
    }

    @ParameterizedTest
    @MethodSource("numPadPathValues")
    void testGetNumPadPaths(final String from, final String to, final List<String> expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final List<String> paths = year2024Day21.getNumPadSequences(from, to);

        // then
        assertEquals(expected, paths);
    }

    static List<Arguments> numPadPathValues() {

        return List.of(
                Arguments.of("A", "3", List.of("^A"))
        );
    }

    @ParameterizedTest
    @MethodSource("numPadShortestPathValues")
    void testGetNumPadShortestPaths(final String from, final String to, final List<String> unsorted) {

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
    void testGetDirPadPaths(final String from, final String to, final List<String> expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final List<String> paths = year2024Day21.getDirPadSequences(from, to);

        // then
        assertEquals(expected, paths);
    }

    static List<Arguments> dirPadPathValues() {

        return List.of(
                Arguments.of("A", "^", List.of("<A")),
                Arguments.of("A", "<", List.of("v<<A"))
        );
    }

    @Test
    void testBuildDirPadKeyPressesForSequence() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final List<String> expected = List.of("<Av<A>>^A");

        // when
        final List<String> actual = year2024Day21.buildDirPadKeyPressesForSequence("^<A", 3);

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("shortestKeypadSequenceValues")
    void testShortestKeypadSequence(final String sequence,
                                    final int robotLevel,
                                    final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final String actual = year2024Day21.shortestFullSequence(sequence, robotLevel);

        // then
        assertEquals(expected, actual);
    }

    static List<Arguments> shortestKeypadSequenceValues() {

        return List.of(
                Arguments.of("3", 1, "^A"),
                Arguments.of("378A", 1, "^A<<^^A>A>vvvA"),
                Arguments.of("2", 1, "<^A"),
                Arguments.of("3", 2, "<A>A"),
                Arguments.of("3", 3, "v<<A>>^AvA^A"),
                Arguments.of("7", 1, "^^^<<A"),
                Arguments.of("7", 2, "<AAAv<AA>>^A"),
                Arguments.of("7", 3, "v<<A>>^AAA<vA<A>>^AAvAA<^A>A"),
                Arguments.of("0", 1, "<A"),
                Arguments.of("0", 2, "v<<A>>^A"),
                Arguments.of("0", 3, "<vA<AA>>^AvAA<^A>A")
        );
    }

    @Test
    void testNumPadPaths() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String from = "1";
        final String to = "5";

        // when
        final List<String> paths = year2024Day21.getNumPadPaths(from, to);

        // then
        assertEquals(2, paths.size());
        assertTrue(paths.contains("^>A"));
        assertTrue(paths.contains(">^A"));
    }

    @Test
    void testRepeatedNumber() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String sequence = "000A";
        final String expected = "<vA<AA>>^AvAA<^A>AAA<vA>^A<A>A"; // generated, not worked through

        // when
        final String actual = year2024Day21.shortestFullSequence(sequence, 3);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void testBuildKeySequences() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String sequence = "<A";

        // when
        final List<String> actual = year2024Day21.buildKeySequences(sequence);

        // then
        assertEquals(1, actual.size());
        assertTrue(actual.contains("v<<A>>^A"));
    }

    @ParameterizedTest
    @MethodSource("shortestSequenceRecursivelyValues")
    void testShortestSequenceRecursively(final String sequence, final int maxLevel, final int expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final int level = 1;
        final Map<String, Long> cache = new HashMap<>();

        // when
        final long actual = year2024Day21.shortestSequenceRecursively(sequence, null, level, maxLevel, cache);

        // then
        assertEquals(expected, actual);
    }

    static List<Arguments> shortestSequenceRecursivelyValues() {

        return List.of(
                Arguments.of("<A", 1, 2),
                Arguments.of("<A", 2, 8),
                Arguments.of("<A", 3, 18),
                Arguments.of("<A", 4, 46),
                Arguments.of("<A^A>^^AvvvA", 3, 68),
                Arguments.of("<A", 3, 18),
                Arguments.of("^A", 3, 12),
                Arguments.of(">^^A", 3, 20),
                Arguments.of("vvvA", 3, 18)
        );
    }

    @ParameterizedTest
    @MethodSource("part1SampleValues")
    void testPart1SampleWithRecursion(final String numericCode, final String expected) {

        // it works for 3 out of 5 samples; 3 and 4 I over estimate by 8 or 10.

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final int level = 1;
        final int maxLevel = 3;
        final Map<String, Long> cache = new HashMap<>();
        final String sequence = year2024Day21.shortestFullSequence(numericCode, 1);

        // when
        final long actual = year2024Day21.shortestSequenceRecursively(sequence, null, level, maxLevel, cache);

        // then
        assertEquals(expected.length(), actual);
    }

    @Test
    void testLengths() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final int level = 1;
        final String numericCode = "176A";

        // when
        for (int maxLevel = 1; maxLevel <= 27; maxLevel++) {
            final Map<String, Long> cache = new HashMap<>();
            final long actual = year2024Day21.shortestSequenceRecursively(numericCode, null, level, maxLevel, cache);
            System.out.println(numericCode + ":" + maxLevel + "=" + actual);
        }
    }

    @Test
    void testOneValueForPart2() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final int level = 1;
        final int maxLevel = 27;
        final Map<String, Long> cache = new HashMap<>();
        final String numericCode = "176A";
        final long expected = 89741193602L;

        // when
        final long actual = year2024Day21.shortestSequenceRecursively(numericCode, null, level, maxLevel, cache);

        // then
        assertEquals(expected, actual);

        year2024Day21.dumpTreeNodeGraph();
    }

    @Test
    void howDoesSplitWorkOnMultiples() {

        // as I expected. "<AAA" will give me three sections : "<" and 2 empty strings, which I then add As to each of.

        // given
        final String sequence = "<A>AAA^AA>A";
        final int expected = 7;

        // when
        final String[] subsequences = sequence.split("A");

        // then
        assertEquals(expected, subsequences.length);
        assertEquals("<", subsequences[0]);
        assertEquals(">", subsequences[1]);
        assertEquals("", subsequences[2]);
        assertEquals("", subsequences[3]);
        assertEquals("^", subsequences[4]);
        assertEquals("", subsequences[5]);
        assertEquals(">", subsequences[6]);
    }

    @Test
    void testBuildDirKeySequenceRecursively() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String sequence = "v<<A>>^A";
        final List<String> result = new ArrayList<>();

        // when
        year2024Day21.buildDirKeySequenceRecursively(sequence, 0, "A", "", result, 0);

        // then
        assertEquals(4, result.size());
    }

    @Test
    void testPart1() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String[] input = new String[]{"140A", "180A", "176A", "805A", "638A"};
        final long expected = 138764;

        // when
        final long actual = Long.parseLong(year2024Day21.part1(input));

        // then
        assertEquals(expected, actual);
    }

    @Test
    void testPart1Old() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String[] input = new String[]{"140A", "180A", "176A", "805A", "638A"};
        final long expected = 138764;

        // when
        final long actual = Long.parseLong(year2024Day21.part1Old(input));

        // then
        assertEquals(expected, actual);
    }

    @Test
    void testPart2() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String[] input = new String[]{"140A", "180A", "176A", "805A", "638A"};
        final long expected = 169137886514152L;

        // when
        final long actual = Long.parseLong(year2024Day21.part2(input));

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("zigZagValues")
    void testHasZigZags(final String sequence, final boolean expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final boolean actual = year2024Day21.hasZigZags(sequence);

        // then
        assertEquals(expected, actual);
    }

    static List<Arguments> zigZagValues() {

        return List.of(
                Arguments.of("<A", false),
                Arguments.of("^A", false),
                Arguments.of("^^A", false),
                Arguments.of("^vA", false),
                Arguments.of("<^A", false),
                Arguments.of("v<^A", true),
                Arguments.of("<^>A", true)
        );
    }
}
