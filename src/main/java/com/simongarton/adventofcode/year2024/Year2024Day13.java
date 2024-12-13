package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;
import java.util.stream.Collectors;

public class Year2024Day13 extends AdventOfCodeChallenge {

    private List<Scenario> scenarios;

    @Override
    public String title() {
        return "Day 13: Claw Contraption";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 13);
    }

    @Override
    public String part1(final String[] input) {

        this.scenarios = this.readScenarios(input);

        int minimumTokenSpend = 0;
        for (final Scenario scenario : this.scenarios) {
            final Integer outcome = this.findMinimumTokenSpend(scenario);
            if (outcome != null) {
                minimumTokenSpend += outcome;
            }
        }

        return String.valueOf(minimumTokenSpend);
    }

    private Integer findMinimumTokenSpend(final Scenario scenario) {

        // Djikstra -> A*

        final List<Node> availableNodes = new ArrayList<>();
        final List<Node> visitedNodes = new ArrayList<>();

        final Node startNode = new Node(new Coord(0, 0), 0, null, "");
        availableNodes.add(startNode);

        long iteration = 0;

        while (!availableNodes.isEmpty()) {

            final Node node = this.optimalAvailableNode(availableNodes);

            // can I assume there is only one way ?
            if (node.c.equals(scenario.prize)) {
                System.out.println("Found at " + node);
                this.maybeAddVisitedNode(visitedNodes, node);
                break;
            }
            // System.out.println("Adding + looking at visited " + node);
            this.maybeAddVisitedNode(visitedNodes, node);
            final List<Node> neighbours = this.findNeighbours(node, scenario);
            for (final Node neighbour : neighbours) {
                // have I been here before ?
                // System.out.println("  Testing " + neighbour);
                // check this new node in my list of visited nodes ... if I've found it before
                // and it was better, I don't need to check this again.
                final Optional<Node> previousVisit = this.findBetterPreviousVisit(
                        neighbour,
                        visitedNodes);
                if (previousVisit.isPresent()) {
                    // System.out.println("    previous better " + previousVisit.get());
                    continue;
                }
                // now check to see if I've already planned to check this node.
                if (this.handleWorseOrEqualPreviousVisit(
                        neighbour,
                        availableNodes)) {
                    continue;
                }
                // what do I do about adding it in if it's already in the list ?
                // it would be a different path, so I need to check it anyway.
                // System.out.println("    Adding available " + neighbour);

                availableNodes.add(neighbour);
            }

            iteration++;
            if (iteration % 10000 == 0) {
                System.out.println(iteration + ": a " +
                        availableNodes.size() + " v " +
                        visitedNodes.size() + " (" +
                        availableNodes.get(availableNodes.size() - 1).c + ")");
            }
        }

        String presses = "";
        int buttonAPresses = 0;
        int buttonBPresses = 0;

        for (final Node node : visitedNodes) {
            if (node.c.equals(scenario.prize)) {
                System.out.println("Found prize at " + node);
                Node working = node;
                while (true) {
                    System.out.println("  " + working);
                    presses = working.buttonPress + presses;
                    // first one has no button press
                    if (working.buttonPress.equalsIgnoreCase("A")) {
                        buttonAPresses++;
                    }
                    if (working.buttonPress.equalsIgnoreCase("B")) {
                        buttonBPresses++;
                    }
                    working = working.cameFrom;
                    if (working == null) {
                        break;
                    }
                }
                System.out.println(presses);
                System.out.println("A=" + buttonAPresses);
                System.out.println("B=" + buttonBPresses);
            }
        }
        return (buttonAPresses * 3) + buttonBPresses;
    }

    private Node optimalAvailableNode(final List<Node> availableNodes) {

        final Comparator<Node> comparator = Comparator.comparing(node -> node.cost);
        final Comparator<Node> reverseComparator = comparator.reversed();
        final List<Node> sortedNodes = availableNodes.stream().sorted(reverseComparator).collect(Collectors.toList());
        final Node node = sortedNodes.get(0);
        availableNodes.remove(node);
        return node;
    }

    private void maybeAddVisitedNode(final List<Node> visitedNodes, final Node node) {

        int index = 0;
        for (final Node visitedNode : visitedNodes) {
            if (visitedNode.c.equals(node.c)) {
                if (visitedNode.cost < node.cost) {
                    return;
                }
                visitedNodes.remove(index);
                visitedNodes.add(index, node);
                return;
            }
            index++;
        }
        visitedNodes.add(index, node);
    }

    private boolean handleWorseOrEqualPreviousVisit(final Node neighbour,
                                                    final List<Node> availableNodes) {

        boolean replaced = false;
        int index = 0;
        for (final Node availableNode : availableNodes) {
            if (availableNode.c.equals(neighbour.c)) {
                if (availableNode.cost <= neighbour.cost) {
                    // visitor better, continue
                    // System.out.println("    old " + availableNode + " better than " + neighbour);
                    continue;
                }
                availableNodes.remove(index);
                availableNodes.add(index, neighbour);
                // this is never called ..
                // System.out.println("    replacing " + availableNode + " with " + neighbour);
                replaced = true;
            }
            index++;
        }
        return replaced;
    }

    private Optional<Node> findBetterPreviousVisit(final Node neighbour,
                                                   final List<Node> visitedNodes) {

        for (final Node visitedNode : visitedNodes) {
            if (visitedNode.c.equals(neighbour.c)) {
                if (visitedNode.cost < neighbour.cost) {
                    return Optional.of(visitedNode);
                }
            }
        }
        return Optional.empty();
    }

    private List<Node> findNeighbours(final Node node, final Scenario scenario) {

        final List<Node> nodes = new ArrayList<>();
        final Optional<Node> optionalNodeA = this.findNode(node, scenario.buttonA, scenario.prize);
        optionalNodeA.ifPresent(nodes::add);
        final Optional<Node> optionalNodeB = this.findNode(node, scenario.buttonB, scenario.prize);
        optionalNodeB.ifPresent(nodes::add);
        return nodes;
    }

    private Optional<Node> findNode(final Node node, final Button button, final Coord prize) {

        final Coord c = new Coord(node.c.x + button.deltaX, node.c.y + button.deltaY);
        if ((c.x > prize.x) || (c.y > prize.y)) {
            return Optional.empty();
        }
        return Optional.of(new Node(c, node.cost + 1, node, button.name));
    }

    private List<Scenario> readScenarios(final String[] input) {

        final List<Scenario> scenarios = new ArrayList<>();

        final Iterator<String> iterator = Arrays.stream(input).iterator();

        while (iterator.hasNext()) {
            final Button buttonA = this.readButton(iterator.next());
            final Button buttonB = this.readButton(iterator.next());
            final Scenario scenario = this.readScenario(iterator.next(), buttonA, buttonB);
            scenarios.add(scenario);

            if (iterator.hasNext()) {
                iterator.next();
            }
        }
        return scenarios;
    }

    private Scenario readScenario(final String line, final Button buttonA, final Button buttonB) {

        final String[] parts = line.split(": ");
        final String[] coordDetails = parts[1].split(", ");

        final int x = Integer.parseInt(coordDetails[0].replace("X=", ""));
        final int y = Integer.parseInt(coordDetails[1].replace("Y=", ""));
        final Coord coord = new Coord(x, y);

        return new Scenario(buttonA, buttonB, coord);

    }

    private Button readButton(final String line) {

        final String[] parts = line.split(": ");
        final String[] deltas = parts[1].split(", ");

        final int deltaX = Integer.parseInt(deltas[0].replace("X", ""));
        final int deltaY = Integer.parseInt(deltas[1].replace("Y", ""));

        return new Button(parts[0].replace("Button ", ""), deltaX, deltaY);
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    static class Coord {

        public int x;
        public final int y;

        public Coord(final int x, final int y) {

            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {

            return this.x + "," + this.y;
        }

        @Override
        public boolean equals(final Object o) {

            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Coord coord = (Coord) o;
            return this.x == coord.x && this.y == coord.y;
        }

        @Override
        public int hashCode() {

            return Objects.hash(this.x, this.y);
        }
    }

    static class Button {

        String name;
        int deltaX;
        int deltaY;

        public Button(final String name, final int deltaX, final int deltaY) {

            this.name = name;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }
    }

    static class Scenario {

        Button buttonA;
        Button buttonB;
        Coord prize;

        public Scenario(final Button buttonA, final Button buttonB, final Coord prize) {

            this.buttonA = buttonA;
            this.buttonB = buttonB;
            this.prize = prize;
        }
    }

    static class Node {

        Coord c;
        int cost;
        Node cameFrom;
        String buttonPress;

        public Node(final Coord c, final int cost, final Node cameFrom, final String buttonPress) {
            this.c = c;
            this.cost = cost;
            this.cameFrom = cameFrom;
            this.buttonPress = buttonPress;
        }

        @Override
        public String toString() {
            if (this.cameFrom == null) {
                return this.c + " cost=(" + this.cost + ")";
            } else {
                return this.c + " cost=(" + this.cost + ") " + this.cameFrom.c;
            }
        }
    }
}
