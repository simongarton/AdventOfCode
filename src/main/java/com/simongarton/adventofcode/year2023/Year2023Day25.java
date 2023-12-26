package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Year2023Day25 extends AdventOfCodeChallenge {

    /*

    Not getting anywhere. I can SEE the result on graphviz, but can't read it - too pixelly - and don't have any
    ideas on detecting it.

    I have a nice solution for the sample; I iterate over all permutations of removing three edges and build the
    nodes into sets by tracing through ... but it completely fails to scale.

     */

    private List<Node> nodes;
    private List<Edge> edges;
    private Set<Set<Node>> sets;

    @Override
    public String title() {
        return "Day 25: Snowverload";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 25);
    }

    @Override
    public String part1(final String[] input) {

        final boolean sample = false;

        if (!sample) {
            return String.valueOf(-1);
        }

        this.loadGraph(input);
        final List<Integer> permutation = this.findPermutationFor2SetsIterative(input, this.edges.size());
        final List<Set> finalSets = new ArrayList<>(this.sets);
        return String.valueOf(finalSets.get(0).size() * finalSets.get(1).size());
    }

    private List<Integer> findPermutationFor2SetsIterative(final String[] input, final int edgeCount) {

        for (int i = 0; i < edgeCount; i++) {
            for (int j = i + 1; j < edgeCount; j++) {
                for (int k = j + 1; k < edgeCount; k++) {
                    final List<Integer> permutation = List.of(i, j, k);
                    this.loadGraph(input);
                    for (int index = 2; index >= 0; index--) {
                        final Edge edge = this.edges.get(permutation.get(index));
                        edge.getFrom().getEdges().remove(edge);
                        edge.getTo().getEdges().remove(edge);
                        this.edges.remove(edge);
                    }
                    if (this.buildSets()) {
                        this.drawGraph(permutation.stream().map(String::valueOf).collect(Collectors.joining("-")));
                        return permutation;
                    }
                }
            }
        }
        throw new RuntimeException("Nope.");
    }

    private boolean buildSets() {
        this.sets = new HashSet<>();

        final List<Node> nodesToCheck = new ArrayList<>();
        final Set<Node> visited = new HashSet<>();
        final List<Node> allNodes = new ArrayList<>(this.nodes);

        final int iteration = 0;
        nodesToCheck.add(this.nodes.get(0));
        while (!nodesToCheck.isEmpty()) {
            final Node checkNode = nodesToCheck.remove(0);
            allNodes.remove(checkNode);
            visited.add(checkNode);
            final Set<Node> targetSet = this.setForNode(checkNode);
            targetSet.add(checkNode);
//            System.out.println(++iteration + ": " + this.nodes.size() + " nodes, " + nodesToCheck.size()
//                    + " to check, and " + this.sets.size() + " sets ... " + this.setSizes());
            for (final Node neighbour : this.getNeighbours(checkNode)) {
                if (!visited.contains(neighbour)) {
                    nodesToCheck.add(neighbour);
                }
            }
            if (nodesToCheck.isEmpty()) {
                if (!allNodes.isEmpty()) {
                    nodesToCheck.add(allNodes.get(0));
                }
            }
        }
        return this.sets.size() == 2;
    }

    private String setSizes() {
        return this.sets.stream().map(s -> String.valueOf(s.size())).collect(Collectors.joining(","));
    }

    private Set<Node> setForNode(final Node node) {
        for (final Set<Node> set : this.sets) {
            if (set.contains(node)) {
                return set;
            }
            if (this.canGetToNode(set, node)) {
                set.add(node);
                return set;
            }
        }
        final Set<Node> newSet = new HashSet<>();
        newSet.add(node);
        this.sets.add(newSet);
        return newSet;
    }

    private boolean canGetToNode(final Set<Node> set, final Node node) {
        final List<Node> nodesToCheck = new ArrayList<>();
        final Set<Node> visited = new HashSet<>();

        nodesToCheck.addAll(set);
        while (!nodesToCheck.isEmpty()) {
            final Node checkNode = nodesToCheck.remove(0);
            visited.add(checkNode);
            if (checkNode == node) {
                return true;
            }
            for (final Node neighbour : this.getNeighbours(checkNode)) {
                if (!visited.contains(neighbour)) {
                    nodesToCheck.add(neighbour);
                }
            }
        }

        return false;
    }

    private List<Node> getNeighbours(final Node node) {
        final List<Node> neighbours = new ArrayList<>();
        for (final Edge edge : node.getEdges()) {
            if (edge.getFrom() != node) {
                neighbours.add(edge.getFrom());
            }
            if (edge.getTo() != node) {
                neighbours.add(edge.getTo());
            }
        }
        return neighbours;
    }

    private void calculateScores() {

        for (final Edge edge : this.edges) {
            final Node from = edge.getFrom();
            final Node to = edge.getTo();

            int score = 0;
            for (final Edge fromEdge : from.getEdges()) {
                if (this.edgeHasNode(fromEdge, to)) {
                    continue;
                }
                score++;
            }
            for (final Edge toEdge : to.getEdges()) {
                if (this.edgeHasNode(toEdge, from)) {
                    continue;
                }
                score++;
            }
            edge.setScore(score);
            System.out.println(edge.getId() + " : " + edge.getScore());
        }
    }

    private boolean edgeHasNode(final Edge edge, final Node node) {
        return edge.getFrom() == node || edge.getTo() == node;
    }

    private void drawGraph(final String filename) {

        final List<String> lines = new ArrayList<>();
        lines.add("graph {");
        for (final Edge edge : this.edges) {
            lines.add(edge.getFrom().getId() + "--" + edge.getTo().getId());
        }
        lines.add("}");

        final Path filePath = Path.of("temp", filename + ".dot");
        try {
            Files.deleteIfExists(filePath);
            Files.createFile(filePath);
            for (final String str : lines) {
                Files.writeString(filePath, str + System.lineSeparator(),
                        StandardOpenOption.APPEND);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        try {
            this.createGraph(filename);
        } catch (final IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void createGraph(final String filename) throws IOException, InterruptedException {

        final String command = "dot temp/" + filename + ".dot -Tpng -o temp/" + filename + ".png";
        final Process process = Runtime.getRuntime().exec(String.format(command));
        final StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), System.out::println);

        final ExecutorService executorService =
                new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>());
        final Future<?> future = executorService.submit(streamGobbler);

        final int exitCode = process.waitFor();
    }

    private void loadGraph(final String[] input) {

        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();

        for (final String line : input) {
            this.loadLine(line);
        }
    }

    private void loadLine(final String line) {

        final String[] parts = line.split(":");
        final String fromId = parts[0];
        final String[] toIds = (parts[1].trim()).split(" ");

        final Node from = this.getOrCreateNode(fromId);
        for (final String toId : toIds) {
            final Node to = this.getOrCreateNode(toId);
            if (!this.hasEdge(from, to)) {
                this.addEdge(from, to);
            }
        }
    }

    private void addEdge(final Node from, final Node to) {
        if (from.getId().compareTo(to.getId()) < 1) {
            final Edge edge = Edge.builder()
                    .id(from.getId() + "-" + to.getId())
                    .from(from)
                    .to(to)
                    .build();
            this.edges.add(edge);
            from.getEdges().add(edge);
            to.getEdges().add(edge);
        } else {
            final Edge edge = Edge.builder()
                    .id(to.getId() + "-" + from.getId())
                    .from(to)
                    .to(from)
                    .build();
            this.edges.add(edge);
            from.getEdges().add(edge);
            to.getEdges().add(edge);
        }
    }

    private boolean hasEdge(final Node from, final Node to) {
        for (final Edge edge : from.getEdges()) {
            if (from == edge.getFrom() && to == edge.getTo()) {
                return true;
            }
            if (from == edge.getTo() && to == edge.getFrom()) {
                return true;
            }
        }
        return false;
    }

    private Node getOrCreateNode(final String id) {

        final Optional<Node> optionalNode = this.nodes.stream().filter(n -> n.getId().equalsIgnoreCase(id)).findFirst();
        if (optionalNode.isPresent()) {
            return optionalNode.get();
        }
        final Node node = Node.builder()
                .id(id)
                .edges(new ArrayList<>())
                .build();
        this.nodes.add(node);
        return node;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    @Data
    @Builder
    private static final class Node {

        private String id;
        private List<Edge> edges;

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Node node = (Node) o;
            return Objects.equals(this.id, node.id);
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return this.id;
        }
    }

    @Data
    @Builder
    private static final class Edge {

        private String id;
        private Node from;
        private Node to;

        private int score;
    }

    private static class StreamGobbler implements Runnable {
        private final InputStream inputStream;
        private final Consumer<String> consumer;

        public StreamGobbler(final InputStream inputStream, final Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(this.inputStream)).lines()
                    .forEach(this.consumer);
        }
    }
}
