package com.simongarton.adventofcode.year2022;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.Coord;

import java.io.IOException;
import java.util.*;

public class Year2022Day24 extends AdventOfCodeChallenge {
    private static final String WALL = "#";
    private static final String DOT = ".";
    private static final String MULTIPLE_STORM = "*";
    private static final String RIGHT = ">";
    private static final String LEFT = "<";
    private static final String UP = "^";
    private static final String DOWN = "v";
    private static final String PLAYER = "P";

    private int width;
    private int height;
    private List<String> maps;
    private List<Blizzard> blizzards;
    private TerminalScreen screen;

    @Override
    public String title() {
        return "Day 24: Blizzard Basin";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2022, 24);
    }

    @Override
    public String part1(final String[] input) {
        this.loadMaps(input);
//        this.drawMap(this.maps.get(0));
        final State start = new State(0, new Coord(1, 0), this);
        start.buildNeighbours();
        final State end = new State(-1, new Coord(this.width - 2, this.height - 1), this);
        final List<State> states = this.aStar(start, end);
//        this.safelyAnimateStates(states);
//        this.analyseStates(states);
        return String.valueOf(states.size() - 1); // -1 as includes start
    }

    @Override
    public String part2(final String[] input) {
        this.loadMaps(input);
//        this.drawMap(this.maps.get(0));
        final State top = new State(0, new Coord(1, 0), this);
        top.buildNeighbours();
        final State bottom = new State(-1, new Coord(this.width - 2, this.height - 1), this);
        bottom.buildNeighbours();
        final List<State> statesOne = this.aStar(top, bottom);
        final List<State> statesTwo = this.aStar(statesOne.get(statesOne.size() - 1), top);
        final List<State> statesThree = this.aStar(statesTwo.get(statesTwo.size() - 1), bottom);
        final List<State> allStates = new ArrayList<>();
        allStates.addAll(statesOne);
        allStates.addAll(statesTwo);
        allStates.addAll(statesThree);
//        this.safelyAnimateStates(allStates);
        return String.valueOf(allStates.size() - 3); // -1 for each as includes start
    }

    private void loadMaps(final String[] input) {
        this.maps = new ArrayList<>();
        this.maps.add(String.join("", Arrays.asList(input)));
        this.width = input[0].length();
        this.height = input.length;
//        System.out.printf("Map is %s x %s area %s\n", this.width, this.height, this.width * this.height);
        this.loadBlizzards(this.maps.get(0));
    }

    private String getOrCreateMap(final int index) {
        if (index < this.maps.size()) {
            return this.maps.get(index);
        }
        if (index > this.maps.size()) {
            throw new RuntimeException("trying to get a map that hasn't been created.");
        }
        this.addMap();
        return this.maps.get(index);
    }

    private void loadBlizzards(final String map) {
        this.blizzards = new ArrayList<>();
        for (int col = 0; col < this.width; col++) {
            for (int row = 0; row < this.height; row++) {
                final String symbol = this.getSymbol(map, col, row);
                if (symbol.equalsIgnoreCase(WALL)) {
                    continue;
                }
                if (symbol.equalsIgnoreCase(DOT)) {
                    continue;
                }
                this.blizzards.add(new Blizzard(new Coord(col, row), symbol));
            }
        }
    }

    private String getSymbol(final String map, final int col, final int row) {
        if (col < 0 || col >= this.width) {
            return "x";
        }
        if (row < 0 || row >= this.height) {
            return "x";
        }
        return map.substring((row * this.width) + col, (row * this.width) + col + 1);
    }

    private String updateMapWithSymbol(final String map, final int col, final int row, final String symbol) {
        return map.substring(0, (row * this.width) + col) +
                symbol +
                map.substring((row * this.width) + col + 1);
    }

    private void drawMap(final String map) {
        for (int row = 0; row < (map.length() / this.width); row++) {
            System.out.println(map.substring(row * this.width, (row + 1) * this.width));
        }
        System.out.println("");
    }

    private void addMap() {
        String newMap = this.maps.get(this.maps.size() - 1)
                .replace(DOWN, DOT)
                .replace(UP, DOT)
                .replace(LEFT, DOT)
                .replace(RIGHT, DOT)
                .replace(MULTIPLE_STORM, DOT);
        for (final Blizzard blizzard : this.blizzards) {
            newMap = this.moveBlizzardAndUpdateMap(newMap, blizzard);
        }
        this.maps.add(newMap);
    }

    private String moveBlizzardAndUpdateMap(String newMap, final Blizzard blizzard) {
        switch (blizzard.symbol) {
            case RIGHT:
                blizzard.position.setX(blizzard.position.getX() + 1);
                if (blizzard.position.getX().equals(this.width - 1)) {
                    blizzard.position.setX(1);
                }
                break;
            case LEFT:
                blizzard.position.setX(blizzard.position.getX() - 1);
                if (blizzard.position.getX().equals(0)) {
                    blizzard.position.setX(this.width - 2);
                }
                break;
            case UP:
                blizzard.position.setY(blizzard.position.getY() - 1);
                if (blizzard.position.getY().equals(0)) {
                    blizzard.position.setY(this.height - 2);
                }
                break;
            case DOWN:
                blizzard.position.setY(blizzard.position.getY() + 1);
                if (blizzard.position.getY().equals(this.height - 1)) {
                    blizzard.position.setY(1);
                }
                break;
            default:
                throw new RuntimeException(blizzard.symbol);
        }
        switch (this.getSymbol(newMap, blizzard.position.getX(), blizzard.position.getY())) {
            case DOT:
                newMap = this.updateMapWithSymbol(newMap, blizzard.position.getX(), blizzard.position.getY(), blizzard.symbol);
                return newMap;
            case MULTIPLE_STORM:
            case RIGHT:
            case LEFT:
            case UP:
            case DOWN:
                newMap = this.updateMapWithSymbol(newMap, blizzard.position.getX(), blizzard.position.getY(), MULTIPLE_STORM);
                return newMap;
            default:
                throw new RuntimeException(this.getSymbol(newMap, blizzard.position.getX(), blizzard.position.getY()));
        }
    }

    public static final class Blizzard {
        private final Coord position;
        private final String symbol;

        public Blizzard(final Coord coord, final String symbol) {
            this.position = coord;
            this.symbol = symbol;
        }
    }

    public static final class State {
        private final int iteration;
        private final Coord position;
        private final Year2022Day24 challenge;
        private final List<State> neighbours;

        public State(final int iteration,
                     final Coord position,
                     final Year2022Day24 challenge) {
            this.iteration = iteration;
            this.position = position;
            this.challenge = challenge;
            this.neighbours = new ArrayList<>();
        }

        public void buildNeighbours() {
            final String map = this.challenge.getOrCreateMap(this.iteration + 1);
            if (this.challenge.getSymbol(map, this.position.getX(), this.position.getY()).equalsIgnoreCase(DOT)) {
                this.neighbours.add(new State(this.iteration + 1, this.position, this.challenge));
            }
            if (this.challenge.getSymbol(map, this.position.getX() - 1, this.position.getY()).equalsIgnoreCase(DOT)) {
                final Coord newPosition = new Coord(this.position.getX() - 1, this.position.getY());
                this.neighbours.add(new State(this.iteration + 1, newPosition, this.challenge));
            }
            if (this.challenge.getSymbol(map, this.position.getX() + 1, this.position.getY()).equalsIgnoreCase(DOT)) {
                final Coord newPosition = new Coord(this.position.getX() + 1, this.position.getY());
                this.neighbours.add(new State(this.iteration + 1, newPosition, this.challenge));
            }
            if (this.challenge.getSymbol(map, this.position.getX(), this.position.getY() - 1).equalsIgnoreCase(DOT)) {
                final Coord newPosition = new Coord(this.position.getX(), this.position.getY() - 1);
                this.neighbours.add(new State(this.iteration + 1, newPosition, this.challenge));
            }
            if (this.challenge.getSymbol(map, this.position.getX(), this.position.getY() + 1).equalsIgnoreCase(DOT)) {
                final Coord newPosition = new Coord(this.position.getX(), this.position.getY() + 1);
                this.neighbours.add(new State(this.iteration + 1, newPosition, this.challenge));
            }
        }

        public String key() {
            return this.iteration + " (" + this.position + ") ";
        }

        @Override
        public String toString() {
            return this.iteration + " (" + this.position + ") n=" + this.neighbours.size();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final State state = (State) o;
            return this.key().equalsIgnoreCase(state.key());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.key());
        }
    }

    private List<State> aStar(final State start, final State end) {
        final boolean debug = false;

        final Set<State> openSet = new HashSet<>(Collections.singleton(start));
        final Map<State, State> cameFrom = new HashMap<>();
        final Map<State, Integer> costToGetHereFromStart = new HashMap<>();
        costToGetHereFromStart.put(start, 0);

        final Map<State, Integer> rankingScoreAsToHerePlusEstimateToEnd = new HashMap<>();
        rankingScoreAsToHerePlusEstimateToEnd.put(start, this.estimateCostToEnd(start, start));

        final int bestSoFar = Integer.MAX_VALUE;

        while (!openSet.isEmpty()) {
            final State current = this.bestOpenSetWithLowestFScoreValue(openSet, rankingScoreAsToHerePlusEstimateToEnd);
            if (costToGetHereFromStart.get(current) >= bestSoFar) {
                openSet.remove(current);
                continue;
            }
            if (Objects.equals(current.position.toString(), end.position.toString())) {
                return this.reconstructPath(cameFrom, current);
            }
            this.debugPrint(debug, "working on / removing current " + current + " with openSet.size()=" + openSet.size());
            openSet.remove(current);
            if (debug) {
                System.out.println("current " + current + "\n");
                this.drawMapWithPlayer(this.maps.get(current.iteration), current.position);
            }
            for (final State neighbor : current.neighbours) {
                // tentative_gScore is the distance from start to the neighbor through current
                final int tentative_gScore = costToGetHereFromStart.get(current) + this.cost(current, neighbor);
                this.debugPrint(debug, "  checking neighbour " + neighbor + " tentative_gScore=" + tentative_gScore);
                if (tentative_gScore < costToGetHereFromStart.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    // This path to neighbor is better than any previous one. Record it!
                    this.debugPrint(debug, "     storing neighbour " + neighbor);
                    cameFrom.put(neighbor, current);
                    costToGetHereFromStart.put(neighbor, tentative_gScore);
                    rankingScoreAsToHerePlusEstimateToEnd.put(neighbor, tentative_gScore + this.estimateCostToEnd(neighbor, end));
                    // calculate neighbours lazily
                    neighbor.buildNeighbours();
                    openSet.add(neighbor);
                } else {
                    this.debugPrint(debug, "     ignoring neighbour " + neighbor);
                }
            }
        }

        // Open set is empty but goal was never reached
        throw new RuntimeException("AStar failed.");
    }

    private void drawMapWithPlayer(final String map, final Coord position) {
        final String yetAnotherMap = this.updateMapWithSymbol(map, position.getX(), position.getY(), PLAYER);
        this.drawMap(yetAnotherMap);
    }

    private void debugPrint(final boolean debug, final String s) {
        if (debug) {
            System.out.println(s);
        }
    }

    private State bestOpenSetWithLowestFScoreValue(final Set<State> openSet, final Map<State, Integer> fScore) {
        State best = null;
        int bestValue = Integer.MAX_VALUE;
        for (final State cell : openSet) {
            final int cost = fScore.get(cell); // may not be there ?
            if (best == null || bestValue > cost) {
                best = cell;
                bestValue = cost;
            }
        }
        return best;
    }

    private Integer estimateCostToEnd(final State start, final State end) {
        return this.manhattan(start, end);
    }

    private Integer manhattan(final State start, final State end) {
        return start.position.manhattanDistance(end.position);
    }

    private Integer cost(final State current, final State neighbor) {
        // if this is positive, I never get there
//        return -1;
        // if I do this, I must make it a bad idea to stay still
        // return current.position.manhattanDistance(neighbor.position);
        return 1;
    }

    private List<State> reconstructPath(final Map<State, State> cameFrom, final State end) {
        State current = end;
        final List<State> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        return path;
    }

    private void animateStates(final List<State> states) throws IOException, InterruptedException {
        final Terminal terminal = new DefaultTerminalFactory().createTerminal();
        ((SwingTerminalFrame) terminal).setTitle("AdventOfCode 2022.5 : Supply Stacks");
        ((SwingTerminalFrame) terminal).setSize(800, 600);
        this.screen = new TerminalScreen(terminal);
        this.screen.setCursorPosition(null);

        this.screen.startScreen();

        for (final State state : states) {
            this.drawScreen(state);
            Thread.sleep(250);
        }

        Thread.sleep(3000);

//        while (true) {
//            final KeyStroke keyStroke = this.screen.pollInput();
//            if (keyStroke != null && (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF)) {
//                break;
//            }
//        }
    }

    private void drawScreen(final State state) throws IOException {
        final String map = this.getOrCreateMap(state.iteration);
        this.screen.clear();
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                final String mapSymbol = this.getSymbol(map, col, row);
                final TextColor foregroundColor = this.colorForSymbol(mapSymbol);
                this.drawString(mapSymbol, col, row, foregroundColor, TextColor.ANSI.BLACK);
            }
        }
        this.drawString(PLAYER, state.position.getX(), state.position.getY(), TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.BLACK);
        this.screen.refresh();
    }

    private TextColor colorForSymbol(final String mapSymbol) {
        switch (mapSymbol) {
            case MULTIPLE_STORM:
                return TextColor.ANSI.WHITE_BRIGHT;
            case DOWN:
                return TextColor.ANSI.YELLOW;
            case UP:
                return TextColor.ANSI.RED;
            case RIGHT:
                return TextColor.ANSI.GREEN;
            case LEFT:
                return TextColor.ANSI.CYAN;
            case WALL:
                return TextColor.ANSI.WHITE;
            case DOT:
                return TextColor.ANSI.BLACK_BRIGHT;
            default:
                throw new RuntimeException(mapSymbol);
        }
    }

    private void drawChar(final char c, final int x, final int y, final TextColor foreground, final TextColor background) {
        final TextCharacter textCharacter = new TextCharacter(c, foreground, background);
        this.screen.setCharacter(new TerminalPosition(x, y), textCharacter);
    }

    private void drawString(final String s, final int x, final int y, final TextColor foreground, final TextColor background) {
        for (int i = 0; i < s.length(); i++) {
            this.drawChar(s.charAt(i), x + i, y, foreground, background);
        }
    }

    private void safelyAnimateStates(final List<State> states) {
        try {
            this.animateStates(states);
        } catch (final IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void analyseStates(final List<State> states) {
        Coord position = new Coord(0, 0);
        final Map<String, Integer> visits = new HashMap<>();
        for (final State state : states) {
            String line = state.iteration + ": " + state.position;
            final String key = state.position.toString();
            if (position.toString().equalsIgnoreCase(key)) {
                line += "; unmoved";
            }
            if (visits.containsKey(key)) {
                line += "; has been here " + visits.get(key) + " times before";
            }
            visits.put(key, visits.getOrDefault(key, 0) + 1);
            System.out.println(line);
            position = state.position;
        }
    }
}
