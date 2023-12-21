package com.simongarton.adventofcode.year2023;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.io.IOException;
import java.util.*;

public class Year2023Day17 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = true;
    private static final int CHAR_WIDTH = 12;
    private static final int CHAR_HEIGHT = 24;

    private Map<String, Map<String, Cell>> map;
    private Map<String, String> backtracks;
    private int height;
    private int width;

    private TerminalScreen screen;

    @Override
    public String title() {
        return "Day 17: Clumsy Crucible";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 17);
    }

    @Override
    public String part1(final String[] input) {


        this.loadMap(input);
        try {
            this.setUpLanterna();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        final List<Cell> path = this.aStar();
        if (DEBUG) {
            for (final Cell cell : path) {
                System.out.println(cell);
            }
        }
        return String.valueOf(path.size() - 1);
    }

    private void setUpLanterna() throws IOException {
        final Terminal terminal = new DefaultTerminalFactory().createTerminal();
        ((SwingTerminalFrame) terminal).setTitle(this.title());
        ((SwingTerminalFrame) terminal).setSize(this.width * CHAR_WIDTH, 50 + this.width * CHAR_HEIGHT);
        this.screen = new TerminalScreen(terminal);
        this.screen.setCursorPosition(null);

        this.screen.startScreen();
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    private void loadMap(final String[] input) {
        this.map = new TreeMap<>();
        int row = 0;
        for (final String line : input) {
            for (int col = 0; col < line.length(); col++) {
                final String square = line.substring(col, col + 1);
                final Cell cell = this.buildCoord(square, row, col);
                this.map.put(cell.getAddress(), new TreeMap<>());
                this.map.get(cell.getAddress()).put("-", cell);
            }
            row++;
        }
        this.width = input[0].length();
        this.height = input.length;

        this.backtracks = new HashMap<>();
        this.backtracks.put("N", "S");
        this.backtracks.put("S", "N");
        this.backtracks.put("W", "E");
        this.backtracks.put("E", "W");
    }

    private Integer h(final Cell start, final Cell end) {
        return this.manhattan(start, end);
    }

    private Integer manhattan(final Cell start, final Cell end) {
        return Math.abs(start.x - end.x) + Math.abs(start.y - end.y);
    }

    private List<Cell> aStar() {

        final Cell start = this.getStart();
        return this.aStar(start);
    }

    private Cell getStart() {
        return this.getCell(0, 0, "-");
    }

    private Cell getEnd() {
        return this.getCell(this.height - 1, this.width - 1, "-");
    }

    private Cell getCell(final int row, final int col) {
        if (row < 0 || row >= this.height) {
            return null;
        }
        if (col < 0 || col >= this.width) {
            return null;
        }
        final String key = col + "," + row;
        return this.map.get(key).get(this.map.get(key).keySet().stream().findFirst().get());
    }

    private Cell getCell(final int row, final int col, final String last3Moves) {
        if (row < 0 || row >= this.height) {
            return null;
        }
        if (col < 0 || col >= this.width) {
            return null;
        }
        final String address = col + "," + row;
        return this.map.get(address).get(last3Moves);
    }

    private List<Cell> aStar(final Cell start) {

        final Cell end = this.getEnd();

        final Set<Cell> openSet = new HashSet<>(Collections.singleton(start));
        final Map<Cell, Cell> cameFrom = new HashMap<>();
        final Map<Cell, Integer> gScore = new HashMap<>();
        gScore.put(start, 0);

        final Map<Cell, Integer> fScore = new HashMap<>();
        fScore.put(start, this.h(start, start));

        while (!openSet.isEmpty()) {
            final Cell current = this.bestOpenSetWithFScoreValue(openSet, fScore);
            if (current.getAddress().equalsIgnoreCase(end.getAddress())) {
                this.drawCurrentMap(current, cameFrom);
                return this.endFrom(current, cameFrom);
            }
            this.debugPrint("working on / removing current " + current.toString() + " with openSet.size()=" + openSet.size());
            openSet.remove(current);
            final List<Cell> neighbours = this.getImmediateNeighbours(current);
            for (final Cell neighbor : neighbours) {
                final int tentative_gScore = gScore.get(current) + neighbor.getTotalCost();
                this.debugPrint("  checking neighbour " + neighbor + " tentative_gScore=" + tentative_gScore);
                if (tentative_gScore < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    this.debugPrint("     using neighbour " + neighbor);
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentative_gScore);
                    fScore.put(neighbor, tentative_gScore + this.h(start, neighbor));
                    openSet.add(neighbor);
                    this.drawCurrentMap(current, cameFrom);
                } else {
                    this.debugPrint("     ignoring neighbour " + neighbor);
                }
            }
        }

        throw new RuntimeException("AStar failed.");
    }

    private void drawCurrentMap(final Cell current, final Map<Cell, Cell> cameFrom) {
        final List<Cell> cells = this.reconstructPath(cameFrom, current);
        final String map = this.createMapWithValues(cells);
        for (int i = 0; i < this.height; i++) {
            final String line = map.substring(i * this.width, (i + 1) * this.width);
            this.drawString(line, 0, i, TextColor.ANSI.BLACK);
        }

        this.drawString("total:" + current.getTotalCost() + "  ", 1, 13, TextColor.ANSI.WHITE, TextColor.ANSI.BLACK);

        try {
            this.screen.refresh();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(0);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawString(final String s, final int x, final int y, final TextColor background) {
        for (int i = 0; i < s.length(); i++) {
            this.drawChar(s.charAt(i), x + i, y, background);
        }
    }

    private void drawString(final String s, final int x, final int y, final TextColor foreground, final TextColor background) {
        for (int i = 0; i < s.length(); i++) {
            this.drawChar(s.charAt(i), x + i, y, foreground, background);
        }
    }

    private void drawChar(final char c, final int x, final int y, final TextColor background) {
        final TextColor coloredForeground = this.getCharColor(c);
        this.drawChar(c, x, y, coloredForeground, background);
    }

    private void drawChar(final char c, final int x, final int y, final TextColor foreground, final TextColor background) {
        final TextCharacter textCharacter = new TextCharacter(c, foreground, background);
        this.screen.setCharacter(new TerminalPosition(x, y), textCharacter);
    }

    private TextColor getCharColor(final char c) {
        switch (c) {
            case '#':
                return TextColor.ANSI.WHITE;
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return this.scaledColor(c);
            default:
                return TextColor.ANSI.WHITE;
//                throw new RuntimeException("Bad char");
        }
    }

    private TextColor scaledColor(final char c) {
        final int value = Integer.parseInt("" + c);
        final int red = (255 * value / 9);
        final int green = 0;
        final int blue = 255 - (255 * value / 9);
        return new TextColor.RGB(red, green, blue);
    }

    private List<Cell> endFrom(final Cell current, final Map<Cell, Cell> cameFrom) {
        final List<Cell> cells = this.reconstructPath(cameFrom, current);
        if (DEBUG) {
            this.drawPath(cells);
        }

        while (true) {
            final KeyStroke keyStroke;
            try {
                keyStroke = this.screen.pollInput();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            if (keyStroke != null && (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF)) {
                break;
            }
        }
        return cells;
    }

    private List<Cell> getImmediateNeighbours(final Cell current) {
        final List<Cell> neighbours = new ArrayList<>();
        this.maybeAddNeighbour("N", current, 0, -1, neighbours);
        this.maybeAddNeighbour("E", current, 1, 0, neighbours);
        this.maybeAddNeighbour("S", current, 0, 1, neighbours);
        this.maybeAddNeighbour("N", current, -1, 0, neighbours);
        return neighbours;
    }

    private void maybeAddNeighbour(final String direction, final Cell current, final int xDelta, final int yDelta, final List<Cell> neighbours) {
        // I suspect this isn't working with the A* / Djikstra
        // I think it's backtracking against different cells but then picking the closest one, ignoring how it got here.
        // I suspect my key approach is broken and I should put the
        final int x = current.getX() + xDelta;
        final int y = current.getY() + yDelta;
        final String key = x + "," + y;
        // am I off the map
        if (!this.map.containsKey(key)) {
            return;
        }
        // am I backtracking
        if (this.backtracking(direction, current)) {
            return;
        }
        // am I straight-lining ?
        if (this.straightLining(direction, current)) {
            return;
        }
        String howIGotHereNow = current.getHowIGotHere() + direction;

//        System.out.println(key + " dir " + direction + " from " + current.getAddress() + " via " + howIGotHereNow);
        if (howIGotHereNow.length() == 4) {
            howIGotHereNow = howIGotHereNow.substring(1);
        }
        final Cell any = this.map.get(key).get(this.map.get(key).keySet().stream().findFirst().get());
        final Cell cell = Cell.builder()
                .value(current.getValue())
                .x(x)
                .y(y)
                .cost(any.getCost())
                .totalCost(current.getTotalCost() + any.getCost())
                .howIGotHere(howIGotHereNow)
                .build();
        // have I got here before like this ? I didn't think I was going to need this check
        if (this.map.get(key).containsKey(howIGotHereNow)) {
            return;
        }
        this.map.get(key).put(howIGotHereNow, cell);
        neighbours.add(cell);
    }

    private void debugMap() {
        for (final Map.Entry<String, Map<String, Cell>> entry : this.map.entrySet()) {
            this.debugCell(entry.getKey());
        }
    }

    private void debugCell(final String key) {
        System.out.println(key);
        final Map<String, Cell> cells = this.map.get(key);
        for (final Map.Entry<String, Cell> mapEntry : cells.entrySet()) {
            System.out.println("  " + mapEntry.getKey() + ":" + mapEntry.getValue());
        }
    }

    private boolean straightLining(final String direction, final Cell current) {
        if (current.getHowIGotHere().length() < 3) {
            return false;
        }
        if (direction.repeat(3).equalsIgnoreCase(current.getHowIGotHere())) {
            return true;
        }
        return false;
    }

    private boolean backtracking(final String direction, final Cell current) {
        if (current.getHowIGotHere().isEmpty()) {
            return false;
        }
        final String lastDir = current.getHowIGotHere().substring(current.getHowIGotHere().length() - 1);
        return this.backtracks.get(direction).equalsIgnoreCase(lastDir);
    }

    private void drawPath(final List<Cell> cells) {

        final String map = this.createMap(cells);
        for (int i = 0; i < this.height; i++) {
            final String line = map.substring(i * this.width, (i + 1) * this.width);
            System.out.println(line);
        }
    }

    private String createMapWithValues(final List<Cell> cells) {

        final StringBuilder mapBuilder = new StringBuilder();
        for (int i = 0; i < this.height; i++) {
            final StringBuilder line = new StringBuilder();
            for (int j = 0; j < this.width; j++) {
                line.append(this.getCell(i, j).getValue());
            }
            mapBuilder.append(line);
        }

        String map = mapBuilder.toString();

        for (final Cell cell : cells) {
            map = this.replaceCharacter(map, cell.getX(), cell.getY(), "#");
        }

        return map;
    }

    private String createMap(final List<Cell> cells) {

        final StringBuilder mapBuilder = new StringBuilder();
        for (int i = 0; i < this.height; i++) {
            final String line = ".".repeat(this.width);
            mapBuilder.append(line);
        }

        String map = mapBuilder.toString();

        for (final Cell cell : cells) {
            map = this.replaceCharacter(map, cell.getX(), cell.getY(), "#");
        }

        return map;
    }

    private String replaceCharacter(final String map, final int x, final int y, final String replacement) {

        final int index = (y * this.width) + x;
        return this.replaceCharacter(map, index, replacement);
    }

    private String replaceCharacter(final String original, final Integer index, final String replacement) {

        return original.substring(0, index) + replacement + original.substring(index + 1);
    }

    private void debugPrint(final String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

    private Cell bestOpenSetWithFScoreValue(final Set<Cell> openSet, final Map<Cell, Integer> fScore) {
        Cell best = null;
        int bestValue = 0;
        for (final Cell cell : openSet) {
            final int cost = fScore.get(cell); // may not be there ?
            if (best == null || bestValue > cost) {
                best = cell;
                bestValue = cost;
            }
        }
        return best;
    }

    private List<Cell> reconstructPath(final Map<Cell, Cell> cameFrom, final Cell end) {
        Cell current = end;
        final List<Cell> path = new ArrayList<>();
        path.add(current);
        String line = current.getAddress();
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
            line = line + current.getAddress() + "-";
            System.out.println(line);
        }
        System.out.println("Out at " + current.getAddress());
        return path;
    }

    private Cell buildCoord(final String square, final int row, final int col) {
        final Cell cell = Cell.builder()
                .value(square)
                .x(col)
                .y(row)
                .cost(Integer.parseInt(square))
                .totalCost(0)
                .howIGotHere("")
                .build();
        return cell;
    }

    @Data
    @Builder
    private static final class Cell {

        private String value;
        private int cost;
        private int totalCost;
        private int x;
        private int y;
        private String howIGotHere;

        @Override
        public String toString() {
            return this.x + "," + this.y + " [" + this.cost + "] ' (" + this.howIGotHere + ")";
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Cell cell = (Cell) o;
            return this.x == cell.x && this.y == cell.y;
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
