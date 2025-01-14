package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.ChallengeCoord;
import com.simongarton.adventofcode.common.ChallengeNode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Year2024Day20 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;
    private static final int MAP_TILE = 20;

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

        this.loadChallengeMap(input);
        final ChallengeCoord start = this.findChallengeCoord(START);
        final ChallengeCoord end = this.findChallengeCoord(END);
        final List<ChallengeNode> shortestPath = this.getShortestPathAStar(start, end);

        final List<Cheat> cheats = this.buildQuickCheatList(shortestPath);

        final Map<Long, Long> cheatTable = new HashMap<>();

        for (final Cheat cheat : cheats) {

            if (DEBUG && false) {
                System.out.println("\n" + cheat);
                System.out.println("start node is at " + start + " and has a cost of " + this.costCache.get(start));
                System.out.println("cheat origin is at " + cheat.originOfShortCut.getCoord() + " and has a cost of " + this.costCache.get(cheat.originOfShortCut.getCoord()));
                System.out.println("cheat end is at " + cheat.endWhenBrokenThrough.getCoord() + " and has a cost of " + this.costCache.get(cheat.endWhenBrokenThrough.getCoord()));
                System.out.println("  cheat cost is " + cheat.cost);
                System.out.println("end node is at " + end + " and has a cost of " + this.costCache.get(end));
            }

            cheatTable.put(cheat.savings, cheatTable.getOrDefault(cheat.savings, 0L) + 1);
        }

        final List<Long> keys = new ArrayList<>(cheatTable.keySet());
        // I was sorting this to make it look nice. Sorting a list  with a million entries takes a certain time.
        // keys.sort(Comparator.naturalOrder());

        long atLeast100Picos = 0;
        for (final Long key : keys) {
            final long c = cheatTable.get(key);
            if (DEBUG) {
                System.out.println("There " + this.niceCount(c) + " cheat" + this.nonPlural(c) + " that save" + this.plural(c) + " " + key + " picoseconds.");
            }
            if (key >= 100) {
                atLeast100Picos += c;
            }
        }

        return String.valueOf(atLeast100Picos);

    }

    private List<Cheat> buildQuickCheatList(final List<ChallengeNode> shortestPath) {

        // on reflection, all cheats must start and end on the path; and the savings is just the end-start+manhattan
        // I don't need to check uniques or neighbours ...
        final List<Cheat> cheats = new ArrayList<>();

        for (int first = 0; first < shortestPath.size(); first++) {
            final ChallengeNode shortcutStartNode = shortestPath.get(first);
            final ChallengeCoord shortcutStart = shortcutStartNode.getCoord();
            final long startCost = shortcutStartNode.getCost();

            for (int second = first + 1; second < shortestPath.size(); second++) {
                final ChallengeNode shortcutEndNode = shortestPath.get(second);
                final ChallengeCoord shortcutEnd = shortcutEndNode.getCoord();
                final long endCost = shortcutEndNode.getCost();

                final long sectionChoppedOut = endCost - startCost;
                final long dist = this.manhattanDistance(shortcutStart, shortcutEnd);
                if (dist > 20) {
                    continue;
                }

                final Cheat cheat = new Cheat(shortcutStartNode,
                        shortcutStartNode,
                        shortcutEndNode,
                        null,
                        null,
                        dist);
                cheat.savings = sectionChoppedOut - dist;
                if (cheat.savings >= 100) {
                    cheats.add(cheat);
                }
            }
        }
        return cheats;
    }

    private void paintCheatMap(final Cheat cheat) {

        // fiddling around with file names so they sort nicely for animation
        final DecimalFormat decimalFormat = new DecimalFormat("000");
        String filename = "temp/" +
//                decimalFormat.format(this.costCache.get(cheat.origin.getCoord())) + "|" +
//                cheat.startNode.getCoord() +
//                cheat.origin.getCoord() + "^" +
//                cheat.startNode.getCoord() +
                cheat.originOfShortCut.getCoord() +
                "-" +
//                "savings-" +
//                cheat.savings +
//                "-" +
                cheat.firstWallInShortCut.getCoord() +
                "->" +
                cheat.endWhenBrokenThrough.getCoord() +
                "-cost-" +
                cheat.cost +
                "-savings-" +
                cheat.savings +
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

        final int extra = (MAP_TILE / 5) + 1;
        final int widthBoost = 0;
        ChallengeCoord c = cheat.firstWallInShortCut.getCoord();
        int col = c.getX();
        int row = c.getY();
        graphics2D.setPaint(Color.BLUE);
        graphics2D.fillRect(col * MAP_TILE + extra, row * MAP_TILE + extra, widthBoost + extra * 2, widthBoost + extra * 2);

        c = cheat.endWhenBrokenThrough.getCoord();
        col = c.getX();
        row = c.getY();
        graphics2D.setPaint(Color.ORANGE);
        graphics2D.fillRect(col * MAP_TILE + extra, row * MAP_TILE + extra, widthBoost + extra * 2, widthBoost + extra * 2);

        c = cheat.originOfShortCut.getCoord();
        col = c.getX();
        row = c.getY();
        graphics2D.setPaint(Color.MAGENTA);
        graphics2D.fillRect(col * MAP_TILE + extra, row * MAP_TILE + extra, widthBoost + extra * 2, widthBoost + extra * 2);
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
                graphics2D.setPaint(new Color(0, 0, 0));
                graphics2D.drawRect(col * MAP_TILE, row * MAP_TILE, MAP_TILE, MAP_TILE);
            }
        }
    }

    private void clearBackground(final Graphics2D graphics2D) {

        graphics2D.setPaint(Color.BLACK);
        graphics2D.fillRect(0, 0, this.mapWidth * MAP_TILE, this.mapHeight * MAP_TILE);
    }

    static class Cheat {

        final ChallengeNode originOfShortCut; // will be EMPTY or a WALL
        final ChallengeNode firstWallInShortCut; // will be a WALL
        final ChallengeNode endWhenBrokenThrough; // will be not a WALL (could be start, which is dumb)
        final String startSymbol;
        final String endSymbol;
        final long cost;
        long savings;

        public Cheat(
                final ChallengeNode originOfShortCut,
                final ChallengeNode firstWallInShortCut,
                final ChallengeNode endWhenBrokenThrough,
                final String startSymbol,
                final String endSymbol,
                final long cost) {
            this.originOfShortCut = originOfShortCut;
            this.firstWallInShortCut = firstWallInShortCut;
            this.endWhenBrokenThrough = endWhenBrokenThrough;
            this.startSymbol = startSymbol;
            this.endSymbol = endSymbol;
            this.cost = cost;
        }

        @Override
        public String toString() {
            return this.firstWallInShortCut +
                    " [" + this.startSymbol + "] -> " +
                    this.endWhenBrokenThrough +
                    " [" + this.endSymbol + "] cost " +
                    this.cost + " saves " + this.savings;
        }
    }
}
