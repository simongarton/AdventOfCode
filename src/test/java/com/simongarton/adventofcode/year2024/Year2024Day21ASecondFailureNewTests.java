package com.simongarton.adventofcode.year2024;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Year2024Day21ASecondFailureNewTests {

    // getting stuck on these tests ...

    @Test
    void testNumpadSequenceList() {

        // given
        final Year2024Day21ASecondFailure year2024Day21ASecondFailure = new Year2024Day21ASecondFailure();
        final String start = "9";
        final String end = "A";

        // when
        final List<String> sequences = year2024Day21ASecondFailure.buildNumpadSequencesFor(start, end);

        // then
        assertEquals(4, sequences.size());
        assertEquals("vvvA", sequences.get(0));
        assertEquals("vv<v>A", sequences.get(1));
        assertEquals("v<vv>A", sequences.get(2));
        assertEquals("<vvv>A", sequences.get(3));
    }

    @Test
    void testShortestNumpadSequence() {

        // given
        final Year2024Day21ASecondFailure year2024Day21ASecondFailure = new Year2024Day21ASecondFailure();
        final String start = "9";
        final String end = "A";

        // when
        final String sequence = year2024Day21ASecondFailure.buildShortestNumpadSequenceForASingleDigit(start, end);

        // then
        // this is for the dirpad driving the numpad
        assertEquals("vvvA", sequence);
    }

    @ParameterizedTest
    @MethodSource("dirpadDirections")
    void testDirpad(final String target, final int robotCount, final String expected) {

        // think about this being a dirpad driving a numpad.
        // my target will be a directions sequence ending in A, which I get from the buildShortestNumpadSequence() ethods
        // and I can assume I start at A each time.

        // given
        final Year2024Day21ASecondFailure year2024Day21ASecondFailure = new Year2024Day21ASecondFailure();

        // when
        final String sequence = year2024Day21ASecondFailure.buildShortestDirpadSequenceForMovement(target, robotCount);

        // then
        System.out.println(sequence);
        assertEquals(expected, sequence);
    }

    static List<Arguments> dirpadDirections() {

        // must end in A

        return List.of(
                Arguments.of("A", 1, "A"),
                Arguments.of("^A", 2, "<A>A"),
                Arguments.of("<", 3, "<v<A>>^A")
        );
    }
}
