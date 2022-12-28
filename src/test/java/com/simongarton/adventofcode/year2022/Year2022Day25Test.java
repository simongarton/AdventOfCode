package com.simongarton.adventofcode.year2022;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Year2022Day25Test {

    @ParameterizedTest
    @MethodSource("snafuToDecimalTestValues")
    void snafuToDecimalTest(final String line, final long expected) {

        // given
        final Year2022Day25 year2022Day25 = new Year2022Day25();

        // when
        final long actual = year2022Day25.snafuToDecimal(line);

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("decimalToSnafuTestValues")
    void decimalToSnafuTest(final long value, final String expected) {

        // given
        final Year2022Day25 year2022Day25 = new Year2022Day25();

        // when
        final String actual = year2022Day25.decimalToSnafu(value);

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("snafuDigitValues")
    void snafuDigitTest(final String substring, final int power, final long expected) {

        // given
        final Year2022Day25 year2022Day25 = new Year2022Day25();

        // when
        final long actual = year2022Day25.snafuDigit(substring, power);

        // then
        assertEquals(expected, actual);
    }

    public static Stream<Arguments> snafuToDecimalTestValues() {
        return Stream.of(
                Arguments.of("2", 2),
                Arguments.of("22", 12),
                Arguments.of("21", 11),
                Arguments.of("20", 10),
                Arguments.of("2-", 9),
                Arguments.of("2=", 8)
        );
    }

    // 1 5 25 125
    public static Stream<Arguments> decimalToSnafuTestValues() {
        return Stream.of(
                Arguments.of(0, "0"),
                Arguments.of(1, "1"),
                Arguments.of(2, "2"),
                Arguments.of(3, "1="),
                Arguments.of(4, "1-"),
                Arguments.of(5, "10"),
                Arguments.of(6, "11"),
                Arguments.of(7, "12"),
                Arguments.of(8, "2="),
                Arguments.of(9, "2-"),
                Arguments.of(10, "20"),
                Arguments.of(11, "21"),
                Arguments.of(12, "22"),
                Arguments.of(13, "1=="),
                Arguments.of(14, "1=-"),
                Arguments.of(15, "1=0"),
                Arguments.of(16, "1=1"),
                Arguments.of(17, "1=2"),
                Arguments.of(18, "1-=")
        );
    }

    public static Stream<Arguments> snafuDigitValues() {
        return Stream.of(
                Arguments.of("2", 0, 2),
                Arguments.of("1", 0, 1),
                Arguments.of("0", 0, 0),
                Arguments.of("-", 0, -1),
                Arguments.of("=", 0, -2),
                Arguments.of("2", 1, 10),
                Arguments.of("1", 1, 5),
                Arguments.of("0", 1, 0),
                Arguments.of("-", 1, -5),
                Arguments.of("=", 1, -10),
                Arguments.of("2", 2, 50),
                Arguments.of("1", 2, 25),
                Arguments.of("0", 2, 0),
                Arguments.of("-", 2, -25),
                Arguments.of("=", 2, -50)

        );
    }
}