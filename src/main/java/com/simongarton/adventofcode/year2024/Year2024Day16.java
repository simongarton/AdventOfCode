package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.ChallengeCoord;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Year2024Day16 extends AdventOfCodeChallenge {

    private static final int BITMAP_SCALE = 4;

    private static final boolean DEBUG = true;

    private static final String START = "S";
    private static final String END = "E";

    public static final String RIGHT = ">";
    public static final String LEFT = "<";
    public static final String UP = "^";
    public static final String DOWN = "v";

    @Override
    public String title() {
        return "Day 16: Reindeer Maze";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 16);
    }

    @Override
    public String part1(final String[] input) {

        this.emptyTempFolder();

        this.loadChallengeMap(input);

        final ChallengeCoord startCoord = this.findStart();
        final ChallengeCoord endCoord = this.findEnd();

        final List<State> available = new ArrayList<>();
        final List<State> visited = new ArrayList<>();

        final List<State> hits = new ArrayList<>();

        final State startState = new State(startCoord, 1, 0, null, ".");
        available.add(startState);

        State workingState;
        int time = 0;
        double bestDistance = Double.MAX_VALUE;
        while (!available.isEmpty()) {
            final int index = this.bestOfAvailable(available);
            workingState = available.remove(index);
            time++;

            if (DEBUG) {
                final double distance = this.pythag(workingState.coord, endCoord);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    System.out.println("Closest at " + workingState.coord +
                            " going to " + endCoord +
                            " distance " + Math.round(distance) +
                            " with moves " + workingState.moves +
                            " visited " + visited.size() +
                            " available " + available.size() +
                            " at time " + time);
                    this.paintChallengeMapWithState(workingState, time, available);
                }
            }

            if (workingState.coord.equals(endCoord)) {
                hits.add(workingState);
                if (DEBUG) {
                    System.out.println("Found endCoord at " + time + " with " + workingState + " and I have " + available.size() + " left.");
                }
                // if I got there, I don't need to check neighbours
                continue;
            }
            final List<State> neighbours = this.getAvailableStates(workingState, visited);
            this.addOrUpdateNeighbours(available, neighbours, true);
            visited.add(workingState);
        }

        int score = Integer.MAX_VALUE;
        State bestState = null;
        for (final State hit : hits) {
            if (DEBUG) {
                this.paintChallengeMapWithState(hit, ++time, available);
                System.out.println(
                        "Hit at " + hit.coord +
                                " came from " + hit.previousState.coord +
                                " with moves " + hit.moves);
                System.out.println("  " + this.getPath(hit));
            }
            if (hit.moves < score) {
                score = hit.moves;
                bestState = hit;
            }
        }
        this.paintChallengeMapWithState(bestState, ++time, available);

        return String.valueOf(score);
    }

    private void addNeighbours(final List<State> available, final List<State> neighbours) {
        this.addOrUpdateNeighbours(available, neighbours, false);
    }

    private void addOrUpdateNeighbours(final List<State> available,
                                       final List<State> neighbours,
                                       final boolean update) {

        for (final State neighbour : neighbours) {
            if (!available.contains(neighbour)) {
                available.add(neighbour);
                continue;
            }
            final State original = available.stream().filter(s -> s.key().equalsIgnoreCase(neighbour.key())).findFirst().orElseThrow();
            if (original.moves < neighbour.moves) {
                continue;
            }
            if (update) {
                available.remove(original);
            }
            available.add(neighbour);
        }
    }

    private int bestOfAvailable(final List<State> available) {

        double distance = Double.MAX_VALUE;
        int best = -1;
        int index = 0;
        for (final State state : available) {
            final double thisDistance = state.moves;
            if (thisDistance < distance) {
                best = index;
                distance = thisDistance;
            }
            index++;
        }
        return best;
    }

    private double pythag(final ChallengeCoord coord, final ChallengeCoord endCoord) {
        return Math.sqrt(
                Math.pow(endCoord.getX() - coord.getX(), 2) +
                        Math.pow(endCoord.getY() - coord.getY(), 2)
        );
    }

    private String getPath(final State workingState) {
        final StringBuilder line = new StringBuilder(workingState.action);
        State nextState = workingState.previousState;
        while (nextState != null) {
            line.append(nextState.action);
            nextState = nextState.previousState;
        }
        return line.reverse().toString();
    }

    private void paintChallengeMapWithState(final State startState, final int time, final List<State> available) {

        final List<String> lines = new ArrayList<>(this.challengeMap);
        State workingState = startState;
        while (workingState != null) {
            this.updateWithState(lines, workingState, "O");
            workingState = workingState.previousState;
        }
        for (final State state : available) {
            this.updateWithState(lines, state, "?");
        }
        this.paintMap(time, lines);
    }

    private void updateWithState(final List<String> lines,
                                 final State state,
                                 final String symbol) {

        final ChallengeCoord coord = state.coord;
        final String line = lines.get(coord.getY());
        final String oldSymbol = line.charAt(coord.getX()) + "";
        if (oldSymbol.equalsIgnoreCase("E")) {
            return;
        }
        final String symbolToUse = symbol == null ? this.symbolForDirection(state.direction) : symbol;
        final String newLine = line.substring(0, coord.getX()) + symbolToUse + line.substring(coord.getX() + 1);
        lines.add(coord.getY(), newLine);
        lines.remove(coord.getY() + 1);
    }

    private String symbolForDirection(final int direction) {
        switch (direction) {
            case 0:
                return UP;
            case 1:
                return RIGHT;
            case 2:
                return DOWN;
            case 3:
                return LEFT;
            default:
                throw new RuntimeException("oops");
        }
    }

    private List<State> getAvailableStates(final State workingState, final List<State> visited) {

        final List<State> neighbours = new ArrayList<>();

        // pick up going forward as a default if it's empty
        this.maybeAddState(workingState, visited, neighbours, workingState.direction);

        // now test the left and right
        if (this.looksInteresting(workingState, (workingState.direction + 1) % 4)) {
            this.addTurningState(workingState, visited, neighbours, (workingState.direction + 1) % 4, "R");
        }
        if (this.looksInteresting(workingState, (workingState.direction + 3) % 4)) {
            this.addTurningState(workingState, visited, neighbours, (workingState.direction + 3) % 4, "L");
        }

        return neighbours;
    }

    private boolean looksInteresting(final State workingState, final int newDirection) {
        switch (newDirection) {
            case 0:
                return !this.getChallengeMapSymbol(workingState.coord.getX(), workingState.coord.getY() - 1).equalsIgnoreCase(WALL);
            case 1:
                return !this.getChallengeMapSymbol(workingState.coord.getX() + 1, workingState.coord.getY()).equalsIgnoreCase(WALL);
            case 2:
                return !this.getChallengeMapSymbol(workingState.coord.getX(), workingState.coord.getY() + 1).equalsIgnoreCase(WALL);
            case 3:
                return !this.getChallengeMapSymbol(workingState.coord.getX() - 1, workingState.coord.getY()).equalsIgnoreCase(WALL);
            default:
                throw new RuntimeException("oops");
        }
    }

    private void maybeAddState(final State workingState,
                               final List<State> visited,
                               final List<State> neighbours,
                               final int direction) {

        final Optional<ChallengeCoord> optImmediateCoord = this.possibleNextCoord(workingState.coord, direction);
        if (optImmediateCoord.isPresent()) {
            final State state = new State(
                    optImmediateCoord.get(),
                    workingState.direction,
                    workingState.moves + 1,
                    workingState,
                    "F");
            if (!visited.contains(state)) {
                neighbours.add(state);
            }
        }
    }

    private void addTurningState(final State workingState,
                                 final List<State> visited,
                                 final List<State> neighbours,
                                 final int direction,
                                 final String action) {

        final State state = new State(
                workingState.coord,
                direction,
                workingState.moves + 1000,
                workingState,
                action);

        if (!visited.contains(state)) {
            neighbours.add(state);
        }
    }

    private Optional<ChallengeCoord> possibleNextCoord(final ChallengeCoord coord, final int direction) {

        final ChallengeCoord nextCoord = this.nextCoord(coord, direction);
        final String thing = this.getChallengeMapSymbol(nextCoord);
        if (!thing.equalsIgnoreCase(WALL)) {
            return Optional.of(nextCoord);
        }
        return Optional.empty();
    }

    private ChallengeCoord nextCoord(final ChallengeCoord coord, final int direction) {

        ChallengeCoord nextCoord = null;
        switch (direction) {
            case 0:
                nextCoord = new ChallengeCoord(coord.getX(), coord.getY() - 1);
                break;
            case 1:
                nextCoord = new ChallengeCoord(coord.getX() + 1, coord.getY());
                break;
            case 2:
                nextCoord = new ChallengeCoord(coord.getX(), coord.getY() + 1);
                break;
            case 3:
                nextCoord = new ChallengeCoord(coord.getX() - 1, coord.getY());
                break;
            default:
                throw new RuntimeException("oops");
        }
        return nextCoord;
    }

    private ChallengeCoord findStart() {
        return this.findOnMap(START);
    }

    private ChallengeCoord findEnd() {
        return this.findOnMap(END);
    }

    private ChallengeCoord findOnMap(final String target) {

        for (int row = 0; row < this.mapWidth; row++) {
            for (int col = 0; col < this.mapHeight; col++) {
                final String thing = this.getChallengeMapSymbol(col, row);
                if (thing.equalsIgnoreCase(target)) {
                    return new ChallengeCoord(col, row);
                }
            }
        }
        throw new RuntimeException("oops");
    }

    @Override
    public String part2(final String[] input) {

        /*

        The trick to this one is that we need to check all the best paths. In the previous part
        I found one of the best paths ... and had dropped out options to explore and get equivalent
        paths.

         */

        this.emptyTempFolder();

        this.loadChallengeMap(input);

        final ChallengeCoord startCoord = this.findStart();
        final ChallengeCoord endCoord = this.findEnd();

        final List<State> available = new ArrayList<>();
        final List<State> visited = new ArrayList<>();

        final List<State> hits = new ArrayList<>();

        final State startState = new State(startCoord, 1, 0, null, ".");
        available.add(startState);

        State workingState;
        int time = 0;
        double bestDistance = Double.MAX_VALUE;
        while (!available.isEmpty()) {
            final int index = this.bestOfAvailable(available);
            workingState = available.remove(index);
            time++;

            if (DEBUG) {
                final double distance = this.pythag(workingState.coord, endCoord);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    System.out.println("Closest at " + workingState.coord +
                            " going to " + endCoord +
                            " distance " + Math.round(distance) +
                            " with moves " + workingState.moves +
                            " visited " + visited.size() +
                            " available " + available.size() +
                            " at time " + time);
                    this.paintChallengeMapWithState(workingState, time, available);
                }
            }

            if (workingState.coord.equals(endCoord)) {
                hits.add(workingState);
                if (DEBUG) {
                    System.out.println("Found endCoord at " + time + " with " + workingState + " and I have " + available.size() + " left.");
                }
                // if I got there, I don't need to check neighbours
                continue;
            }
            final List<State> neighbours = this.getAvailableStates(workingState, visited);
            this.addNeighbours(available, neighbours);
            visited.add(workingState);
        }

        int score = Integer.MAX_VALUE;
        State bestState = null;
        for (final State hit : hits) {
            if (DEBUG) {
                this.paintChallengeMapWithState(hit, ++time, available);
                System.out.println(
                        "Hit at " + hit.coord +
                                " came from " + hit.previousState.coord +
                                " with moves " + hit.moves);
                System.out.println("  " + this.getPath(hit));
            }
            if (hit.moves < score) {
                score = hit.moves;
                bestState = hit;
            }
        }
        this.paintChallengeMapWithState(bestState, ++time, available);

        final int bestScore = score;
        final List<State> bestPaths = hits.stream().filter(s -> s.moves == bestScore).collect(Collectors.toList());
        final Set<ChallengeCoord> seatingPlaces = new HashSet<>();
        for (final State path : bestPaths) {
            seatingPlaces.addAll(this.getCoords(path));
        }

        return String.valueOf(seatingPlaces.size());
    }

    private Collection<? extends ChallengeCoord> getCoords(final State path) {

        final Set<ChallengeCoord> coords = new HashSet<>();
        coords.add(path.coord);
        State nextState = path.previousState;
        while (nextState != null) {
            coords.add(nextState.coord);
            nextState = nextState.previousState;
        }
        return coords;
    }

    private void paintMap(final int steps, final List<String> lines) {

        final String filename = "/Users/simongarton/projects/java/AdventOfCode/temp/maze-" + String.format("%06d", steps) + ".png";

        final BufferedImage bufferedImage = new BufferedImage(
                this.mapWidth * BITMAP_SCALE,
                this.mapHeight * BITMAP_SCALE,
                TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);
        this.paintFloor(graphics2D, lines);
        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void paintFloor(final Graphics2D graphics2D, final List<String> lines) {

        for (int row = 0; row < this.mapWidth; row++) {
            for (int col = 0; col < this.mapHeight; col++) {
                final String thing = lines.get(row).charAt(col) + "";
                if (thing.equalsIgnoreCase(EMPTY)) {
                    continue;
                }
                graphics2D.setPaint(new Color(250, 0, 0)); // trail
                if (thing.equalsIgnoreCase(WALL)) {
                    graphics2D.setPaint(new Color(50, 50, 50)); // wall
                }
                if (thing.equalsIgnoreCase("?")) {
                    graphics2D.setPaint(new Color(200, 200, 0)); // end
                }
                if (thing.equalsIgnoreCase("E")) {
                    graphics2D.setPaint(new Color(0, 250, 0)); // end
                }
                graphics2D.fillRect(col * BITMAP_SCALE, row * BITMAP_SCALE, BITMAP_SCALE, BITMAP_SCALE);
            }
        }
    }

    private void clearBackground(final Graphics2D graphics2D) {

        graphics2D.setPaint(Color.BLACK);
        graphics2D.fillRect(0, 0, this.mapWidth * BITMAP_SCALE, this.mapHeight * BITMAP_SCALE);
    }

    static class State {

        final ChallengeCoord coord;
        final int direction; // N 0 E 1 S 2 W 3
        final int moves;
        final State previousState;
        // don't know if I need this
        final String action; // "", "L", "R", "F"

        public State(final ChallengeCoord coord,
                     final int direction,
                     final int moves,
                     final State previousState,
                     final String action
        ) {
            this.coord = coord;
            this.direction = direction;
            this.moves = moves;
            this.previousState = previousState;
            this.action = action;
        }

        public String key() {

            return this.coord + " " + this.direction;
        }

        @Override
        public String toString() {

            return this.coord + " " + this.direction + " [" + this.moves + "]";
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
            return this.direction == state.direction && Objects.equals(this.coord, state.coord);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.coord, this.direction);
        }
    }
}
