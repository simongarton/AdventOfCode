package com.simongarton.adventofcode.year2019;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2019Day5 extends AdventOfCodeChallenge {

    private List<Integer> memory;
    private Integer output;
    private static final boolean DEBUG = true;

    @Override
    public Outcome run() {
        return this.runChallenge(2019, 5);
    }

    @Override
    public String part1(final String[] input) {

        this.loadProgram(input);
        this.runProgram(1);
        return String.valueOf(this.output);
    }

    @Override
    public String part2(final String[] input) {

        this.loadProgram(input);
        this.runProgram(5);
        return String.valueOf(this.output);
    }

    private void loadProgram(final String[] input) {
        this.memory = Arrays.stream(input[0].split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    private void debugPrint(final String message) {
        if (DEBUG) {
            System.out.println(message);
        }
    }

    private void runProgram(final int input) {
        int pointer = 0;
        while (true) {
            System.out.println(this.showMemory());
            final String instruction = String.valueOf(this.memory.get(pointer));
            final Operation operation = new Operation(pointer, instruction);
            if (operation.type == OperationType.HALT) {
                break;
            }
            final int result;
            final int first;
            final int second;
            final int target;
            switch (operation.type) {
                case ADDITION:
                    first = operation.get(this.memory, 1);
                    second = operation.get(this.memory, 2);
                    result = first + second;
                    target = this.memory.get(pointer + 3);
                    this.debugPrint(operation + "(" + first + "+" + second + ")=" + result + " -> " + target);
                    this.memory.set(target, result);
                    pointer += 4;
                    break;
                case MULTIPLICATION:
                    first = operation.get(this.memory, 1);
                    second = operation.get(this.memory, 2);
                    result = first * second;
                    target = this.memory.get(pointer + 3);
                    this.debugPrint(operation + "(" + first + "*" + second + ")=" + result + " -> " + target);
                    this.memory.set(target, result);
                    pointer += 4;
                    break;
                case INPUT:
                    target = this.memory.get(pointer + 1);
                    this.debugPrint(operation + "input " + input + " -> " + target);
                    this.memory.set(target, input);
                    pointer += 2;
                    break;
                case OUTPUT:
                    this.output(operation.get(this.memory, 1));
                    this.debugPrint(operation + "output " + this.output);
                    pointer += 2;
                    break;
                case JUMP_IF_TRUE:
                    first = operation.get(this.memory, 1);
                    second = operation.get(this.memory, 2);
                    if (first != 0) {
                        this.debugPrint(operation + "true to " + second);
                        pointer = second;
                    } else {
                        this.debugPrint(operation + "false to " + pointer + 3);
                        pointer += 3;
                    }
                    break;
                case JUMP_IF_FALSE:
                    first = operation.get(this.memory, 1);
                    second = operation.get(this.memory, 2);
                    if (first == 0) {
                        this.debugPrint(operation + "true to " + second);
                        pointer = second;
                    } else {
                        this.debugPrint(operation + "false to " + pointer + 3);
                        pointer += 3;
                    }
                    break;
                case LESS_THAN:
                    first = operation.get(this.memory, 1);
                    second = operation.get(this.memory, 2);
                    target = this.memory.get(pointer + 3);
                    if (first < second) {
                        this.debugPrint(operation + "true set 1");
                        this.memory.set(target, 1);
                    } else {
                        this.debugPrint(operation + "false set 0");
                        this.memory.set(target, 0);
                    }
                    pointer += 4;
                    break;
                case EQUALS:
                    first = operation.get(this.memory, 1);
                    second = operation.get(this.memory, 2);
                    target = this.memory.get(pointer + 3);
                    if (first == second) {
                        this.debugPrint(operation + "true set 1");
                        this.memory.set(target, 1);
                    } else {
                        this.debugPrint(operation + "false set 0");
                        this.memory.set(target, 0);
                    }
                    pointer += 4;
                    break;
            }
        }
    }

    private String showMemory() {
        final String result = this.memory.stream().map(i -> String.valueOf(i)).collect(Collectors.joining(","));
        return result;
    }

    private void output(final Integer value) {
        this.output = value;
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
            return this.pointer + ":" + this.type + " [" + this.modes.stream().map(Object::toString).collect(Collectors.joining(",")) + "] ";
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
