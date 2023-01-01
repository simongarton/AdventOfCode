package com.simongarton.adventofcode.year2020;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

import static com.simongarton.adventofcode.year2020.Year2020Day8.InstructionType.*;

public class Year2020Day8 extends AdventOfCodeChallenge {

    private Computer computer;

    @Override
    public String title() {
        return "Day 8: Handheld Halting";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2020, 8);
    }

    @Override
    public String part1(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        this.buildComputer(lines);
        this.computer.run();
        return String.valueOf(this.computer.accumulator);
    }

    @Override
    public String part2(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        this.buildComputer(lines);
        return String.valueOf(this.computer.findBadInstruction(lines));
    }


    private void buildComputer(final List<String> lines) {
        this.computer = new Computer();
        this.computer.loadProgram(lines);
    }

    public enum InstructionType {
        NOP,
        ACC,
        JMP
    }

    @Getter
    @AllArgsConstructor
    public static class Instruction {

        private InstructionType type;
        private final int value;
    }

    public static class Computer {

        private final List<Instruction> instructions;
        private int pointer;
        private int accumulator;
        private final Set<Integer> visited;

        public Computer() {
            this.instructions = new ArrayList<>();
            this.visited = new HashSet<>();
        }

        public boolean run() {
            while (true) {
                if (this.pointer >= this.instructions.size()) {
                    //System.out.printf("finished with p %d a %d\n", pointer, accumulator);
                    return true;
                }
                final Instruction instruction = this.instructions.get(this.pointer);
                //System.out.printf("p %d a %d i %s v %d\n", pointer, accumulator, instruction.type, instruction.value);
                if (this.visited.contains(this.pointer)) {
                    //System.out.printf("loop with p %d a %d\n", pointer, accumulator);
                    return false;
                }
                this.visited.add(this.pointer);
                switch (instruction.type) {
                    case NOP:
                        this.pointer++;
                        break;
                    case ACC:
                        this.accumulator += instruction.value;
                        this.pointer++;
                        break;
                    case JMP:
                        this.pointer += instruction.value;
                        break;
                }
            }
        }

        public Integer findBadInstruction(final List<String> lines) {
            final Integer result = this.test(lines, NOP, JMP);
            if (result == null) {
                return this.test(lines, JMP, NOP);
            } else {
                return result;
            }
        }

        private Integer test(final List<String> lines, final InstructionType from, final InstructionType to) {
            for (int i = 0; i < lines.size(); i++) {
                this.loadProgram(lines);
                final Instruction instruction = this.instructions.get(i);
                if (instruction.type != from) {
                    continue;
                }
                instruction.type = to;
                final boolean outcome = this.run();
                if (outcome) {
                    return this.accumulator;
                }
            }
            return null;
        }

        public void loadProgram(final List<String> lines) {
            this.instructions.clear();
            this.visited.clear();
            lines.forEach(this::processLine);
            this.pointer = 0;
            this.accumulator = 0;
        }

        private void processLine(final String line) {
            final String[] parts = line.split(" ");
            final int value = Integer.parseInt(parts[1]);
            switch (parts[0]) {
                case "nop":
                    this.instructions.add(new Instruction(NOP, value));
                    break;
                case "acc":
                    this.instructions.add(new Instruction(ACC, value));
                    break;
                case "jmp":
                    this.instructions.add(new Instruction(JMP, value));
                    break;
                default:
                    throw new RuntimeException("unrecognised instruction " + parts[0]);
            }
        }
    }
}
