package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.ChallengeCoord;
import com.simongarton.adventofcode.common.ChallengeNode;

import java.util.*;

public class Year2024Day20 extends AdventOfCodeChallenge {

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
        final ChallengeCoord start = this.findChallengeCoord("S");
        final ChallengeCoord end = this.findChallengeCoord("E");
        final List<ChallengeNode> shortestPath = this.getShortestPathAStar(start, end);
        final ChallengeNode endNode = shortestPath.get(shortestPath.size() - 1);

        System.out.println("endNode " + endNode + " total " + this.totalCost(endNode));

        final List<String> lines = this.updateMapWithNode(endNode, PATH);
        this.drawMapFromLines(lines);

        this.setupCache(shortestPath);
        final Map<Long, Long> cheats = this.countCheats(shortestPath);
        final List<Long> keys = new ArrayList<>(cheats.keySet());
        keys.sort(Comparator.naturalOrder());
        int totalCheats = 0;
        int atLeast100Picos = 0;
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

        final List<ChallengeCoord> vectors = List.of(
                ChallengeCoord.builder().x(-1).y(0).build(),
                ChallengeCoord.builder().x(1).y(0).build(),
                ChallengeCoord.builder().x(0).y(-1).build(),
                ChallengeCoord.builder().x(0).y(1).build()
        );

        return vectors;
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

        final List<ChallengeCoord> startingPoints = this.findStartingPoints();
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
            if (otherSideSymbol == null || otherSideSymbol.equalsIgnoreCase(WALL)) { // don't know about starts and ends ...
                continue;
            }

            final ChallengeNode startNode = this.getNodeForCoordinate(start, shortestPath);
            final ChallengeNode endNode = this.getNodeForCoordinate(otherSide, shortestPath);

            if (endNode.getCost() < startNode.getCost()) {
                continue;
            }

            final long costBenefit = (endNode.getCost() - startNode.getCost()) - 2;
//            System.out.println(
//                    "Found a cheat for " + start + "->" + brokenWall + "->" + otherSide +
//                            " saving " + endNode.getCost() + "-" + startNode.getCost() + "=" +
//                            costBenefit
//            );

            cheats.put(costBenefit, cheats.getOrDefault(costBenefit, 0L) + 1);
        }
        return cheats;
    }

    private Map<Long, Long> cheatsFromStart4Way(final ChallengeCoord start, final List<ChallengeNode> shortestPath) {

        final Map<Long, Long> cheats = new HashMap<>();

        for (final ChallengeCoord breakThroughDelta : this.getVectors()) {

            final ChallengeCoord brokenWall = this.addChallengeCoords(start, breakThroughDelta);
            final String wallSymbol = this.getChallengeMapSymbol(brokenWall);
            if (!(wallSymbol.equalsIgnoreCase(WALL))) {
                continue;
            }
            for (final ChallengeCoord reappearDelta : this.getVectors()) {
                final ChallengeCoord otherSide = this.addChallengeCoords(brokenWall, reappearDelta);
                if (otherSide.equals(start)) {
                    continue;
                }
                final String otherSideSymbol = this.getChallengeMapSymbol(otherSide);
                if (otherSideSymbol == null || otherSideSymbol.equalsIgnoreCase(WALL)) { // don't know about starts and ends ...
                    continue;
                }

                final ChallengeNode startNode = this.getNodeForCoordinate(start, shortestPath);
                final ChallengeNode endNode = this.getNodeForCoordinate(otherSide, shortestPath);

                if (endNode.getCost() < startNode.getCost()) {
                    continue;
                }

                final long costBenefit = (endNode.getCost() - startNode.getCost());
                System.out.println(
                        "Found a cheat for " + start + "->" + brokenWall + "->" + otherSide +
                                " saving " + endNode.getCost() + "-" + startNode.getCost() + "=" +
                                costBenefit
                );

                cheats.put(costBenefit, cheats.getOrDefault(costBenefit, 0L) + 1);
            }
        }
        return cheats;
    }

    private ChallengeNode getNodeForCoordinate(final ChallengeCoord coord, final List<ChallengeNode> shortestPath) {

        return shortestPath.stream().filter(n -> n.getCoord().equals(coord)).findFirst().orElseThrow();
    }

    private List<ChallengeCoord> findStartingPoints() {

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
        return null;
    }
}
