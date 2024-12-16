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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AdventOfCodeChallenge {

    public String title() {
        return null;
    }

    private static final int CHAR_WIDTH = 12;
    private static final int CHAR_HEIGHT = 24;

    public abstract Outcome run();

    @Getter
    protected int year;
    @Getter
    protected int day;

    protected List<String> challengeMap;
    protected int mapWidth;
    protected int mapHeight;

    protected TerminalScreen screen;

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

        for (final String line : this.challengeMap) {
            System.out.println(line);
        }
        System.out.println();
    }

    protected String getChallengeMapLetter(final AoCCoord c) {

        return this.getChallengeMapLetter(c.x, c.y);
    }

    protected void setChallengeMapLetter(final AoCCoord c, final String letter) {

        this.setChallengeMapLetter(c.x, c.y, letter);
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


    protected String getChallengeMapLetter(final int x, final int y) {

        if (x < 0 || x >= this.mapWidth) {
            return null;
        }
        if (y < 0 || y >= this.mapHeight) {
            return null;
        }
        return this.challengeMap.get(y).charAt(x) + "";
    }

    protected void putChallengeMapLetter(final int x, final int y, final String letter) {

        if (x < 0 || x >= this.mapWidth) {
            throw new RuntimeException("Out of bounds for x=" + x);
        }
        if (y < 0 || y >= this.mapHeight) {
            throw new RuntimeException("Out of bounds for y=" + y);

        }
        final String existingRow = this.challengeMap.get(y);
        final String newRow = existingRow.substring(0, x) + letter + existingRow.substring(x + 1);
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

    protected void emptyTemp() {
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

    public static class AoCCoord {

        public final int x;
        public final int y;

        public AoCCoord(final int x, final int y) {

            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {

            return this.x + "," + this.y;
        }

        @Override
        public boolean equals(final Object o) {

            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final AoCCoord aoCCoord = (AoCCoord) o;
            return this.x == aoCCoord.x && this.y == aoCCoord.y;
        }

        @Override
        public int hashCode() {

            return Objects.hash(this.x, this.y);
        }
    }

    public static class AoCLongCoord {

        public final long x;
        public final long y;

        public AoCLongCoord(final long x, final long y) {

            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {

            return this.x + "," + this.y;
        }

        @Override
        public boolean equals(final Object o) {

            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final AoCLongCoord coord = (AoCLongCoord) o;
            return this.x == coord.x && this.y == coord.y;
        }

        @Override
        public int hashCode() {

            return Objects.hash(this.x, this.y);
        }
    }
}
