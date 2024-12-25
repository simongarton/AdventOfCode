package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

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

        final List<OutputSwap> swapsNeeded = List.of(
                new OutputSwap(53, 150), // hdt & z05
                new OutputSwap(55, 89), // z09 & gbf
                new OutputSwap(174, 114) // nbf & z30
        );

        this.swapOutputs(swapsNeeded);
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

    public String part2BruteForceDidntWork(final String[] input) {

        this.loadWiresAndGates(input);
        final List<Integer> gateIds = this.gates.stream().map(g -> g.id).toList();

        long z = 0;
        final List<Integer> alreadyDone = List.of(53, 150, 55, 89, 174, 114);
        for (int i = 0; i < gateIds.size(); i++) {
            System.out.println(i + " " + gateIds.size());
            for (int j = 0; j < gateIds.size(); j++) {
                if (i == j) {
                    continue;
                }
                if (alreadyDone.contains(i) || alreadyDone.contains(j)) {
                    continue;
                }

                this.loadWiresAndGates(input);
                this.doVisualSwaps();

                final List<OutputSwap> swapsNeeded = List.of(
                        new OutputSwap(i, j)
                );
                this.swapOutputs(swapsNeeded);

                this.runUntilStable();

                try {
                    final long x = this.figureOutWires("x");
                    final long y = this.figureOutWires("y");
                    z = this.figureOutWires("z");
                    final long check = x & y; // this should match z
                    if (DEBUG) {
                        System.out.println(x + " && " + y + "=" + z + " (" + check + ")");
                    }
                    if (check == z) {
                        System.out.println("swapping " + i + "," + j + " (" + this.getGate(i).output + "," + this.getGate(j).output + ")");
                        break;
                    }
                } catch (final NullPointerException npe) {
                    continue;
                }
            }
        }

        return String.valueOf(z);
    }


    @Override
    public String part2(final String[] input) {

        this.explainBinary();

        this.loadWiresAndGates(input);
        this.doVisualSwaps();

        // let's take a look at the gates
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

        this.buildGraph();
        if (DEBUG) {
            System.out.println("wires: " + this.wires.size());
            System.out.println("gates: " + this.gates.size());
        }

        this.runUntilStable();

        final long x = this.figureOutWires("x");
        final long y = this.figureOutWires("y");
        final long z = this.figureOutWires("z");
        final long check = x & y; // this should match z
        if (DEBUG) {
            System.out.println(x + " && " + y + "=" + z + " (" + check + ")");
        }
        System.out.println(x + " && " + y + "=" + z + " (" + check + ")");
        return String.valueOf(z);
    }

    private void explainBinary() {

        final Long x = 21117783899853L;
        final Long y = 30540314920985L;
        final Long sum = 51658098853606L;
        final Long check = 20910451017737L;

        final int size = 48;
        System.out.println(x + " " + this.leftPad(longToBinary(x), size, " "));
        System.out.println(y + " " + this.leftPad(longToBinary(y), size, " "));
        System.out.println(sum + " " + this.leftPad(longToBinary(sum), size, " "));
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
        this.dumpGraphToFile("2024-24.2.dot", lines);
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

    private Wire getWire(final String part) {

        final Optional<Wire> optionalWire = this.wires.stream().filter(w -> w.name.equalsIgnoreCase(part)).findFirst();
        if (optionalWire.isPresent()) {
            return optionalWire.get();
        }
        final Wire wire = new Wire(part, null);
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
