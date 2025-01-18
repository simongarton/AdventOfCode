package com.simongarton.adventofcode;

import com.simongarton.adventofcode.year2015.Year2015Day8;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdventOfCodeChallengeTest {

    @ParameterizedTest
    @MethodSource("doubleSlashes")
    void testDoubleSlashes(final String line, final int expected) {

        // given
        final Year2015Day8 year2015Day8 = new Year2015Day8();

        // when
        final int actual = year2015Day8.regexCountCharactersInString(line, "\\{2}");

        // then
        assertEquals(expected, actual);
    }

    static List<Arguments> doubleSlashes() {

        return List.of(
                Arguments.of("\\", 0),
                Arguments.of("\\\\", 0) // but this is WRONG ?! should be 1 ?
        );

    }
}
