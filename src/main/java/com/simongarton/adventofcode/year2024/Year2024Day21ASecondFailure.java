package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Year2024Day21ASecondFailure extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 21: Keypad Conundrum";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 21);
    }

    @Override
    public String part1(final String[] input) {

        int total = 0;
        for (final String numericCode : input) {
            final String fullNumpadSequence = this.fullNumpadSequence(numericCode);
            final int numericPart = Integer.parseInt(numericCode.replace("A", ""));
            total += numericPart * fullNumpadSequence.length();
            System.out.println(fullNumpadSequence);
            System.out.println("  " + numericCode + ": " + numericPart + " * " + fullNumpadSequence.length() + " = " + numericPart * fullNumpadSequence.length());
        }
        return String.valueOf(total);
    }

    private String fullNumpadSequence(final String numericCode) {

        // this will be given a full numeric code, and will return a long sequence of presses
        // for the top button.

        // I can treat each char independently for the robots, but not for the numpad.
        // so for each char I need to build it up from where it was to where it's going.
        // each individual sequence for each number (or A) will end in an A, and will have other
        // As if it involves intermediate dirpads.
        String lastPosition = "A";
        final StringBuilder fullSequence = new StringBuilder();
        for (int i = 0; i < numericCode.length(); i++) {
            final String thisPosition = numericCode.substring(i, i + 1);
            fullSequence.append(this.buildShortestNumpadSequenceForASingleDigit(lastPosition, thisPosition));
            lastPosition = thisPosition;
        }
        return fullSequence.toString();
    }

    public String buildShortestNumpadSequenceForASingleDigit(final String lastPosition, final String thisPosition) {

        // last position moves around 'cos I'm a numpad
        // both will be a digit or A
        if (!this.validNumpadMovements().contains(lastPosition)) {
            throw new RuntimeException(lastPosition);
        }
        if (!this.validNumpadMovements().contains(thisPosition)) {
            throw new RuntimeException(thisPosition);
        }
        final List<String> sequences = this.buildNumpadSequencesFor(lastPosition, thisPosition);
        return this.shortest(sequences);
    }

    // now when I try and build my Dirpad sequence, I have to do it on 2 levels - and think about 25.
    // 1 is trivial and will not give me the right answer.
    // I need to do this recursively and memoize it.

    // but I don't need do do a two parter. I just need to know how many it will take for a single key press.
    // because for this to work, the dirpads all have to be on A - so I can treat them indepenently.

    public String buildShortestDirpadSequenceForMovement(final String movement, final int robotCount) {

        // I can stick a cache here for movement and robotcount
        return this.recursiveShortestDirpadSequence(movement, robotCount, 1);
    }

    private String recursiveShortestDirpadSequence(final String movement, final int robotCount, final int depth) {

        // this will be a sequence, ending in A. I have to break it apart
        final int level = depth + 1;
        if (level == robotCount) {
            return this.actualShortestDirpadSequence(movement);
        }
        final String shortestSequenceForThisLevel = this.actualShortestDirpadSequence(movement);
        return this.recursiveShortestDirpadSequence(shortestSequenceForThisLevel, robotCount, level) + "A";
    }

    private String actualShortestDirpadSequence(final String movement) {

        // this is a fresh dirpad starting on A. I hope.
        final StringBuilder fullSequence = new StringBuilder();
        for (int i = 0; i < movement.length(); i++) {
            final String thisPosition = movement.substring(i, i + 1);
            fullSequence.append(this.buildShortestDirpadSequence("A", thisPosition));
        }
        return fullSequence.toString();
    }

    private List<String> validNumpadMovements() {

        return List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A");
    }

    private String buildShortestDirpadSequence(final String lastPosition, final String thisPosition) {

        final List<String> sequences = this.buildDirpadSequencesFor(lastPosition, thisPosition);
        return this.shortest(sequences);
    }

    private String shortest(final List<String> sequences) {

        int shortestLength = Integer.MAX_VALUE;
        String shortest = "";
        for (final String sequence : sequences) {
            if (sequence.length() < shortestLength) {
                shortest = sequence;
                shortestLength = sequence.length();
            }
        }
        return shortest;
    }

    public List<String> buildNumpadSequencesFor(final String start, final String end) {

        // last position moves around 'cos I'm a numpad
        // both will be a digit or A
        if (!this.validNumpadMovements().contains(start)) {
            throw new RuntimeException(start);
        }
        if (!this.validNumpadMovements().contains(end)) {
            throw new RuntimeException(end);
        }

        // BFS
        final Node a = new Node(start, null, null);
        final List<Node> visited = new ArrayList<>();
        final List<Node> available = new ArrayList<>(this.numpadNeighboursFor(a, visited));
        visited.add(a);
        final List<Node> success = new ArrayList<>();

        while (!available.isEmpty()) {
            final Node current = available.removeFirst();
            visited.add(current);
            if (current.key.equalsIgnoreCase(end)) {
                success.add(current);
                continue;
            }
            available.addAll(this.numpadNeighboursFor(current, visited));
        }

        final List<String> sequences = new ArrayList<>();
        for (final Node node : success) {
            sequences.add(this.buildSequence(node));
        }
        return sequences;
    }

    public List<String> buildDirpadSequencesFor(final String start, final String end) {

        // BFS
        final Node a = new Node(start, null, null);
        final List<Node> visited = new ArrayList<>();
        final List<Node> available = new ArrayList<>(this.dirpadNeighboursFor(a, visited));
        visited.add(a);
        final List<Node> success = new ArrayList<>();

        while (!available.isEmpty()) {
            final Node current = available.removeFirst();
            visited.add(current);
            if (current.key.equalsIgnoreCase(end)) {
                success.add(current);
                continue;
            }
            available.addAll(this.dirpadNeighboursFor(current, visited));
        }

        final List<String> sequences = new ArrayList<>();
        for (final Node node : success) {
            sequences.add(this.buildSequence(node));
        }
        return sequences;
    }

    private String buildSequence(final Node node) {

        final StringBuilder sequence = new StringBuilder();
        Node current = node;
        while (current != null) {
            if (current.direction != null) {
                sequence.append(current.direction);
            }
            current = current.previous;
        }
        return sequence.reverse() + "A";
    }

    private List<Node> numpadNeighboursFor(final Node current, final List<Node> visited) {

        final List<List<String>> neighbourLists = this.numpadNeighbours(current.key);
        final List<String> keys = neighbourLists.get(0);
        final List<String> directions = neighbourLists.get(1);

        final List<Node> neighbours = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            final String key = keys.get(i);
            final String direction = directions.get(i);
            if (visited.stream().anyMatch(n -> this.visitedNeighbour(n, key, direction))) {
                continue;
            }
            final Node node = new Node(key, direction, current);
            neighbours.add(node);
        }

        return neighbours;
    }

    private List<Node> dirpadNeighboursFor(final Node current, final List<Node> visited) {

        final List<List<String>> neighbourLists = this.dirpadNeighbours(current.key);
        final List<String> keys = neighbourLists.get(0);
        final List<String> directions = neighbourLists.get(1);

        final List<Node> neighbours = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            final String key = keys.get(i);
            final String direction = directions.get(i);
            if (visited.stream().anyMatch(n -> this.visitedNeighbour(n, key, direction))) {
                continue;
            }
            final Node node = new Node(key, direction, current);
            neighbours.add(node);
        }

        return neighbours;
    }

    private boolean visitedNeighbour(final Node n, final String key, final String direction) {

        if (!(n.key.equalsIgnoreCase(key))) {
            return false;
        }
        if (n.direction == null) {
            return true; // I came from here
        }
        if (!(n.direction.equalsIgnoreCase(direction))) {
            return false;
        }
        return true;
    }

    private List<List<String>> numpadNeighbours(final String start) {
        return switch (start) {
            case "A" -> List.of(List.of("0", "3"), List.of("<", "^"));
            case "0" -> List.of(List.of("A", "2"), List.of(">", "^"));
            case "1" -> List.of(List.of("2", "4"), List.of(">", "^"));
            case "2" -> List.of(List.of("0", "1", "3", "5"), List.of("v", "<", ">", "^"));
            case "3" -> List.of(List.of("A", "2", "6"), List.of("v", "<", "^"));
            case "4" -> List.of(List.of("1", "5", "7"), List.of("v", ">", "^"));
            case "5" -> List.of(List.of("2", "4", "6", "8"), List.of("v", "<", ">", "^"));
            case "6" -> List.of(List.of("3", "5", "9"), List.of("v", "<", "^"));
            case "7" -> List.of(List.of("4", "8"), List.of("v", ">"));
            case "8" -> List.of(List.of("5", "7", "9"), List.of("v", "<", ">"));
            case "9" -> List.of(List.of("6", "8"), List.of("v", "<"));
            default -> throw new RuntimeException("oops");
        };
    }

    private List<List<String>> dirpadNeighbours(final String start) {
        return switch (start) {
            case "A" -> List.of(List.of("^", ">"), List.of("<", "v"));
            case "^" -> List.of(List.of("A", "v"), List.of(">", "v"));
            case "<" -> List.of(List.of("v"), List.of(">"));
            case "v" -> List.of(List.of("<", "^", ">"), List.of("<", "^", ">"));
            case ">" -> List.of(List.of("A", "v"), List.of("^", "<"));
            default -> throw new RuntimeException("oops");
        };
    }

    @Override
    public String part2(final String[] input) {

        return null;
    }

    static class Node {

        String key;
        String direction;
        Node previous;

        public Node(final String key, final String direction, final Node previous) {

            this.key = key;
            this.direction = direction;
            this.previous = previous;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Node node = (Node) o;
            return Objects.equals(this.key, node.key) && Objects.equals(this.direction, node.direction);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.key, this.direction);
        }
    }
}
