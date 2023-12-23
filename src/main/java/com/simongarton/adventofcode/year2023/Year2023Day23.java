package com.simongarton.adventofcode.year2023;

import com.googlecode.lanterna.TextColor;
import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.*;

public class Year2023Day23 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = true;

    final Map<Integer, List<Tile>> openSet = new HashMap<>();
    private String map;
    private int width;
    private int height;
    private Tile start;
    private Tile end;

    private Map<Tile, Tile> cameFrom;

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
            this.debugMap();
            this.setUpLanterna(this.width, this.height);
        }
        this.cameFrom = new HashMap<>();

        this.start = Tile.builder()
                .x(1)
                .y(0)
                .cost(0)
                .build();
        this.end = Tile.builder()
                .x(this.width - 2)
                .y(this.height - 1)
                .cost(0)
                .build();

        //this.aStar();
        this.dfs();
        final List<Tile> path = this.reconstructPath(this.end);
        this.debugMap();

        this.waitForKeys();

        // -1 as I have start AND end
        return String.valueOf(path.size() - 1);
    }

    private void dfs() {

        final Set<Tile> visited = new HashSet<>();
        final Stack<Tile> stack = new Stack<>();

        stack.push(this.start);
        visited.add(this.start);

        while (!stack.isEmpty()) {
            final Tile current = stack.pop();
            for (final Tile neighbour : this.getNeighbours(current, visited)) {
                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    stack.push(neighbour);
                }
            }
            this.drawCurrentMap(visited);
        }
    }

    private List<Tile> getNeighbours(final Tile tile, final Set<Tile> visited) {
        final List<Tile> neighbours = new ArrayList<>();
        Optional<Tile> optionalTile = this.getNeighbour(tile, 1, 0, visited);
        optionalTile.ifPresent(neighbours::add);
        optionalTile = this.getNeighbour(tile, -1, 0, visited);
        optionalTile.ifPresent(neighbours::add);
        optionalTile = this.getNeighbour(tile, 0, 1, visited);
        optionalTile.ifPresent(neighbours::add);
        optionalTile = this.getNeighbour(tile, 0, -1, visited);
        optionalTile.ifPresent(neighbours::add);
        return neighbours;
    }

    private Optional<Tile> getNeighbour(final Tile tile, final int deltaX, final int deltaY, final Set<Tile> visited) {
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

        if (newTile.getX() == this.end.getX() && newTile.getY() == this.end.getY()) {
            System.out.println("Found end with cost " + cost);
            return Optional.of(newTile);
        }

        if (visited.contains(newTile)) {
            return Optional.empty();
        }

        return Optional.of(newTile);
    }

    private void aStar() {

        this.openSet.clear();
        this.openSet.put(0, List.of(this.start));

        while (!this.openSet.isEmpty()) {
            final int minCost = this.openSet.keySet().stream().min(Integer::compareTo).get();
            final List<Tile> tiles = this.openSet.remove(minCost);
            for (final Tile tile : tiles) {
                this.neighbours(tile);
            }
            if (DEBUG) {
                final List<Tile> path = this.reconstructPath(this.end);
                this.drawCurrentMap(path);
            }
        }
    }

    private void drawCurrentMap(final Collection<Tile> tiles) {

        String newMap = this.map;
        for (final Tile tile : tiles) {
            newMap = this.replaceCharacter(newMap, tile.getX(), tile.getY(), this.width, "o");
        }

        for (int i = 0; i < this.height; i++) {
            final String line = newMap.substring(i * this.width, (i + 1) * this.width);
            this.drawColoredString(line, 0, i, TextColor.ANSI.BLACK);
        }

        this.refreshAndSleep(10);
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
            default:
                return TextColor.ANSI.WHITE;
        }
    }

    private void debugMap() {
        for (int row = 0; row < this.height; row++) {
            final String line = this.map.substring(row * this.width, (row + 1) * this.width);
            System.out.println(line);
        }
        this.blankLine();
    }

    private void neighbours(final Tile tile) {
        this.neighbour(tile, 1, 0);
        this.neighbour(tile, -1, 0);
        this.neighbour(tile, 0, 1);
        this.neighbour(tile, 0, -1);
    }

    private void neighbour(final Tile tile, final int deltaX, final int deltaY) {
        final int x = tile.getX() + deltaX;
        final int y = tile.getY() + deltaY;
        final int cost = tile.getCost() + 1;

        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            return;
        }
        final String floor = this.getFloor(x, y);
        if (floor.equalsIgnoreCase("#") || floor.equalsIgnoreCase(" ")) {
            return;
        }

        if (floor.equalsIgnoreCase(">") && deltaX != 1) {
            return;
        }
        if (floor.equalsIgnoreCase("<") && deltaX != -1) {
            return;
        }
        if (floor.equalsIgnoreCase("v") && deltaY != 1) {
            return;
        }
        if (floor.equalsIgnoreCase("^") && deltaY != -1) {
            return;
        }

        final Tile newTile = Tile.builder()
                .x(x)
                .y(y)
                .cost(cost)
                .build();

        if (newTile.getX() == this.end.getX() && newTile.getY() == this.end.getY()) {
            this.cameFrom.put(this.end, tile);
            return;
        }

        final List<Tile> tiles = this.openSet.getOrDefault(cost, new ArrayList<>());
        tiles.add(newTile);
        this.map = this.replaceCharacter(this.map, x, y, this.width, " ");
        this.cameFrom.put(newTile, tile);
        this.openSet.put(cost, tiles);
    }

    private String getFloor(final int x, final int y) {

        final int index = (y * this.width) + x;
        return this.map.substring(index, index + 1);
    }

    private Integer h(final Tile tile) {
        return this.manhattanDistance(tile, this.end);
    }

    private int manhattanDistance(final Tile from, final Tile to) {
        return Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY());
    }

    private List<Tile> reconstructPath(final Tile end) {

        final List<Tile> path = new ArrayList<>();
        Tile current = end;
        while (true) {
            path.add(0, current);
            this.map = this.replaceCharacter(this.map, current.getX(), current.getY(), this.width, "o");
            if (!this.cameFrom.containsKey(current)) {
                break;
            }
            current = this.cameFrom.get(current);
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
    }
}
