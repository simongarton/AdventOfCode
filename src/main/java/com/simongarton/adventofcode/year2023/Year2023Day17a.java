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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Year2023Day17a extends AdventOfCodeChallenge {


    private static final boolean DEBUG = false;
    private static final int CHAR_WIDTH = 12;
    private static final int CHAR_HEIGHT = 24;
    private static final int BITMAP_SCALE = 4;

    private String map;
    private int width;
    private int height;
    private Map<Integer, List<State>> statesByCost;
    private Map<State, Integer> seenStateByCost;
    private Map<State, State> cameFrom;
    private List<State> path;

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

        if (DEBUG) {
            this.setUpLanternaQuietly();
        }

        this.statesByCost = new HashMap<>();
        this.seenStateByCost = new HashMap<>();
        this.cameFrom = new HashMap<>();

        final State firstState = State.builder()
                .x(0)
                .y(0)
                .dx(0)
                .dy(0)
                .cost(0)
                .distance(0)
                .build();

        this.addNeighbour(0, 0, 0, 1, 0, 1, firstState);
        this.addNeighbour(0, 0, 0, 0, 1, 1, firstState);

        int currentCost;
        while (true) {
            currentCost = this.statesByCost.keySet().stream().min(Integer::compareTo).get();
            final List<State> nextStates = this.statesByCost.remove(currentCost);

            boolean foundEnd = false;
            for (final State nextState : nextStates) {
                this.reconstructPath(nextState);
                if (DEBUG) {
                    this.drawCurrentMap();
                }
                if (this.addNeighbour(
                        currentCost,
                        nextState.getX(),
                        nextState.getY(),
                        // rotations
                        nextState.getDy(),
                        -nextState.getDx(),
                        1,
                        nextState
                )) {
                    foundEnd = true;
                    break;
                }
                if (this.addNeighbour(
                        currentCost,
                        nextState.getX(),
                        nextState.getY(),
                        // rotations
                        -nextState.getDy(),
                        nextState.getDx(),
                        1,
                        nextState)) {
                    foundEnd = true;
                    break;
                }
                if (nextState.getDistance() < 3) {
                    if (this.addNeighbour(currentCost,
                            nextState.getX(),
                            nextState.getY(),
                            // rotations
                            nextState.getDx(),
                            nextState.getDy(),
                            nextState.getDistance() + 1,
                            nextState)) {
                        foundEnd = true;
                        break;
                    }
                }
            }
            if (foundEnd) {
                break;
            }
        }

        if (DEBUG) {
            this.drawCurrentMap();
            this.waitForKeys();
        }
        this.paintMap("crucible.png");
        for (final State step : this.path) {
            System.out.println(step + " " + this.getCost(step.getX(), step.getY()));
        }

        return String.valueOf(currentCost);
    }

    private void paintMap(final String filename) {

        final BufferedImage bufferedImage = new BufferedImage(this.width * BITMAP_SCALE, this.height * BITMAP_SCALE, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);
        this.paintFloor(graphics2D);
        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void clearBackground(final Graphics2D graphics2D) {

        graphics2D.setPaint(Color.BLACK);
        graphics2D.fillRect(0, 0, this.width, this.height);
    }

    private void paintFloor(final Graphics2D graphics2D) {
        for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
                graphics2D.setPaint(new Color(25 * this.getCost(col, row), 0, 0));
                graphics2D.fillRect(col * BITMAP_SCALE, row * BITMAP_SCALE, BITMAP_SCALE, BITMAP_SCALE);
            }
        }

        graphics2D.setPaint(Color.WHITE);
        for (final State state : this.path) {
            graphics2D.fillRect(state.getX() * BITMAP_SCALE, state.getY() * BITMAP_SCALE, BITMAP_SCALE, BITMAP_SCALE);
        }
    }

    private void setUpLanternaQuietly() {
        try {
            this.setUpLanterna();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitForKeys() {
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

    private void reconstructPath(final State endState) {
        State current = endState;
        this.path = new ArrayList<>();
        this.path.add(0, current);
        while (true) {
            if (!this.cameFrom.containsKey(current)) {
                break;
            }
            current = this.cameFrom.get(current);
            this.path.add(0, current);
        }
        // don't include the first
        this.path.remove(0);
    }

    private boolean addNeighbour(int cost, int x, int y, final int dx, final int dy, final int distance, final State currentState) {

        x = x + dx;
        y = y + dy;
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            return false;
        }

        cost = cost + this.getCost(x, y);
        if (x == this.width - 1 && y == this.height - 1) {
            final State newState = State.builder()
                    .x(x)
                    .y(y)
                    .dx(dx)
                    .dy(dy)
                    .cost(cost)
                    .distance(distance) // weird
                    .build();
            this.cameFrom.put(newState, currentState);
            this.reconstructPath(newState);
            return true;
        }

        final State newState = State.builder()
                .x(x)
                .y(y)
                .dx(dx)
                .dy(dy)
                .cost(cost)
                .distance(distance) // weird
                .build();

        if (!this.seenStateByCost.containsKey(newState)) {
            final List<State> costStates = this.statesByCost.getOrDefault(cost, new ArrayList<>());
            costStates.add(newState);
            this.statesByCost.put(cost, costStates);
            this.seenStateByCost.put(newState, cost);
            this.cameFrom.put(newState, currentState);
            this.reconstructPath(newState);
        }

        return false;
    }

    private void loadMap(final String[] input) {

        this.map = String.join("", input);
        this.width = input[0].length();
        this.height = input.length;
    }

    private int getCost(final int x, final int y) {

        final int index = (y * this.width) + x;
        return Integer.parseInt(this.map.substring(index, index + 1));
    }

    private void drawCurrentMap() {

        for (int i = 0; i < this.height; i++) {
            final String line = this.map.substring(i * this.width, (i + 1) * this.width);
            this.drawString(line, 0, i, TextColor.ANSI.BLACK);
        }

        for (final State state : this.path) {
            this.drawString("#", state.getX(), state.getY(), TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK);
        }

        try {
            this.screen.refresh();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(10);
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

        final TextColor coloredForeground = this.getCharColor(c, x, y);
        this.drawChar(c, x, y, coloredForeground, background);
    }

    private void drawChar(final char c, final int x, final int y, final TextColor foreground, final TextColor background) {
        final TextCharacter textCharacter = new TextCharacter(c, foreground, background);
        this.screen.setCharacter(new TerminalPosition(x, y), textCharacter);
    }

    private TextColor getCharColor(final char c, final int x, final int y) {

        for (final State state : this.path) {
            if (state.getX() == x) {
                if (state.getY() == y) {
                    return TextColor.ANSI.WHITE_BRIGHT;
                }
            }
        }
        return this.getCharColor(c);
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
                throw new RuntimeException("Bad char");
        }
    }

    private TextColor scaledColor(final char c) {

        final int value = Integer.parseInt("" + c);
        final int red = (255 * value / 9);
        final int green = 0;
        final int blue = 255 - (255 * value / 9);
        return new TextColor.RGB(red, green, blue);
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

    @Data
    @Builder
    private static final class State {
        // https://www.reddit.com/r/adventofcode/comments/18luw6q/2023_day_17_a_longform_tutorial_on_day_17/

        private int x;
        private int y;
        private int dx;
        private int dy;
        private int distance;
        private int cost;

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final State state = (State) o;
            return this.x == state.x && this.y == state.y && this.dx == state.dx && this.dy == state.dy;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.x, this.y, this.dx, this.dy);
        }
    }
}
