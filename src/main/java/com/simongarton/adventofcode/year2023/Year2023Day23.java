package com.simongarton.adventofcode.year2023;

import com.googlecode.lanterna.TextColor;
import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

public class Year2023Day23 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = true;

    private String map;
    private int width;
    private int height;

    private Map<Tile, Tile> cameFrom;
    private List<Tile> stops;

    @Override
    public String title() {
        return "Day 23: A Long Walk";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 23);
    }

    @Override
    public String part1(final String[] input) {

        this.loadMap(input);
        if (DEBUG) {
            this.setUpLanterna(this.width, this.height);
        }
        this.cameFrom = new HashMap<>();

        final Tile start = Tile.builder()
                .x(1)
                .y(0)
                .cost(0)
                .build();
        final Tile end = Tile.builder()
                .x(this.width - 2)
                .y(this.height - 1)
                .cost(0)
                .build();

        final Set<Tile> intersections = this.dfsToFindIntersections(start, end);
        intersections.add(start);
        intersections.add(end);
        this.stops = new ArrayList<>(intersections);
        // "shortest" but there can be only 1 ... won't go through other intersections
        final Map<String, Integer> shortestPaths = this.buildShortestPathMap(intersections);
        // you can get to all of them from the start ... I could change this to skip intersections
        // but yeah, nah
        final Map<String, Integer> pruned = this.pruneOthers(shortestPaths, start);
        this.drawCurrentMap(intersections, "!");
        final List<Integer> journeys = this.buildJourneys(pruned, start, end);
        if (DEBUG) {
            this.waitForKeys();
        }

        return String.valueOf(journeys.stream().mapToInt(Integer::valueOf).max());
    }

    private List<Integer> buildJourneys(final Map<String, Integer> pruned, final Tile start, final Tile end) {

        if (false) {
            this.digraph(pruned);
        }

        final List<List<Tile>> journeys = new ArrayList<>();
        final List<Tile> firstList = new ArrayList<>();
        firstList.add(start);
        journeys.add(firstList);
        final Set<List<Tile>> journeysDone = new HashSet<>();
        while (true) {
            boolean didSomething = false;
            final List<List<Tile>> journeysToAdd = new ArrayList<>();
            for (final List<Tile> workingJourney : journeys) {
                if (journeysDone.contains(workingJourney)) {
                    continue;
                }
                journeysDone.add(workingJourney);
                final Tile last = workingJourney.get(workingJourney.size() - 1);
                for (final Map.Entry<String, Integer> entry : pruned.entrySet()) {
                    final String[] parts = entry.getKey().split(" -> ");
                    final Tile from = this.getTileFromAddress(parts[0]);
                    final Tile to = this.getTileFromAddress(parts[1]);
                    if (from == last) {
                        final List<Tile> newJourney = new ArrayList<>();
                        newJourney.addAll(workingJourney);
                        newJourney.add(to);
                        journeysToAdd.add(newJourney);
                        didSomething = true;
                    }
                }
            }
            journeys.addAll(journeysToAdd);
            if (!didSomething) {
                break;
            }
        }
        final List<List<Tile>> completeJourneys = new ArrayList<>();
        for (final List<Tile> journey : journeys) {
            final Tile last = journey.get(journey.size() - 1);
            if (last.getAddress().equalsIgnoreCase(end.getAddress())) {
                completeJourneys.add(journey);
            }
        }

        final List<Integer> costs = new ArrayList<>();
        for (final List<Tile> journey : completeJourneys) {
            int cost = 0;
            final String line = journey.stream().map(Tile::getAddress).collect(Collectors.joining(" -> "));
            Tile from = null;
            Tile to = null;
            for (final Tile tile : journey) {
                to = tile;
                if (from != null) {
                    final String key = from.getAddress() + " -> " + to.getAddress();
                    cost = cost + pruned.get(key);
                }
                from = to;
            }
            System.out.println(line + " : " + cost);
            costs.add(cost);
        }

        return costs;
    }

    private void digraph(final Map<String, Integer> pruned) {
        System.out.println("digraph {");
        for (final Map.Entry<String, Integer> entry : pruned.entrySet()) {
            final String[] parts = entry.getKey().split(" -> ");
            System.out.println("\"" + parts[0] + "\" -> \"" + parts[1] + "\"");
        }
        System.out.println("}");
    }

    private Tile findFirstStop(final Tile start, final Map<String, Integer> pruned) {

        final String pattern = start.getAddress();
        for (final String key : pruned.keySet()) {
            if (key.startsWith(pattern)) {
                final String[] parts = key.split(" -> ");
                return this.getTileFromAddress(parts[1]);
            }
        }
        throw new RuntimeException("not found ");
    }

    private Tile getTileFromAddress(final String address) {

        return this.stops.stream()
                .filter(s -> s.getAddress().equalsIgnoreCase(address))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("awkward"));
    }

    private Map<String, Integer> pruneOthers(final Map<String, Integer> shortestPaths, final Tile start) {

        final Map<String, Integer> pruned = new HashMap<>();

        int lowest = Integer.MAX_VALUE;
        String lowEdge = null;
        final String pattern = start.getAddress();

        for (final Map.Entry<String, Integer> entry : shortestPaths.entrySet()) {
            if (!entry.getKey().startsWith(pattern)) {
                continue;
            }
            final int value = entry.getValue();
            if (value < lowest) {
                lowest = value;
                lowEdge = entry.getKey();
            }
        }
        for (final Map.Entry<String, Integer> entry : shortestPaths.entrySet()) {
            if (!entry.getKey().startsWith(pattern)) {
                pruned.put(entry.getKey(), entry.getValue());
                continue;
            }
            if (entry.getKey().equalsIgnoreCase(lowEdge)) {
                pruned.put(entry.getKey(), entry.getValue());
            }
        }
        return pruned;
    }

    private Map<String, Integer> buildShortestPathMap(final Set<Tile> intersections) {
        final List<Tile> intersectionList = new ArrayList<>(intersections);
        final Map<String, Integer> distances = new HashMap<>();
        for (final Tile from : intersectionList) {
            for (final Tile to : intersectionList) {
                if (from == to) {
                    continue;
                }
                final String key = from.getAddress() + " -> " + to.getAddress();
                final Integer distance = this.aStar(from, to, intersections);

                if (distance != null) {
                    distances.put(key, distance);
                }
                /*

                Something funny here. I think my costs are overestimated, and ...

                1,0 -> 21,22:74
                13,13 -> 21,22:74

                ... they should not be the same. The shortest path from start to end is 74. But I
                could be doubling back ?

                I'm getting 6 paths, the answer has 6 paths. But only 1 of my answers is even in the range of
                possibilities, and it' not one of the actual answers.

                 */
            }
        }
        for (final Map.Entry<String, Integer> entry : distances.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        return distances;
    }

    private Set<Tile> dfsToFindIntersections(final Tile start, final Tile end) {

        final Set<Tile> visited = new HashSet<>();
        final Stack<Tile> stack = new Stack<>();
        final Set<Tile> intersections = new HashSet<>();

        stack.push(start);
        visited.add(start);

        while (!stack.isEmpty()) {
            final Tile current = stack.pop();
            final List<Tile> neighbours = this.getNeighbours(current, visited, end);
            if (neighbours.size() > 1) {
                final Optional<Tile> optionalIntersection = this.getIntersection(current, neighbours);
                if (optionalIntersection.isPresent()) {
                    System.out.println("Found intersection at " + current);
                    visited.clear();
                    visited.add(current);
                    intersections.add(current);
                    visited.addAll(intersections);
                }
            }
            for (final Tile neighbour : neighbours) {
                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    stack.push(neighbour);
                }
            }

            this.drawCurrentMap(visited, "o");
        }
        return intersections;
    }

    private Optional<Tile> getIntersection(final Tile current, final List<Tile> neighbours) {
        for (final Tile tile : neighbours) {
            if (this.getFloor(tile.getX(), tile.getY()).equalsIgnoreCase(".")) {
                return (Optional.empty());
            }
        }
        return Optional.of(current);
    }

    private List<Tile> getNeighbours(final Tile tile, final Set<Tile> visited, final Tile end) {
        final List<Tile> neighbours = new ArrayList<>();
        Optional<Tile> optionalTile = this.getNeighbour(tile, 1, 0, visited, end);
        optionalTile.ifPresent(neighbours::add);
        optionalTile = this.getNeighbour(tile, -1, 0, visited, end);
        optionalTile.ifPresent(neighbours::add);
        optionalTile = this.getNeighbour(tile, 0, 1, visited, end);
        optionalTile.ifPresent(neighbours::add);
        optionalTile = this.getNeighbour(tile, 0, -1, visited, end);
        optionalTile.ifPresent(neighbours::add);
        return neighbours;
    }

    private Optional<Tile> getNeighbour(final Tile tile,
                                        final int deltaX,
                                        final int deltaY,
                                        final Set<Tile> visited,
                                        final Tile end) {
        final int x = tile.getX() + deltaX;
        final int y = tile.getY() + deltaY;
        final int cost = tile.getCost() + 1;

        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            return Optional.empty();
        }
        final String floor = this.getFloor(x, y);
        if (floor.equalsIgnoreCase("#")) {
            return Optional.empty();
        }

        if (floor.equalsIgnoreCase(">") && deltaX != 1) {
            return Optional.empty();
        }
        if (floor.equalsIgnoreCase("<") && deltaX != -1) {
            return Optional.empty();
        }
        if (floor.equalsIgnoreCase("v") && deltaY != 1) {
            return Optional.empty();
        }
        if (floor.equalsIgnoreCase("^") && deltaY != -1) {
            return Optional.empty();
        }

        final Tile newTile = Tile.builder()
                .x(x)
                .y(y)
                .cost(cost)
                .build();

        if (newTile.getX() == end.getX() && newTile.getY() == end.getY()) {
            return Optional.of(newTile);
        }

        if (visited.contains(newTile)) {
            return Optional.empty();
        }

        return Optional.of(newTile);
    }

    private Integer aStar(final Tile start, final Tile end, final Set<Tile> intersections) {

        final Map<Integer, List<Tile>> openSet = new HashMap<>();
        final Set<Tile> visited = new HashSet<>();
        visited.add(start);
        this.cameFrom.clear();
        openSet.put(0, List.of(start));

        while (!openSet.isEmpty()) {
            final int minCost = openSet.keySet().stream().min(Integer::compareTo).get();
            final List<Tile> tiles = openSet.remove(minCost);
            for (final Tile tile : tiles) {
                final List<Tile> neighbours = this.neighbours(tile, end, openSet, visited);
                final Integer distance = this.findEnd(neighbours, end);
                if (distance != null) {
                    if (DEBUG) {
                        final List<Tile> path = this.reconstructPath(end);
                        this.drawCurrentMap(intersections, "!");
                        this.sleep(1000);
                        System.out.println("traced from " + start + " to " + end + " distance " + distance);
                        this.drawCurrentMap(path, "x");
                        this.sleep(10000);
                    }
                    return distance;
                }
            }
        }
        return null;
    }

    private void sleep(final int millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer findEnd(final List<Tile> neighbours, final Tile end) {
        for (final Tile neighbour : neighbours) {
            if (neighbour.getX() == end.getX() && neighbour.getY() == end.getY()) {
                return end.getCost() - neighbour.getCost();
            }
        }
        return null;
    }

    private void drawCurrentMap(final Collection<Tile> tiles, final String tileSymbol) {

        String newMap = this.map;
        for (final Tile tile : tiles) {
            newMap = this.replaceCharacter(newMap, tile.getX(), tile.getY(), this.width, tileSymbol);
        }

        for (int i = 0; i < this.height; i++) {
            final String line = newMap.substring(i * this.width, (i + 1) * this.width);
            this.drawColoredString(line, 0, i, TextColor.ANSI.BLACK);
        }

        this.refreshAndSleep(0);
    }

    private void drawColoredString(final String s, final int x, final int y, final TextColor background) {

        for (int i = 0; i < s.length(); i++) {
            final TextColor foreground = this.textColor(s.charAt(i));
            this.drawChar(s.charAt(i), x + i, y, foreground, background);
        }
    }

    private TextColor textColor(final char c) {
        switch (c) {
            case '#':
                return TextColor.ANSI.GREEN;
            case 'o':
                return TextColor.ANSI.RED_BRIGHT;
            case 'x':
                return TextColor.ANSI.BLUE_BRIGHT;
            case '!':
                return TextColor.ANSI.WHITE_BRIGHT;
            default:
                return TextColor.ANSI.BLACK_BRIGHT;
        }
    }

    private void debugMap() {
        for (int row = 0; row < this.height; row++) {
            final String line = this.map.substring(row * this.width, (row + 1) * this.width);
            System.out.println(line);
        }
        this.blankLine();
    }

    private List<Tile> neighbours(final Tile tile, final Tile end, final Map<Integer, List<Tile>> openSet,
                                  final Set<Tile> visited) {
        final List<Tile> neighbours = new ArrayList<>();
        Optional<Tile> optionalTile = this.neighbour(tile, 1, 0, end, openSet, visited);
        optionalTile.ifPresent(neighbours::add);
        optionalTile = this.neighbour(tile, -1, 0, end, openSet, visited);
        optionalTile.ifPresent(neighbours::add);
        optionalTile = this.neighbour(tile, 0, 1, end, openSet, visited);
        optionalTile.ifPresent(neighbours::add);
        optionalTile = this.neighbour(tile, 0, -1, end, openSet, visited);
        optionalTile.ifPresent(neighbours::add);
        return neighbours;
    }

    private Optional<Tile> neighbour(final Tile tile,
                                     final int deltaX,
                                     final int deltaY,
                                     final Tile end,
                                     final Map<Integer, List<Tile>> openSet,
                                     final Set<Tile> visited) {

        final Tile newTile = this.getNewTile(tile, deltaX, deltaY, end);
        if (visited.contains(newTile)) {
            return Optional.empty();
        }
        if (newTile == null) {
            return Optional.empty();
        }

        if (newTile.getX() == end.getX() && newTile.getY() == end.getY()) {
            this.cameFrom.put(end, tile);
            return Optional.of(newTile);
        }

        final List<Tile> tiles = openSet.getOrDefault(newTile.getCost(), new ArrayList<>());
        tiles.add(newTile);
        visited.add(newTile);
        this.cameFrom.put(newTile, tile);
        openSet.put(newTile.getCost(), tiles);
        return Optional.of(newTile);
    }

    private Tile getNewTile(final Tile tile, final int deltaX, final int deltaY, final Tile end) {

        final int x = tile.getX() + deltaX;
        final int y = tile.getY() + deltaY;
        final int cost = tile.getCost() + 1;

        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            return null;
        }
        final String floor = this.getFloor(x, y);
        if (floor.equalsIgnoreCase("#") || floor.equalsIgnoreCase(" ")) {
            return null;
        }

        if (floor.equalsIgnoreCase(">") && deltaX != 1) {
            return null;
        }
        if (floor.equalsIgnoreCase("<") && deltaX != -1) {
            return null;
        }
        if (floor.equalsIgnoreCase("v") && deltaY != 1) {
            return null;
        }
        if (floor.equalsIgnoreCase("^") && deltaY != -1) {
            return null;
        }

        return Tile.builder()
                .x(x)
                .y(y)
                .cost(cost)
                .build();
    }

    private String getFloor(final int x, final int y) {

        final int index = (y * this.width) + x;
        return this.map.substring(index, index + 1);
    }

    private List<Tile> reconstructPath(final Tile end) {

        final Set<Tile> visited = new HashSet<>();
        final List<Tile> path = new ArrayList<>();
        Tile current = end;
        while (true) {
            path.add(0, current);
            if (!this.cameFrom.containsKey(current)) {
                break;
            }
            current = this.cameFrom.get(current);
            if (visited.contains(current)) {
                for (final Map.Entry<Tile, Tile> entry : this.cameFrom.entrySet()) {
                    System.out.println(entry.getKey().getAddress() + " : " + entry.getValue().getAddress());
                }
                throw new RuntimeException("Inception");
            }
            visited.add(current);
        }

        return path;
    }

    private void loadMap(final String[] input) {

        this.map = String.join("", input);
        this.width = input[0].length();
        this.height = input.length;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    @Data
    @Builder
    private static final class Tile {

        private int x;
        private int y;
        private int cost;

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Tile tile = (Tile) o;
            return this.x == tile.x && this.y == tile.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.x, this.y);
        }

        public String getAddress() {
            return this.x + "," + this.y;
        }
    }
}
