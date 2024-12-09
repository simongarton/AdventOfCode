package com.simongarton.adventofcode.year2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Year2024Day9Test {

    @Test
    void swapCharacter() {

        // given
        final String a = "1.3";
        final Year2024Day9 year2024Day9 = new Year2024Day9();
        final int frontPointer = 1;
        final String front = ".";
        final int backPointer = 2;
        final String back = "3";

        // when
        final String shuffled = year2024Day9.swapCharacter(a, front, frontPointer, back, backPointer);

        // then
        assertEquals("13.", shuffled);
    }

    @Test
    void swapCharacter2() {

        // given
        final String a = "1..3";
        final Year2024Day9 year2024Day9 = new Year2024Day9();
        final int frontPointer = 1;
        final String front = ".";
        final int backPointer = 3;
        final String back = "3";

        // when
        final String shuffled = year2024Day9.swapCharacter(a, front, frontPointer, back, backPointer);

        // then
        assertEquals("13..", shuffled);
    }
}