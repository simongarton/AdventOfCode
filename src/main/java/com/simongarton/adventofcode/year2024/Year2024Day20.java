package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.ChallengeCoord;
import com.simongarton.adventofcode.common.ChallengeNode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Year2024Day20 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;
    private static final int MAP_TILE = 10;

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
        long atLeast100Picos = 0;
        for (final Long key : keys) {
            final long c = cheats.get(key);
//            System.out.println("There " + this.niceCount(c) + " cheat" + this.nonPlural(c) + " that save" + this.plural(c) + " " + key + " picoseconds.");
            if (key >= 100) {
                atLeast100Picos += c;
            }
        }

        return String.valueOf(atLeast100Picos);
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
        this.emptyTempFolder();
        this.paintNormalMap();

        final List<Cheat> cheats = this.buildCheatList();

        final Map<Long, Long> cheatTable = new HashMap<>();
        final ChallengeCoord start = this.findChallengeCoord(START);
        final ChallengeCoord end = this.findChallengeCoord(END);
        final List<ChallengeNode> shortestPath = this.getShortestPathAStar(start, end);
        final ChallengeNode endNode = shortestPath.get(shortestPath.size() - 1);
        this.paintNormalMapWithTrace(shortestPath);

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

            this.paintCheatMap(cheat, shortestPathForCheat);
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

    private void paintNormalMap() {

        String filename = "temp/normal.png";
        filename = filename.replace(" ", "_").replace(",", "|");

        final BufferedImage bufferedImage = new BufferedImage(this.mapWidth * MAP_TILE, this.mapHeight * MAP_TILE, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);
        this.paintNormalBackground(graphics2D);
        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void paintNormalMapWithTrace(final List<ChallengeNode> shortestPath) {

        String filename = "temp/normal-trace.png";
        filename = filename.replace(" ", "_").replace(",", "|");

        final BufferedImage bufferedImage = new BufferedImage(this.mapWidth * MAP_TILE, this.mapHeight * MAP_TILE, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);
        this.paintNormalBackground(graphics2D);
        this.paintShortestPath(graphics2D, shortestPath);
        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void paintCheatMap(final Cheat cheat, final List<ChallengeNode> shortestPathForCheat) {

        String filename = "temp/" + cheat.startNode.getCoord().toString() + "->" + cheat.endNode.getCoord().toString() + ".png";
        filename = filename.replace(" ", "_").replace(",", "|");

        final BufferedImage bufferedImage = new BufferedImage(this.mapWidth * MAP_TILE, this.mapHeight * MAP_TILE, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);
        this.paintNormalBackground(graphics2D);
        this.paintRemovedWalls(graphics2D, cheat.wallsToRemove);
        this.paintCheatStartEnd(graphics2D, cheat);
        this.paintShortestPath(graphics2D, shortestPathForCheat);
        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void paintShortestPath(final Graphics2D graphics2D, final List<ChallengeNode> shortestPathForCheat) {

        final int quarterMapTile = MAP_TILE / 4;
        final int halfMapTile = MAP_TILE / 2;

        for (final ChallengeNode challengeNode : shortestPathForCheat) {
            final int col = challengeNode.getCoord().getX();
            final int row = challengeNode.getCoord().getY();

            graphics2D.setPaint(new Color(200, 200, 200));
            graphics2D.fillOval(col * MAP_TILE + quarterMapTile, row * MAP_TILE + quarterMapTile, halfMapTile, halfMapTile);
            graphics2D.setPaint(new Color(0, 0, 0));
            graphics2D.drawOval(col * MAP_TILE + quarterMapTile, row * MAP_TILE + quarterMapTile, halfMapTile, halfMapTile);
        }
    }

    private void paintCheatStartEnd(final Graphics2D graphics2D, final Cheat cheat) {

        ChallengeCoord c = cheat.startNode.getCoord();
        int col = c.getX();
        int row = c.getY();
        graphics2D.setPaint(new Color(100, 200, 100));
        graphics2D.fillRect(col * MAP_TILE, row * MAP_TILE, MAP_TILE, MAP_TILE);
        graphics2D.setPaint(new Color(0, 0, 0));
        graphics2D.drawRect(col * MAP_TILE, row * MAP_TILE, MAP_TILE, MAP_TILE);

        c = cheat.endNode.getCoord();
        col = c.getX();
        row = c.getY();
        graphics2D.setPaint(new Color(100, 100, 200));
        graphics2D.fillRect(col * MAP_TILE, row * MAP_TILE, MAP_TILE, MAP_TILE);
        graphics2D.setPaint(new Color(0, 0, 0));
        graphics2D.drawRect(col * MAP_TILE, row * MAP_TILE, MAP_TILE, MAP_TILE);
    }

    private void paintRemovedWalls(final Graphics2D graphics2D, final List<ChallengeCoord> wallsToRemove) {

        for (final ChallengeCoord c : wallsToRemove) {
            final int col = c.getX();
            final int row = c.getY();
            graphics2D.setPaint(new Color(0, 0, 0));
            graphics2D.fillRect(col * MAP_TILE, row * MAP_TILE, MAP_TILE, MAP_TILE);
            graphics2D.setPaint(new Color(200, 200, 200));
            graphics2D.drawRect(col * MAP_TILE, row * MAP_TILE, MAP_TILE, MAP_TILE);
        }
    }

    private void paintNormalBackground(final Graphics2D graphics2D) {

        for (int row = 0; row < this.mapHeight; row++) {
            for (int col = 0; col < this.mapWidth; col++) {
                final String thing = this.getChallengeMapSymbol(col, row);
                if (thing.equalsIgnoreCase(EMPTY)) {
                    continue;
                }
                graphics2D.setPaint(new Color(200, 0, 200));
                if (thing.equalsIgnoreCase(START)) {
                    graphics2D.setPaint(new Color(0, 200, 0));
                }
                if (thing.equalsIgnoreCase(END)) {
                    graphics2D.setPaint(new Color(200, 0, 0));
                }
                if (thing.equalsIgnoreCase(WALL)) {
                    graphics2D.setPaint(new Color(100, 100, 100));
                }
                graphics2D.fillRect(col * MAP_TILE, row * MAP_TILE, MAP_TILE, MAP_TILE);
            }
        }

    }

    private void clearBackground(final Graphics2D graphics2D) {

        graphics2D.setPaint(Color.BLACK);
        graphics2D.fillRect(0, 0, this.mapWidth * MAP_TILE, this.mapHeight * MAP_TILE);
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

        if (newCoord.getX() == 0 || newCoord.getX() == (this.mapWidth - 1)) {
            return;
        }
        if (newCoord.getY() == 0 || newCoord.getY() == (this.mapHeight - 1)) {
            return;
        }
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

        final ChallengeNode startNode; // will be a wall or the Start
        final ChallengeNode endNode; // will be a wall or the End

        final List<ChallengeCoord> wallsToRemove;

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
