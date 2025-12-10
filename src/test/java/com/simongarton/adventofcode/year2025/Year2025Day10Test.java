package com.simongarton.adventofcode.year2025;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Year2025Day10Test {

    @Test
    void test1_1() {

        // given
        final Year2025Day10.Machine machine = this.getMachine1();

        // when
        System.out.println(machine.state() + " -> " + machine.targetState());
        machine.pressButton(0);
        machine.pressButton(1);
        machine.pressButton(2);
        System.out.println(machine.state() + " -> " + machine.targetState());

        // then
        assertEquals(machine.state(), machine.targetState());
    }

    @Test
    void test1_2() {

        // given
        final Year2025Day10.Machine machine = this.getMachine1();

        // when
        System.out.println(machine.state() + " -> " + machine.targetState());
        machine.pressButton(1);
        machine.pressButton(3);
        machine.pressButton(5);
        machine.pressButton(5);
        System.out.println(machine.state() + " -> " + machine.targetState());

        // then
        assertEquals(machine.state(), machine.targetState());
    }

    @Test
    void test1_3() {

        // given
        final Year2025Day10.Machine machine = this.getMachine1();

        // when
        System.out.println(machine.state() + " -> " + machine.targetState());
        machine.pressButton(0);
        machine.pressButton(2);
        machine.pressButton(3);
        machine.pressButton(4);
        machine.pressButton(5);
        System.out.println(machine.state() + " -> " + machine.targetState());

        // then
        assertEquals(machine.state(), machine.targetState());
    }

    @Test
    void test2_1() {

        // given
        final Year2025Day10.Machine machine = this.getMachine2();

        // when
        System.out.println(machine.state() + " -> " + machine.targetState());
        machine.pressButton(2);
        machine.pressButton(3);
        machine.pressButton(4);
        System.out.println(machine.state() + " -> " + machine.targetState());

        // then
        assertEquals(machine.state(), machine.targetState());
    }

    @Test
    void test3_1() {

        // given
        final Year2025Day10.Machine machine = this.getMachine3();

        // when
        System.out.println(machine.state() + " -> " + machine.targetState());
        machine.pressButton(1);
        machine.pressButton(2);
        System.out.println(machine.state() + " -> " + machine.targetState());

        // then
        assertEquals(machine.state(), machine.targetState());
    }

    @Test
    void solve_part1_8() {

        // given
        final Year2025Day10 year2025Day10 = new Year2025Day10();
        final Year2025Day10.Machine machine = this.getMachine1_8();

        // when
        final Year2025Day10.Node node = year2025Day10.minimumPresses(machine);

        // then
        System.out.println(node.totalPresses());

    }

    private Year2025Day10.Machine getMachine1_8() {

        final boolean[] lights = new boolean[]{true, false, false, false, true, false, true, false, true};
        final Year2025Day10.Machine machine = new Year2025Day10.Machine(0, lights);
        final List<Year2025Day10.Button> buttons = new ArrayList<>();
        buttons.add(new Year2025Day10.Button(0, List.of(1, 4)));
        buttons.add(new Year2025Day10.Button(1, List.of(0, 1, 4, 5, 7, 8)));
        buttons.add(new Year2025Day10.Button(2, List.of(1, 2, 3, 4, 6, 7, 8)));
        buttons.add(new Year2025Day10.Button(3, List.of(2, 4)));
        buttons.add(new Year2025Day10.Button(4, List.of(5, 8)));
        buttons.add(new Year2025Day10.Button(5, List.of(0, 1, 2, 6, 7, 8)));
        buttons.add(new Year2025Day10.Button(6, List.of(0, 1, 4, 7, 8)));
        buttons.add(new Year2025Day10.Button(7, List.of(1, 2, 3, 5, 6, 8)));
        for (final Year2025Day10.Button button : buttons) {
            button.setMachine(machine);
            machine.getButtons().add(button);
        }
        return machine;
    }

    private Year2025Day10.Machine getMachine1() {

        final boolean[] lights = new boolean[]{false, true, true, false};
        final Year2025Day10.Machine machine = new Year2025Day10.Machine(0, lights);
        final List<Year2025Day10.Button> buttons = new ArrayList<>();
        buttons.add(new Year2025Day10.Button(0, List.of(3)));
        buttons.add(new Year2025Day10.Button(1, List.of(1, 3)));
        buttons.add(new Year2025Day10.Button(2, List.of(2)));
        buttons.add(new Year2025Day10.Button(3, List.of(2, 3)));
        buttons.add(new Year2025Day10.Button(4, List.of(0, 2)));
        buttons.add(new Year2025Day10.Button(5, List.of(0, 1)));
        for (final Year2025Day10.Button button : buttons) {
            button.setMachine(machine);
            machine.getButtons().add(button);
        }
        return machine;
    }

    private Year2025Day10.Machine getMachine2() {

        final boolean[] lights = new boolean[]{false, false, false, true, false};
        final Year2025Day10.Machine machine = new Year2025Day10.Machine(0, lights);
        final List<Year2025Day10.Button> buttons = new ArrayList<>();
        buttons.add(new Year2025Day10.Button(0, List.of(0, 2, 3, 4)));
        buttons.add(new Year2025Day10.Button(1, List.of(2, 3)));
        buttons.add(new Year2025Day10.Button(2, List.of(0, 4)));
        buttons.add(new Year2025Day10.Button(3, List.of(0, 1, 2)));
        buttons.add(new Year2025Day10.Button(4, List.of(1, 2, 3, 4)));
        for (final Year2025Day10.Button button : buttons) {
            button.setMachine(machine);
            machine.getButtons().add(button);
        }
        return machine;
    }

    private Year2025Day10.Machine getMachine3() {

        final boolean[] lights = new boolean[]{false, true, true, true, false, true};
        final Year2025Day10.Machine machine = new Year2025Day10.Machine(0, lights);
        final List<Year2025Day10.Button> buttons = new ArrayList<>();
        buttons.add(new Year2025Day10.Button(0, List.of(0, 1, 2, 3, 4)));
        buttons.add(new Year2025Day10.Button(1, List.of(0, 3, 4)));
        buttons.add(new Year2025Day10.Button(2, List.of(0, 1, 2, 4, 5)));
        buttons.add(new Year2025Day10.Button(3, List.of(1, 2)));
        for (final Year2025Day10.Button button : buttons) {
            button.setMachine(machine);
            machine.getButtons().add(button);
        }
        return machine;
    }
}