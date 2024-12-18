package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Year2024Day18 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;

    private static final int BITMAP_SCALE = 10;
    private static final String CORRUPT = "#";
    private static final String WALL = "#";
    private static final String EMPTY = ".";

    int shortest = Integer.MAX_VALUE;

    @Override
    public String title() {
        return "Day 18: RAM Run";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 18);
    }

    @Override
    public String part1(final String[] input) {

        this.setup(input);

        final int totalIterations = 1024;

        for (int iteration = 0; iteration < totalIterations; iteration++) {
            final String data = input[iteration];
            final AoCCoord coord = new AoCCoord(data);
            this.setChallengeMapLetter(coord.x, coord.y, "#");
        }

        this.shortest = Integer.MAX_VALUE;
        final List<Node> shortestPaths = this.shortestPaths(true);

        int best = Integer.MAX_VALUE;
        this.paintChallengeMapWithAllNodes(shortestPaths);
        for (final Node shorted : shortestPaths) {
            final int length = this.lengthOfTrail(shorted);
            if (length < best) {
                best = length;
            }
        }

        return String.valueOf(best);
    }

    private void setup(final String[] input) {

        this.emptyTempFolder();

        this.mapHeight = 71;
        this.mapWidth = 71;

        this.challengeMap = new ArrayList<>();
        for (int i = 0; i < this.mapHeight; i++) {
            this.challengeMap.add(".".repeat(this.mapWidth));
        }
    }

    private void paintChallengeMapWithAllNodes(final List<Node> shortestPaths) {

        final List<String> lines = new ArrayList<>(this.challengeMap);
        this.paintMap(0, lines);
        int index = 0;
        for (final Node node : shortestPaths) {
            Node workingNode = node;
            while (workingNode != null) {
                this.updateWithNode(lines, workingNode, String.valueOf(index % 10));
                workingNode = workingNode.previous;
            }
            index++;
        }
        this.paintMap(1, lines);
    }

    private void drawChallengeMapWithNode(final Node shorted) {

        final List<String> lines = new ArrayList<>(this.challengeMap);
        Node node = shorted;
        while (node != null) {
            this.updateWithNode(lines, node, "O");
            node = node.previous;
        }

        for (final String line : lines) {
            System.out.println(line);
        }
        System.out.println();
    }

    private void updateWithNode(final List<String> lines,
                                final Node node,
                                final String symbol) {

        final AoCCoord coord = node.coord;
        final String line = lines.get(coord.y);
        final String newLine = line.substring(0, coord.x) + symbol + line.substring(coord.x + 1);
        lines.add(coord.y, newLine);
        lines.remove(coord.y + 1);
    }

    private List<Node> shortestPaths(final boolean findAll) {

        final Node startNode = new Node(new AoCCoord(0, 0), null, 0);
        final AoCCoord endCoord = new AoCCoord(this.mapWidth - 1, this.mapHeight - 1);

        final List<Node> available = new ArrayList<>();
        available.add(startNode);
        final List<Node> visited = new ArrayList<>();

        int iteration = 0;
        final List<Node> hits = new ArrayList<>();
        while (!available.isEmpty()) {
            final int index = this.bestAvailable(available, endCoord);
            final Node current = available.remove(index);
            visited.add(current);
            if (current.coord.equals(endCoord)) {
                if (DEBUG) {
                    System.out.println("got there at " + iteration + " length " + this.lengthOfTrail(current));
                }
                hits.add(current);
                if (!findAll) {
                    break;
                }
                continue;
            }
            final List<Node> neighbours = this.getNeighbours(current, visited, available);
            available.addAll(neighbours);
            iteration++;
        }

        return hits;
    }

    private void paintChallengeMapWithNode(final Node node, final int time, final List<Node> available) {

        final List<String> lines = new ArrayList<>(this.challengeMap);
        Node workingNode = node;
        while (workingNode != null) {
            this.updateWithNode(lines, workingNode, "O");
            workingNode = workingNode.previous;
        }
        this.paintMap(time, lines);
    }

    private void paintMap(final int steps, final List<String> lines) {

        final String filename = "/Users/simongarton/projects/java/AdventOfCode/temp/memory-" + String.format("%06d", steps) + ".png";

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

        final Map<String, Color> colors = new HashMap<>();
        final Random random = new Random();

        for (int row = 0; row < this.mapWidth; row++) {
            for (int col = 0; col < this.mapHeight; col++) {
                final String thing = lines.get(row).charAt(col) + "";
                if (thing.equalsIgnoreCase(EMPTY)) {
                    continue;
                }
                if (!colors.containsKey(thing)) {
                    colors.put(thing, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                }
                graphics2D.setPaint(colors.get(thing));
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

    private int lengthOfTrail(final Node target) {

        Node working = target.previous;
        int length = 0;
        while (working != null) {
            length++;
            working = working.previous;
        }
        return length;
    }

    private List<Node> getNeighbours(final Node current, final List<Node> visited, final List<Node> available) {

        final List<Node> neighbours = new ArrayList<>();
        this.maybeAddNeighbour(neighbours, current, -1, 0, visited, available);
        this.maybeAddNeighbour(neighbours, current, 0, -1, visited, available);
        this.maybeAddNeighbour(neighbours, current, 1, 0, visited, available);
        this.maybeAddNeighbour(neighbours, current, 0, 1, visited, available);

        return neighbours;
    }

    private void maybeAddNeighbour(final List<Node> neighbours,
                                   final Node current,
                                   final int x,
                                   final int y,
                                   final List<Node> visited,
                                   final List<Node> available) {

        final AoCCoord coord = new AoCCoord(current.coord.x + x, current.coord.y + y);
        final String mapLetter = this.getChallengeMapLetter(coord);
        if (mapLetter == null) {
            return;
        }
        if (mapLetter.equalsIgnoreCase(CORRUPT)) {
            return;
        }
        final Node node = new Node(coord, current, current.cost + 1);
        if (node.cost > this.shortest) {
            return;
        }
        if (this.alreadyVisited(visited, node)) {
            return;
        }
        if (this.alreadyVisited(available, node)) {
            return;
        }
        neighbours.add(node);
    }

    private boolean alreadyVisited(final List<Node> visited, final Node node) {

        for (final Node visit : visited) {
            if (visit.coord.equals(node.coord)) {
                if (visit.cost <= node.cost) {
                    return true;
                }
            }
        }
        return false;
    }

    private int bestAvailable(final List<Node> available, final AoCCoord endCoord) {

        int best = 0;
        int index = 0;
        double distance = Integer.MAX_VALUE;
        for (final Node node : available) {
            final double thisDistance = this.pythagoras(this.mapWidth - node.coord.x, this.mapHeight - node.coord.y);
//            final double thisDistance = this.manhattan(node.coord, endCoord);
            if (thisDistance < distance) {
                distance = thisDistance;
                best = index;
            }
            index++;
        }
        return best;
    }

    @Override
    public String part2(final String[] input) {

        this.setup(input);

        final int low = 2850;
        final int high = 2860; // input.length;

        String badCoord = null;
        for (int totalIterations = high; totalIterations >= low; totalIterations--) {

            this.setup(input);

            for (int iteration = 0; iteration < totalIterations; iteration++) {
                final String data = input[iteration];
                final AoCCoord coord = new AoCCoord(data);
                this.setChallengeMapLetter(coord.x, coord.y, "#");
            }

            this.shortest = Integer.MAX_VALUE;
            final List<Node> shortestPaths = this.shortestPaths(false);
            if (!shortestPaths.isEmpty()) {
                badCoord = input[totalIterations];
                break;
            }
        }

        return badCoord;
    }

    static class Node {

        AoCCoord coord;
        Node previous;
        int cost;

        public Node(final AoCCoord coord, final Node previous, final int cost) {
            this.coord = coord;
            this.previous = previous;
            this.cost = cost;
        }
    }
}
