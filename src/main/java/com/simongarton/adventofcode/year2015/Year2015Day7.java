package com.simongarton.adventofcode.year2015;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;

import static com.simongarton.adventofcode.year2015.Year2015Day7.Operation.*;

public class Year2015Day7 extends AdventOfCodeChallenge {

    Map<String, Wire> wires = new HashMap<>();
    List<Connection> connections = new ArrayList<>();

    @Override
    public Outcome run() {
        return this.runChallenge(2015, 7);
    }

    @Override
    public String part1(final String[] input) {

        this.connections = new ArrayList<>();
        this.loadCircuit(input);

        boolean anythingChanged = true;
        while (anythingChanged) {
            anythingChanged = false;
            for (final Connection connection : this.connections) {
                if (this.updateConnection(connection)) {
                    anythingChanged = true;
                }
            }
        }

        this.buildGraph();

        return String.valueOf(this.u(this.wires.get("a").voltage));
    }

    private int u(final Integer i) {
        return i & 0xFFFF;
    }

    private void buildGraph() {

        final List<String> lines = new ArrayList<>();
        lines.add("digraph {");
        lines.add("rankdir = \"LR\"");
        for (final Wire wire : this.wires.values()) {
            lines.add("\"" + wire.name + "\" [label=\"" + wire.name + " [" + this.u(wire.voltage) + "]\"]");
        }
        for (final Connection connection : this.connections) {
            if (connection.input.operation == FIXED) {
                lines.add(connection.input.fixedVoltage + "->" + connection.output.name);
                continue;
            }
            if (connection.input.left != null) {
                lines.add("\"" + connection.input.left.name + "\"" + "->" + "\"" + connection.output.name + "\"");
            }
            if (connection.input.right != null) {
                lines.add("\"" + connection.input.right.name + "\"" + "->" + "\"" + connection.output.name + "\"");
            }
        }
        lines.add("}");
        this.dumpGraphToFile("wires.dot", lines);
    }

    private Wire getOrCreateWire(final String name, final Integer voltage) {

        if (this.wires.containsKey(name)) {
            return this.wires.get(name);
        }

        if (this.isNumeric(name)) {
            final Wire wire = new Wire(UUID.randomUUID().toString(), Integer.parseInt(name));
            this.wires.put(name, wire);
            return wire;
        }

        final Wire wire = new Wire(name, voltage);
        this.wires.put(name, wire);
        return wire;
    }

    private boolean updateConnection(final Connection connection) {

        if (connection.output.voltage != null) {
            return false;
        }
        return switch (connection.input.operation) {
            case AND -> this.handleAnd(connection);
            case OR -> this.handleOr(connection);
            case LSHIFT -> this.handleLShift(connection);
            case RSHIFT -> this.handleRShift(connection);
            case NOT -> this.handleNot(connection);
            case FIXED -> this.handleFixed(connection);
            case DIRECT -> this.handleDirect(connection);
        };
    }

    private boolean handleDirect(final Connection connection) {

        if (connection.output.voltage != null) {
            return false;
        }
        if (connection.input.right.voltage == null) {
            return false;
        }

        connection.output.voltage = connection.input.right.voltage;
        return true;
    }

    private boolean handleFixed(final Connection connection) {

        if (connection.output.voltage != null) {
            return false;
        }

        connection.output.voltage = connection.input.fixedVoltage;
        return true;
    }

    private boolean handleRShift(final Connection connection) {

        if (connection.input.left.voltage == null) {
            return false;
        }
        connection.output.voltage = this.u(connection.input.left.voltage >>> connection.input.fixedVoltage);
        return true;
    }

    private boolean handleLShift(final Connection connection) {

        if (connection.input.left.voltage == null) {
            return false;
        }
        connection.output.voltage = this.u(connection.input.left.voltage << connection.input.fixedVoltage);
        return true;
    }

    private boolean handleOr(final Connection connection) {

        if (connection.input.left.voltage == null) {
            return false;
        }
        if (connection.input.right.voltage == null) {
            return false;
        }
        connection.output.voltage = this.u(connection.input.left.voltage | connection.input.right.voltage);
        return true;
    }

    private boolean handleAnd(final Connection connection) {

        if (connection.input.left.voltage == null) {
            return false;
        }
        if (connection.input.right.voltage == null) {
            return false;
        }
        connection.output.voltage = this.u(connection.input.left.voltage & connection.input.right.voltage);
        return true;
    }

    private boolean handleNot(final Connection connection) {

        if (connection.input.right.voltage == null) {
            return false;
        }
        connection.output.voltage = this.u(~connection.input.right.voltage);
        return true;
    }

    private void loadCircuit(final String[] input) {

        Arrays.stream(input).forEach(this::loadConnection);
    }

    private void loadConnection(final String s) {

        final String[] parts = s.split(" -> ");
        final Wire output = this.getOrCreateWire(parts[1], null);

        final String[] inputParts = parts[0].split(" ");

        if (inputParts.length == 1) {
            this.addWireInput(inputParts, output);
            return;
        }

        if (inputParts.length == 2) {
            this.addNotInput(inputParts, output);
            return;
        }

        this.addThreePartInput(inputParts, output);
    }

    private void addThreePartInput(final String[] inputParts, final Wire output) {

        final Operation operation = Operation.fromString(inputParts[1]);
        if (List.of(Operation.RSHIFT, LSHIFT).contains(operation)) {
            this.addShift(inputParts, output, operation);
            return;
        }

        // 1 AND am -> an Bugger.
        final Wire one = this.getOrCreateWire(inputParts[0], null);
        final Wire two = this.getOrCreateWire(inputParts[2], null);
        final Input input = new Input(one, operation, two, null);
        final Connection connection = new Connection(input, output);
        this.connections.add(connection);
    }

    private void addShift(final String[] inputParts, final Wire output, final Operation operation) {

        final Wire one = this.getOrCreateWire(inputParts[0], null);
        final Input input = new Input(one, operation, null, Integer.parseInt(inputParts[2]));
        final Connection connection = new Connection(input, output);
        this.connections.add(connection);
    }

    private void addNotInput(final String[] inputParts, final Wire output) {

        final Wire wire = this.getOrCreateWire(inputParts[1], null);
        final Input input = new Input(null, Operation.NOT, wire, null);
        final Connection connection = new Connection(input, output);
        this.connections.add(connection);
    }

    private void addWireInput(final String[] inputParts, final Wire output) {

        if (this.isNumeric(inputParts[0])) {
            final int voltage = Integer.parseInt(inputParts[0]);
            final Input input = new Input(null, FIXED, null, voltage);
            final Connection connection = new Connection(input, output);
            this.connections.add(connection);
            return;
        }

        final Wire wire = this.getOrCreateWire(inputParts[0], null);
        final Input input = new Input(null, DIRECT, wire, null);
        final Connection connection = new Connection(input, output);
        this.connections.add(connection);
    }

    @Override
    public String part2(final String[] input) {

        this.connections = new ArrayList<>();
        this.loadCircuit(input);
        final int b = Integer.parseInt(this.part1(input));

        // reset everything
        this.wires.clear();
        this.connections.clear();
        this.loadCircuit(input);
        for (final Connection connection : this.connections) {
            if (connection.output.name.equalsIgnoreCase("b")) {
                connection.output.voltage = b;
            }
        }

        boolean anythingChanged = true;
        while (anythingChanged) {
            anythingChanged = false;
            for (final Connection connection : this.connections) {
                if (this.updateConnection(connection)) {
                    anythingChanged = true;
                }
            }
        }

        this.buildGraph();

        return String.valueOf(this.u(this.wires.get("a").voltage));
    }

    static class Wire {

        String name;
        Integer voltage;

        Wire(final String name, final Integer voltage) {
            this.name = name;
            this.voltage = voltage;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Wire wire = (Wire) o;
            return Objects.equals(this.voltage, wire.voltage);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.voltage);
        }

        @Override
        public String toString() {
            return "Wire{" +
                    "name=" + this.name +
                    ", voltage=" + this.voltage +
                    '}';
        }
    }

    static class Input {

        Wire left;
        Operation operation;
        Wire right;
        Integer fixedVoltage;

        Input(final Wire left, final Operation operation, final Wire right, final Integer fixedVoltage) {
            this.left = left;
            this.operation = operation;
            this.right = right;
            this.fixedVoltage = fixedVoltage;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Input input = (Input) o;
            return Objects.equals(this.left, input.left) && Objects.equals(this.operation, input.operation) && Objects.equals(this.right, input.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.left, this.operation, this.right);
        }

        @Override
        public String toString() {
            return "Input{" +
                    "left=" + this.left +
                    ", operation='" + this.operation + '\'' +
                    ", right=" + this.right +
                    '}';
        }
    }

    static class Connection {

        Input input;
        Wire output;

        public Connection(final Input input, final Wire output) {
            this.input = input;
            this.output = output;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Connection that = (Connection) o;
            return Objects.equals(this.input, that.input) && Objects.equals(this.output, that.output);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.input, this.output);
        }

        @Override
        public String toString() {
            return "Connection{" +
                    "input=" + this.input +
                    ", output=" + this.output +
                    '}';
        }
    }

    enum Operation {
        AND("AND"),
        OR("OR"),
        LSHIFT("LSHIFT"),
        RSHIFT("RSHIFT"),
        NOT("NOT"),
        FIXED("FIXED"),
        DIRECT("DIRECT");

        private final String value;

        Operation(final String value) {
            this.value = value;
        }

        public static Operation fromString(final String enumValue) {

            final Optional<Operation> operation = Arrays.stream(Operation.values())
                    .filter(v -> v.value.equalsIgnoreCase(enumValue))
                    .findFirst();
            if (operation.isEmpty()) {
                throw new IllegalArgumentException(enumValue);
            }
            return operation.get();
        }
    }
}
