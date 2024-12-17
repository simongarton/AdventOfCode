package com.simongarton.adventofcode.year2024;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChronospatialComputer {

    private static final boolean DEBUG = true;

    private int instructionPointer;
    private final List<Integer> program;
    @Getter
    private final List<Integer> output;
    @Getter
    @Setter
    private int registerA;
    @Getter
    @Setter
    private int registerB;
    @Getter
    @Setter
    private int registerC;

    private final List<String> instructions = List.of(
            "adv",
            "bxl",
            "bst",
            "jnz",
            "bxc",
            "out",
            "bdv",
            "adv"
    );

    public ChronospatialComputer(final List<Integer> program) {
        this.instructionPointer = 0;
        this.program = new ArrayList<>();
        this.program.addAll(program);
        this.output = new ArrayList<>();
    }

    public ChronospatialComputer(final String program) {

        this(Arrays.stream(program.split(",")).map(Integer::parseInt).collect(Collectors.toList()));
    }

    public void run() {

        while (true) {

            if (this.instructionPointer > this.program.size()) {
                throw new RuntimeException("overrun");
            }
            if (this.instructionPointer == this.program.size()) {
                break;
            }

            this.tick();
        }
    }

    private void tick() {

        final int opcode = this.program.get(this.instructionPointer);
        final int operand = this.program.get(this.instructionPointer + 1);

        this.debugMessage(String.format("read %s (%s) with %s", opcode, this.opcodeInstruction(opcode), operand));

        final boolean jump = this.handleInstruction(opcode, operand);

        if (!jump) {
            this.instructionPointer = this.instructionPointer + 2;
        }

        this.debugMessage(this.statusLine());
    }

    public String getOutputString() {

        final List<String> outputString = this.output.stream().map(String::valueOf).collect(Collectors.toList());
        return String.join(",", outputString);
    }

    private String statusLine() {

        final StringBuilder status = new StringBuilder(this.instructionPointer);
        status.append(" ");
        status.append(" A:").append(this.registerA);
        status.append(" B:").append(this.registerB);
        status.append(" C:").append(this.registerC);
        final List<String> outputString = this.output.stream().map(String::valueOf).collect(Collectors.toList());
        status.append(" output:").append(this.getOutputString());
        return status.toString();
    }

    private void debugMessage(final String message) {
        if (!DEBUG) {
            return;
        }
        System.out.println(message);
    }

    private int comboOperand(final int operand) {
        switch (operand) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return this.registerA;
            case 5:
                return this.registerB;
            case 6:
                return this.registerC;
            default:
                throw new RuntimeException("Bad operand " + operand);
        }
    }

    private boolean handleInstruction(final int opcode, final int operand) {

        switch (opcode) {
            case 0:
                return this.adv(opcode, operand);
            case 1:
                return this.bxl(opcode, operand);
            case 2:
                return this.bst(opcode, operand);
            case 3:
                return this.jnz(opcode, operand);
            case 4:
                return this.bxc(opcode, operand);
            case 5:
                return this.out(opcode, operand);
            case 6:
                return this.bdv(opcode, operand);
            case 7:
                return this.cdv(opcode, operand);
            default:
                throw new RuntimeException("Bad opcode " + opcode);
        }
    }

    private boolean cdv(final int opcode, final int operand) {

        final double numerator = this.registerA;
        final double denominator = Math.pow(2, operand);
        final Double result = numerator / denominator;
        this.registerC = result.intValue();

        this.debugMessage(String.format("  set C to %s", this.registerC));
        return false;
    }

    private boolean bdv(final int opcode, final int operand) {

        final double numerator = this.registerA;
        final double denominator = Math.pow(2, operand);
        final Double result = numerator / denominator;
        this.registerB = result.intValue();

        this.debugMessage(String.format("  set B to %s", this.registerB));
        return false;

    }

    private boolean out(final int opcode, final int operand) {

        final int result = this.comboOperand(operand) % 8;
        this.output.add(result);

        this.debugMessage(String.format("  added %s to output", result));
        return false;
    }

    private boolean bxc(final int opcode, final int operand) {

        final int b = this.registerB;
        final int c = this.registerC;
        final int result = b ^ c;
        this.registerB = result;

        this.debugMessage(String.format("  set B to %s", this.registerB));
        return false;

    }

    private boolean jnz(final int opcode, final int operand) {

        if (this.registerA == 0) {
            this.debugMessage(String.format("  did not jump"));
            return false;
        }

        this.instructionPointer = operand;
        this.debugMessage(String.format("  jumped to %s", operand));
        return true;
    }

    private boolean bst(final int opcode, final int operand) {

        final int result = this.comboOperand(operand) % 8;
        this.registerB = result;

        this.debugMessage(String.format("  set B to %s", this.registerB));
        return false;
    }

    private boolean bxl(final int opcode, final int operand) {

        final int b = this.registerB;
        final int o = operand; // literal, not combo
        final int result = b ^ o;
        this.registerB = result;

        this.debugMessage(String.format("  set B to %s", this.registerB));
        return false;
    }

    private boolean adv(final int opcode, final int operand) {

        final double numerator = this.registerA;
        final double denominator = Math.pow(2, operand);
        final Double result = numerator / denominator;
        this.registerA = result.intValue();

        this.debugMessage(String.format("  set A to %s", this.registerA));

        return false;
    }

    private String opcodeInstruction(final int opcode) {
        return this.instructions.get(opcode);
    }
}
