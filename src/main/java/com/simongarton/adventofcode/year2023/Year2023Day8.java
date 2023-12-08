package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Year2023Day8 extends AdventOfCodeChallenge {

    private Node currentNode;
    private List<Node> currentNodes;
    private final Map<String, Node> nodeMap = new HashMap<>();
    private String directions;
    private int index;

    @Override
    public String title() {
        return "Day 8: Haunted Wasteland";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 8);
    }

    @Override
    public String part1(final String[] input) {

        this.loadMap(input);
        long steps = 0;
        final Node firstNode = this.nodeMap.get("AAA");
        final Node lastNode = this.nodeMap.get("ZZZ");
        this.currentNode = firstNode;
        while (!this.currentNode.getId().equalsIgnoreCase(lastNode.getId())) {
            this.move();
            steps++;
        }

        return String.valueOf(steps);
    }

    private void loadMap(final String[] input) {

        this.directions = input[0];

        for (int i = 2; i < input.length; i++) {
            final String[] parts = input[i].split(" = ");
            final String id = parts[0];
            final String rest = parts[1].replace("(", "").replace(")", "");
            final String[] directions = rest.split(",");
            final Node node = Node.builder()
                    .id(id)
                    .left(directions[0].trim())
                    .right(directions[1].trim())
                    .build();
            this.nodeMap.put(id, node);
        }
    }

    private void move() {
        final String direction = this.directions.substring(this.index, this.index + 1);
        this.index = this.index + 1;
        if (this.index == this.directions.length()) {
            this.index = 0;
        }
        if (direction.equalsIgnoreCase("L")) {
            this.currentNode = this.nodeMap.get(this.currentNode.getLeft());
        } else {
            this.currentNode = this.nodeMap.get(this.currentNode.getRight());
        }
    }

    @Override
    public String part2(final String[] input) {

        this.loadMap(input);

        this.currentNodes = new ArrayList<>();
        for (final Map.Entry<String, Node> node : this.nodeMap.entrySet()) {
            if (node.getKey().substring(2, 3).equalsIgnoreCase("A")) {
                this.currentNodes.add(this.nodeMap.get(node.getKey()));
            }
        }

        // insight : because we have to keep going until we're ALL at a Z, have a look at the
        // patterns for EACH one. Count how many steps between EACH time we hit a Z, repeat a few
        // times until it looks like a pattern or not.
        final List<Long> patterns = new ArrayList<>();
        for (final Node node : this.currentNodes) {
            patterns.add(this.findPatterns(node));
        }

        final long result = this.getLowestCommonMultiple(patterns);

        return String.valueOf(result);
    }

    private long getLowestCommonMultiple(final List<Long> patterns) {
        final Long[] longs = new Long[patterns.size()];
        return lcm(patterns.toArray(longs));
    }

    private static long gcd(long a, long b) {
        while (b > 0) {
            final long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    private static long lcm(final long a, final long b) {
        return a * (b / gcd(a, b));
    }

    private static long lcm(final Long[] input) {
        long result = input[0];
        for (int i = 1; i < input.length; i++) {
            result = lcm(result, input[i]);
        }
        return result;
    }

    private long findPatterns(final Node node) {
        this.currentNode = node;
        long steps = 0;
        long zs = 0;
        final List<Long> pattern = new ArrayList<>();
        while (true) {
            this.move();
            steps++;
            if (this.currentNode.getId().substring(2, 3).equalsIgnoreCase("Z")) {
                pattern.add(steps);
                if (++zs == 5) {
                    break;
                }
            }
        }
//        System.out.println(node.getId() + ":" + pattern.stream().map(String::valueOf).collect(Collectors.joining(",")));
        final List<Long> deltas = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            deltas.add(pattern.get(i) - pattern.get(i - 1));
        }
//        System.out.println("  cycle" + ":" + deltas.stream().map(String::valueOf).collect(Collectors.joining(",")));
        return deltas.get(0);
    }

    private void debugNodes(final long steps) {
        String line = "";
        for (final Node node : this.currentNodes) {
            line = line + node.getId() + ",";
        }
        if (steps % 1000000 == 0) {
            System.out.println(steps + ":" + line.substring(0, line.length() - 1));
        }
    }

    @Data
    @Builder
    private static final class Node {

        private String id;
        private String left;
        private String right;
    }
}
