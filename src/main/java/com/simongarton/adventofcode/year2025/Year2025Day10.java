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
            final Node best = this.minimumPresses(machine);
            System.out.println(machines + ":" + machine.state() + " -> " + machine.targetState() + " in " + best.totalPresses + " (" + best.sequence() + ")");
            totalPresses += best.totalPresses;
            machines++;
        }

        return String.valueOf(totalPresses);
    }

    protected Node minimumPresses(final Machine machine) {

        long minimumPresses = Long.MAX_VALUE;
        Node minimum = null;
        final Set<String> attemptedStates = new HashSet<>();

        this.breadthFirst.clear();
        this.pressesToTry.clear();
        this.nodesAdded = 0;

        this.addNodes(machine, null, machine.state());

        while (!this.pressesToTry.isEmpty()) {
            final Node node = this.getBestNode();
//            System.out.println("pt " + this.pressesToTry.size() + " bf " + this.breadthFirst.size() + " b " + node.buttonPressed + " n " + node);
            this.pressesToTry.remove(node);
            if (node.totalPresses >= minimumPresses) {
//                System.out.println("Abandoning, " + node.totalPresses + " > " + minimumPresses);
                continue;
            }
            final String outputState = node.pressButton();
//            System.out.println(node.inputState + " [" + node.buttonPressed + "] -> " + node.outputState);
            if (node.isComplete()) {
                final long pressesTaken = node.totalPresses;
                if (pressesTaken < minimumPresses) {
                    minimumPresses = pressesTaken;
                    minimum = node;
//                    System.out.println("Found a new minimum presses of " + minimumPresses);
                }
            }
            if (!attemptedStates.contains(outputState)) {
                this.addNodes(machine, node, outputState);
            }
            attemptedStates.add(outputState);
        }
        return minimum;
    }

    private Node getBestNode() {

        int depth = 1;
        while (true) {
            if (this.breadthFirst.get(depth).isEmpty()) {
                depth++;
                continue;
            }
            return this.breadthFirst.get(depth).removeFirst();
        }
    }

    private void addNodes(final Machine machine, final Node parent, final String state) {

        for (final Button button : machine.buttons) {
            final Node node = new Node(
                    this.nodesAdded,
                    machine,
                    state,
                    parent,
                    button.id
            );
            this.pressesToTry.add(node);
            this.addNode(node);
            this.nodesAdded++;
        }
    }

    private void addNode(final Node node) {

        final int depth = node.totalPresses();
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
        final List<Integer> joltages = this.getJoltages(sections[sections.length - 1]);
        final Machine machine = new Machine(id, lights);
        for (final Button button : buttons) {
            button.setMachine(machine);
            machine.buttons.add(button);
        }

        return machine;
    }

    private List<Integer> getJoltages(final String section) {

        final String lightSection = section.substring(1, section.length() - 1);
        final String[] lightStrings = lightSection.split(",");
        return Arrays.stream(lightStrings).map(Integer::parseInt).toList();
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
        return null;
    }

    public static class Machine {

        private final int id;
        private final boolean[] targetLights;
        private final boolean[] lights;
        private final List<Button> buttons;
        private final List<Integer> joltages;

        public Machine(final int id, final boolean[] targetLights) {
            this.id = id;
            this.targetLights = targetLights;
            this.lights = new boolean[targetLights.length];
            this.buttons = new ArrayList<>();
            this.joltages = new ArrayList<>();
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

        public String pressButton(final int index) {
            return this.buttons.get(index).press();
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

        public boolean isComplete() {

            return this.state().equalsIgnoreCase(this.targetState());
        }
    }

    public static class Button {

        private final int id;
        private final List<Integer> lights;
        private Machine machine;

        public Button(final int id, final List<Integer> lights) {
            this.id = id;
            this.lights = lights;
        }

        public int getId() {
            return this.id;
        }

        public void setMachine(final Machine machine) {
            this.machine = machine;
        }

        public String press() {
            for (final Integer light : this.lights) {
                this.machine.toggle(light);
            }
            return this.machine.state();
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

        public String pressButton() {

            this.machine.setState(this.inputState);
            this.machine.pressButton(this.buttonPressed);
            this.outputState = this.machine.state();
            return this.outputState;
        }

        public boolean isComplete() {

            return this.machine.isComplete();
        }

        public int totalPresses() {

            return this.totalPresses;
        }

        @Override
        public String toString() {
            return this.id + " v" + this.totalPresses + " " + this.machine.getId() + ":" + this.machine.state() + " -> " + this.machine.targetState();
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
    }
}
