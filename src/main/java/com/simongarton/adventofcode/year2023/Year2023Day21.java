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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Year2023Day21 extends AdventOfCodeChallenge {

    private String map;
    private int width;
    private int height;
    private int start;
    private TerminalScreen screen;
    List<List<Long>> gridCounts;

    @Override
    public String title() {
        return "Day 21: Step Counter";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 21);
    }

    @Override
    public String part1(final String[] input) {

        this.loadMap(input);
        this.width = input[0].length();
        this.height = input.length;

        this.gridCounts = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            this.gridCounts.add(new ArrayList<>());
        }

        try {
            this.setupScreen();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        this.map = this.map.replace("S", "O");
        for (int step = 0; step < 64; step++) {
            this.walkies();
            this.drawMap();
            System.out.println(this.countGotSomewhere());
            this.countGrids();
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
//            this.debugMap();
        }

        try {
            this.pauseForTheJet();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        this.dumpGridCounts();

        return String.valueOf(this.countGotSomewhere());
    }

    private void dumpGridCounts() {
        final List<String> lines = new ArrayList<>();
        lines.add("index,1,2,3,4,5,6,7,8,9");
        for (int index = 0; index < 64; index++) {
            String line = index + ",";
            for (int grid = 0; grid < 9; grid++) {
                line = line + this.gridCounts.get(grid).get(index) + ",";
            }
            lines.add(line);
        }
        try {
            Files.write(Path.of("gridCounts.csv"), lines);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void countGrids() {
        int i = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                final long result = this.countGrid(i, row, col);
                this.gridCounts.get(i).add(result);
                i++;
            }
        }
    }

    private long countGrid(final int grid, final int row, final int col) {
        // for this ith grid, count the 11x11 grid
        String line = "";
        final int startX = 11 * col;
        for (int i = 0; i < 11; i++) {
            final int index = ((i + (row * 11)) * this.width) + startX;
            final String fragment = this.map.substring(index, index + 11);
            line = line + fragment;
        }
        final long result = line.chars().filter(ch -> ch == 'O').count() + line.chars().filter(ch -> ch == 'S').count();
        System.out.println(result);
        return result;
    }

    private void pauseForTheJet() throws IOException {
        while (true) {
            final KeyStroke keyStroke = this.screen.pollInput();
            if (keyStroke != null && (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF)) {
                break;
            }
        }
    }

    private void drawMap() {
        for (int row = 0; row < this.height; row++) {
            final String line = this.map.substring(row * this.width, (row + 1) * this.width);
            this.drawString(line, 0, row, TextColor.ANSI.WHITE, TextColor.ANSI.BLACK);
        }
        try {
            this.screen.refresh();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawChar(final char c, final int x, final int y, final TextColor foreground, final TextColor background) {
        final TextCharacter textCharacter = new TextCharacter(c, foreground, background);
        this.screen.setCharacter(new TerminalPosition(x, y), textCharacter);
    }

    private void drawString(final String s, final int x, final int y, final TextColor foreground, final TextColor background) {
        for (int i = 0; i < s.length(); i++) {
            this.drawChar(s.charAt(i), x + i, y, foreground, background);
        }
    }

    private void setupScreen() throws IOException {
        final Terminal terminal = new DefaultTerminalFactory().createTerminal();
        ((SwingTerminalFrame) terminal).setTitle(this.title());
        ((SwingTerminalFrame) terminal).setSize(800, 600);
        this.screen = new TerminalScreen(terminal);
        this.screen.setCursorPosition(null);

        this.screen.startScreen();
    }

    private long countGotSomewhere() {
        return this.map.chars().filter(ch -> ch == 'O').count() + 1;
    }

    private void walkies() {

        final String original = this.map;
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                if (this.getCell(original, row, col).equalsIgnoreCase("O")) {
                    this.walky(row, col);
                }
            }
        }
//        this.map = this.map.substring(0, this.start) + "S" + this.map.substring(this.start + 1);
    }

    private void walky(final int row, final int col) {

        this.maybe(row - 1, col);
        this.maybe(row + 1, col);
        this.maybe(row, col - 1);
        this.maybe(row, col + 1);
        this.map = this.replaceCharacter(this.map, row, col, ".");
    }

    private void maybe(final int row, final int col) {

        if (row < 0 || row >= this.height || col < 0 || col >= this.width) {
            return;
        }
        final String old = this.getCell(this.map, row, col);
        if (old.equalsIgnoreCase("#")) {
            return;
        }
        this.map = this.replaceCharacter(this.map, row, col, "O");
    }

    private String replaceCharacter(final String original, final int row, final int col, final String replacement) {

        final int index = (row * this.width) + col;
        return original.substring(0, index) + replacement + original.substring(index + 1);
    }

    private String getCell(final String original, final int row, final int col) {

        final int index = (row * this.width) + col;
        return original.substring(index, index + 1);
    }

    private void blankLine() {

        System.out.println();
    }

    private void debugMap() {

        this.debug(this.map);
    }

    private void debug(final String aMap) {
        for (int row = 0; row < this.height; row++) {
            final String line = aMap.substring(row * this.width, (row + 1) * this.width);
            System.out.println(line);
        }
        this.blankLine();
    }

    private void loadMap(final String[] input) {

        this.map = String.join("", input);
    }

    @Override
    public String part2(final String[] input) {

        return null;
    }
}
