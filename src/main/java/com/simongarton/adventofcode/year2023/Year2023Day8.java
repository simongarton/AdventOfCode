package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Year2023Day8 extends AdventOfCodeChallenge {

    private Node firstNode;
    private Node lastNode;
    private Node currentNode;
    private List<Node> currentNodes;
    private final Map<String, Node> nodeMap = new HashMap<>();
    private String directions;
    private int index;

    @Override
    public String title() {
        return "Day 8: XXX";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 8);
    }

    @Override
    public String part1(final String[] input) {

        this.loadMap(input);
        long steps = 0;
        this.firstNode = this.nodeMap.get("AAA");
        this.lastNode = this.nodeMap.get("ZZZ");
        this.currentNode = this.firstNode;
        while (!this.currentNode.getId().equalsIgnoreCase(this.lastNode.getId())) {
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
        long steps = 0;

        this.currentNodes = new ArrayList<>();
        for (final Map.Entry<String, Node> node : this.nodeMap.entrySet()) {
            if (node.getKey().substring(2, 3).equalsIgnoreCase("A")) {
                this.currentNodes.add(this.nodeMap.get(node.getKey()));
            }
        }

        System.out.println(steps + " " + this.currentNodes.size());
        this.debugNodes(steps);
        while (!this.finished()) {
            this.moveMany();
            steps++;
//            System.out.println(steps + " " + this.currentNodes.size());
            this.debugNodes(steps);
        }

        return String.valueOf(steps);
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

    private boolean finished() {
        for (final Node node : this.currentNodes) {
            if (!node.getId().substring(2, 3).equalsIgnoreCase("Z")) {
                return false;
            }
        }
        return true;
    }

    private void moveMany() {

        final String direction = this.directions.substring(this.index, this.index + 1);
        this.index = this.index + 1;
        if (this.index == this.directions.length()) {
            this.index = 0;
        }

        final List<Node> originals = new ArrayList<>();
        final List<Node> splits = new ArrayList<>();
        for (Node node : this.currentNodes) {
            final boolean both = (node.getId().substring(2, 3).equalsIgnoreCase("A"));
            if (direction.equalsIgnoreCase("L")) {
                node = this.nodeMap.get(node.getLeft());
                if (both) {
                    splits.add(this.nodeMap.get(node.getRight()));
                }
            } else {
                node = this.nodeMap.get(node.getRight());
                if (both) {
                    splits.add(this.nodeMap.get(node.getLeft()));
                }
            }
            originals.add(node);
        }
        this.currentNodes.clear();
        this.currentNodes.addAll(originals);
//        this.currentNodes.addAll(splits);
    }

    @Data
    @Builder
    private static final class Node {

        private String id;
        private String left;
        private String right;
    }
}
