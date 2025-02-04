package com.simongarton.adventofcode.year2024;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Getter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Year2024Day21 extends AdventOfCodeChallenge {


    private Map<String, Map<String, List<String>>> numPadSequences;
    private Map<String, Map<String, List<String>>> dirPadSequences;
    private static final boolean SAVE_SEQUENCE_MAPS = false;
    private static final boolean DEBUG = false;

    private final Map<String, List<String>> cache;

    private final List<String> numPadButtons = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A");
    private final List<String> dirPadButtons = List.of("<", ">", "^", "v", "A");

    public Year2024Day21() {

        super();
        this.setupSequences();
        this.cache = new HashMap<>();
    }

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

        return this.commonLogic(input, 4);
    }

    public String part1Old(final String[] input) {

        return this.oldCommonLogic(input, 3);
    }

    private String oldCommonLogic(final String[] input, final int keypads) {

        long total = 0;
        for (final String numericCode : input) {
            final String sequence = this.shortestFullSequence(numericCode, keypads);
            final int numericPart = Integer.parseInt(numericCode.replace("A", ""));
            total += (long) numericPart * sequence.length();
            if (DEBUG) {
                System.out.println("  " + numericCode + ": " + numericPart + " * " + sequence.length() + " = " + numericPart * sequence.length());
            }
        }
        return String.valueOf(total);
    }

    private void setupSequences() {

        // called from the constructor, this does some BFS to work out what sequences of movements
        // are needed to go between buttons ... for both keyboards ... and stores them
        // just for reference and checking (and my Python version borrowed them.)

        this.setupNumPadSequences();
        this.setupDirPadSequences();
    }

    private void setupNumPadSequences() {

        final String filename = "numPadPaths.json";

        this.numPadSequences = new HashMap<>();
        for (final String from : this.numPadButtons) {
            this.numPadSequences.put(from, new HashMap<>());
            final Map<String, List<String>> currentMap = this.numPadSequences.get(from);
            for (final String to : this.numPadButtons) {
                final List<String> paths = this.getNumPadPaths(from, to);
                currentMap.put(to, paths);
            }
        }

        if (SAVE_SEQUENCE_MAPS) {
            try (final Writer writer = new FileWriter(filename)) {
                final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
                gson.toJson(this.numPadSequences, writer);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<String> getNumPadPaths(final String start, final String end) {

        // use BFS to find the moves needed to go from start to end on a num pad,
        // and end in an A to activate it
        // e.g for 1 -> 5, I'll get both ">^A" and "^>A".
        // I previously also got ">>^<A" and "^^>vA" but have pruned out anything that isn't minimum length.

        // interesting gotcha that caught me out for ages - on the keypads I will need to tap
        // the same key twice in a row ...
        if (start.equalsIgnoreCase(end)) {
            return List.of("A");
        }

        final KeypadNode startNode = new KeypadNode(start, null, null);
        final List<KeypadNode> visited = new ArrayList<>();
        final List<KeypadNode> available = new ArrayList<>(this.numpadNeighboursFor(startNode, visited));
        visited.add(startNode);
        final List<KeypadNode> success = new ArrayList<>();

        while (!available.isEmpty()) {
            final KeypadNode current = available.removeFirst();
            visited.add(current);
            if (current.key.equalsIgnoreCase(end)) {
                success.add(current);
                continue;
            }
            available.addAll(this.numpadNeighboursFor(current, visited));
        }

        return this.buildFilterAndSortSequences(success);
    }

    private List<String> buildFilterAndSortSequences(final List<KeypadNode> success) {

        final List<String> sequences = new ArrayList<>();
        for (final KeypadNode keypadNode : success) {
            sequences.add(this.buildSequence(keypadNode));
        }

        final int minLength = sequences.stream()
                .map(String::length)
                .min(Integer::compareTo)
                .orElseThrow();

        return sequences.stream()
                .filter(s -> s.length() == minLength)
                .filter(s -> !this.hasZigZags(s))
                .toList();

    }

    private void setupDirPadSequences() {

        final String filename = "dirPadPaths.json";

        this.dirPadSequences = new HashMap<>();
        for (final String from : this.dirPadButtons) {
            this.dirPadSequences.put(from, new HashMap<>());
            final Map<String, List<String>> currentMap = this.dirPadSequences.get(from);
            for (final String to : this.dirPadButtons) {
                final List<String> paths = this.getDirPadPaths(from, to);
                currentMap.put(to, paths);
            }
        }

        if (SAVE_SEQUENCE_MAPS) {
            try (final Writer writer = new FileWriter(filename)) {
                final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
                gson.toJson(this.dirPadSequences, writer);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public List<String> getDirPadPaths(final String start, final String end) {

        // use BFS to find the moves needed to go from start to end on a dir pad,
        // and end in an A to activate it

        // interesting gotcha that caught me out for ages - on the keypads I will need to tap
        // the same key twice in a row ...
        if (start.equalsIgnoreCase(end)) {
            return List.of("A");
        }

        final KeypadNode startNode = new KeypadNode(start, null, null);
        final List<KeypadNode> visited = new ArrayList<>();
        final List<KeypadNode> available = new ArrayList<>(this.dirpadNeighboursFor(startNode, visited));
        visited.add(startNode);
        final List<KeypadNode> success = new ArrayList<>();

        while (!available.isEmpty()) {
            final KeypadNode current = available.removeFirst();
            visited.add(current);
            if (current.key.equalsIgnoreCase(end)) {
                success.add(current);
                continue;
            }
            available.addAll(this.dirpadNeighboursFor(current, visited));
        }

        return this.buildFilterAndSortSequences(success);
    }

    public boolean hasZigZags(final String s) {

        // there must be a cleverer way than this

        final List<String> upDown = List.of("^", "v");
        final List<String> leftRight = List.of("<", ">");

        final String first = s.substring(0, 1);
        final boolean upDownFirst = (upDown.contains(first));
        boolean gotOther = false;
        for (int i = 1; i < s.length(); i++) {
            final String current = s.substring(i, i + 1);
            if (current.equalsIgnoreCase("A")) {
                continue;
            }
            if (upDownFirst) {
                if (leftRight.contains(current)) {
                    gotOther = true;
                    continue;
                }
            } else {
                if (upDown.contains(current)) {
                    gotOther = true;
                    continue;
                }
            }
            if (gotOther) {
                if (!upDownFirst) {
                    if (leftRight.contains(current)) {
                        return true;
                    }
                } else {
                    if (upDown.contains(current)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String buildSequence(final KeypadNode keypadNode) {

        // turns my node tree into a simple string, with an A on the end

        final StringBuilder sequence = new StringBuilder();
        KeypadNode current = keypadNode;
        while (current != null) {
            if (current.direction != null) {
                sequence.append(current.direction);
            }
            current = current.previous;
        }
        return sequence.reverse() + "A";
    }

    private List<KeypadNode> numpadNeighboursFor(final KeypadNode current, final List<KeypadNode> visited) {

        // neighbour support for BFS, numpad version

        return this.neighboursFor(current, visited, this.numpadNeighbours(current.key));
    }

    private List<KeypadNode> dirpadNeighboursFor(final KeypadNode current, final List<KeypadNode> visited) {

        // neighbour support for BFS, dirpad version

        return this.neighboursFor(current, visited, this.dirpadNeighbours(current.key));
    }

    private List<KeypadNode> neighboursFor(final KeypadNode current, final List<KeypadNode> visited,
                                           final List<List<String>> neighbourLists) {

        // neighbour support for BFS, checking to see if I have hit this button in the same direction ...
        // this gives me support for  ^>v as well as > which I don't think I really need.

        final List<String> keys = neighbourLists.get(0);
        final List<String> directions = neighbourLists.get(1);

        final List<KeypadNode> neighbours = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            final String key = keys.get(i);
            final String direction = directions.get(i);
            if (visited.stream().anyMatch(n -> this.visitedNeighbour(n, key, direction))) {
                continue;
            }
            final KeypadNode keypadNode = new KeypadNode(key, direction, current);
            neighbours.add(keypadNode);
        }

        return neighbours;
    }

    private boolean visitedNeighbour(final KeypadNode n, final String key, final String direction) {

        if (!(n.key.equalsIgnoreCase(key))) {
            return false;
        }
        if (n.direction == null) {
            return true; // I came from here
        }
        return n.direction.equalsIgnoreCase(direction);
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

    public String shortestFullSequence(final String numericSequence, final int directionalKeypads) {

        // this is the magic - and the seventh or eighth iteration of this logic :facepalm -
        // for part 1. if you try part 2 or do 4 characters at once, it's just too big

        final List<String> sequencesForDigit = new ArrayList<>();

        String start = "A";
        for (int i = 0; i < numericSequence.length(); i++) {
            // this is one character
            final String end = numericSequence.substring(i, i + 1);
            // this is the taps I need to do - with state - to get this character
            final List<String> numPadSequences = this.getNumPadSequences(start, end);
            start = end;
            // this is my root node for this one character
            final Node rootNode = new Node(end, 0);
            final List<Node> firstDirPadNodes = new ArrayList<>();
            for (final String numPadSequence : numPadSequences) {
                final Node childNode = new Node(numPadSequence, 1);
                firstDirPadNodes.add(childNode);
            }
            rootNode.generatedBy.addAll(firstDirPadNodes);

            // now go off and fill it out
            this.expandRootNode(new ArrayList<>(firstDirPadNodes), directionalKeypads);

            sequencesForDigit.add(this.shortestKeypadSequenceForNode(rootNode, directionalKeypads));
        }

        return String.join("", sequencesForDigit);
    }

    private void expandRootNode(final List<Node> available, final int directionalKeypads) {

        // use a dirpad to expand out the nodes for everything in available ... each of which
        // are already linked to a root node.

        while (!available.isEmpty()) {

            final Node current = available.removeFirst();
            if (current.robotLevel == directionalKeypads) {
                continue;
            }

            final List<Node> nextLevelNodes = this.buildNodesForDirPad(current.sequence, current.robotLevel + 1);
            for (final Node node : nextLevelNodes) {
                current.generatedBy.add(node);
                available.add(node);
            }
        }
    }

    public String shortestKeypadSequenceForNode(final Node rootNode, final int robotLevel) {

        // I've got hopefully a fully expanded tree of nodes, with the top level being my final numpad
        // sequence, first level down being the presses on the first dirpad, and so on.  Break it into
        // levels so that I can figure out the max branch depth, which gives me the leaves, so I can
        // then find all the sequences and pick the first of the shortest.

        final Map<Integer, List<Node>> levels = this.buildLevels(rootNode);

        final int maxLevel = levels.keySet().stream().max(Integer::compareTo).orElseThrow();

        // I ran into this a few times until I handled double key presses.
        if (maxLevel < robotLevel) {
            throw new RuntimeException("something broke with max level " + maxLevel + " but robot level " + robotLevel);
        }

        final List<String> sequences = levels.get(maxLevel).stream().map(n -> n.sequence).toList();
        return this.shortest(sequences).getFirst();
    }

    public Map<Integer, List<Node>> buildLevels(final Node rootNode) {

        // splits out all the children (plus me) into hierarchy levels, and I can then find the
        // deepest branch to get the leaves.

        final Map<Integer, List<Node>> levels = new HashMap<>();
        levels.put(rootNode.robotLevel, List.of(rootNode));

        final List<Node> available = new ArrayList<>(rootNode.generatedBy);

        while (!available.isEmpty()) {
            final Node current = available.removeFirst();
            final List<Node> nodesAtLevel = levels.getOrDefault(current.robotLevel, new ArrayList<>());
            nodesAtLevel.add(current);
            levels.put(current.robotLevel, nodesAtLevel);
            available.addAll(current.generatedBy);
        }

        return levels;
    }

    private List<Node> buildNodesForDirPad(final String sequence, final int robotLevel) {

        // for this long sequence, at this robot level, build up my nodes.
        // this has to be a list as there can be more than one way of doing it.

        final List<Node> nodes = new ArrayList<>();

        // there's a cache here ...
        final List<String> keyPressesForSequence = this.buildDirPadKeyPressesForSequence(sequence, robotLevel);
        // System.out.println("buildNodesForDirPad() sequence " + sequence + " and got " + keyPressesForSequence.size());
        for (final String keyPressSequence : keyPressesForSequence) {
            final Node node = new Node(keyPressSequence, robotLevel);
            nodes.add(node);
        }
        return nodes;
    }

    public List<String> buildDirPadKeyPressesForSequence(final String sequence, final int robotLevel) {

        // I have a sequence like "^<A" which I need to get a robot to type out on another directional keypad.
        // this is where I start needing to build a tree
        // and I think I should just BFS this.
        // it's fast for part 1, but not good enough for part 2, even with memoisation. I'm seeing
        // available go up to 250k, sequence was "<v<A>A<A>>^AvA^A<v<A>>^AvAA<^A>A<vA>^AA<v<A>^A>AvA^A"

        final String key = robotLevel + ":" + sequence;
        if (this.cache.containsKey(key)) {
            return this.cache.get(key);
        }

        final List<KeyPressNode> available = new ArrayList<>();
        final List<KeyPressNode> complete = new ArrayList<>();

        final String start = "A";
        final String second = sequence.substring(0, 1);

        final List<String> firstSequences = this.getOptimalDirPadSequences(start, second);
        for (final String padSequence : firstSequences) {
            final KeyPressNode keyPressNode = new KeyPressNode(second, second, padSequence);
            available.add(keyPressNode);
        }

        while (!available.isEmpty()) {
            final KeyPressNode current = available.removeFirst();

            if (current.keysPressedSoFar.equalsIgnoreCase(sequence)) {
                complete.add(current);
                continue;
            }
            final String currentKey = current.keyPressed;
            final int currentKeyIndex = current.keysPressedSoFar.length();
            final String nextKey = sequence.substring(currentKeyIndex, currentKeyIndex + 1);
            final List<String> currentSequences = this.getOptimalDirPadSequences(currentKey, nextKey);
            if (currentSequences.isEmpty()) {
                throw new RuntimeException("failed to go from " + currentKey + " to " + nextKey);
            }
            for (final String padSequence : currentSequences) {
                final KeyPressNode keyPressNode = new KeyPressNode(nextKey, current.keysPressedSoFar + nextKey, current.sequenceToPressKey + padSequence);
                available.add(keyPressNode);
            }
        }

        if (complete.isEmpty()) {
            throw new RuntimeException("couldn't buildDirPadKeyPressesForSequence() for " + sequence);
        }

        final List<String> completedAndSorted = complete.stream()
                .map(KeyPressNode::getSequenceToPressKey)
                .sorted(Comparator.naturalOrder()).toList();
        this.cache.put(key, completedAndSorted);
        return completedAndSorted;
    }

    public List<String> getNumPadSequences(final String armPosition, final String buttonToPress) {

        return this.numPadSequences.get(armPosition).get(buttonToPress);
    }

    public List<String> getNumPadShortestSequences(final String armPosition, final String buttonToPress) {

        return this.shortest(this.numPadSequences.get(armPosition).get(buttonToPress));
    }

    public List<String> getOptimalDirPadSequences(final String armPosition, final String buttonToPress) {

        // find the shortest option

        final List<String> sequences = this.getDirPadSequences(armPosition, buttonToPress);

        final int minLength = sequences.stream()
                .map(String::length)
                .min(Integer::compareTo)
                .orElseThrow();

        return sequences.stream()
                .filter(s -> s.length() == minLength)
                .toList();
    }

    public List<String> getDirPadSequences(final String armPosition, final String buttonToPress) {

        return this.dirPadSequences.get(armPosition).get(buttonToPress);
    }

    private List<String> shortest(final List<String> sequences) {

        // this will return a list, as there may be more than one.
        int shortestLength = Integer.MAX_VALUE;
        for (final String sequence : sequences) {
            if (sequence.length() < shortestLength) {
                shortestLength = sequence.length();
            }
        }

        final int targetLength = shortestLength;
        return sequences.stream()
                .filter(s -> s.length() == targetLength)
                .sorted(Comparator.naturalOrder()).toList();
    }

    @Override
    public String part2(final String[] input) {

        // 27 keypads = 25 robots
        return this.commonLogic(input, 27);
    }

    private String commonLogic(final String[] input, final int keypads) {

        long total = 0;
        for (final String numericCode : input) {
            final Map<String, Long> cache = new HashMap<>();
            final long sequenceLength = this.shortestSequenceRecursively(numericCode, 1, keypads, cache);
            final long numericPart = Long.parseLong(numericCode.replace("A", ""));
            total += numericPart * sequenceLength;
            if (DEBUG) {
                System.out.println("  " + numericCode + ": " + numericPart + " * " + sequenceLength + " = " + numericPart * sequenceLength);
            }
        }
        return String.valueOf(total);
    }

    public List<String> buildKeySequences(final String sequence) {

        final List<String> result = new ArrayList<>();
        if (this.isDirpadSequence(sequence)) {
            this.buildDirKeySequenceRecursively(sequence, 0, "A", "", result, 0);
        } else {
            this.buildNumKeySequenceRecursively(sequence, 0, "A", "", result, 0);
        }

        return result;
    }

    private boolean isDirpadSequence(final String sequence) {

        // this is hacky ... but is working. I didn't build in
        // a reliable way to test what level I'm at ...
        final List<String> arrows = List.of("<", "^", "v", ">");
        for (int i = 0; i < sequence.length(); i++) {
            if (arrows.contains(sequence.substring(i, i + 1))) {
                return true;
            }
        }
        return false;
    }

    public void buildDirKeySequenceRecursively(final String sequence,
                                               final int index,
                                               final String previousKey,
                                               final String currentPath,
                                               final List<String> result,
                                               final int recursionLevel) {

        if (index == sequence.length()) {
            result.add(currentPath);
            return;
        }

        final String currentKey = sequence.substring(index, index + 1);
        final List<String> pathsBetweenKeys = this.getDirPadSequences(previousKey, currentKey);

        for (final String path : pathsBetweenKeys) {
            this.buildDirKeySequenceRecursively(sequence,
                    index + 1,
                    currentKey,
                    currentPath + path,
                    result,
                    recursionLevel + 1);
        }
    }

    public void buildNumKeySequenceRecursively(final String sequence,
                                               final int index,
                                               final String previousKey,
                                               final String currentPath,
                                               final List<String> result,
                                               final int recursionLevel) {

        if (index == sequence.length()) {
            result.add(currentPath);
            return;
        }

        final String currentKey = sequence.substring(index, index + 1);
        final List<String> pathsBetweenKeys = this.getNumPadSequences(previousKey, currentKey);

        for (final String path : pathsBetweenKeys) {
            this.buildNumKeySequenceRecursively(sequence,
                    index + 1,
                    currentKey,
                    currentPath + path,
                    result,
                    recursionLevel + 1);
        }
    }

    public long shortestSequenceRecursively(final String sequence,
                                            final int level,
                                            final int maxLevel,
                                            final Map<String, Long> cache) {

        final String key = level + ":" + sequence;

        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        if (level == maxLevel) {
            return sequence.length();
        }

        long total = 0;

        String fromKey = "A";
        for (int i = 0; i < sequence.length(); i++) {

            final String toKey = sequence.substring(i, i + 1);
            final List<String> options = !this.isDirpadSequence(sequence) ? this.getNumPadSequences(fromKey, toKey) : this.getDirPadSequences(fromKey, toKey);

            long shortest = Long.MAX_VALUE;
            for (final String option : options) {
                final long optionLength = this.shortestSequenceRecursively(option, level + 1, maxLevel, cache);
                if (shortest > optionLength) {
                    shortest = optionLength;
                }
            }
            total += shortest;
            fromKey = toKey;
        }

        cache.put(key, total);
        return total;
    }

    record KeypadNode(String key, String direction, Year2024Day21.KeypadNode previous) {

        @Override
        public boolean equals(final Object o) {

            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final KeypadNode keypadNode = (KeypadNode) o;
            return Objects.equals(this.key, keypadNode.key) && Objects.equals(this.direction, keypadNode.direction);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.key, this.direction);
        }
    }

    static class KeyPressNode {

        final String keyPressed;
        final String keysPressedSoFar; // will end with keyPressed
        @Getter
        String sequenceToPressKey;

        public KeyPressNode(final String keyPressed,
                            final String keysPressedSoFar,
                            final String sequenceToPressKey) {

            this.keyPressed = keyPressed;
            this.keysPressedSoFar = keysPressedSoFar;
            this.sequenceToPressKey = sequenceToPressKey;
        }

        @Override
        public String toString() {
            return this.keyPressed + " (" + this.keysPressedSoFar + ") [" + this.sequenceToPressKey + "]";
        }
    }

    public static class Node {

        final String sequence;
        final int robotLevel;
        final List<Node> generatedBy;

        public Node(final String sequence, final int robotLevel) {

            this.sequence = sequence;
            this.robotLevel = robotLevel;
            this.generatedBy = new ArrayList<>();
        }

        @Override
        public String toString() {

            return this.sequence + " (" + this.robotLevel + ") [" + this.generatedBy.size() + "]";
        }

        @Override
        public boolean equals(final Object o) {
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Node node = (Node) o;
            return this.robotLevel == node.robotLevel && Objects.equals(this.sequence, node.sequence);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.sequence, this.robotLevel);
        }
    }
}