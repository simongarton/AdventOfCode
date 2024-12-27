package com.simongarton.adventofcode.year2024;

import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.simongarton.adventofcode.year2024.day21.DirectionalKeypad;
import com.simongarton.adventofcode.year2024.day21.NumericKeypad;
import com.simongarton.adventofcode.year2024.day21.Radio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Year2024Day21Test {

    private static final int CHAR_WIDTH = 12;
    private static final int CHAR_HEIGHT = 24;

    private TerminalScreen screen;

    @ParameterizedTest
    @MethodSource("sampleSequences")
    void generateFullSequences(final String code, final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final String actual = year2024Day21.fullSequence(code);

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("sampleSequences")
    void generateFullSequencesAndJustCheckLength(final String code, final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();

        // when
        final String actual = year2024Day21.fullSequence(code);

        // then
        assertEquals(expected.length(), actual.length());
    }

    @ParameterizedTest
    @MethodSource("sampleSequences")
    void driveFullSequences(final String code, final String expected) {

        // given
        final NumericKeypad target = new NumericKeypad("target");
        final DirectionalKeypad robot1 = new DirectionalKeypad("robot1", 1, null, target);
        final DirectionalKeypad robot2 = new DirectionalKeypad("robot2", 2, robot1, null);

        // when
        Radio.resetTick();
        robot2.run(expected);

        // then
        assertEquals(code, String.join("", target.getKeysPressed()));
    }

    @ParameterizedTest
    @MethodSource("sampleSequences")
    void generateThenDriveFullSequencesAndJustCheckOutput(final String code, final String expected) {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String program = year2024Day21.fullSequence(code);
        final NumericKeypad target = new NumericKeypad("target");
        final DirectionalKeypad robot1 = new DirectionalKeypad("robot1", 1, null, target);
        final DirectionalKeypad robot2 = new DirectionalKeypad("robot2", 2, robot1, null);

        // when
        Radio.resetTick();
        robot2.run(program);

        // then
        assertEquals(code, String.join("", target.getKeysPressed()));
//        assertEquals(expected, program);
    }

    @ParameterizedTest
    @MethodSource("exploringOneDirpadOptions")
    void exploringOneDirpad(final String expected, final String program) {

        // this is just checking that my keypads talk together nicely, given I have a valid program

        // given
        final NumericKeypad target = new NumericKeypad("target");
        final DirectionalKeypad robot1 = new DirectionalKeypad("robot1", 1, null, target);

        // when
        Radio.resetTick();
        robot1.run(program);

        // then
        assertEquals(expected, String.join("", target.getKeysPressed()));
    }

    @ParameterizedTest
    @MethodSource("exploringTwoDirpadOptions")
    void exploringTwoDirpads(final String expected, final String program) {

        // this is just checking that my keypads all talk together nicely, given I have a valid program

        // given
        final NumericKeypad target = new NumericKeypad("target");
        final DirectionalKeypad robot1 = new DirectionalKeypad("robot1", 1, null, target);
        final DirectionalKeypad robot2 = new DirectionalKeypad("robot2", 2, robot1, null);

//        this.setUpLanterna(64, 12);
//
//        target.setScreen(this.screen, 2, 7);
//        robot1.setScreen(this.screen, 12, 8);
//        robot2.setScreen(this.screen, 22, 9);

        // when
        Radio.resetTick();
        robot2.run(program);

        // then
        assertEquals(expected, String.join("", target.getKeysPressed()));
    }


    @ParameterizedTest
    @MethodSource("testingOutputToGetCode")
    void runningProgramsAndCheckingKeysPressed(final String program, final String code) {

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

    @Test
    void simpleBuildingTest() {

        // given
        final Year2024Day21 year2024Day21 = new Year2024Day21();
        final String code = "179A";
        final String expected = "<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A";
//        final String expected = "<vA<AA>>^AAvA<^A>AvA^A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A";

        // when
        final String actual = year2024Day21.fullSequence(code);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void simpleDrivingTest() {

        // this is just checking that my keypads all talk together nicely, given I have a valid program

        // given
        final NumericKeypad target = new NumericKeypad("target");
        final DirectionalKeypad robot1 = new DirectionalKeypad("robot1", 1, null, target);
        final DirectionalKeypad robot2 = new DirectionalKeypad("robot2", 2, robot1, null);
        final String program = "<vA<AA>>^AvAA<^A>A";
        final String expected = "0";

        // when
        Radio.resetTick();
        robot2.run(program);

        // then
        assertEquals(expected, String.join("", target.getKeysPressed()));
    }

    @Test
    void simpleDrivingTestAnimated() {

        // this is just checking that my keypads all talk together nicely, given I have a valid program

        // given
        final NumericKeypad target = new NumericKeypad("target");
        final DirectionalKeypad robot1 = new DirectionalKeypad("robot1", 1, null, target);
        final DirectionalKeypad robot2 = new DirectionalKeypad("robot2", 2, robot1, null);
        // lovely
        final String program = "<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A";
//        final String program = "<vA<AA>>^AAvA<^A>AvA^A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A";
        final String expected = "179A";

//        this.setUpLanterna(64, 16);
//
//        target.setScreen(this.screen, 2, 7);
//        robot1.setScreen(this.screen, 12, 8);
//        robot2.setScreen(this.screen, 22, 9);

        // when
        Radio.resetTick();
        robot2.run(program);

        // then
        assertEquals(expected, String.join("", target.getKeysPressed()));
    }

    static List<Arguments> numberMovement() {

        return List.of(
                Arguments.of("A", "3", "^A"),
                Arguments.of("A", "0", "<A"),
                Arguments.of("A", "2", "^<A"),
                Arguments.of("7", "A", ">>vvvA"),
                Arguments.of("4", "9", ">>^A"),
                Arguments.of("8", "1", "vv<A"),
                Arguments.of("A", "1", "^<<A"),
                Arguments.of("1", "A", ">>vA")
        );
    }

    // from the sample on the web page
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

    static List<Arguments> exploringOneDirpadOptions() {

        return List.of(
                Arguments.of("0", "v<<A>>^A"),
                Arguments.of("3", "<A>A"),
                Arguments.of("2", "v<<A>^A>A"),
                Arguments.of("23", "v<<A>^A>AvA^A")
        );
    }

    static List<Arguments> exploringTwoDirpadOptions() {

//        Expected :v<A<AA>>^AvAA<^A>A
//        Actual   :v<A<AA>>^AAA^<A>A


        return List.of(
                Arguments.of("0", "v<A<AA>>^AvAA<^A>A"), // from inspection

                // these are the samples (again)
                Arguments.of("029A", "<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A"),
                Arguments.of("980A", "<v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A"),
                Arguments.of("179A", "<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A"),
                Arguments.of("456A", "<v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A"),
                Arguments.of("379A", "<v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A")

        );
    }

    private void setUpLanterna(final int width, final int height) {

        try {
            final Terminal terminal = new DefaultTerminalFactory().createTerminal();
            ((SwingTerminalFrame) terminal).setTitle(this.getClass().getSimpleName());
            ((SwingTerminalFrame) terminal).setSize(width * CHAR_WIDTH, height * CHAR_HEIGHT);
            this.screen = new TerminalScreen(terminal);
            this.screen.setCursorPosition(null);
            this.screen.startScreen();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
