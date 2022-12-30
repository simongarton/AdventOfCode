package com.simongarton.adventofcode.year2022;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Year2022Day19Test {

    @Test
    void testClone() {

        // given
        final String blueprint = "Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.";
        final Year2022Day19.Factory factory = new Year2022Day19.Factory(blueprint, 24);

        // when
        final Year2022Day19.Factory factory2 = factory.clone();
        factory2.setTitle("Not me.");
        factory.setTitle("Me.");

        // then
        assertEquals("Me.", factory.getBlueprintTitle());
        assertEquals("Not me.", factory2.getBlueprintTitle());
    }

    @ParameterizedTest
    @MethodSource("sequencesAndGeodes")
    void testSequence(final String sequence, final int expected) {

        // given
        final String blueprint = "Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.";
        final Year2022Day19.Factory factory = new Year2022Day19.Factory(blueprint, 24);

        // when
        factory.testSequence(sequence);

        // then
        assertEquals(expected, factory.getGeodes());
    }

    @ParameterizedTest
    @MethodSource("sequencesAndGeodes2")
    void testSequenceOnBlueprint2(final String sequence, final int expected) {

        // given
        final String blueprint = "Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.";
        final Year2022Day19.Factory factory = new Year2022Day19.Factory(blueprint, 24);
        factory.setFactoryDebug(true);

        // when
        factory.testSequence(sequence);

        // then
        assertEquals(expected, factory.getGeodes());
    }

    public static Stream<Arguments> sequencesAndGeodes2() {
        return Stream.of(
//                Arguments.of("CCBBGG", 4)
//                Arguments.of("OCOCCBCBBGG", 12)
                Arguments.of("COOCOCCBCOBOOGOOG", 11)
        );

    }

    public static Stream<Arguments> sequencesAndGeodes() {
        return Stream.of(
                Arguments.of("CCCBCBGG", 9),
                Arguments.of("CCBGG", 4),
                Arguments.of("CCCBGG", 5),
                Arguments.of("CCBBGGB", 4),
                Arguments.of("CCCBGBGG", 5),
                Arguments.of("CCBCBGGG", 5),
                Arguments.of("CCCBBGGGO", 8)
        );
    }


}