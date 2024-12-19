package com.simongarton.adventofcode.year2024;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChronospatialComputer {

    private static final boolean DEBUG = false;

    private int instructionPointer;
    private final List<Integer> program;
    @Getter
    private final List<Long> output;
    @Getter
    @Setter
    private long registerA;
    @Getter
    @Setter
    private long registerB;
    @Getter
    @Setter
    private long registerC;

    private final List<String> instructions = List.of(
            "adv", // 0, division A/2**operand, store in A
            "bxl", // 1, XOR B + operand, store in B
            "bst", // 2, mod operand, store in B
            "jnz", // 3, jump if A non zero
            "bxc", // 4, XOR B + C, store in B
            "out", // 5, out operand mod 8
            "bdv", // 6, division A/2**operand, store in B
            "cdv"  // 7, division A/2**operand, store in C
    );

    // so the only way to write values to A or C, is to get the value into A and divide by 2**operand

    /*

    Part 1 program : 2,4,1,1,7,5,4,4,1,4,0,3,5,5,3,0
    2,4 - mod A by 8, store in B.
    1,1 - XOR B and 1, store in B (mod by 10, if even add 1 else sub 1)
    7,5 - divide A by 2**B and store in C
    4,4 - XOR B and C, store in B
    1,4 - XOR B and 4, store in B
    0,3 - divide A by 2**3 (8) and store in A
    5,5 - output B
    3,0 - if A > 0, jump to 0

     */

    public ChronospatialComputer(final List<Integer> program) {

        this.instructionPointer = 0;
        this.program = new ArrayList<>();
        this.program.addAll(program);
        this.output = new ArrayList<>();

        this.debugMessage(String.format("Initialized with %s", this.getProgramString()));
    }

    public ChronospatialComputer(final String program) {

        this(Arrays.stream(program.split(",")).map(Integer::parseInt).collect(Collectors.toList()));
    }

    public static ChronospatialComputer initializeFromLines(final String[] input) {

        final long a = Long.parseLong(input[0].replace("Register A: ", ""));
        final long b = Long.parseLong(input[1].replace("Register B: ", ""));
        final long c = Long.parseLong(input[2].replace("Register C: ", ""));

        final String program = input[4].replace("Program: ", "");

        final ChronospatialComputer computer = new ChronospatialComputer(program);
        computer.setRegisterA(a);
        computer.setRegisterB(b);
        computer.setRegisterC(c);

        return computer;
    }

    public static ChronospatialComputer initialiseFromFile(final String filename) throws IOException {

        final List<String> data = Files.readAllLines(Path.of(filename));
        return ChronospatialComputer.initializeFromLines(data.toArray(new String[0]));
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

    public String getProgramString() {

        final List<String> outputString = this.program.stream().map(String::valueOf).collect(Collectors.toList());
        return String.join(",", outputString);
    }

    private String statusLine() {

        final StringBuilder status = new StringBuilder(this.instructionPointer);
        status.append(" ");
        status.append(" A:").append(this.registerA);
        status.append(" B:").append(this.registerB);
        status.append(" C:").append(this.registerC);
        status.append(" output:").append(this.getOutputString());
        return status.toString();
    }

    private void debugMessage(final String message) {

        if (!DEBUG) {
            return;
        }
        System.out.println(message);
    }

    private long comboOperand(final int operand) {

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
        final double denominator = Math.pow(2, this.comboOperand(operand));
        final Double result = numerator / denominator;
        this.registerC = result.longValue();

        this.debugMessage(String.format("  set C to %s", this.registerC));
        return false;
    }

    private boolean bdv(final int opcode, final int operand) {

        final double numerator = this.registerA;
        final double denominator = Math.pow(2, this.comboOperand(operand));
        final Double result = numerator / denominator;
        this.registerB = result.longValue();

        this.debugMessage(String.format("  set B to %s", this.registerB));
        return false;

    }

    private boolean out(final int opcode, final int operand) {

        final long result = this.comboOperand(operand) % 8;
        this.output.add(result);

        this.debugMessage(String.format("  added %s to output", result));
        return false;
    }

    private boolean bxc(final int opcode, final int operand) {

        final long b = this.registerB;
        final long c = this.registerC;
        final long result = b ^ c;
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

        final long result = this.comboOperand(operand) % 8;
        this.registerB = result;

        this.debugMessage(String.format("  set B to %s", this.registerB));
        return false;
    }

    private boolean bxl(final int opcode, final int operand) {

        final long b = this.registerB;
        final long o = operand; // literal, not combo
        final long result = b ^ o;
        this.registerB = result;

        this.debugMessage(String.format("  set B to %s", this.registerB));
        return false;
    }

    private boolean adv(final int opcode, final int operand) {

        final double numerator = this.registerA;
        final double denominator = Math.pow(2, this.comboOperand(operand));
        final Double result = numerator / denominator;
        this.registerA = result.longValue();

        this.debugMessage(String.format("  set A to %s", this.registerA));

        return false;
    }

    private String opcodeInstruction(final int opcode) {
        return this.instructions.get(opcode);
    }

    public void reset() {

        this.instructionPointer = 0;
        this.registerA = 0;
        this.registerB = 0;
        this.registerC = 0;

        this.output.clear();
    }
}
