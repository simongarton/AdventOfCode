package com.simongarton.adventofcode.year2019;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Year2019Day5 extends AdventOfCodeChallenge {

    /* not working and not really understanding. Need to redo this properly. */

    private List<Integer> memory;

    @Override
    public Outcome run() {
        return this.runChallenge(2019, 5);
    }

    @Override
    public String part1(final String[] input) {
        this.loadProgram(input);
        this.runProgram();
        return null;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    private void loadProgram(final String[] input) {
        this.memory = Arrays.stream(input[0].split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    private void runProgram() {
        int pointer = 0;
        while (true) {
            final String instruction = String.valueOf(this.memory.get(pointer));
            final Operation operation = new Operation(pointer, instruction);
            if (operation.type == OperationType.HALT) {
                break;
            }
            final int result;
            final int first;
            final int second;
            final int third;
            switch (operation.type) {
                case ADDITION:
                    first = operation.get(this.memory, 1);
                    second = operation.get(this.memory, 2);
                    result = first + second;
                    this.memory.set(this.memory.get(pointer + 3), result);
                    pointer += 4;
                    break;
                case MULTIPLICATION:
                    first = operation.get(this.memory, 1);
                    second = operation.get(this.memory, 2);
                    result = first * second;
                    this.memory.set(this.memory.get(pointer + 3), result);
                    pointer += 4;
                    break;
                case INPUT:
                    final int input = this.getInput();
                    this.memory.set(this.memory.get(pointer + 1), input);
                    pointer += 2;
                    break;
                case OUTPUT:
                    this.output(operation.get(this.memory, 1));
                    pointer += 2;
                    break;
                case JUMP_IF_TRUE:
                    first = operation.get(this.memory, 1);
                    second = operation.get(this.memory, 2);
                    if (first != 0) {
                        pointer = second;
                    } else {
                        pointer += 3;
                    }
                    break;
                case JUMP_IF_FALSE:
                    first = operation.get(this.memory, 1);
                    second = operation.get(this.memory, 2);
                    if (first == 0) {
                        pointer = second;
                    } else {
                        pointer += 3;
                    }
                    break;
                case LESS_THAN:
                    first = operation.get(this.memory, 1);
                    second = operation.get(this.memory, 2);
                    third = this.memory.get(pointer + 3);
                    if (first < second) {
                        this.memory.set(third, 1);
                    } else {
                        this.memory.set(third, 0);
                    }
                    pointer += 4;
                    break;
                case EQUALS:
                    first = operation.get(this.memory, 1);
                    second = operation.get(this.memory, 2);
                    third = this.memory.get(pointer + 3);
                    if (first == second) {
                        this.memory.set(third, 1);
                    } else {
                        this.memory.set(third, 0);
                    }
                    pointer += 4;
                    break;
            }
        }
    }


    private String getMemory(final Operation operation, final List<Integer> memory) {
        String line = "instruction " + operation.pointer + " ";
        line = line + "operation " + operation.type + " ";
        line = line + memory.get(operation.pointer + 1);
        if (operation.type == OperationType.ADDITION || operation.type == OperationType.MULTIPLICATION) {
            line = line + ", " + memory.get(operation.pointer + 2);
            line = line + ", " + memory.get(operation.pointer + 3);
        }
        return line;
    }

    private void output(final Integer value) {
        System.out.println(String.valueOf(value));
    }

    private int getInput() {
        if (true) {
            return 5;
        }
        System.out.println("Enter a number, then press return.");
        final Scanner in = new Scanner(System.in);
        return in.nextInt();
    }

    private void setInitial(final int one, final int two) {
        this.memory.set(1, one);
        this.memory.set(2, two);
    }

    @Getter
    public enum OperationType {
        ADDITION(1),
        MULTIPLICATION(2),
        INPUT(3),
        OUTPUT(4),
        JUMP_IF_TRUE(5),
        JUMP_IF_FALSE(6),
        LESS_THAN(7),
        EQUALS(8),
        HALT(99);

        private final int operationCode;

        OperationType(final int operationCode) {
            this.operationCode = operationCode;
        }

        static OperationType fromCode(final int operationCode) {
            for (final OperationType operationType : values()) {
                if (operationType.getOperationCode() == operationCode) {
                    return operationType;
                }
            }
            return null;
        }
    }

    @Data
    private static class Operation {

        private int pointer;
        private OperationType type;
        private List<Integer> modes = new ArrayList<>();

        public Operation(final int pointer, final String instruction) {
            this.pointer = pointer;
            final int length = instruction.length();
            final int operationCode = length > 1 ? Integer.parseInt(instruction.substring(length - 2, length)) : Integer.parseInt(instruction);
            this.type = OperationType.fromCode(operationCode);
            if (length > 0) {
                for (int i = length - 3; i >= 0; i--) {
                    this.modes.add(Integer.parseInt(instruction.substring(i, i + 1)));
                }
            }
        }

        @Override
        public String toString() {
            return this.pointer + ":" + this.type + " " + this.modes.stream().map(Object::toString).collect(Collectors.joining(","));
        }

        public int get(final List<Integer> memory, final int i) {
            final int val = memory.get(this.pointer + i);
            if (this.modes.isEmpty() || this.modes.size() < i || this.modes.get(i - 1) == 0) {
                return memory.get(val);
            }
            return val;
        }
    }
}
