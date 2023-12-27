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

    private List<Node> nodes;
    private List<Edge> edges;
    private Set<Set<Node>> sets;
    private Map<Edge, Integer> edgeWeights = new HashMap<>();

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

        // code works sort of OK on the sample - right answer, but I have to do something twice I don't expect to
        // but real file is ... 1000/2202256 remaining 27330.794 seconds
        final boolean sample = true;

        if (!sample) {
            return String.valueOf(-1);
        }

        this.loadGraph(input);
        this.drawGraph("test");
        final long start = System.currentTimeMillis();

        final Set<String> combinationsTried = new HashSet<>();
        this.edgeWeights = new HashMap<>();
        int iteration = 0;
        final int total = this.nodes.size() * this.nodes.size();
        for (final Node from : this.nodes) {
            for (final Node to : this.nodes) {
                if (++iteration % 1000 == 0) {
                    final long now = System.currentTimeMillis();
                    final long elapsedMillis = now - start;
                    final long millisToGo = Math.round(elapsedMillis / (iteration * 1.0 / total));
                    final long end = start + millisToGo;
                    final long remaining = end - now;
                    System.out.println(iteration + "/" + total + " remaining " + remaining / 1000.0 + " seconds");
                }
                if (from == to) {
                    // I shouldn't need this - but the sample fails without it ...
                    continue;
                }
                final String key = from.getId().compareTo(to.getId()) < 0 ? from.getId() + "-" + to.getId() : to.getId() + "-" + from.getId();
                if (combinationsTried.contains(key)) {
//                    continue;
                }
                combinationsTried.add(key);
                this.traceShortestPathAndUpdateWeights(from, to);
            }
        }

//        for (final Map.Entry<Edge, Integer> entry : this.edgeWeights.entrySet()) {
//            System.out.println(entry.getKey() + ":" + entry.getValue());
//        }

        final List<Edge> bridges = this.getTopThreeEdges();
        this.removeEdges(bridges);
        this.buildSets();
        final List<Set> setList = new ArrayList<>(this.sets);

        return String.valueOf(setList.get(0).size() * setList.get(1).size());
    }

    private List<Edge> getTopThreeEdges() {

        final List<Integer> keys = new ArrayList<>(this.edgeWeights.values());
        keys.sort(Comparator.comparing(Integer::intValue).reversed());
        final List<Edge> bridges = new ArrayList<>();
        bridges.add(this.findEdgeWeight(keys.get(0)));
        bridges.add(this.findEdgeWeight(keys.get(1)));
        bridges.add(this.findEdgeWeight(keys.get(2)));
        return bridges;
    }

    private Edge findEdgeWeight(final Integer integer) {
        for (final Map.Entry<Edge, Integer> entry : this.edgeWeights.entrySet()) {
            if (entry.getValue() == integer) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("nope.");
    }

    private void traceShortestPathAndUpdateWeights(final Node from, final Node to) {

//        System.out.println("\nGoing from " + from + " to " + to + "\n");

        final Map<Integer, List<Node>> nodeListsToCheck = new HashMap<>();
        final Set<Node> visited = new HashSet<>();
        final Map<Node, Node> cameFrom = new HashMap<>();

        int currentCost = 0;
        nodeListsToCheck.put(0, List.of(from));
        visited.add(from);

        while (true) {
            final List<Node> nextSet = nodeListsToCheck.remove(currentCost);
//            System.out.println("for cost " + currentCost + " I have " + nextSet.stream().map(Node::getId).collect(Collectors.joining(",")));
            currentCost++;
            for (final Node nextNode : nextSet) {
//                System.out.println("  working on " + nextNode);
                if (nextNode == to) {
//                    System.out.println("   got path : " + this.showShortestPath(cameFrom, from, to));
                    this.updateWeights(cameFrom, from, to);
                    // don't know about this.
                    return;
                }
                for (final Node neighbour : this.getNeighbours(nextNode)) {
                    if (!visited.contains(neighbour)) {
//                        System.out.println("    adding in neighbour " + neighbour);
                        final List<Node> more = nodeListsToCheck.getOrDefault(currentCost, new ArrayList<>());
                        more.add(neighbour);
                        nodeListsToCheck.put(currentCost, more);
                        cameFrom.put(neighbour, nextNode);
                        visited.add(neighbour);
                    } else {
//                        System.out.println("    skipping neighbour " + neighbour);
                    }
                }
            }
        }
    }

    private String showShortestPath(final Map<Node, Node> cameFrom, final Node from, final Node to) {
        for (final Map.Entry<Node, Node> entry : cameFrom.entrySet()) {
            System.out.println(entry.getKey() + " came from " + entry.getValue());
        }
        Node current = to;
        Node previous = cameFrom.get(to);
        String line = current.getId();
        int cost = 1;
        while (previous != null) {
            line = previous.getId() + "-" + line;
            current = previous;
            previous = cameFrom.get(current);
            cost++;
        }
        return line + " : " + cost;
    }

    private void updateWeights(final Map<Node, Node> cameFrom, final Node from, final Node to) {
        Node current = to;
        Node previous = cameFrom.get(to);
        while (previous != null) {
            final Edge edge = this.findEdgeBothWays(current, previous);
            this.edgeWeights.put(edge, this.edgeWeights.getOrDefault(edge, 0) + 1);
            current = previous;
            previous = cameFrom.get(current);
        }
    }

    private Edge findEdgeBothWays(final Node current, final Node previous) {
        for (final Edge edge : this.edges) {
            if (edge.getFrom() == current && edge.getTo() == previous) {
                return edge;
            }
            if (edge.getFrom() == previous && edge.getTo() == current) {
                return edge;
            }
        }
        throw new RuntimeException("Not found edge");
    }

    private void removeEdgesAsIds(final List<Integer> bridgeIds) {
        for (final Integer bridgeId : bridgeIds) {
            final Edge edge = this.edges.get(bridgeId);
            edge.getFrom().getEdges().remove(edge);
            edge.getTo().getEdges().remove(edge);
            this.edges.remove(edge);
        }
    }

    private void removeEdges(final List<Edge> bridges) {
        for (final Edge edge : bridges) {
            edge.getFrom().getEdges().remove(edge);
            edge.getTo().getEdges().remove(edge);
            this.edges.remove(edge);
        }
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

    private List<Node> findNeighboursRecursivelyNotUsingBridge(final Node node, final Node exclude) {

        final List<Node> nodesToCheck = new ArrayList<>();
        final Set<Node> visited = new HashSet<>();
        final List<Node> neighbours = new ArrayList<>();
        visited.add(exclude);

        final int iteration = 0;
        nodesToCheck.add(node);
        while (!nodesToCheck.isEmpty()) {
            final Node checkNode = nodesToCheck.remove(0);
            // don't include self
            if (node != checkNode) {
                neighbours.add(checkNode);
            }
            visited.add(checkNode);
//            System.out.println(++iteration + ": testing " + checkNode + " still got " + nodesToCheck.size()
//                    + " to check, visited = " + visited.size());
            for (final Node neighbour : this.getNeighbours(checkNode)) {
//                System.out.println("  neighbour " + neighbour);
                if (!visited.contains(neighbour) && !nodesToCheck.contains(neighbour)) {
//                    System.out.println("    adding " + neighbour);
                    nodesToCheck.add(neighbour);
                }
            }
        }
        System.out.println("node " + node + " has " + neighbours.size() + " neighbours.");
        return neighbours;
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
//            System.out.println("Checking " + checkNode);
            allNodes.remove(checkNode);
            visited.add(checkNode);
            final Set<Node> targetSet = this.setForNode(checkNode);
            targetSet.add(checkNode);
//            System.out.println(++iteration + ": " + this.nodes.size() + " nodes, " + nodesToCheck.size()
//                    + " to check, and " + this.sets.size() + " sets ... " + this.setSizes());
            for (final Node neighbour : this.getNeighbours(checkNode)) {
//                System.out.println("  found neighbour " + neighbour);
                if (!visited.contains(neighbour)) {
                    nodesToCheck.add(neighbour);
//                    System.out.println("    added neighbour " + neighbour);

                }
            }
            if (nodesToCheck.isEmpty()) {
                if (!allNodes.isEmpty()) {
                    nodesToCheck.add(allNodes.get(0));
                }
            }
        }
//        System.out.println("edges");
//        this.edges.stream().forEach(System.out::println);
//        for (final Set<Node> set : this.sets) {
//            System.out.println("buildSets() got " + set.stream().map(Node::getId).collect(Collectors.joining(",")));
//        }
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
//                System.out.println("    adding " + edge.getFrom() + " using edge " + edge);
                neighbours.add(edge.getFrom());
            }
            if (edge.getTo() != node) {
//                System.out.println("    adding " + edge.getTo() + " using edge " + edge);
                neighbours.add(edge.getTo());
            }
        }
        return neighbours;
    }

    private List<Integer> calculateSharedNeighbours(final String[] input) {

        this.loadGraph(input);
        final int edgeCount = this.edges.size();

        final List<Integer> edges = new ArrayList<>();
        for (int i = 0; i < edgeCount; i++) {
            this.loadGraph(input);
            final Edge edge = this.edges.get(i);
            final Node from = edge.getFrom();
            final Node to = edge.getTo();
            this.removeEdges(List.of(edge));
            this.drawGraph("test-tidied-" + i);
            // TODO I don't think I need to exclude any more.
            final List<Node> fromNeighbours = this.findNeighboursRecursivelyNotUsingBridge(from, to);
            final List<Node> toNeighbours = this.findNeighboursRecursivelyNotUsingBridge(to, from);
            final int score = this.countSharedNeighbours(fromNeighbours, toNeighbours);
            System.out.println("test-tidied-" + i + " with " + from + "-" + to + " got " + score);
            System.out.println(from + " : " + fromNeighbours.stream().map(Node::getId).collect(Collectors.joining(",")));
            System.out.println(to + " : " + toNeighbours.stream().map(Node::getId).collect(Collectors.joining(",")));
            if (score == 0) {
                edges.add(i);
            }
            System.out.println(edge.getId() + " : " + score);
        }
        return edges;
    }

    private List<Edge> calculateSharedNeighboursSimple() {

        final List<Edge> edges = new ArrayList<>();
        for (final Edge edge : this.edges) {
            final Node from = edge.getFrom();
            final Node to = edge.getTo();
            final List<Node> fromNeighbours = this.getNeighbours(from);
            final List<Node> toNeighbours = this.getNeighbours(to);
            final int score = this.countSharedNeighbours(fromNeighbours, toNeighbours);
            if (score == 0) {
                edges.add(edge);
            }
            System.out.println(edge.getId() + " : " + score);
        }
        return edges;
    }

    private int countSharedNeighbours(final List<Node> fromNeighbours, final List<Node> toNeighbours) {
        int score = 0;
        for (final Node from : fromNeighbours) {
            if (toNeighbours.contains(from)) {
                score++;
            }
        }
        return score;
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
