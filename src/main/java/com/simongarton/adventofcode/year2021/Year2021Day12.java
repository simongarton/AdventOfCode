package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;
import java.util.stream.Collectors;

public class Year2021Day12 extends AdventOfCodeChallenge {

    private List<Node> nodes;
    private List<Edge> edges;
    private List<String> journeys;
    private boolean allowDoubleVisits;

    @Override
    public String title() {
        return "Day 12: Passage Pathing";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2021, 12);
    }

    @Override
    public String part1(final String[] input) {
        this.buildNodes(input);
        this.journeys = new ArrayList<>();
        this.allowDoubleVisits = false;
        this.buildPaths();
        final long result = this.journeys.size();
        return String.valueOf(result);
    }

    @Override
    public String part2(final String[] input) {
        this.buildNodes(input);
        this.journeys = new ArrayList<>();
        this.allowDoubleVisits = true;
        this.buildPaths();
        final long result = this.journeys.size();
        return String.valueOf(result);
    }

    private void graphViz() {
        System.out.println("digraph {");
        System.out.println("rankdir=LR;");
        for (final Edge edge : this.edges) {
            System.out.println(edge.from.name + " -> " + edge.to.name);
        }
        System.out.println("}");
    }

    private void buildNodes(final String[] lines) {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        for (final String line : lines) {
            this.addEdge(line);
        }
    }

    private void buildPaths() {
        final Stack<String> journey = new Stack<>();
        journey.push("start");
        this.recursiveSearch(journey);
    }

    private void recursiveSearch(final Stack<String> journey) {
        final Node current = this.getNode(journey.peek());
        for (final Node next : current.endNodes()) {
            if (!this.alreadyVisited(next, journey)) {
                journey.push(next.name);
                if (next.name.equalsIgnoreCase("end")) {
                    final String map = this.mapJourney(journey);
                    this.journeys.add(map);
                } else {
                    this.recursiveSearch(journey);
                }
                journey.pop();
            }
        }
    }

    private boolean alreadyVisited(final Node next, final Stack<String> journey) {
        if (next.name.equals(next.name.toUpperCase(Locale.ROOT))) {
            return false;
        }
        if (this.allowDoubleVisits) {
            return this.itsComplicated(next, journey);
        }
        return this.mapJourney(journey).contains("-" + next.name + "-");
    }

    private boolean itsComplicated(final Node next, final Stack<String> journey) {
        final String name = next.name;
        final String map = this.mapJourney(journey);
        final boolean contained = map.contains("-" + next.name + "-");
        if (!contained) {
            return false;
        }
        if (name.equalsIgnoreCase("start") || name.equalsIgnoreCase("end")) {
            return contained;
        }
        final Map<String, Integer> counts = new HashMap<>();
        for (final String nodeName : journey) {
            if (nodeName.equals(nodeName.toUpperCase(Locale.ROOT))) {
                continue;
            }
            counts.put(nodeName, counts.getOrDefault(nodeName, 0) + 1);
        }
        return counts.values().stream().anyMatch(i -> i > 1);
    }

    private Node getNode(final String name) {
        return this.nodes.stream().filter(n -> n.name.equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new RuntimeException(name + " not found"));
    }

    private String mapJourney(final List<String> journey) {
        return String.join("-", journey);
    }

    private void addEdge(final String line) {
        final String[] parts = line.split("-");
        final String fromName = parts[0];
        final String toName = parts[1];
        final Node from = this.maybeAddNode(fromName);
        final Node to = this.maybeAddNode(toName);
        if (!to.name.equalsIgnoreCase("start") &&
                !from.name.equalsIgnoreCase("end")) {
            final Edge edgeForwards = new Edge(from, to);
            this.edges.add(edgeForwards);
            from.edges.add(edgeForwards);
        }
        if (!to.name.equalsIgnoreCase("end") &&
                !from.name.equalsIgnoreCase("start")) {
            final Edge edgeBack = new Edge(to, from);
            this.edges.add(edgeBack);
            to.edges.add(edgeBack);
        }
    }

    private Node maybeAddNode(final String name) {
        Node node = this.nodes.stream().filter(n -> n.name.equalsIgnoreCase(name)).findFirst().orElse(null);
        if (node == null) {
            node = new Node(name);
            this.nodes.add(node);
        }
        return node;
    }


    private static final class Node {
        private final String name;
        private final List<Edge> edges;

        public Node(final String name) {
            this.name = name;
            this.edges = new ArrayList<>();
        }

        public List<Node> endNodes() {
            return this.edges.stream().map(e -> e.to).collect(Collectors.toList());
        }
    }

    private static final class Edge {
        private final Node from;
        private final Node to;

        public Edge(final Node from, final Node to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return "Edge{" + this.from.name + "-" + this.to.name + "}";
        }
    }
}
