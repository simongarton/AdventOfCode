package com.simongarton.adventofcode.year2024;

import org.junit.jupiter.api.Test;

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

}