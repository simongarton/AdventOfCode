package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.year2024.day21.DirectionalKeypad;
import com.simongarton.adventofcode.year2024.day21.NumericKeypad;
import com.simongarton.adventofcode.year2024.day21.Radio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Year2024Day21Test {

    @ParameterizedTest
    @MethodSource("sampleSequences")
    void fullSequencesIdentical(final String code, final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final String actual = year2024Day21.fullSequence(code);

        // then
        // this is weird. I am getting different sequences, but 4 out of 5 are the same length.
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("sampleSequences")
    void fullSequencesReversedSameLength(final String code, final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final String actual = year2024Day21.fullSequence(code);

        // then
        // this is weird. I am getting different sequences, but 4 out of 5 are the same length.
        assertEquals(expected.length(), actual.length());
    }

    @ParameterizedTest
    @MethodSource("testingOutputToGetCode")
    void checkingOutput(final String program, final String code) {

        // given
        final NumericKeypad target = new NumericKeypad("target");
        final DirectionalKeypad robot1 = new DirectionalKeypad("robot1", 1, null, target);
        final DirectionalKeypad robot2 = new DirectionalKeypad("robot2", 2, robot1, null);

        // when
        Radio.resetTick();
        robot2.run(program);

        // then
        assertEquals(code, String.join("", target.getKeysPressed()));
        System.out.println(program.length() + ":" + program);
        System.out.println(String.join("", target.getKeysPressed()));
        System.out.println(String.join("", robot1.getKeysPressed()));
        System.out.println(String.join("", robot2.getKeysPressed()));
    }

    @ParameterizedTest
    @MethodSource("sampleSequences")
    void fullSequences(final String code, final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String program = year2024Day21.fullSequence(code);
        final NumericKeypad target = new NumericKeypad("target");
        final DirectionalKeypad robot1 = new DirectionalKeypad("robot1", 1, null, target);
        final DirectionalKeypad robot2 = new DirectionalKeypad("robot2", 2, robot1, null);

        // when
        robot2.run(program);

        // then
        assertEquals(code, String.join("", target.getKeysPressed()));
    }

    @Test
    void buildStateForNumpad() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String from = "3";
        final String to = "4";
        final Year2024Day21.State expected = new Year2024Day21.State();
        expected.initialLocation = from;
        expected.finalLocation = to;
        expected.requiredPress = to; // this may / may not be redundant ?
        expected.presses = "^<<A";

        // when
        final Year2024Day21.State actual = year2024Day21.buildStateForNumpad(from, to);

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("numberMovement")
    void buildPressesForNumberMovement(final String from, final String to, final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final String actual = year2024Day21.buildPressesForNumberMovement(from, to);

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("dirpadMovement")
    void buildStateForDirpad(final String from, final String to, final String expectedPresses) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final Year2024Day21.State expected = new Year2024Day21.State();
        expected.initialLocation = from;
        expected.finalLocation = to;
        expected.requiredPress = to; // this may / may not be redundant ?
        expected.presses = expectedPresses;

        // when
        final Year2024Day21.State actual = year2024Day21.buildStateForDirpad(from, to);

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("drivingRoutesForDirpad")
    void testDirpadDriving(final String program, final List<String> expectedController, final List<String> expectedTarget) {

        // given
        final DirectionalKeypad target = new DirectionalKeypad("target", 1, null, null);
        final DirectionalKeypad controller = new DirectionalKeypad("controller", 2, target, null);

        // when
        controller.run(program);

        // then
        assertEquals(expectedController, controller.getKeysPressed());
        System.out.println(controller.getKeysPressed());
        assertEquals(expectedTarget, target.getKeysPressed());
        System.out.println(target.getKeysPressed());
    }

    @ParameterizedTest
    @MethodSource("drivingRoutesForNumpad")
    void testNumpadDriving(final String program, final List<String> expectedController, final List<String> expectedTarget) {

        // given
        final NumericKeypad target = new NumericKeypad("target");
        final DirectionalKeypad controller = new DirectionalKeypad("controller", 0, null, target);

        // when
        controller.run(program);

        // then
        assertEquals(expectedController, controller.getKeysPressed());
        System.out.println(controller.getKeysPressed());
        assertEquals(expectedTarget, target.getKeysPressed());
        System.out.println(target.getKeysPressed());
    }

    static List<Arguments> numberMovement() {

        return List.of(
                Arguments.of("A", "3", "^A"),
                Arguments.of("A", "0", "<A"),
                Arguments.of("A", "2", "^<A"),
                Arguments.of("7", "A", ">>vvvA"),
                Arguments.of("4", "9", ">>^A"),
                Arguments.of("8", "1", "<vvA"),
                Arguments.of("A", "1", "^<<A"),
                Arguments.of("1", "A", ">>vA")
        );
    }

    static List<Arguments> dirpadMovement() {

        return List.of(
                Arguments.of("A", "^", "<A"),
                Arguments.of("A", "<", "v<<A"),
                Arguments.of("<", "A", ">>^A"),
                Arguments.of("v", "^", "^A"),
                Arguments.of("^", ">", "v>A")
        );
    }

    static List<Arguments> sampleSequences() {

        return List.of(
                Arguments.of("029A", "<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A"),
                Arguments.of("980A", "<v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A"),
                Arguments.of("179A", "<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A"),
                Arguments.of("456A", "<v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A"),
                Arguments.of("379A", "<v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A")
        );
    }

    static List<Arguments> drivingRoutesForDirpad() {

        return List.of(
                Arguments.of("A", List.of("A"), List.of("A")),
                Arguments.of("<v<A", List.of("<"), List.of()),
                Arguments.of("<v<A>>^A", List.of("<", "A"), List.of("^"))
        );
    }

    static List<Arguments> drivingRoutesForNumpad() {

        return List.of(
                Arguments.of("A", List.of("A"), List.of("A")),
                Arguments.of("<A", List.of("^"), List.of()),
                Arguments.of("<A>A", List.of("^", "A"), List.of("3"))
        );
    }

    static List<Arguments> testingOutputToGetCode() {

        // these appear to both be valid, but mine is longer
        return List.of(
                Arguments.of("<v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A", "379A"),
                Arguments.of("v<<A>>^AvA^Av<<A>>^AAv<A<A>>^AAvAA<^A>Av<A>^AA<A>Av<A<A>>^AAAvA<^A>A", "379A")
        );
    }
}
