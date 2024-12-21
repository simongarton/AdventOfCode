package com.simongarton.adventofcode;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.simongarton.adventofcode.common.ChallengeCoord;
import com.simongarton.adventofcode.common.ChallengeNode;
import com.simongarton.adventofcode.exceptions.InvalidSetupException;
import lombok.Getter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AdventOfCodeChallenge {

    public String title() {
        return null;
    }

    private static final int CHAR_WIDTH = 12;
    private static final int CHAR_HEIGHT = 24;

    public static final String WALL = "#";
    public static final String EMPTY = ".";
    public static final String PATH = "O";

    public abstract Outcome run();

    @Getter
    protected int year;
    @Getter
    protected int day;

    protected List<String> challengeMap;
    protected int mapWidth;
    protected int mapHeight;

    protected TerminalScreen screen;

    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0");

    public abstract String part1(final String[] input);

    public abstract String part2(final String[] input);

    public Outcome runChallenge(final int year, final int day) {
        this.year = year;
        this.day = day;
        final Outcome outcome = new Outcome();
        for (int part = 1; part <= 2; part++) {
            final long start = System.currentTimeMillis();
            final String[] input = this.loadStrings(year, day, part);
            final String expected = this.loadAnswer(year, day, part);
            final String actual = part == 1 ? this.part1(input) : this.part2(input);
            if (!expected.equals(actual)) {
                if (part == 1) {
                    outcome.part1 = false;
                } else {
                    outcome.part2 = false;
                }
                if (this.title() == null) {
                    System.out.printf("Attempted %s.%02d.%s but wrong/null answer in %s ms : %20s%n",
                            year,
                            day,
                            part,
                            this.leftPad("" + (System.currentTimeMillis() - start), 9),
                            actual
                    );
                } else {
                    System.out.printf("Attempted %s.%02d.%s but wrong/null answer in %s ms : %20s (%s part %s)%n",
                            year,
                            day,
                            part,
                            this.leftPad("" + (System.currentTimeMillis() - start), 9),
                            actual,
                            this.title(),
                            part
                    );
                }
            } else {
                if (this.title() == null) {
                    System.out.printf("Attempted %s.%02d.%s and got correct answer in %s ms : %20s%n",
                            year,
                            day,
                            part,
                            this.leftPad("" + (System.currentTimeMillis() - start), 8),
                            actual
                    );
                } else {
                    System.out.printf("Attempted %s.%02d.%s and got correct answer in %s ms : %20s (%s part %s)%n",
                            year,
                            day,
                            part,
                            this.leftPad("" + (System.currentTimeMillis() - start), 8),
                            actual,
                            this.title(),
                            part
                    );
                }
            }
        }
        return outcome;
    }

    private String leftPad(final String s, final int length) {
        if (s.length() == length) {
            return s;
        }
        if (s.length() > length) {
            return "*".repeat(length);
        }
        return " ".repeat(length - s.length()) + s;
    }

    private String[] loadStrings(final int year, final int day, final int part) {
        final Path path = Paths.get(String.format("src/main/resources/%s/%s-Day%s-%s.txt",
                year,
                year,
                day,
                part));
        try {
            return Files.lines(path, StandardCharsets.UTF_8).toArray(String[]::new);
        } catch (final IOException e) {
            e.printStackTrace();
            throw new InvalidSetupException(e.getMessage());
        }
    }

    private String loadAnswer(final int year, final int day, final int part) {
        final Path path = Paths.get(String.format("src/main/resources/%s/%s-Day%s-%s-answer.txt",
                year,
                year,
                day,
                part));
        try {
            final List<String> lines = Files.lines(path, StandardCharsets.UTF_8)
                    .collect(Collectors.toList());
            return lines.get(0);
        } catch (final IOException e) {
            e.printStackTrace();
            throw new InvalidSetupException(e.getMessage());
        }
    }

    protected void writeStringsToFile(final List<String> lines, final File file) {

        try {
            final BufferedWriter br = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
            for (final String str : lines) {
                br.write(str + System.lineSeparator());
            }
            br.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final class Outcome {
        boolean part1 = true;
        boolean part2 = true;

        public boolean both() {
            return this.part1 && this.part2;
        }
    }

    protected void setUpLanterna(final int width, final int height) {

        try {
            final Terminal terminal = new DefaultTerminalFactory().createTerminal();
            ((SwingTerminalFrame) terminal).setTitle(this.title());
            // 2023.23 needed 1,0 ...
            ((SwingTerminalFrame) terminal).setSize((width - 1) * CHAR_WIDTH, (height - 0) * CHAR_HEIGHT);
            this.screen = new TerminalScreen(terminal);
            this.screen.setCursorPosition(null);
            this.screen.startScreen();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void drawString(final String s, final int x, final int y, final TextColor background) {

        for (int i = 0; i < s.length(); i++) {
            this.drawChar(s.charAt(i), x + i, y, TextColor.ANSI.WHITE_BRIGHT, background);
        }
    }

    protected void drawString(final String s, final int x, final int y, final TextColor foreground, final TextColor background) {

        for (int i = 0; i < s.length(); i++) {
            this.drawChar(s.charAt(i), x + i, y, foreground, background);
        }
    }

    protected void drawChar(final char c, final int x, final int y, final TextColor foreground, final TextColor background) {
        final TextCharacter textCharacter = new TextCharacter(c, foreground, background);
        this.screen.setCharacter(new TerminalPosition(x, y), textCharacter);
    }

    protected void waitForKeys() {
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
    }

    protected void refreshAndSleep(final int millis) {
        try {
            this.screen.refresh();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void blankLine() {
        System.out.println();
    }

    protected String replaceCharacter(final String map, final int x, final int y, final int width, final String replacement) {

        final int index = (y * width) + x;
        return map.substring(0, index) + replacement + map.substring(index + 1);
    }

    protected void loadChallengeMap(final String[] input) {

        this.mapWidth = input[0].length();
        this.mapHeight = input.length;

        this.challengeMap = new ArrayList<>();
        this.challengeMap.addAll(Arrays.asList(input));
    }

    protected void drawChallengeMap() {

        this.drawMapFromLines(this.challengeMap);
    }

    protected void drawMapFromLines(final List<String> lines) {

        for (final String line : lines) {
            System.out.println(line);
        }
        System.out.println();
    }

    protected List<String> updateMapWithNode(final ChallengeNode end, final String symbol) {

        final List<String> lines = new ArrayList<>(this.challengeMap);
        ChallengeNode node = end;
        while (node != null) {
            this.updateWithNode(lines, node, symbol);
            node = node.getPrevious();
        }

        return lines;
    }

    protected void updateWithNode(final List<String> lines,
                                  final ChallengeNode node,
                                  final String symbol) {

        final ChallengeCoord coord = node.getCoord();
        final String line = lines.get(coord.getY());
        final String newLine = line.substring(0, coord.getX()) + symbol + line.substring(coord.getX() + 1);
        lines.add(coord.getY(), newLine);
        lines.remove(coord.getY() + 1);
    }


    protected String getChallengeMapSymbol(final ChallengeCoord c) {

        return this.getChallengeMapSymbol(c.getX(), c.getY());
    }

    protected void setChallengeMapLetter(final ChallengeCoord c, final String letter) {

        this.setChallengeMapLetter(c.getX(), c.getY(), letter);
    }

    protected void setChallengeMapLetter(final int x, final int y, final String letter) {

        if (x < 0 || x >= this.mapWidth) {
            throw new RuntimeException(x + " is out of range");
        }
        if (y < 0 || y >= this.mapHeight) {
            throw new RuntimeException(y + " is out of range");
        }
        final String line = this.challengeMap.get(y);
        final String newLine = line.substring(0, x) + letter + line.substring(x + 1);
        this.challengeMap.remove(y);
        this.challengeMap.add(y, newLine);
    }

    protected String getChallengeMapSymbol(final int x, final int y) {

        if (x < 0 || x >= this.mapWidth) {
            return null;
        }
        if (y < 0 || y >= this.mapHeight) {
            return null;
        }
        return this.challengeMap.get(y).charAt(x) + "";
    }

    protected void setChallengeMapSymbol(final int x, final int y, final String symbol) {

        if (x < 0 || x >= this.mapWidth) {
            throw new RuntimeException("Out of bounds for x=" + x);
        }
        if (y < 0 || y >= this.mapHeight) {
            throw new RuntimeException("Out of bounds for y=" + y);

        }
        final String existingRow = this.challengeMap.get(y);
        final String newRow = existingRow.substring(0, x) + symbol + existingRow.substring(x + 1);
        this.challengeMap.add(y, newRow);
        this.challengeMap.remove(y + 1);
    }

    protected void dumpGraphToFile(final String filename, final List<String> lines) {

        try {
            final BufferedWriter br = new BufferedWriter(new FileWriter(filename));
            for (final String str : lines) {
                br.write(str + System.lineSeparator());
            }
            br.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected double pythagoras(final double a, final double b) {

        return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }

    protected long manhattanDistance(final ChallengeCoord a, final ChallengeCoord b) {

        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    protected void emptyTempFolder() {
        final File tempFile = Path.of("temp").toFile();
        this.deleteFolder(tempFile);
        tempFile.mkdir();
    }

    private void deleteFolder(final File folder) {
        final File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (final File f : files) {
                if (f.isDirectory()) {
                    this.deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public String formatBig(final Object o) {
        return this.decimalFormat.format(o);
    }

    public List<ChallengeNode> getShortestPathAStar(final ChallengeCoord start, final ChallengeCoord end) {

        ChallengeNode working = this.getShortestPathAStarEnd(start, end);
        if (working == null) {
            return Collections.emptyList();
        }
        final List<ChallengeNode> path = new ArrayList<>();
        path.add(0, working);
        working = working.getPrevious();
        while (working != null) {
            path.add(0, working);
            working = working.getPrevious();
        }
        return path;
    }

    public ChallengeNode getShortestPathAStarEnd(final ChallengeCoord start, final ChallengeCoord end) {

        final List<ChallengeNode> available = new ArrayList<>(
                List.of(
                        ChallengeNode.builder()
                                .coord(ChallengeCoord.builder()
                                        .x(start.getX())
                                        .y(start.getY())
                                        .build())
                                .cost(0L)
                                .build())
        );

        final List<ChallengeNode> visited = new ArrayList<>();

        ChallengeNode current = null;
        while (!available.isEmpty()) {
            final int bestIndex = this.getBestNodeIndexForAStar(available);
            current = available.remove(bestIndex);
            visited.add(current);

            if (current.getCoord().equals(end)) {
                break;
            }
            final List<ChallengeNode> neighbours = this.getNeighboursForAStar(current, available, visited);
            available.addAll(neighbours);
        }

        return current;
    }

    private List<ChallengeNode> getNeighboursForAStar(final ChallengeNode current, final List<ChallengeNode> available, final List<ChallengeNode> visited) {

        final List<ChallengeNode> neighbours = new ArrayList<>();
        this.maybeAddNeighbour(neighbours, current, +1, 0, available, visited);
        this.maybeAddNeighbour(neighbours, current, -1, 0, available, visited);
        this.maybeAddNeighbour(neighbours, current, 0, +1, available, visited);
        this.maybeAddNeighbour(neighbours, current, 0, -1, available, visited);

        return neighbours;
    }

    private void maybeAddNeighbour(final List<ChallengeNode> neighbours,
                                   final ChallengeNode current,
                                   final int xDelta,
                                   final int yDelta,
                                   final List<ChallengeNode> available,
                                   final List<ChallengeNode> visited) {

        final ChallengeCoord coord = ChallengeCoord.builder()
                .x(current.getCoord().getX() + xDelta)
                .y(current.getCoord().getY() + yDelta)
                .build();
        final String challengeMapSymbol = this.getChallengeMapSymbol(coord);
        if (challengeMapSymbol == null || challengeMapSymbol.equalsIgnoreCase(WALL)) {
            return;
        }
        if (this.nodeListContainsCoord(available, coord)) {
            return;
        }
        if (this.nodeListContainsCoord(visited, coord)) {
            return;
        }
        final ChallengeNode next = ChallengeNode.builder()
                .coord(coord)
                .cost(current.getCost() + 1)
                .previous(current)
                .build();
        neighbours.add(next);
    }

    private boolean nodeListContainsCoord(final List<ChallengeNode> visited, final ChallengeCoord coord) {

        return visited.stream().anyMatch(n -> n.getCoord().equals(coord));
    }

    private int getBestNodeIndexForAStar(final List<ChallengeNode> available) {

        // TBC
        return 0;
    }

    protected long heuristicForAStart(final ChallengeCoord workingCoord) {

        return 1L;
    }

    public ChallengeNode getShortestPathDjikstra(final ChallengeCoord start, final ChallengeCoord end) {

        return null;
    }

    public List<ChallengeNode> getBFS(final ChallengeCoord start) {

        return null;
    }

    public List<ChallengeNode> getDFS(final ChallengeCoord start) {

        return null;
    }

    public ChallengeCoord findChallengeCoord(final String symbol) {

        for (int x = 0; x < this.mapWidth; x++) {
            for (int y = 0; y < this.mapHeight; y++) {
                if (this.getChallengeMapSymbol(x, y).equalsIgnoreCase(symbol)) {
                    return ChallengeCoord.builder().x(x).y(y).build();
                }
            }
        }
        return null;
    }

    protected String plural(final long c) {
        return c == 1 ? "s" : "";
    }

    protected String nonPlural(final long c) {
        return c == 1 ? "" : "s";
    }

    protected String niceCount(final long c) {

        if (c == 0) {
            return "are no";
        }
        if (c == 1) {
            return "is one";
        }
        return "are " + c;
    }
}
