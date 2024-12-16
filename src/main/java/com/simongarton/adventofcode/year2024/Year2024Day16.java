package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Year2024Day16 extends AdventOfCodeChallenge {

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

        this.loadChallengeMap(input);

        final AoCCoord startCoord = this.findStart();
        final AoCCoord endCoord = this.findEnd();

//        System.out.println("Start " + startCoord);
        System.out.println("End " + endCoord);

        final List<State> available = new ArrayList<>();
        final List<State> visited = new ArrayList<>();

        final List<State> hits = new ArrayList<>();

        final State startState = new State(startCoord, 1, 0, null, "");
        available.add(startState);

        State workingState = null;
        final int steps = 0;
        int bestScore = Integer.MAX_VALUE;
        int bestX = 0;
        int bestY = 0;
        while (!available.isEmpty()) {
            workingState = available.remove(0);
//            this.explainState(workingState);
//            this.listStates(available);
//            this.drawChallengeMapWithState(workingState);
            if (workingState.coord.equals(endCoord)) {
                this.drawChallengeMapWithState(workingState);
                hits.add(workingState);
                bestScore = workingState.moves;
                continue;
            }
            if (workingState.moves >= bestScore) {
                continue;
            }
            if (workingState.coord.x > bestX) {
                bestX = workingState.coord.x;
                System.out.println("best " + new AoCCoord(bestX, bestY));
            }
            if (workingState.coord.y > bestY) {
                bestY = workingState.coord.y;
                System.out.println("best " + new AoCCoord(bestX, bestY));
            }
            visited.add(workingState);
            final List<State> neighbours = this.getAvailableStates(workingState, visited);
            available.addAll(neighbours);
//            System.out.println("steps " + steps++ + ": " + available.size() + " -> " + visited.size());

        }
//        System.out.println("finished on " + steps + ": " + available.size() + " -> " + visited.size());
        State bestState = null;
        int score = Integer.MAX_VALUE;
        for (final State hit : hits) {
            if (hit.moves < score) {
                bestState = hit;
                score = hit.moves;
            }
        }
        return String.valueOf(score);
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

    private void drawChallengeMapWithState(final State startState) {

        final List<String> lines = new ArrayList<>(this.challengeMap);
        State workingState = startState;
        while (workingState != null) {
            this.updateWithState(lines, workingState);
            workingState = workingState.previousState;
        }
        for (final String line : lines) {
            System.out.println(line);
        }
        System.out.println();
    }

    private void updateWithState(final List<String> lines, final State state) {

        final AoCCoord coord = state.coord;
        final String line = lines.get(coord.y);
        final String newLine = line.substring(0, coord.x) + this.symbolForDirection(state.direction) + line.substring(coord.x + 1);
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
        this.addState(workingState, visited, neighbours, (workingState.direction + 1) % 4, "R");
        this.addState(workingState, visited, neighbours, (workingState.direction + 3) % 4, "L");
        return neighbours;
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
            neighbours.add(state);
//            System.out.println("I have added state " + state.action + " which came from " + workingState.action);
        }
    }

    private void addState(final State workingState,
                          final List<State> visited,
                          final List<State> neighbours,
                          final int direction,
                          final String action) {

        if (this.alreadyVisited(
                workingState.coord,
                action,
                visited,
                true)) {
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

    private boolean alreadyVisited(final AoCCoord aoCCoord,
                                   final String action,
                                   final List<State> visited,
                                   final boolean includeAction) {

        for (final State state : visited) {
            if (state.coord.equals(aoCCoord)) {
                if (!includeAction || action.equalsIgnoreCase(state.action)) {
                    return true;
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

    static class State {

        String id;
        AoCCoord coord;
        int moves;
        int direction; // N 0 E 1 S 2 W 3
        State previousState;
        String action; // "", "L", "R", "F"

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
    }

}
