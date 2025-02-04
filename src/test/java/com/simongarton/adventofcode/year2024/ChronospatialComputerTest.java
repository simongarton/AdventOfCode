package com.simongarton.adventofcode.year2024;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChronospatialComputerTest {

    @Test
    void test1() {

        // given
        final List<Integer> program = List.of(2, 6);
        final ChronospatialComputer computer = new ChronospatialComputer(program);
        computer.setRegisterC(9);

        // when
        computer.run();

        // then
        assertEquals(1, computer.getRegisterB());
    }

    @Test
    void test2() {

        // given
        final List<Integer> program = List.of(5, 0, 5, 1, 5, 4);
        final ChronospatialComputer computer = new ChronospatialComputer(program);
        computer.setRegisterA(10);

        // when
        computer.run();

        // then
        assertEquals(List.of(0, 1, 2), computer.getOutput());
    }

    @Test
    void test3() {

        // given
        final List<Integer> program = List.of(0, 1, 5, 4, 3, 0);
        final ChronospatialComputer computer = new ChronospatialComputer(program);
        computer.setRegisterA(2024);

        // when
        computer.run();

        // then
        assertEquals(List.of(4, 2, 5, 6, 7, 7, 7, 7, 3, 1, 0), computer.getOutput());
        assertEquals(0, computer.getRegisterA());
    }

    @Test
    void test4() {

        // given
        final List<Integer> program = List.of(1, 7);
        final ChronospatialComputer computer = new ChronospatialComputer(program);
        computer.setRegisterB(29);

        // when
        computer.run();

        // then
        assertEquals(26, computer.getRegisterB());
    }

    @Test
    void test5() {

        // given
        final List<Integer> program = List.of(4, 0);
        final ChronospatialComputer computer = new ChronospatialComputer(program);
        computer.setRegisterB(2024);
        computer.setRegisterC(43690);

        // when
        computer.run();

        // then
        assertEquals(44354, computer.getRegisterB());
    }

    @Test
    void test6() {

        // this is the sample

        // given
        final List<Integer> program = List.of(0, 1, 5, 4, 3, 0);
        final ChronospatialComputer computer = new ChronospatialComputer(program);
        computer.setRegisterA(729);
        computer.setRegisterB(0);
        computer.setRegisterC(0);

        // when
        computer.run();

        // then
        assertEquals("4,6,3,5,6,3,5,2,1,0", computer.getOutputString());
    }

    @ParameterizedTest
    @MethodSource(value = "readTestFiles")
    void testFile(final String filename, final String expectedOutput) throws IOException {

        // given
        final ChronospatialComputer computer = ChronospatialComputer.initialiseFromFile(filename);

        // when
        computer.run();

        // then
        assertEquals(expectedOutput, computer.getOutputString());

    }

    @Test
    void hacking() {
        for (int i = 0; i <= 20; i++) {
            System.out.println(i + ": " + (i ^ 1));
        }
    }

    static List<Arguments> readTestFiles() {

        return List.of(
                Arguments.of("src/test/resources/2024-17/sample1.txt", "4,6,3,5,6,3,5,2,1,0"),
                Arguments.of("src/test/resources/2024-17/adv-1.txt", "5"),
                Arguments.of("src/test/resources/2024-17/bxl-1.txt", "6"),
                Arguments.of("src/test/resources/2024-17/bst-1.txt", "3"),
                Arguments.of("src/test/resources/2024-17/bst-2.txt", "4"),
                Arguments.of("src/test/resources/2024-17/jnz-1.txt", "5"),
                Arguments.of("src/test/resources/2024-17/bxc-1.txt", "6"),
                Arguments.of("src/test/resources/2024-17/out-1.txt", "5"),
                Arguments.of("src/test/resources/2024-17/bdv-1.txt", "5"),
                Arguments.of("src/test/resources/2024-17/cdv-1.txt", "5")
        );
    }
}