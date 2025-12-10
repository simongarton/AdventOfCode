package com.simongarton.adventofcode.year2025;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;

public class Year2025Day10 extends AdventOfCodeChallenge {

    private final Map<Integer, List<Node>> breadthFirst = new HashMap<>();
    private final List<Node> pressesToTry = new ArrayList<>();
    private int nodesAdded;

    @Override
    public String title() {
        return "Day 10: Factory";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2025, 10);
    }

    @Override
    public String part1(final String[] input) {

        long totalPresses = 0;
        int machines = 0;
        for (final String line : input) {
            final Machine machine = this.parseLine(machines, line);
            final Node best = this.minimumPressesLights(machine);
            System.out.println(machines + ":" + machine.state() + " -> " + machine.targetState() + " in " + best.totalPresses + " (" + best.sequence() + ")");
            totalPresses += best.totalPresses;
            machines++;
        }

        return String.valueOf(totalPresses);
    }

    protected Node minimumPressesLights(final Machine machine) {
        long minimumPresses = Long.MAX_VALUE;
        Node minimum = null;
        final Set<String> attemptedStates = new HashSet<>();

        this.breadthFirst.clear();
        this.pressesToTry.clear();
        this.nodesAdded = 0;

        this.addNodes(machine, null, machine.state(), true);

        while (!this.pressesToTry.isEmpty()) {
            final Node node = this.getBestNode();
//            System.out.println("pt " + this.pressesToTry.size() + " bf " + this.breadthFirst.size() + " b " + node.buttonPressed + " n " + node);
            this.pressesToTry.remove(node);
            if (node.totalPresses >= minimumPresses) {
//                System.out.println("Abandoning, " + node.totalPresses + " > " + minimumPresses);
                continue;
            }
            final String outputState = node.pressButtonLights();
//            System.out.println(node.inputState + " [" + node.buttonPressed + "] -> " + node.outputState);
            final boolean complete = node.isCompleteForLights();
            if (complete) {
                final long pressesTaken = node.totalPresses;
                if (pressesTaken < minimumPresses) {
                    minimumPresses = pressesTaken;
                    minimum = node;
//                    System.out.println("Found a new minimum presses of " + minimumPresses);
                }
            }
            if (!attemptedStates.contains(outputState)) {
                this.addNodes(machine, node, outputState, true);
            }
            attemptedStates.add(outputState);
        }
        return minimum;
    }

    protected Node minimumPressesJoltage(final Machine machine) {

        long minimumPresses = Long.MAX_VALUE;
        Node minimum = null;
        final Set<String> attemptedStates = new HashSet<>();

        this.breadthFirst.clear();
        this.pressesToTry.clear();
        this.nodesAdded = 0;

        this.addNodes(machine, null, machine.joltageState(), false);

        while (!this.pressesToTry.isEmpty()) {
            final Node node = this.getBestNode();
//            System.out.println("pt " + this.pressesToTry.size() + " bf " + this.breadthFirst.size() + " b " + node.buttonPressed + " n " + node.toStringJoltage());
            this.pressesToTry.remove(node);
            if (node.totalPresses >= minimumPresses) {
//                System.out.println("Abandoning, " + node.totalPresses + " > " + minimumPresses);
                continue;
            }
            final String outputState = node.pressButtonJoltage();
//            System.out.println(node.inputState + " [" + node.buttonPressed + "] -> " + node.outputState);
            final boolean complete = node.isCompleteForJoltage();
//            System.out.println(node.sequence() + " -> " + outputState);

            if (complete) {
                final long pressesTaken = node.totalPresses;
                if (pressesTaken < minimumPresses) {
                    minimumPresses = pressesTaken;
                    minimum = node;
//                    System.out.println("Found a new minimum presses of " + minimumPresses);
                }
            }
            final boolean stillValid = !attemptedStates.contains(node.sequence());
            final boolean exceeded = machine.exceededBy(outputState);
            if (stillValid && !exceeded) {
                this.addNodes(machine, node, outputState, false);
            }
            attemptedStates.add(node.sequence());
        }
        return minimum;
    }

    private Node getBestNode() {

        int depth = 0;
        while (true) {
            if (!this.breadthFirst.containsKey(depth)) {
                depth++;
                continue;
            }
            if (this.breadthFirst.get(depth).isEmpty()) {
                depth++;
                continue;
            }
            return this.breadthFirst.get(depth).removeFirst();
        }
    }

    private void addNodes(final Machine machine, final Node parent, final String state, final boolean useLights) {

        for (final Button button : machine.buttons) {
            final Node node = new Node(
                    this.nodesAdded,
                    machine,
                    state,
                    parent,
                    button.id
            );
            this.pressesToTry.add(node);
            this.addNode(node, useLights);
            this.nodesAdded++;
        }
    }

    private void addNode(final Node node, final boolean useLights) {

        final int depth = useLights ? node.totalPresses() : node.lackOfVoltage();
        if (!this.breadthFirst.containsKey(depth)) {
            this.breadthFirst.put(depth, new ArrayList<>());
        }
        this.breadthFirst.get(depth).add(node);
    }

    private Machine parseLine(final int id, final String line) {

        final String[] sections = line.split(" ");
        final boolean[] lights = this.getLights(sections[0]);
        final List<Button> buttons = new ArrayList<>();
        int buttonid = 0;
        for (int i = 1; i < sections.length - 1; i++) {
            buttons.add(this.getButton(buttonid, sections[i]));
            buttonid++;
        }
        final int[] joltages = this.getJoltages(sections[sections.length - 1]);
        final Machine machine = new Machine(id, lights, joltages);
        for (final Button button : buttons) {
            button.setMachine(machine);
            machine.buttons.add(button);
        }

        return machine;
    }

    private int[] getJoltages(final String section) {

        final String joltageSection = section.substring(1, section.length() - 1);
        final String[] joltageStrings = joltageSection.split(",");
        final int[] joltages = new int[joltageStrings.length];
        for (int i = 0; i < joltages.length; i++) {
            joltages[i] = Integer.parseInt(joltageStrings[i]);
        }
        return joltages;
    }

    private Button getButton(final int buttonId, final String section) {

        final String lightSection = section.substring(1, section.length() - 1);
        final String[] lightStrings = lightSection.split(",");
        return new Button(buttonId, Arrays.stream(lightStrings).map(Integer::parseInt).toList());
    }

    private boolean[] getLights(final String section) {

        final boolean[] lights = new boolean[section.length() - 2];
        for (int i = 1; i < section.length() - 1; i++) {
            lights[i - 1] = section.charAt(i) == '#';
        }
        return lights;
    }

    @Override
    public String part2(final String[] input) {

        long totalPresses = 0;
        int machines = 0;
        for (final String line : input) {
            final Machine machine = this.parseLine(machines, line);
            final String startingJoltages = Arrays.toString(machine.joltages);
            final Node best = this.minimumPressesJoltage(machine);
            System.out.println(machines + ":" + startingJoltages + " -> " + Arrays.toString(machine.targetJoltages) + " in " + best.totalPresses + " (" + best.sequence() + ")");
            totalPresses += best.totalPresses;
            machines++;
        }

        return String.valueOf(totalPresses);
    }

    public static class Machine {

        private final int id;
        private final boolean[] targetLights;
        private final boolean[] lights;
        private final int[] targetJoltages;
        private final int[] joltages;
        private final List<Button> buttons;

        public Machine(final int id,
                       final boolean[] targetLights,
                       final int[] targetJoltages) {
            this.id = id;
            this.targetLights = targetLights;
            this.lights = new boolean[targetLights.length];
            this.buttons = new ArrayList<>();
            this.targetJoltages = targetJoltages;
            this.joltages = new int[targetJoltages.length];

        }

        public int lightCount() {
            return this.lights.length;
        }

        public boolean toggle(final int index) {
            this.lights[index] = !this.lights[index];
            return this.lights[index];
        }

        public boolean state(final int index) {

            return this.lights[index];
        }

        public String targetState() {

            final StringBuilder line = new StringBuilder();
            for (final boolean light : this.targetLights) {
                line.append(light ? "#" : ".");
            }
            return line.toString();
        }

        public String state() {

            final StringBuilder line = new StringBuilder();
            for (final boolean light : this.lights) {
                line.append(light ? "#" : ".");
            }
            return line.toString();
        }

        public String pressButtonLights(final int index) {
            return this.buttons.get(index).pressButtonLights();
        }

        public String pressButtonJoltage(final int index) {
            return this.buttons.get(index).pressButtonJoltage();
        }

        public int getId() {
            return this.id;
        }

        public List<Button> getButtons() {
            return this.buttons;
        }

        public void setState(final String inputState) {

            for (int i = 0; i < inputState.length(); i++) {
                this.lights[i] = inputState.charAt(i) == '#';
            }
        }

        public boolean isCompleteForLights() {

            return this.state().equalsIgnoreCase(this.targetState());
        }

        public boolean isCompleteForJoltage() {

            return Arrays.equals(this.joltages, this.targetJoltages);
        }

        public String joltageState() {

            String line = "";
            for (final int joltage : this.joltages) {
                line = line + joltage + ",";
            }
            return line.substring(0, line.length() - 1);
        }

        public String targetJoltageState() {

            String line = "";
            for (final int joltage : this.targetJoltages) {
                line = line + joltage + ",";
            }
            return line.substring(0, line.length() - 1);
        }

        public void setJoltages(final String inputState) {
            final String[] joltageStrings = inputState.split(",");
            for (int i = 0; i < joltageStrings.length; i++) {
                this.joltages[i] = Integer.parseInt(joltageStrings[i]);
            }
        }

        public boolean exceededBy(final String outputState) {

            final String[] joltageStrings = outputState.split(",");
            for (int i = 0; i < joltageStrings.length; i++) {
                if (Integer.parseInt(joltageStrings[i]) > this.targetJoltages[i]) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class Button {

        private final int id;
        private final List<Integer> circuits;
        private Machine machine;

        public Button(final int id, final List<Integer> circuits) {
            this.id = id;
            this.circuits = circuits;
        }

        public void setMachine(final Machine machine) {

            this.machine = machine;
        }

        public String pressButtonLights() {

            for (final Integer circuit : this.circuits) {
                this.machine.toggle(circuit);
            }
            return this.machine.state();
        }

        public String pressButtonJoltage() {

            for (final Integer circuit : this.circuits) {
                this.machine.joltages[circuit] = this.machine.joltages[circuit] + 1;
            }
            return this.machine.joltageState();
        }

        public String getCircuits() {

            return this.circuits.toString();
        }
    }

    public static class Node {

        private final int id;
        private final Machine machine;
        private final String inputState;
        private final Node parent;
        private final int totalPresses;
        private final int buttonPressed;
        private String outputState;

        public Node(final int id, final Machine machine, final String inputState, final Node parent, final int buttonPressed) {
            this.id = id;
            this.machine = machine;
            this.inputState = inputState;
            this.parent = parent;
            this.totalPresses = parent == null ? 1 : 1 + parent.totalPresses;
            this.buttonPressed = buttonPressed;
        }

        public String pressButtonLights() {

            this.machine.setState(this.inputState);
            this.machine.pressButtonLights(this.buttonPressed);
            this.outputState = this.machine.state();
            return this.outputState;
        }

        public String pressButtonJoltage() {
            this.machine.setJoltages(this.inputState);
            this.machine.pressButtonJoltage(this.buttonPressed);
            this.outputState = this.machine.joltageState();
            return this.outputState;
        }

        public boolean isCompleteForLights() {

            return this.machine.isCompleteForLights();
        }

        public boolean isCompleteForJoltage() {

            return this.machine.isCompleteForJoltage();
        }

        public int totalPresses() {

            return this.totalPresses;
        }

        @Override
        public String toString() {
            return this.id + " v" + this.totalPresses + " " + this.machine.getId() + ":" + this.machine.state() + " -> " + this.machine.targetState();
        }

        public String toStringJoltage() {
            return this.id + " v" + this.totalPresses + " " + this.machine.getId() + ":" + this.machine.joltageState() + " -> " + this.machine.targetJoltageState();
        }

        public String sequence() {

            Node path = this;
            final List<Integer> sequence = new ArrayList<>();
            sequence.add(path.buttonPressed);
            path = path.parent;
            while (path != null) {
                sequence.add(path.buttonPressed);
                path = path.parent;
            }
            return String.join(",", sequence.reversed().stream().map(String::valueOf).toList());
        }

        public int lackOfVoltage() {

            int difference = 0;
            for (int i = 0; i < this.machine.joltages.length; i++) {
                difference = difference + this.machine.targetJoltages[i] - this.machine.joltages[i];
            }
            return difference;
        }
    }
}
