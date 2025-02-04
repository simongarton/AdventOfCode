package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Year2024Day17 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;

    private final List<Integer> part2Program = List.of(2, 4, 1, 1, 7, 5, 4, 4, 1, 4, 0, 3, 5, 5, 3, 0);

    @Override
    public String title() {
        return "Day 17: Chronospatial Computer";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 17);
    }

    @Override
    public String part1(final String[] input) {

        final ChronospatialComputer computer = ChronospatialComputer.initializeFromLines(input);
        computer.run();

        return computer.getOutputString();
    }

    @Override
    public String part2(final String[] input) {

        // use this for speed
        final CompiledProgram compiledProgram = new CompiledProgram();

        final List<ProgramNode> available = new ArrayList<>();
        final List<ProgramNode> visited = new ArrayList<>();

        final int lastLineOfProgram = 0;
        List<Integer> values = this.findValuesForIndex(0, lastLineOfProgram, compiledProgram);
        if (values.size() != 1) {
            throw new RuntimeException("didn't find just one node for " + lastLineOfProgram);
        }

        final ProgramNode node = new ProgramNode(lastLineOfProgram, values.get(0), null);
        available.add(node);

        // the target program (output) is 2, 4, 1, 1, 7, 5, 4, 4, 1, 4, 0, 3, 5, 5, 3, 0
        // this gives me i 0 v 5 a 5 so at index 0 - the far right, the last number of the input
        // a 5 going into the main loop once will output 0
        // so now I need to << 3 (multiply by 8) and find another number, that going through the
        // loop twice would first emit a 3, then be changed back down to 5 so on the second loop
        // would emit the 0 again.
        // that number is 6, giving a shifted a value of 46
        // now, on the 3rd iteration, starting with 46 << 3 = 368, I find that 2 numbers would work : 0,1
        // 368 gives me values 0,1,4; 369 gives me 1,4. 368 << 3 = 2944, 369 << 3 = 2952

        // this ticks on until the 5th iteration, setting i 4, the 4th index (zero based) from back,
        // when I start getting no results back for some iterations
        // that ... is probably fine.
        // but on the 8th iteration, I have 3 previous a's 1507748, 1509796 and 1513892, each returned
        // 6 as the option - and none of them give me a target .. so I run out of available nodes.

        // and debugging carefully, and I find my loop to check numbers up to 8 is i=0;i<7=i++ :facepalm

        // OK, now my compiled program will give me the right answer ... but the interpreted computer
        // doesn't. It's close - but something went wrong
        //  For a=202366925068016 I get 2,4,1,1,7,5,4,4,1,4,0,3,5,5,3,0,
        //  Real computer says          5,6,0,4,6,5,4,4,1,4,0,3,5,5,3,0

        // so lets look at what happens on each loop
        // real computer has big numbers in first two lines
        // a 25295865633502 b 101183462534013 c 101183462534008 out [5]
        // a 3161983204187 b 197623950262 c 197623950261 out [5, 6]

        // compiled program has what looks like ints for b and c
        // a 202366925068016 b 0 c 0 out
        // a 25295865633502 b 2147483642 c 2147483647 out 2,
        // a 3161983204187 b 2147483644 c 2147483647 out 2,4,

        // a tracks correctly.

        // ah, there was a damn intValue() on one of my calculations. :facepalm

        final List<String> graph = new ArrayList<>();
        graph.add("digraph {");
        graph.add("rankdir=\"LR\"");
        long correctInput = 0;
        while (!available.isEmpty()) {
            final ProgramNode current = available.remove(0);
            visited.add(current);
            graph.add("\"" + current.graphString() + "\"");
            final int programLineFromEnd = current.index + 1;
            if (programLineFromEnd == 16) {
                correctInput = current.a;
                this.updateGraphWithCurrent(graph, current);
                break;
            }

            values = this.findValuesForIndex(current.a, programLineFromEnd, compiledProgram);
            if (DEBUG) {
                System.out.println("working with " + current + " and I got " + values);
            }
            if (values.isEmpty()) {
                continue;
            }
            for (final int value : values) {
                final ProgramNode previousNode = new ProgramNode(programLineFromEnd, value, current);
                available.add(previousNode);
                graph.add("\"" + current.graphString() + "\" -> \"" + previousNode.graphString() + "\"");
            }
        }

        if (DEBUG) {
            // let's check it again
            final String output = compiledProgram.run(correctInput);
            System.out.println("For a=" + correctInput + " I get " + output);

            // and let's check the real computer
            // use this to validate the result when I get one
            final ChronospatialComputer computer = ChronospatialComputer.initializeFromLines(input);
            computer.setRegisterA(correctInput);
            computer.run();
            System.out.println("Real computer says          " + computer.getOutputString());
            System.out.println("Real program is             " + computer.getProgramString());

            graph.add("}");
            this.dumpGraphToFile("graph.dot", graph);
        }

        return String.valueOf(correctInput);
    }

    private void updateGraphWithCurrent(final List<String> graph, final ProgramNode current) {

        graph.add("\"" + current.graphString() + "\" [style=filled color=green]");
        ProgramNode previousNode = current.next;
        while (previousNode != null) {
            graph.add("\"" + previousNode.graphString() + "\" [style=filled color=green]");
            previousNode = previousNode.next;
        }
    }

    private String displayNodeAsTarget(final ProgramNode programNode) {

        String line = "";
        ProgramNode workingNode = programNode;
        while (workingNode != null) {
            line = workingNode.value + "," + line;
            workingNode = workingNode.next;
        }
        return line;
    }

    private List<Integer> findValuesForIndex(final long previousA,
                                             final int programLineFromEnd,
                                             final CompiledProgram compiledProgram) {

        final List<Integer> values = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            final long a = (previousA << 3) + i;
            final String singleShot = compiledProgram.singleShot(a);
            final int hit = Integer.parseInt(singleShot.substring(0, 1));
            if (hit == this.part2Program.get(15 - programLineFromEnd)) {
                values.add(i);
            }
        }

        return values;
    }

    static class ProgramNode {

        final int index; // 0 -> 15, the index in the array
        final long value; // the value I will use here, one digit
        final long a; // the overall value
        final ProgramNode next; // next because it's in reverse

        public ProgramNode(final int index, final int value, final ProgramNode next) {
            this.index = index;
            this.value = value;
            if (next == null) {
                this.a = value;
            } else {
                this.a = (next.a << 3) + value;
            }
            this.next = next;
        }

        @Override
        public String toString() {

            return "i " + this.index + " v " + this.value + " a " + this.a;
        }

        public String graphString() {

            final DecimalFormat decimalFormat = new DecimalFormat("#,##0");
            return decimalFormat.format(this.a);
        }
    }
}
