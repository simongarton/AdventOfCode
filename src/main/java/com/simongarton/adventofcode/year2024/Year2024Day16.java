package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Year2024Day16 extends AdventOfCodeChallenge {

    private static final int BITMAP_SCALE = 4;

    private static final String WALL = "#";
    private static final String START = "S";
    private static final String END = "E";
    private static final String EMPTY = ".";

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

        this.emptyTemp();

        // 121476 too high
        // 127488 also too high

        // huh, new approach gave me 123480

        // 119484 still too high - but way quicker
        // going in reverse gets to 121480 not 119484. might be interesting, might not
        // 119476 saved a few
        // 115484 not the right answer (no more clues)
        // 114480 if I face North - which (a) is cheating and (b) means I'm not evaluating all options

        this.loadChallengeMap(input);

        final AoCCoord startCoord = this.findStart();
        final AoCCoord endCoord = this.findEnd();
//        final AoCCoord endCoord = this.findStart();
//        final AoCCoord startCoord = this.findEnd();

//        System.out.println("Start " + startCoord);
        System.out.println("End " + endCoord);

        final List<State> available = new ArrayList<>();
        final List<State> visited = new ArrayList<>();

        final List<State> hits = new ArrayList<>();

        final State startState = new State(startCoord, 0, 0, null, "");
        available.add(startState);
        // I didn't need to do this for the samples
        // this.addTurningState(startState, visited, available, (startState.direction + 1) % 4, "R");
        // this.addTurningState(startState, visited, available, (startState.direction + 3) % 4, "L");

        State workingState = null;
        int bestScore = Integer.MAX_VALUE;
        double bestDistance = Double.MAX_VALUE;
        int time = 0;
        /*
        I am seeing just one hit. I can see it exploring fully, but once it hits the
        EndCoord it just gives up.
         */
        while (!available.isEmpty()) {
            final int index = this.bestOfAvailable(available, endCoord);
            workingState = available.remove(index);
            time++;
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
            if (workingState.coord.equals(endCoord)) {
//                this.drawChallengeMapWithState(workingState);
                hits.add(workingState);
                bestScore = workingState.moves;
//                this.paintChallengeMapWithState(workingState, time, available);
                // if I got there, I don't need to check neighbours ?
//                continue;
                System.out.println("Found endCoord at " + time + " with " + workingState + " and I have " + available.size() + " left.");
            }
            if (workingState.moves >= bestScore) {
                // no point, bro.
//                continue;
            }
            visited.add(workingState);
            final List<State> neighbours = this.getAvailableStates(workingState, visited);
            available.addAll(neighbours);
        }
        int score = Integer.MAX_VALUE;
        State bestState = null;
        for (final State hit : hits) {
//            this.paintChallengeMapWithState(workingState, ++time, available);
            if (hit.moves < score) {
                score = hit.moves;
                bestState = hit;
            }
        }
        this.paintChallengeMapWithState(bestState, ++time, available);
        return String.valueOf(score);
    }

    private int countFs(final String path) {

        int fs = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.substring(i, i + 1).equalsIgnoreCase("F")) {
                fs++;
            }
        }
        return fs;
    }

    private int bestOfAvailable(final List<State> available, final AoCCoord endCoord) {

        double distance = Double.MAX_VALUE;
        int best = -1;
        int index = 0;
        for (final State state : available) {
            final AoCCoord coord = state.coord;
            // I know I'm available - but what about the next ? I should keep going where possible
            final AoCCoord nextCoord = this.nextCoord(coord, state.direction);
            final String thing = this.getChallengeMapLetter(nextCoord);
            if (EMPTY.equalsIgnoreCase(thing)) {
                continue;
            }
            final double thisDistance = this.pythag(coord, endCoord) + state.moves;
            if (thisDistance < distance) {
                best = index;
                distance = thisDistance;
            }
            index++;
        }
        if (best > -1) {
            return best;
        }
        best = -1;
        index = 0;
        distance = Double.MAX_VALUE;
        for (final State state : available) {
            final AoCCoord coord = state.coord;
            final double thisDistance = this.pythag(coord, endCoord) + state.moves;
            if (thisDistance < distance) {
                best = index;
                distance = thisDistance;
            }
            index++;
        }
        if (best == -1) {
            throw new RuntimeException("nothing ?");
        }
        return best;
    }

    private double pythag(final AoCCoord coord, final AoCCoord endCoord) {
        return Math.sqrt(
                Math.pow(endCoord.x - coord.x, 2) +
                        Math.pow(endCoord.y - coord.y, 2)
        );
    }

    private void listStates(final List<State> stateList) {

        final StringBuilder line = new StringBuilder();
        for (final State state : stateList) {
            line.append(state.coord).append(" [").append(state.direction).append("] ");
        }
        System.out.println(line);

    }

    private void countStates(final State workingState) {
        int forwards = 0;
        int lefts = 0;
        int rights = 0;
        State currentState = workingState;
        while (currentState != null) {
            if (currentState.action.equalsIgnoreCase("F")) {
                forwards++;
            }
            if (currentState.action.equalsIgnoreCase("L")) {
                lefts++;
            }
            if (currentState.action.equalsIgnoreCase("R")) {
                rights++;
            }
            currentState = currentState.previousState;
        }
        System.out.println("Forwards " + forwards);
        System.out.println("Lefts " + lefts);
        System.out.println("Rights " + rights);
    }

    private String buildPath(final State workingState) {

        final StringBuilder line = new StringBuilder(workingState.action);
        State nextState = workingState.previousState;
        while (nextState != null) {
            line.append(nextState.action);
            nextState = nextState.previousState;
        }
        return line.toString();
    }

    private void explainState(final State workingState) {
        final StringBuilder line = new StringBuilder("working " + workingState.id + " @ " + workingState.coord + " [" + workingState.direction + "] action " + workingState.action + "\n");
        State nextState = workingState.previousState;
        while (nextState != null) {
            line.append("  ")
                    .append(nextState.id)
                    .append(" @ ")
                    .append(nextState.coord)
                    .append(" [")
                    .append(nextState.direction)
                    .append("] ")
                    .append(nextState.action)
                    .append("\n");
            nextState = nextState.previousState;
        }
        System.out.println(line);
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

    private void drawChallengeMapWithState(final State startState) {

        final List<String> lines = new ArrayList<>(this.challengeMap);
        State workingState = startState;
        while (workingState != null) {
            this.updateWithState(lines, workingState, null);
            workingState = workingState.previousState;
        }
        for (final String line : lines) {
            System.out.println(line);
        }
        System.out.println();
    }

    private void updateWithState(final List<String> lines,
                                 final State state,
                                 final String symbol) {

        final AoCCoord coord = state.coord;
        final String line = lines.get(coord.y);
        final String oldSymbol = line.charAt(coord.x) + "";
        if (oldSymbol.equalsIgnoreCase("E")) {
            return;
        }
        final String symbolToUse = symbol == null ? this.symbolForDirection(state.direction) : symbol;
        final String newLine = line.substring(0, coord.x) + symbolToUse + line.substring(coord.x + 1);
        lines.add(coord.y, newLine);
        lines.remove(coord.y + 1);
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
        this.maybeAddState(workingState, visited, neighbours, workingState.direction);
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
                return !this.getChallengeMapLetter(workingState.coord.x, workingState.coord.y - 1).equalsIgnoreCase(WALL);
            case 1:
                return !this.getChallengeMapLetter(workingState.coord.x + 1, workingState.coord.y).equalsIgnoreCase(WALL);
            case 2:
                return !this.getChallengeMapLetter(workingState.coord.x, workingState.coord.y + 1).equalsIgnoreCase(WALL);
            case 3:
                return !this.getChallengeMapLetter(workingState.coord.x - 1, workingState.coord.y).equalsIgnoreCase(WALL);
            default:
                throw new RuntimeException("oops");
        }
    }

    private void maybeAddState(final State workingState,
                               final List<State> visited,
                               final List<State> neighbours,
                               final int direction) {

        final Optional<AoCCoord> optImmediateCoord = this.possibleNextCoord(workingState.coord, direction);
        if (optImmediateCoord.isPresent()) {
            final State state = new State(
                    optImmediateCoord.get(),
                    workingState.direction,
                    workingState.moves + 1,
                    workingState,
                    "F");
            // this saved 8 ?!
            if (this.alreadyVisitedWithBetterCost(
                    state,
                    "F",
                    direction,
                    visited)) {
                return;
            }
            neighbours.add(state);
//            System.out.println("I have added state " + state.action + " which came from " + workingState.action);
        }
    }

    private void addTurningState(final State workingState,
                                 final List<State> visited,
                                 final List<State> neighbours,
                                 final int direction,
                                 final String action) {

        if (this.alreadyVisitedWithBetterCost(
                workingState,
                action,
                direction,
                visited)) {
            return;
        }
        final State state = new State(
                workingState.coord,
                direction,
                workingState.moves + 1000,
                workingState,
                action);
        neighbours.add(state);
    }

    private boolean alreadyVisitedWithBetterCost(final State workingState,
                                                 final String action,
                                                 final int direction,
                                                 final List<State> visited) {

        for (final State state : visited) {
            if (state.coord.equals(workingState.coord)) {
                if (direction == state.direction && action.equalsIgnoreCase(state.action)) {
//                    if (state.moves < workingState.moves) {
                    return true;
//                    }
                }
            }
        }
        return false;
    }

    private Optional<AoCCoord> possibleNextCoord(final AoCCoord coord, final int direction) {

        final AoCCoord nextCoord = this.nextCoord(coord, direction);
        final String thing = this.getChallengeMapLetter(nextCoord);
        if (!thing.equalsIgnoreCase(WALL)) {
            return Optional.of(nextCoord);
        }
        return Optional.empty();
    }

    private AoCCoord nextCoord(final AoCCoord coord, final int direction) {

        AoCCoord nextCoord = null;
        switch (direction) {
            case 0:
                nextCoord = new AoCCoord(coord.x, coord.y - 1);
                break;
            case 1:
                nextCoord = new AoCCoord(coord.x + 1, coord.y);
                break;
            case 2:
                nextCoord = new AoCCoord(coord.x, coord.y + 1);
                break;
            case 3:
                nextCoord = new AoCCoord(coord.x - 1, coord.y);
                break;
            default:
                throw new RuntimeException("oops");
        }
        return nextCoord;
    }

    private AoCCoord findStart() {
        return this.findOnMap(START);
    }

    private AoCCoord findEnd() {
        return this.findOnMap(END);
    }

    private AoCCoord findOnMap(final String target) {

        for (int row = 0; row < this.mapWidth; row++) {
            for (int col = 0; col < this.mapHeight; col++) {
                final String thing = this.getChallengeMapLetter(col, row);
                if (thing.equalsIgnoreCase(target)) {
                    return new AoCCoord(col, row);
                }
            }
        }
        throw new RuntimeException("oops");
    }

    @Override
    public String part2(final String[] input) {
        return null;
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
                    graphics2D.setPaint(new Color(150, 150, 0)); // end
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

        final String id;
        final AoCCoord coord;
        final int moves;
        final int direction; // N 0 E 1 S 2 W 3
        final State previousState;
        final String action; // "", "L", "R", "F"

        public State(final AoCCoord coord,
                     final int direction,
                     final int moves,
                     final State previousState,
                     final String action
        ) {
            this.id = UUID.randomUUID().toString();
            this.coord = coord;
            this.moves = moves;
            this.direction = direction;
            this.previousState = previousState;
            this.action = action;
        }

        @Override
        public String toString() {

            return this.coord + " " + this.direction + " [" + this.moves + "]";
        }
    }
}
