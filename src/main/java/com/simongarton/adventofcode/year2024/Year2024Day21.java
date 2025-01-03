package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Getter;

import java.util.*;

public class Year2024Day21 extends AdventOfCodeChallenge {

    /*

    I don't think I can ever have weird non-shortest paths on the dirpad cos only two rows, can't leave and come back

    I think I want to drop the non shortest paths anyway. I'm getting 216 options for 378A.

     */

    private Map<String, Map<String, List<String>>> numPadSequences;
    private Map<String, Map<String, List<String>>> dirPadSequences;

    private final List<String> numPadButtons = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A");
    private final List<String> dirPadButtons = List.of("<", ">", "^", "v", "A");

    public Year2024Day21() {

        super();
        this.setupSequences();
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

        int total = 0;
        for (final String numericCode : input) {
            final String fullSequence = this.shortestFullSequence(numericCode);
            final int numericPart = Integer.parseInt(numericCode.replace("A", ""));
            total += numericPart * fullSequence.length();
            System.out.println(fullSequence);
            System.out.println("  " + numericCode + ": " + numericPart + " * " + fullSequence.length() + " = " + numericPart * fullSequence.length());
        }
        return String.valueOf(total);
    }

    private void setupSequences() {

        this.setupNumPadSequences();
        this.setupDirPadSequences();
    }

    private void setupDirPadSequences() {

        this.dirPadSequences = new HashMap<>();
        for (final String from : this.dirPadButtons) {
            this.dirPadSequences.put(from, new HashMap<>());
            final Map<String, List<String>> currentMap = this.dirPadSequences.get(from);
            for (final String to : this.dirPadButtons) {
                final List<String> paths = this.getDirPadPaths(from, to);
                currentMap.put(to, paths);
            }
        }
    }

    private void setupNumPadSequences() {

        this.numPadSequences = new HashMap<>();
        for (final String from : this.numPadButtons) {
            this.numPadSequences.put(from, new HashMap<>());
            final Map<String, List<String>> currentMap = this.numPadSequences.get(from);
            for (final String to : this.numPadButtons) {
                final List<String> paths = this.getNumPadPaths(from, to);
                currentMap.put(to, paths);
            }
        }
    }

    public List<String> getNumPadPaths(final String start, final String end) {

        final KeypadNode a = new KeypadNode(start, null, null);
        final List<KeypadNode> visited = new ArrayList<>();
        final List<KeypadNode> available = new ArrayList<>(this.numpadNeighboursFor(a, visited));
        visited.add(a);
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

        final List<String> sequences = new ArrayList<>();
        for (final KeypadNode keypadNode : success) {
            sequences.add(this.buildSequence(keypadNode));
        }
        return sequences;
    }

    public List<String> getDirPadPaths(final String start, final String end) {

        final KeypadNode a = new KeypadNode(start, null, null);
        final List<KeypadNode> visited = new ArrayList<>();
        final List<KeypadNode> available = new ArrayList<>(this.dirpadNeighboursFor(a, visited));
        visited.add(a);
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

        final List<String> sequences = new ArrayList<>();
        for (final KeypadNode keypadNode : success) {
            sequences.add(this.buildSequence(keypadNode));
        }
        return sequences;
    }

    private String buildSequence(final KeypadNode keypadNode) {

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

        final List<List<String>> neighbourLists = this.numpadNeighbours(current.key);
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

    private List<KeypadNode> dirpadNeighboursFor(final KeypadNode current, final List<KeypadNode> visited) {

        final List<List<String>> neighbourLists = this.dirpadNeighbours(current.key);
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

    public String shortestFullSequence(final String numericCode) {

        /*

         This will be given a full numeric code, and will return a long sequence of presses
         that I must type in.

         I am typing directly into robot 2 and can tap any of it's buttons in any order. This moves robot 2's
         arm which is hovering over robot 1's directional keypad. When I tap an A, robot 2's arm taps a button on
         robot 1's directional keypad, which will either move robot 1's arm around the numeric keypad, or (if it was
         an `A`) actually press a button on the numeric keypad.

         Since there is often more than one way to move a robot arm from one button to another, and Eric goes on about
         making sure it's the shortest path, I probably need to worry about this ... e.g. is `<<v` better than `v<<`
         or `<v<` ?

         My approach is going to be that I have two classes for the keypads; and they will pre-calculate all the ways
         in which you can move around. I can then use ... probably BFS, I'm not sure if Djikstra might not miss
         something ? - to build up all the trees of moves.

         I'll also have functions to map any movement between any two buttons so that I can do this recursively.

         As an example, I want to press `1`.

         I will need to move from `A` the starting point to `1` by going up and then left twice (or left/up/left) and
         then pressing the `A` on the numeric keypad.

         Which means robot 1 must be told to go `^<<A`.

         The first `^` will be done by pressing `<A` on robot 2; that leaves me on the `^` key on robot 1, so I need to
         then press `v<AA` on robot 2 to get the two lefts on robot 1; finally `>>^A` moves robot 2's arm to robot 1's
         `A` button, and presses it, and robot 1's arm is currently on the `1` on the numpad which will get pressed.

         So `1` came from `^<<A` which in turn came from `<Av<AA>>^A`

         Critical points:

         - Every move on either keypad - numeric and directional - needs to know where it started from.
         - Every button press means that the particular keypad is at `A` the starting point.

         Why this is important is that when I'm e.g. getting robot 2 to press `^<<A` on robot 1 ... those 4 key presses
         can be treated independently, BUT the moves for each key press need to maintain state.

         */

        return shortestKeypadSequence(numericCode, 2);
    }

    public String shortestKeypadSequence(final String numericSequence, final int robotLevel) {

        final Node rootNode = this.buildNodeForNumericSequence(numericSequence, robotLevel);
        final Map<Integer, List<Node>> levels = this.buildLevels(rootNode);

        final int maxLevel = levels.keySet().stream().max(Integer::compareTo).get();
        final List<String> sequences = levels.get(maxLevel).stream().map(n -> n.sequence).toList();
        return this.shortest(sequences).getFirst();
    }

    public Map<Integer, List<Node>> buildLevels(final Node rootNode) {

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

    public Node buildNodeForNumericSequence(final String numericSequence, final int robotLevel) {

        if (robotLevel < 1) {
            throw new RuntimeException("need at least 1 robot, not " + robotLevel);
        }

        // this is going to return a single node e.g. "3" or "378A". But that node could have more than one
        // to generate it, e.g. "2" could be "<^A" or "^<A".

        final Node rootNode = new Node(numericSequence, 0);
        final List<Node> firstDirPadNodes = this.buildNodesForNumPad(numericSequence);
        rootNode.generatedBy.addAll(firstDirPadNodes);

        final List<Node> available = new ArrayList<>(firstDirPadNodes);

        while (!available.isEmpty()) {

            final Node current = available.removeFirst();
            if (current.robotLevel == robotLevel) {
                continue;
            }
            final List<Node> nextLevelNodes = this.buildNodesForDirPad(current.sequence, current.robotLevel + 1);
            for (final Node node : nextLevelNodes) {
                current.generatedBy.add(node);
                available.add(node);
            }
        }

        return rootNode;
    }

    private List<Node> buildNodesForDirPad(final String sequence, final int robotLevel) {

        // this has to be a list as there can be more than one way of doing it.

        final List<Node> nodes = new ArrayList<>();
        final List<String> keyPressesForSequence = this.buildDirPadKeyPressesForSequence(sequence);
        for (final String keyPressSequence : keyPressesForSequence) {
            final Node node = new Node(keyPressSequence, robotLevel);
            nodes.add(node);
        }
        return nodes;
    }

    private List<Node> buildNodesForNumPad(final String numericCode) {

        // this has to be a list as there can be more than one way of doing it.

        final int robotLevel = 1;

        final List<Node> nodes = new ArrayList<>();
        final List<String> keyPressesForSequence = this.buildNumPadKeyPressesForSequence(numericCode);
        for (final String keyPressSequence : keyPressesForSequence) {
            final Node node = new Node(keyPressSequence, robotLevel);
            nodes.add(node);
        }
        return nodes;
    }

    private Node buildShortestNodeForNumPad(final String numericCode) {

        // this is not a list, it's just a node - because I'm not moving an arm around on a keypad,
        // there is only one possible option, no alternate paths. No, I'm wrong.

        String armPosition = "A";
        final StringBuilder fullSequence = new StringBuilder();
        for (int i = 0; i < numericCode.length(); i++) {
            final String buttonToPress = numericCode.substring(i, i + 1);
            fullSequence.append(this.buildShortestKeyPressSequence(armPosition, buttonToPress, 0));
            armPosition = buttonToPress;
        }

        return new Node(fullSequence.toString(), 1);
    }

    public String buildShortestKeyPressSequence(final String armPosition, final String buttonToPress, final int robotsInvolved) {

        final List<String> possibleSequences = this.buildKeyPressSequences(armPosition, buttonToPress, robotsInvolved);

        // shortest returns a list of the possibly-more-than-one sequences of the shortest length. if there is more than one, it doesn't
        // matter which one I return. This is always just between two buttons.
        return this.shortest(possibleSequences).getFirst();
    }

    public List<String> buildKeyPressSequences(final String armPosition, final String buttonToPress, final int robotsInvolved) {

        // this is the first time I need to find a sequence, and I'm on a numeric keypad, so I need to find out the sequences for this.
        final List<String> keyPressSequences = this.getNumPadSequences(armPosition, buttonToPress);

        // at this point for A -> 3 I have List.of(^A) (but I might have more than one.)

        // if I have no robots, I can just return this (won't happen, but useful for testing.)
        if (robotsInvolved == 0) {
            return keyPressSequences;
        }

        // Ok, I need a robot to press this sequence. Now, it's a sequence, so I can treat it in it's entirety, but while
        // working on it, I will need to manage state. Also, if I have more than one, I need to include all the branching
        // paths I might go down.

        final List<String> keyPressSequencesRecursed = new ArrayList<>();
        keyPressSequences.forEach(s -> keyPressSequencesRecursed.addAll(this.buildDirPadKeyPressesForSequence(s, robotsInvolved, 1)));

        return keyPressSequencesRecursed;
    }

    // this is the recursive one which will split things up
    private List<String> buildDirPadKeyPressesForSequence(final String sequence, final int robotsInvolved, final int currentRobot) {

        // I have a sequence like "^<A". I need to work out what the next robot needs to do to get this pressed
        // I am later going to also test "<^A".

        // any given input sequence might have more than one solution, so I will get lists back.

        // if I have reached the final level of robot, turn it into key presses.
        final List<String> keyPressesForSequence = this.buildDirPadKeyPressesForSequence(sequence);
        if (currentRobot == robotsInvolved) {
            return keyPressesForSequence;
        }

        // I need to break this up into individual presses ... I have ^<A but I need to split it into
        // ^ < and A and each one will have trees ... but I need to combine them, so the trees from < get added to
        // each of the trees from ^ and so on ...
        // which sounds like BFS

        // I have a sequence like "^<A" which I need to get a robot to type out on another directional keypad.
        // this is where I start needing to build a tree
        // and I think I should just BFS this.


        return null;
    }

    // this is the non recursive one which will actually implement the key presses, with state
    public List<String> buildDirPadKeyPressesForSequence(final String sequence) {

        // I have a sequence like "^<A" which I need to get a robot to type out on another directional keypad.
        // this is where I start needing to build a tree
        // and I think I should just BFS this.

        final List<KeyPressNode> available = new ArrayList<>();
        final List<KeyPressNode> complete = new ArrayList<>();

        final String start = "A";
        final String second = sequence.substring(0, 1);

        final List<String> firstSequences = this.getDirPadSequences(start, second);
        // firstSequences is now List.of(<A, v<^A) which will give me the keys I need to get from A to <
        for (final String padSequence : firstSequences) {
            final KeyPressNode keyPressNode = new KeyPressNode(second, second, padSequence, null);
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
            final List<String> currentSequences = this.getDirPadSequences(currentKey, nextKey);
            for (final String padSequence : currentSequences) {
                final KeyPressNode keyPressNode = new KeyPressNode(nextKey, current.keysPressedSoFar + nextKey, current.sequenceToPressKey + padSequence, current);
                available.add(keyPressNode);
            }
        }

        return complete.stream().map(KeyPressNode::getSequenceToPressKey).sorted(Comparator.naturalOrder()).toList();
    }

    public List<String> buildNumPadKeyPressesForSequence(final String sequence) {

        final List<KeyPressNode> available = new ArrayList<>();
        final List<KeyPressNode> complete = new ArrayList<>();

        final String start = "A";
        final String second = sequence.substring(0, 1);

        final List<String> firstSequences = this.getNumPadSequences(start, second);
        for (final String padSequence : firstSequences) {
            final KeyPressNode keyPressNode = new KeyPressNode(second, second, padSequence, null);
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
            final List<String> currentSequences = this.getNumPadSequences(currentKey, nextKey);
            for (final String padSequence : currentSequences) {
                final KeyPressNode keyPressNode = new KeyPressNode(nextKey, current.keysPressedSoFar + nextKey, current.sequenceToPressKey + padSequence, current);
                available.add(keyPressNode);
            }
        }

        return complete.stream().map(KeyPressNode::getSequenceToPressKey).sorted(Comparator.naturalOrder()).toList();
    }

    public List<String> getNumPadSequences(final String armPosition, final String buttonToPress) {

        return this.numPadSequences.get(armPosition).get(buttonToPress);
    }

    public List<String> getNumPadShortestSequences(final String armPosition, final String buttonToPress) {

        return this.shortest(this.numPadSequences.get(armPosition).get(buttonToPress));
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
        return sequences.stream().filter(s -> s.length() == targetLength).sorted(Comparator.naturalOrder()).toList();
    }

    @Override
    public String part2(final String[] input) {

        return null;
    }

    static class KeypadNode {

        String key;
        String direction;
        KeypadNode previous;

        public KeypadNode(final String key, final String direction, final KeypadNode previous) {

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
            final KeypadNode keypadNode = (KeypadNode) o;
            return Objects.equals(this.key, keypadNode.key) && Objects.equals(this.direction, keypadNode.direction);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.key, this.direction);
        }
    }

    static class KeyPressNode {

        String keyPressed;
        String keysPressedSoFar; // will end with keyPressed
        @Getter
        String sequenceToPressKey;
        KeyPressNode previous;

        public KeyPressNode(final String keyPressed,
                            final String keysPressedSoFar,
                            final String sequenceToPressKey,
                            final KeyPressNode previous) {

            this.keyPressed = keyPressed;
            this.keysPressedSoFar = keysPressedSoFar;
            this.sequenceToPressKey = sequenceToPressKey;
            this.previous = previous;
        }
    }

    public static class Node {

        String sequence;
        int robotLevel;
        List<Node> generatedBy;

        public Node(final String sequence, final int robotLevel) {

            this.sequence = sequence;
            this.robotLevel = robotLevel;
            this.generatedBy = new ArrayList<>();
        }
    }
}
