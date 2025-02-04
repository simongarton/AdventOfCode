package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.text.DecimalFormat;
import java.util.*;

public class Year2024Day24 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;

    private List<Wire> wires;
    private List<Gate> gates;

    @Override
    public String title() {
        return "Day 24: Crossed Wires";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 24);
    }

    @Override
    public String part1(final String[] input) {

        boolean processingGates = false;
        this.wires = new ArrayList<>();
        this.gates = new ArrayList<>();

        for (final String line : input) {

            if (line.isEmpty()) {
                processingGates = true;
                continue;
            }

            if (!processingGates) {
                this.wires.add(this.parseWire(line));
            } else {
                this.gates.add(this.parseGate(line));
            }
        }

        boolean somethingHappened;
        while (true) {
            somethingHappened = false;
            for (final Gate gate : this.gates) {
                if (gate.evaluate()) {
                    somethingHappened = true;
                }
                if (DEBUG) {
                    System.out.println(gate);
                }
            }
            if (DEBUG) {
                this.wires.forEach(System.out::println);
            }
            if (!somethingHappened) {
                break;
            }

        }

        return String.valueOf(this.figureOutWires("z"));
    }

    private long figureOutWires(final String prefix) {

        long total = 0;
        final List<Wire> zWires = new ArrayList<>(this.wires.stream().filter(w -> w.name.startsWith(prefix)).toList());
        zWires.sort(Comparator.comparing(w -> w.name));
        for (int i = 0; i < zWires.size(); i++) {
            total += zWires.get(i).voltage * (long) Math.pow(2, i);
        }
        return total;
    }

    private void loadWiresAndGates(final String[] input) {

        boolean processingGates = false;
        this.wires = new ArrayList<>();
        this.gates = new ArrayList<>();

        for (final String line : input) {

            if (line.isEmpty()) {
                processingGates = true;
                continue;
            }

            if (!processingGates) {
                this.wires.add(this.parseWire(line));
            } else {
                this.gates.add(this.parseGate(line));
            }
        }

    }

    private void doVisualSwaps() {

        this.swapOutputs(this.getSwapsNeeded());
    }

    private List<OutputSwap> getSwapsNeeded() {

        // gbf,hdt,jgt,mht,nbf,z05,z09,z30

        return List.of(
                new OutputSwap(53, 150), // hdt & z05
                new OutputSwap(55, 89), // z09 & gbf
                new OutputSwap(174, 114),
                // nbf & z30
                new OutputSwap(109, 20) // mht, jgt
        );
    }

    private void runUntilStable() {

        boolean somethingHappened;
        while (true) {
            somethingHappened = false;
            for (final Gate gate : this.gates) {
                if (gate.evaluate()) {
                    somethingHappened = true;
                }
                if (DEBUG) {
                    System.out.println(gate);
                }
            }
            if (DEBUG) {
                this.wires.forEach(System.out::println);
            }
            if (!somethingHappened) {
                break;
            }
        }
    }

    @Override
    public String part2(final String[] input) {

        // this found another pair ...
        // this.iterate(input);

        this.loadWiresAndGates(input);
        this.doVisualSwaps();

        // this.adhocAnalysis();

        // let's take a look at the gates
        if (DEBUG) {
            final Map<String, Integer> counts = new HashMap<>();
            for (final Gate gate : this.gates) {
                final String explained = this.explainGate(gate);
                System.out.println(explained);
                final String key = explained.substring(6).trim();
                counts.put(key, counts.getOrDefault(key, 0) + 1);
            }
            for (final Map.Entry<String, Integer> entry : counts.entrySet()) {
                System.out.println(entry.getKey() + "=" + entry.getValue());
            }
        }

        this.buildGraph();
        if (DEBUG) {
            System.out.println("wires: " + this.wires.size());
            System.out.println("gates: " + this.gates.size());
        }

        this.runUntilStable();

        final long x = this.figureOutWires("x");
        final long y = this.figureOutWires("y");
        final long z = this.figureOutWires("z");
        final long check = x + y;
        if (DEBUG) {
            System.out.println(x + " && " + y + "=" + z + " (" + check + ")");
            this.displayBinary(x, y, z, check);
        }

        final List<OutputSwap> swapsNeeded = this.getSwapsNeeded();
        final List<String> wireNames = new ArrayList<>();
        for (final OutputSwap outputSwap : swapsNeeded) {
            wireNames.add(this.getGate(outputSwap.gate1).output.name);
            wireNames.add(this.getGate(outputSwap.gate2).output.name);
        }
        wireNames.sort(Comparator.naturalOrder());
        return String.join(",", wireNames);
    }

    private void iterate(final String[] input) {

        for (long i = 0; i < 45; i++) {
            this.loadWiresAndGates(input);
            this.doVisualSwaps();
            final long boost = (long) Math.pow(2, i);
            this.setWiresToValue(boost, boost);
            this.runUntilStable();
            final long z = this.figureOutWires("z");
            if (z != (2 * boost)) {
                System.out.println("broke at " + i + " with " + (2 * boost) + " != " + z);
            }
        }
    }

    private void setWiresToValue(final long x, final long y) {

        final DecimalFormat decimalFormat = new DecimalFormat("00");

        final String binaryXReversed = new StringBuilder(this.leftPad(longToBinary(x), 45, "0")).reverse().toString();
        final String binaryYReversed = new StringBuilder(this.leftPad(longToBinary(y), 45, "0")).reverse().toString();

        for (int i = 0; i < 45; i++) {
            this.getWire("x" + decimalFormat.format(i)).voltage = Long.parseLong(binaryXReversed.substring(i, i + 1));
            this.getWire("y" + decimalFormat.format(i)).voltage = Long.parseLong(binaryYReversed.substring(i, i + 1));
        }
    }

    private void adhocAnalysis() {

        // all the x/y wires go to the same pair of gates
        final DecimalFormat decimalFormat = new DecimalFormat("00");
        for (int i = 0; i < 45; i++) {
            final Wire x = this.getWire("x" + decimalFormat.format(i));
            final Wire y = this.getWire("y" + decimalFormat.format(i));
            final List<Gate> xGates = this.gatesConsumingWire(x);
            final List<Gate> yGates = this.gatesConsumingWire(y);
            System.out.println(x + " " + y);
            final String xGateString = String.join(" ", xGates.stream().map(Gate::toString).toList());
            final String yGateString = String.join(" ", yGates.stream().map(Gate::toString).toList());
            if (!xGateString.equalsIgnoreCase(yGateString)) {
                System.out.println(xGateString);
                System.out.println(yGateString);
            }
        }
    }

    private void explainBinary() {

        // these are values after the three swaps I think I've found
        // I was hoping to see a clear pattern to point me at the 4th.
        // but sadly no.

        // 21117783899853    100110011010011011110000000011111111011001101
        // 30540314920985    110111100011010111000100111110100100000011001
        // 51658098853606   1011101111101110010110101000011100011011100110
        // 20910451017737    100110000010010011000000000010100100000001001

        // wait. The two real numbers I have would end in 8, which is 0 in binary
        // so my check must end in a 0. It doesn't. Which end of the circuit is doing
        // the first digit ?

        final long x = 21117783899853L;
        final long y = 30540314920985L;
        final long z = 51658098853606L;
        final long check = 20910451017737L;

        this.displayBinary(x, y, z, check);
    }

    private void displayBinary(final long x, final long y, final long z, final long check) {

        final int size = 48;
        System.out.println(x + " " + this.leftPad(longToBinary(x), size, " "));
        System.out.println(y + " " + this.leftPad(longToBinary(y), size, " "));
        System.out.println(z + " " + this.leftPad(longToBinary(z), size, " "));
        System.out.println(check + " " + this.leftPad(longToBinary(check), size, " "));
    }

    public static String longToBinary(long n) {
        String s = "";
        while (n > 0) {
            s = ((n % 2) == 0 ? "0" : "1") + s;
            n = n / 2;
        }
        return s;
    }


    private void swapOutputs(final List<OutputSwap> swapsNeeded) {
        swapsNeeded.forEach(this::swapOutput);
    }

    private void swapOutput(final OutputSwap outputSwap) {

        final Gate gate1 = this.getGate(outputSwap.gate1);
        final Gate gate2 = this.getGate(outputSwap.gate2);
        final Wire temp = gate1.output;
        gate1.output = gate2.output;
        gate2.output = temp;
    }

    private Gate getGate(final int gateId) {

        return this.gates.stream().filter(g -> g.id == gateId).findFirst().orElseThrow();
    }

    private String explainGate(final Gate gate) {

        String line = this.leftPad(String.valueOf(gate.id), 5, " ") + " ";
        line = line + this.rightPad(gate.operation.name(), 3, " ") + "->";

        final List<String> targets = new ArrayList<>(this.gatesConsumingWire(gate.output).stream().map(g -> g.operation.name()).toList());
        targets.sort(Comparator.naturalOrder());
        line = line + "[" + String.join(",", targets) + "]";

        return line;
    }

    private List<Gate> gatesConsumingWire(final Wire wire) {

        return this.gates.stream().filter(g -> (g.wire1 == wire || g.wire2 == wire)).toList();
    }

    private void buildGraph() {

        final List<String> lines = new ArrayList<>();
        lines.add("digraph {");
        lines.add("rankdir = \"LR\"");

        for (final Wire wire : this.wires) {
            lines.add(wire.name + " [style=\"filled\" shape=\"box\" color=\"gray50\" fillcolor=\"" + this.getWireColor(wire.name) + "\"]");
        }
        for (final Gate gate : this.gates) {
            lines.add(gate.id + " [label=\"" + gate.operation + " (" + gate.id + ")" + "\" style=\"filled\" color=\"gray50\" fillcolor=\"" + this.getGateColor(gate.operation) + "\"]");
        }
        for (final Gate gate : this.gates) {
            lines.add(gate.wire1.name + "->" + gate.id);
            lines.add(gate.wire2.name + "->" + gate.id);
            lines.add(gate.id + "->" + gate.output.name);
        }
        lines.add("}");
        if (DEBUG) {
            this.dumpGraphToFile("2024-24.2.dot", lines);
        }
    }

    private String getWireColor(final String name) {

        if (name.startsWith("x")) {
            return "lawngreen";
        }
        if (name.startsWith("y")) {
            return "gold";
        }
        if (name.startsWith("z")) {
            return "orangered";
        }
        return "ghostwhite";
    }

    private String getGateColor(final Operation operation) {

        switch (operation) {
            case AND -> {
                return "cornflowerblue";
            }
            case OR -> {
                return "dodgerblue";
            }
            case XOR -> {
                return "lightslateblue";
            }
        }
        return null;
    }

    private Wire parseWire(final String line) {

        final String[] parts = line.split(": ");
        return new Wire(parts[0], Long.parseLong(parts[1]));
    }

    private Gate parseGate(final String line) {

        final String[] parts = line.split(" ");
        return new Gate(
                this.gates.size(),
                this.getWire(parts[0]),
                this.parseOperation(parts[1]),
                this.getWire(parts[2]),
                this.getWire(parts[4])
        );
    }

    private Operation parseOperation(final String part) {
        if (part.equalsIgnoreCase("AND")) {
            return Operation.AND;
        }
        if (part.equalsIgnoreCase("OR")) {
            return Operation.OR;
        }
        if (part.equalsIgnoreCase("XOR")) {
            return Operation.XOR;
        }
        throw new RuntimeException(part);
    }

    private Wire getWire(final String name) {

        final Optional<Wire> optionalWire = this.wires.stream().filter(w -> w.name.equalsIgnoreCase(name)).findFirst();
        if (optionalWire.isPresent()) {
            return optionalWire.get();
        }
        final Wire wire = new Wire(name, null);
        this.wires.add(wire);
        return wire;
    }

    public enum Operation {
        AND, OR, XOR
    }

    static class Wire {

        String name;
        Long voltage;

        public Wire(final String name, final Long voltage) {
            this.name = name;
            this.voltage = voltage;
        }

        @Override
        public String toString() {
            return this.name + " (" + this.voltage + ")";
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Wire wire = (Wire) o;
            return Objects.equals(this.name, wire.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.name);
        }
    }

    record OutputSwap(int gate1, int gate2) {
    }

    static class Gate {

        int id;
        Wire wire1;
        Operation operation;
        Wire wire2;
        Wire output;

        public Gate(final int id,
                    final Wire wire1,
                    final Operation operation,
                    final Wire wire2,
                    final Wire output) {
            this.id = id;
            this.wire1 = wire1;
            this.operation = operation;
            this.wire2 = wire2;
            this.output = output;
        }

        @Override
        public String toString() {
            return this.wire1 + " " + this.operation + " " + this.wire2 + " -> " + this.output;
        }

        public boolean evaluate() {

            if (this.output.voltage != null) {
                return false;
            }

            if (this.wire1.voltage == null || this.wire2.voltage == null) {
                return false;
            }
            switch (this.operation) {
                case AND -> {
                    this.output.voltage = (this.wire1.voltage == 1 && this.wire2.voltage == 1) ? 1L : 0;
                }
                case OR -> {
                    this.output.voltage = (this.wire1.voltage == 1 || this.wire2.voltage == 1) ? 1L : 0;
                }
                case XOR -> {
                    this.output.voltage = (this.wire1.voltage == 1 ^ this.wire2.voltage == 1) ? 1L : 0;
                }
            }
            return true;
        }
    }
}
