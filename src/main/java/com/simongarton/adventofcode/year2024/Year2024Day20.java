package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.ChallengeCoord;
import com.simongarton.adventofcode.common.ChallengeNode;

import java.util.*;
import java.util.stream.Collectors;

public class Year2024Day20 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;

    private final Map<ChallengeCoord, Long> costCache = new HashMap<>();

    @Override
    public String title() {
        return "Day 20: Race Condition";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 20);
    }

    @Override
    public String part1(final String[] input) {

        this.loadChallengeMap(input);

        final ChallengeCoord start = this.findChallengeCoord(START);
        final ChallengeCoord end = this.findChallengeCoord(END);
        final List<ChallengeNode> shortestPath = this.getShortestPathAStar(start, end);
        final ChallengeNode endNode = shortestPath.get(shortestPath.size() - 1);

        final List<String> lines = this.updateMapWithNode(endNode, PATH);
        this.drawMapFromLines(lines);

        this.setupCache(shortestPath);
        final Map<Long, Long> cheats = this.countCheats(shortestPath);
        final List<Long> keys = new ArrayList<>(cheats.keySet());
        keys.sort(Comparator.naturalOrder());
        int totalCheats = 0;
        long atLeast100Picos = 0;
        for (final Long key : keys) {
            final long c = cheats.get(key);
//            System.out.println("There " + this.niceCount(c) + " cheat" + this.nonPlural(c) + " that save" + this.plural(c) + " " + key + " picoseconds.");
            totalCheats += cheats.get(key);
            if (key >= 100) {
                atLeast100Picos += c;
            }
        }

        return String.valueOf(atLeast100Picos);
    }

    private long totalCost(final ChallengeNode endNode) {

        long cost = 0;
        ChallengeNode workingNode = endNode;
        while (workingNode != null) {
            cost++;
            workingNode = workingNode.getPrevious();
        }
        return cost - 1; // dont count the first step
    }

    private void setupCache(final List<ChallengeNode> shortestPath) {

        shortestPath.forEach(n -> this.costCache.put(n.getCoord(), n.getCost()));
    }

    private List<ChallengeCoord> getVectors() {

        return List.of(
                ChallengeCoord.builder().x(-1).y(0).build(),
                ChallengeCoord.builder().x(1).y(0).build(),
                ChallengeCoord.builder().x(0).y(-1).build(),
                ChallengeCoord.builder().x(0).y(1).build()
        );
    }

    private List<List<ChallengeCoord>> getTuples() {

        final List<ChallengeCoord> vectors = this.getVectors();

        final List<List<ChallengeCoord>> tuples = List.of(
                List.of(vectors.get(0), vectors.get(1)),
                List.of(vectors.get(0), vectors.get(2)),
                List.of(vectors.get(0), vectors.get(3)),
                List.of(vectors.get(1), vectors.get(0)),
                List.of(vectors.get(1), vectors.get(2)),
                List.of(vectors.get(1), vectors.get(3)),
                List.of(vectors.get(2), vectors.get(0)),
                List.of(vectors.get(2), vectors.get(1)),
                List.of(vectors.get(2), vectors.get(3)),
                List.of(vectors.get(3), vectors.get(0)),
                List.of(vectors.get(3), vectors.get(1)),
                List.of(vectors.get(3), vectors.get(2))
        );

        return tuples;
    }

    private Map<Long, Long> countCheats(final List<ChallengeNode> shortestPath) {

        final List<ChallengeCoord> startingPoints = this.findStartingPointsWhichAreNotWallsForPart1();
        final Map<Long, Long> cheats = new HashMap<>();
        for (final ChallengeCoord start : startingPoints) {

            final Map<Long, Long> coordCheats = this.cheatsFromStart(start, shortestPath);
            for (final Map.Entry<Long, Long> entry : coordCheats.entrySet()) {
                cheats.put(entry.getKey(), cheats.getOrDefault(entry.getKey(), 0L) + 1);
            }
        }
        return cheats;
    }

    private Map<Long, Long> cheatsFromStart(final ChallengeCoord start, final List<ChallengeNode> shortestPath) {

        final Map<Long, Long> cheats = new HashMap<>();

        for (final ChallengeCoord breakThroughDelta : this.getVectors()) {

            final ChallengeCoord brokenWall = this.addChallengeCoords(start, breakThroughDelta);
            final String wallSymbol = this.getChallengeMapSymbol(brokenWall);
            if (!(wallSymbol.equalsIgnoreCase(WALL))) {
                continue;
            }
            final ChallengeCoord otherSide = this.addChallengeCoords(brokenWall, breakThroughDelta);
            final String otherSideSymbol = this.getChallengeMapSymbol(otherSide);
            if (otherSideSymbol == null || otherSideSymbol.equalsIgnoreCase(WALL)) {
                continue;
            }

            final ChallengeNode startNode = this.getNodeForCoordinate(start, shortestPath);
            final ChallengeNode endNode = this.getNodeForCoordinate(otherSide, shortestPath);

            if (endNode.getCost() < startNode.getCost()) {
                continue;
            }

            final long costBenefit = (endNode.getCost() - startNode.getCost()) - 2;
            cheats.put(costBenefit, cheats.getOrDefault(costBenefit, 0L) + 1);
        }
        return cheats;
    }

    private ChallengeNode getNodeForCoordinate(final ChallengeCoord coord, final List<ChallengeNode> shortestPath) {

        return shortestPath.stream().filter(n -> n.getCoord().equals(coord)).findFirst().orElseThrow();
    }

    private List<ChallengeCoord> findStartingPointsWhichAreNotWallsForPart1() {

        final List<ChallengeCoord> startingPoints = new ArrayList<>();

        for (int x = 1; x < this.mapWidth - 1; x++) {
            for (int y = 1; y < this.mapHeight - 1; y++) {
                final ChallengeCoord start = ChallengeCoord.builder().x(x).y(y).build();
                final String startSymbol = this.getChallengeMapSymbol(start);
                if (!startSymbol.equalsIgnoreCase(WALL)) {
                    startingPoints.add(start);
                }
            }
        }
        return startingPoints;
    }


    private ChallengeCoord addChallengeCoords(final ChallengeCoord one, final ChallengeCoord two) {

        return ChallengeCoord.builder().x(one.getX() + two.getX()).y(one.getY() + two.getY()).build();
    }

    @Override
    public String part2(final String[] input) {

        /*

        Map is 141 x 141
        There are 9944 interior walls.
        I could iterate through all versions of taking up to 20 of those out, but that would take a little while.

        Cheats must start on a wall and end on the first time we hit empty again.
        So I could iterate through all walls, and build up trees of all paths IN ALL DIRECTIONS - can't be just towards
        the finish that end on an empty.

        For each, remember the start, end and the walls I have to take out. Then for each, shortest path the whole thing.

        I'm worried that there will be weird edge cases.

        A start point is always a wall.

         */

        this.loadChallengeMap(input);

        final List<Cheat> cheats = this.buildCheatList();

        final Map<Long, Long> cheatTable = new HashMap<>();
        final ChallengeCoord start = this.findChallengeCoord(START);
        final ChallengeCoord end = this.findChallengeCoord(END);
        final List<ChallengeNode> shortestPath = this.getShortestPathAStar(start, end);
        final ChallengeNode endNode = shortestPath.get(shortestPath.size() - 1);

        System.out.println("for no cheats I now trace at " + endNode.getCost());
        final long startingCost = endNode.getCost();

        for (final Cheat cheat : cheats) {
            System.out.println(cheat);

            this.loadChallengeMap(input);
            this.removeSomeWalls(cheat.wallsToRemove);

            final List<ChallengeNode> shortestPathForCheat = this.getShortestPathAStar(start, end);
            final ChallengeNode endNodeForCheat = shortestPathForCheat.get(shortestPathForCheat.size() - 1);

            final long savings = startingCost - endNodeForCheat.getCost();
            if (savings == 0) {
                continue;
            }
            // System.out.println("for cheat " + cheat + " I now trace at " + endNodeForCheat.getCost() + " saving " + savings);

            cheatTable.put(savings, cheatTable.getOrDefault(savings, 0L) + 1);
        }

        final List<Long> keys = new ArrayList<>(cheatTable.keySet());
        keys.sort(Comparator.naturalOrder());

        long atLeast100Picos = 0;
        for (final Long key : keys) {
            final long c = cheatTable.get(key);
            System.out.println("There " + this.niceCount(c) + " cheat" + this.nonPlural(c) + " that save" + this.plural(c) + " " + key + " picoseconds.");
            if (key >= 50) {
                atLeast100Picos += c;
            }
        }

        return String.valueOf(atLeast100Picos);
    }

    private void removeSomeWalls(final List<ChallengeCoord> wallsToRemove) {

        wallsToRemove.forEach(this::removeWall);
    }

    private void removeWall(final ChallengeCoord c) {

        this.setChallengeMapLetter(c, EMPTY);
    }

    private List<Cheat> buildCheatList() {

        final List<Cheat> cheats = new ArrayList<>();
        for (final ChallengeNode start : this.findAllCheatStartsWhichAreWallsInPart2()) {
            final List<Cheat> cheatsForNode = this.buildCheats(start);
            final List<String> lines = this.getMapLines();
            if (DEBUG) {
                this.drawChallengeMap();
                for (final Cheat cheat : cheatsForNode) {
                    this.updateWithNode(lines, cheat.endNode, "*");
                    this.updateWithNode(lines, cheat.startNode, "O");
                }
                this.drawMapFromLines(lines);
            }
            cheats.addAll(cheatsForNode);
            // System.out.println("for " + start + " I built " + cheatsForNode.size() + " and now have " + cheats.size());
        }
        return cheats;
    }

    private Cheat buildCheat(final ChallengeNode start, final List<ChallengeNode> pointsEndedUpAt) {

        // start is a wall. pointsEndedUpAt will be empty or end. I'm not worrying about
        // stepping backwards like part 1, because that was a different algorithm.

        final List<ChallengeNode> available = this.addShortcutNeighbours(start,
                Collections.emptyList(),
                Collections.emptyList(),
                pointsEndedUpAt);
        final List<ChallengeNode> visited = new ArrayList<>();

        ChallengeNode exitNode = null;
        while (!available.isEmpty()) {
            final ChallengeNode current = available.remove(0); // 0 for BFS, size()-1 for DFS
            if (current.getCost() > 20) {
                continue;
            }
            final String currentSymbol = this.getChallengeMapSymbol(current.getCoord());
            if (currentSymbol == null) {
                continue;
            }
            if (currentSymbol.equalsIgnoreCase(EMPTY) ||
                    currentSymbol.equalsIgnoreCase(START) ||
                    currentSymbol.equalsIgnoreCase(END)) {
                // I'm out
                exitNode = current;
                break;
            }
            visited.add(current);

            // I'm a wall
            final List<ChallengeNode> neighbours = this.addShortcutNeighbours(current, available, visited, pointsEndedUpAt);
            available.addAll(neighbours);
        }

        if (exitNode == null) {
            // I didn't find anything to test
            return null;
        }
        final List<ChallengeCoord> wallCoords = visited.stream().map(ChallengeNode::getCoord).collect(Collectors.toList());
        return new Cheat(start, exitNode, wallCoords);
    }

    private List<ChallengeNode> addShortcutNeighbours(final ChallengeNode current,
                                                      final List<ChallengeNode> available,
                                                      final List<ChallengeNode> visited,
                                                      final List<ChallengeNode> pointsEndedUpAt) {

        final List<ChallengeNode> neighbours = new ArrayList<>();
        this.maybeAddNeighbour2024202(neighbours, current, +1, 0, available, visited, pointsEndedUpAt);
        this.maybeAddNeighbour2024202(neighbours, current, -1, 0, available, visited, pointsEndedUpAt);
        this.maybeAddNeighbour2024202(neighbours, current, 0, +1, available, visited, pointsEndedUpAt);
        this.maybeAddNeighbour2024202(neighbours, current, 0, -1, available, visited, pointsEndedUpAt);
        return neighbours;
    }

    private void maybeAddNeighbour2024202(final List<ChallengeNode> neighbours,
                                          final ChallengeNode current,
                                          final int xDelta,
                                          final int yDelta,
                                          final List<ChallengeNode> available,
                                          final List<ChallengeNode> visited,
                                          final List<ChallengeNode> pointsEndedUpAt) {

        final ChallengeCoord newCoord = ChallengeCoord.builder()
                .x(current.getCoord().getX() + xDelta)
                .y(current.getCoord().getY() + yDelta)
                .build();
        final String challengeMapSymbol = this.getChallengeMapSymbol(newCoord);
        if (challengeMapSymbol == null) {
            return;
        }
        if (this.nodeListContainsCoord(available, newCoord)) {
            return;
        }
        if (this.nodeListContainsCoord(visited, newCoord)) {
            return;
        }
        if (this.nodeListContainsCoord(pointsEndedUpAt, newCoord)) {
            return;
        }
        final ChallengeNode newNode = ChallengeNode.builder()
                .coord(newCoord)
                .cost(current.getCost() + 1)
                .previous(current)
                .build();
        neighbours.add(newNode);
    }

    private List<Cheat> buildCheats(final ChallengeNode start) {

        final List<Cheat> cheats = new ArrayList<>();
        final List<ChallengeNode> pointsEndedUpAt = new ArrayList<>();

        Cheat cheatFromHereButNotToThere = this.buildCheat(start, pointsEndedUpAt);
        System.out.println("first cheat for " + start + " is " + cheatFromHereButNotToThere);

        while (cheatFromHereButNotToThere != null) {
            cheats.add(cheatFromHereButNotToThere);
            pointsEndedUpAt.add(cheatFromHereButNotToThere.endNode);
            cheatFromHereButNotToThere = this.buildCheat(start, pointsEndedUpAt);
            System.out.println("  next cheat for " + start + " is " + cheatFromHereButNotToThere);
        }

        return cheats;
    }

    private List<ChallengeNode> findAllCheatStartsWhichAreWallsInPart2() {

        final List<ChallengeNode> cheatStarts = new ArrayList<>();
        for (int x = 1; x < this.mapWidth - 1; x++) {
            for (int y = 1; y < this.mapHeight - 1; y++) {
                final ChallengeCoord start = ChallengeCoord.builder().x(x).y(y).build();
                final String startSymbol = this.getChallengeMapSymbol(start);
                if (startSymbol.equalsIgnoreCase(WALL)) {
                    final ChallengeNode startNode = ChallengeNode.builder()
                            .coord(start)
                            .previous(null)
                            .cost(0)
                            .build();
                    cheatStarts.add(startNode);
                }
            }
        }
        return cheatStarts;
    }

    private List<ChallengeCoord> findInteriorWalls() {

        final List<ChallengeCoord> interiorWalls = new ArrayList<>();
        for (int x = 1; x < this.mapWidth - 1; x++) {
            for (int y = 1; y < this.mapHeight - 1; y++) {
                final ChallengeCoord start = ChallengeCoord.builder().x(x).y(y).build();
                final String startSymbol = this.getChallengeMapSymbol(start);
                if (startSymbol.equalsIgnoreCase(WALL)) {
                    interiorWalls.add(start);
                }
            }
        }
        return interiorWalls;
    }

    static class Cheat {

        ChallengeNode startNode; // will be a wall or the Start
        ChallengeNode endNode; // will be a wall or the End

        List<ChallengeCoord> wallsToRemove;

        public Cheat(final ChallengeNode startNode, final ChallengeNode endNode, final List<ChallengeCoord> wallsToRemove) {
            this.startNode = startNode;
            this.endNode = endNode;
            this.wallsToRemove = wallsToRemove;
        }

        @Override
        public String toString() {
            return this.startNode + "->" + this.endNode + " [" + this.wallsToRemove.size() + "]";
        }
    }
}
