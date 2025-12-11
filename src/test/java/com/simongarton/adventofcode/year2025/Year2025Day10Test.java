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
        machine.pressButtonLights(0);
        machine.pressButtonLights(1);
        machine.pressButtonLights(2);
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
        machine.pressButtonLights(1);
        machine.pressButtonLights(3);
        machine.pressButtonLights(5);
        machine.pressButtonLights(5);
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
        machine.pressButtonLights(0);
        machine.pressButtonLights(2);
        machine.pressButtonLights(3);
        machine.pressButtonLights(4);
        machine.pressButtonLights(5);
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
        machine.pressButtonLights(2);
        machine.pressButtonLights(3);
        machine.pressButtonLights(4);
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
        machine.pressButtonLights(1);
        machine.pressButtonLights(2);
        System.out.println(machine.state() + " -> " + machine.targetState());

        // then
        assertEquals(machine.state(), machine.targetState());
    }

    @Test
    void test1_1_1() {

        // this is theirs which works in 10

        // given
        final Year2025Day10.Machine machine = this.getMachine1();

        // when
        System.out.println(machine.joltageState() + " -> " + machine.targetJoltageState());
        final List<Integer> presses = List.of(0, 1, 1, 1, 3, 3, 3, 4, 5, 5);
        for (final Integer press : presses) {
            final String state = machine.joltageState();
            machine.pressButtonJoltage(press);
            System.out.println("\t" + press + ": " + state + " -> " + machine.joltageState() + " " + machine.getButtons().get(press).getCircuits());

        }
        System.out.println(machine.joltageState() + " -> " + machine.targetJoltageState());

        // then
        assertEquals(machine.joltageState(), machine.targetJoltageState());
    }

    @Test
    void test1_1_2() {

        // this is mine which works, but does take 13, not 10 steps

        // given
        final Year2025Day10.Machine machine = this.getMachine1();

        // when
        System.out.println(machine.joltageState() + " -> " + machine.targetJoltageState());
        final List<Integer> presses = List.of(0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 5, 5, 4);
        for (final Integer press : presses) {
            final String state = machine.joltageState();
            machine.pressButtonJoltage(press);
            System.out.println("\t" + press + ": " + state + " -> " + machine.joltageState() + " " + machine.getButtons().get(press).getCircuits());

        }
        System.out.println(machine.joltageState() + " -> " + machine.targetJoltageState());

        // then
        assertEquals(machine.joltageState(), machine.targetJoltageState());
    }

    @Test
    void solve_part1_8() {

        // given
        final Year2025Day10 year2025Day10 = new Year2025Day10();
        final Year2025Day10.Machine machine = this.getMachine1_8();

        // when
        final Year2025Day10.Node node = year2025Day10.minimumPressesLights(machine);

        // then
        System.out.println(node.totalPresses());

    }

    @Test
    void solve_part2_1() {

        // given
        final Year2025Day10 year2025Day10 = new Year2025Day10();
        final Year2025Day10.Machine machine = this.getMachine1();

        // when
        final long start = System.currentTimeMillis();
        final Year2025Day10.Node node = year2025Day10.minimumPressesJoltage(machine);

        // then
        System.out.println(node.totalPresses() + (" in " + (System.currentTimeMillis() - start)));

    }

    @Test
    void solve_part2_1_button_order() {

        // given
        final Year2025Day10 year2025Day10 = new Year2025Day10();
//        final String machineData = "[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}";
        final String machineData = "[.##.] (0,1) (0,2) (1,3) (3) (2) (2,3) {3,5,4,7}";
        final Year2025Day10.Machine machine = year2025Day10.parseLine(0, machineData);

        // when
        final long start = System.currentTimeMillis();
        final Year2025Day10.Node node = year2025Day10.minimumPressesJoltage(machine);

        // then
        System.out.println(node.totalPresses() + (" in " + (System.currentTimeMillis() - start)));
    }

    @Test
    void validate_z3_sample_1() {

        // given
        final Year2025Day10 year2025Day10 = new Year2025Day10();
        final String machineData = "[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}";
        final Year2025Day10.Machine machine = year2025Day10.parseLine(0, machineData);

        // when
        for (final Integer buttonToPress : List.of(0, 1, 1, 1, 1, 1, 3, 4, 4, 4)) {
            machine.pressButtonJoltage(buttonToPress);
        }

        // then
        assertEquals("3,5,4,7", machine.joltageState());
    }

    private Year2025Day10.Machine getMachine1_8() {

        final boolean[] lights = new boolean[]{true, false, false, false, true, false, true, false, true};
        final int[] joltages = new int[]{30, 54, 42, 24, 47, 24, 34, 49, 68};
        final Year2025Day10.Machine machine = new Year2025Day10.Machine(0, lights, joltages);
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
        final int[] joltages = new int[]{3, 5, 4, 7};
        final Year2025Day10.Machine machine = new Year2025Day10.Machine(0, lights, joltages);
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
        final int[] joltages = new int[]{7, 5, 12, 7, 2};
        final Year2025Day10.Machine machine = new Year2025Day10.Machine(0, lights, joltages);
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
        final int[] joltages = new int[]{10, 11, 11, 5, 10, 5};
        final Year2025Day10.Machine machine = new Year2025Day10.Machine(0, lights, joltages);
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