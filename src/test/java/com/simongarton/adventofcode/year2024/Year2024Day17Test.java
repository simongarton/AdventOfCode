package com.simongarton.adventofcode.year2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Year2024Day17Test {

    @Test
    void octalGeneratorTest0() {

        // given
        final Year2024Day17.OctalGenerator octalGenerator = new Year2024Day17.OctalGenerator();

        // when
        final String value = octalGenerator.valueToString();

        // then
        assertEquals("0000000000000000", value);
    }

    @Test
    void octalGeneratorTest1() {

        // given
        final Year2024Day17.OctalGenerator octalGenerator = new Year2024Day17.OctalGenerator();

        // when
        octalGenerator.yield();
        final String value = octalGenerator.valueToString();

        // then
        assertEquals("0000000000000001", value);
    }

    @Test
    void octalGeneratorTest8() {

        // given
        final Year2024Day17.OctalGenerator octalGenerator = new Year2024Day17.OctalGenerator();

        // when
        for (int i = 0; i < 8; i++) {
            octalGenerator.yield();
        }
        final String value = octalGenerator.valueToString();

        // then
        assertEquals("0000000000000010", value);
    }

    @Test
    void octalGeneratorTest65() {

        // given
        final Year2024Day17.OctalGenerator octalGenerator = new Year2024Day17.OctalGenerator();

        // when
        for (int i = 0; i < 65; i++) {
            octalGenerator.yield();
        }
        final String value = octalGenerator.valueToString();

        // then
        assertEquals("0000000000000101", value);
    }

    @Test
    void octalGeneratorTest123plus1() {

        // given
        final Year2024Day17.OctalGenerator octalGenerator = new Year2024Day17.OctalGenerator();

        // when
        for (int i = 0; i < 123; i++) {
            octalGenerator.yield();
        }
        final long value = octalGenerator.yield();

        // then
        assertEquals(124, value);
    }

}