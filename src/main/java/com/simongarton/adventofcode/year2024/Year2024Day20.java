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

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Year2024Day20 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = true;
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
        if (DEBUG) {
            this.drawMapFromLines(lines);
        }

        this.setupCache(shortestPath);
        final Map<Long, Long> cheats = this.countCheats(shortestPath);
        final List<Long> keys = new ArrayList<>(cheats.keySet());
        keys.sort(Comparator.naturalOrder());
        long atLeast100Picos = 0;
        for (final Long key : keys) {
            final long c = cheats.get(key);
            if (DEBUG) {
                System.out.println("There " + this.niceCount(c) + " cheat" + this.nonPlural(c) + " that save" + this.plural(c) + " " + key + " picoseconds.");
            }
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

        No - I already know how long it will take from my first pass.

        I'm worried that there will be weird edge cases.

        A start point is always a wall.

         */

        this.loadChallengeMap(input);
        this.emptyTempFolder();
        this.paintNormalMap();
        this.drawChallengeMap();

        final List<Cheat> cheats = this.buildBruteForceCheatList();

        final Map<Long, Long> cheatTable = new HashMap<>();
        final ChallengeCoord start = this.findChallengeCoord(START);
        final ChallengeCoord end = this.findChallengeCoord(END);
        final List<ChallengeNode> shortestPath = this.getShortestPathAStar(start, end);
        final ChallengeNode endNode = shortestPath.get(shortestPath.size() - 1);
        this.paintNormalMapWithTrace(shortestPath);

        final long normalCost = endNode.getCost();
        if (DEBUG) {
            System.out.println("for no cheats I now trace at " + normalCost);
        }

        for (final Cheat cheat : cheats) {

            System.out.println(cheat);
            System.out.println("start node is at " + start + " and has a cost of " + this.getNodeForCoordinate(start, shortestPath).getCost());
            System.out.println("cheat origin is at " + cheat.origin.getCoord() + " and has a cost of " + this.getNodeForCoordinate(cheat.origin.getCoord(), shortestPath).getCost());
            System.out.println("cheat end is at " + cheat.endNode.getCoord() + " and has a cost of " + this.getNodeForCoordinate(cheat.endNode.getCoord(), shortestPath).getCost());
            System.out.println("  cheat cost is " + cheat.cost);
            System.out.println("end node is at " + end + " and has a cost of " + this.getNodeForCoordinate(end, shortestPath).getCost());

            /*

            Now I have ended up at the end of the cheat - back on the trail at a cost;
            But with the shortcut

             */

            final long normalCostToShortcutEnd = this.getNodeForCoordinate(cheat.endNode.getCoord(), shortestPath).getCost();
            final long shortcutCostToShortcutEnd = this.getNodeForCoordinate(cheat.origin.getCoord(), shortestPath).getCost() // this is how much it cost me on the map to get to before the cheat
                    + 1 // and one to step into the shortcut
                    + cheat.cost; // and the actual cost of the cheat
            // sorry about cheat.cost - am I one over ?
            final long savings = normalCostToShortcutEnd - shortcutCostToShortcutEnd;

            cheat.savings = savings;
            this.paintCheatMap(cheat);

            cheatTable.put(savings, cheatTable.getOrDefault(savings, 0L) + 1);
        }

        final List<Long> keys = new ArrayList<>(cheatTable.keySet());
        keys.sort(Comparator.naturalOrder());

        long atLeast100Picos = 0;
        for (final Long key : keys) {
            final long c = cheatTable.get(key);
            if (DEBUG) {
                System.out.println("There " + this.niceCount(c) + " cheat" + this.nonPlural(c) + " that save" + this.plural(c) + " " + key + " picoseconds.");
            }
            if (key >= 50) {
                atLeast100Picos += c;
            }
        }

        return String.valueOf(atLeast100Picos);
    }

    private void paintCheatMap(final Cheat cheat) {

        String filename = "temp/" +
                "savings-" +
                cheat.savings +
                "-" +
                cheat.startNode.getCoord() +
                "->" +
                cheat.endNode.getCoord() +
                "-cost-" +
                cheat.cost +
                ".png";
        filename = filename.replace(" ", "_").replace(",", "|");

        final BufferedImage bufferedImage = new BufferedImage(this.mapWidth * MAP_TILE, this.mapHeight * MAP_TILE, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);
        this.paintNormalBackground(graphics2D);
        this.paintCheatStartEndOrigin(graphics2D, cheat);
        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
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
        this.paintShortestPath(graphics2D, shortestPath, Color.YELLOW);
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
        this.paintCheatStartEndOrigin(graphics2D, cheat);
        this.paintShortestPath(graphics2D, shortestPathForCheat, Color.YELLOW);
        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void paintShortestPath(final Graphics2D graphics2D, final List<ChallengeNode> shortestPathForCheat,
                                   final Color fillColor) {

        final int quarterMapTile = MAP_TILE / 4;
        final int halfMapTile = MAP_TILE / 2;

        for (final ChallengeNode challengeNode : shortestPathForCheat) {
            final int col = challengeNode.getCoord().getX();
            final int row = challengeNode.getCoord().getY();

            graphics2D.setPaint(fillColor);
            graphics2D.fillOval(col * MAP_TILE + quarterMapTile, row * MAP_TILE + quarterMapTile, halfMapTile, halfMapTile);
            graphics2D.setPaint(new Color(0, 0, 0));
            graphics2D.drawOval(col * MAP_TILE + quarterMapTile, row * MAP_TILE + quarterMapTile, halfMapTile, halfMapTile);
        }
    }

    private void paintCheatStartEndOrigin(final Graphics2D graphics2D, final Cheat cheat) {

        ChallengeCoord c = cheat.startNode.getCoord();
        int col = c.getX();
        int row = c.getY();
        graphics2D.setPaint(Color.BLUE);
        graphics2D.fillRect(col * MAP_TILE, row * MAP_TILE, MAP_TILE, MAP_TILE);
        graphics2D.setPaint(new Color(0, 0, 0));
        graphics2D.drawRect(col * MAP_TILE, row * MAP_TILE, MAP_TILE, MAP_TILE);

        c = cheat.endNode.getCoord();
        col = c.getX();
        row = c.getY();
        graphics2D.setPaint(Color.ORANGE);
        graphics2D.fillRect(col * MAP_TILE, row * MAP_TILE, MAP_TILE, MAP_TILE);
        graphics2D.setPaint(new Color(0, 0, 0));
        graphics2D.drawRect(col * MAP_TILE, row * MAP_TILE, MAP_TILE, MAP_TILE);

        final int extra = MAP_TILE / 4;
        c = cheat.origin.getCoord();
        col = c.getX();
        row = c.getY();
        graphics2D.setPaint(Color.MAGENTA);
        graphics2D.fillRect(col * MAP_TILE + extra, row * MAP_TILE + extra, extra * 2, extra * 2);
        graphics2D.setPaint(new Color(0, 0, 0));
        graphics2D.drawRect(col * MAP_TILE + extra, row * MAP_TILE + extra, extra * 2, extra * 2);
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

    private List<Cheat> buildBruteForceCheatList() {

        final Map<String, Cheat> uniqueCheats = new HashMap<>();

        for (int x = 1; x < this.mapWidth - 1; x++) {
            for (int y = 1; y < this.mapHeight - 1; y++) {
                // get the origin, where I would leave the trail for the short cut.
                // must be EMPTY or START
                final ChallengeCoord origin = ChallengeCoord.builder().x(x).y(y).build();
                final String originSymbol = this.getChallengeMapSymbol(origin);
                if (!(originSymbol.equalsIgnoreCase(EMPTY) || originSymbol.equalsIgnoreCase(START))) {
                    continue;
                }
                final ChallengeNode originNode = ChallengeNode.builder()
                        .coord(origin)
                        .cost(0)
                        .previous(null)
                        .build();

                // for the origin, get the (up to) 4 orthogonal neighbours
                // these must be walls
                for (final ChallengeCoord start : this.findWallsNearOrigin(origin)) {
                    final String startSymbol = this.getChallengeMapSymbol(start);
                    if (!startSymbol.equalsIgnoreCase(WALL)) {
                        continue;
                    }
                    final ChallengeNode startNode = ChallengeNode.builder()
                            .coord(start)
                            .cost(0)
                            .previous(null)
                            .build();
                    // now roam around the map for the start, and find end points
                    // that are not walls, and are at least 1 and less than / equal to 20
                    // manhattan around.
                    for (int x1 = 1; x1 < this.mapWidth - 1; x1++) {
                        for (int y1 = 1; y1 < this.mapHeight - 1; y1++) {
                            final ChallengeCoord end = ChallengeCoord.builder().x(x1).y(y1).build();
                            final long dist = this.manhattanDistance(start, end);
                            if (!(dist > 1 && dist <= 20)) {
                                continue;
                            }
                            final String endSymbol = this.getChallengeMapSymbol(end);
                            if (endSymbol.equalsIgnoreCase(WALL)) {
                                continue;
                            }
                            final ChallengeNode endNode = ChallengeNode.builder()
                                    .coord(end)
                                    .cost(dist)
                                    .previous(null)
                                    .build();
                            final Cheat cheat = new Cheat(originNode, startNode, endNode, startSymbol, endSymbol, dist);
                            final String key = startNode.getCoord() + "->" + endNode.getCoord();
//                            if (key.equalsIgnoreCase("6,6->5,7")) {
                            uniqueCheats.put(key, cheat);
//                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(uniqueCheats.values());
    }

    private List<ChallengeCoord> findWallsNearOrigin(final ChallengeCoord origin) {

        final List<ChallengeCoord> neighbours = new ArrayList<>();
        this.maybeAddWall(neighbours, origin, +1, 0);
        this.maybeAddWall(neighbours, origin, -1, 0);
        this.maybeAddWall(neighbours, origin, 0, +1);
        this.maybeAddWall(neighbours, origin, 0, -1);
        return neighbours;
    }

    private void maybeAddWall(final List<ChallengeCoord> neighbours,
                              final ChallengeCoord origin,
                              final int xDelta,
                              final int yDelta) {

        final ChallengeCoord newCoord = ChallengeCoord.builder()
                .x(origin.getX() + xDelta)
                .y(origin.getY() + yDelta)
                .build();

        if (newCoord.getX() == 0 || newCoord.getX() == (this.mapWidth - 1)) {
            return;
        }
        if (newCoord.getY() == 0 || newCoord.getY() == (this.mapHeight - 1)) {
            return;
        }
        final String challengeMapSymbol = this.getChallengeMapSymbol(newCoord);
        if (!challengeMapSymbol.equalsIgnoreCase(WALL)) {
            return;
        }
        neighbours.add(newCoord);
    }

    static class Cheat {

        final ChallengeNode origin; // will be EMPTY or a WALL
        final ChallengeNode startNode; // will be a WALL
        final ChallengeNode endNode; // will be not a WALL (could be start, which is dumb)
        final String startSymbol;
        final String endSymbol;
        final long cost;
        long savings;

        public Cheat(
                final ChallengeNode origin,
                final ChallengeNode startNode,
                final ChallengeNode endNode,
                final String startSymbol,
                final String endSymbol,
                final long cost) {
            this.origin = origin;
            this.startNode = startNode;
            this.endNode = endNode;
            this.startSymbol = startSymbol;
            this.endSymbol = endSymbol;
            this.cost = cost;
        }

        @Override
        public String toString() {
            return this.startNode +
                    " [" + this.startSymbol + "] -> " +
                    this.endNode +
                    " [" + this.endSymbol + "] cost " +
                    this.cost + " saves " + this.savings;
        }
    }
}
