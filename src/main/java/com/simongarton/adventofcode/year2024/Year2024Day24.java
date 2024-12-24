package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Year2024Day24 extends AdventOfCodeChallenge {

    private List<Wire> wires;
    private List<Gate> gates;

    @Override
    public String title() {
        return "Day 24: Crossed Wires";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2018, 24);
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
                System.out.println(gate);
            }
            this.wires.forEach(System.out::println);
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

    @Override
    public String part2(final String[] input) {

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

        this.buildGraph();

        System.out.println("wires: " + this.wires.size());
        System.out.println("gates: " + this.gates.size());

        boolean somethingHappened;
        while (true) {
            somethingHappened = false;
            for (final Gate gate : this.gates) {
                if (gate.evaluate()) {
                    somethingHappened = true;
                }
                System.out.println(gate);
            }
            this.wires.forEach(System.out::println);
            if (!somethingHappened) {
                break;
            }
        }

        final long x = this.figureOutWires("x");
        final long y = this.figureOutWires("y");
        final long z = this.figureOutWires("z");
        final long check = x & y;
        System.out.println(x + " && " + y + "=" + z + " (" + check + ")");
        return String.valueOf(z);
    }

    private void buildGraph() {

        final List<String> lines = new ArrayList<>();
        lines.add("digraph {");
        lines.add("rankdir = \"LR\"");

        for (final Wire wire : this.wires) {
            lines.add(wire.name + " [style=\"filled\" shape=\"box\" color=\"gray50\" fillcolor=\"" + this.getWireColor(wire.name) + "\"]");
        }
        for (final Gate gate : this.gates) {
            lines.add(gate.id + " [label=\"" + gate.operation + "\" style=\"filled\" color=\"gray50\" fillcolor=\"" + this.getGateColor(gate.operation) + "\"]");
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
