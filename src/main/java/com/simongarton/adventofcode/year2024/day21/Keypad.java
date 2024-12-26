package com.simongarton.adventofcode.year2024.day21;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.TerminalScreen;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Keypad {

    public static final String ACTIVATE = "A";
    public static final String UP = "^";
    public static final String DOWN = "v";
    public static final String LEFT = "<";
    public static final String RIGHT = ">";

    @Getter
    protected int level;
    @Getter
    protected String name;
    protected DirectionalKeypad controller;

    protected TerminalScreen screen;
    protected int screenStart;
    protected int infoStart;

    @Getter
    private final List<String> keysPressed;

    public Keypad(final String name, final int level) {
        this.name = name;
        this.level = level;
        this.keysPressed = new ArrayList<>();
    }

    public Keypad getController() {
        return this.controller;
    }

    abstract Map<String, Map<String, String>> getMovements();

    abstract Map<String, String> getPositionsForUp();

    abstract Map<String, String> getPositionsForRight();

    abstract Map<String, String> getPositionsForDown();

    abstract Map<String, String> getPositionsForLeft();

    abstract void activate();

    abstract void drawFullGrid();

    public void setScreen(final TerminalScreen screen, final int screenStart, final int infoStart) {

        this.screen = screen;
        this.screenStart = screenStart;
        this.infoStart = infoStart;
        this.drawFullGrid();
    }

    protected void drawChar(final char c, final int x, final int y, final TextColor foreground, final TextColor background) {
        final TextCharacter[] textCharacter = TextCharacter.fromCharacter(c, foreground, background);

        if (this.screen != null) {
            this.screen.setCharacter(new TerminalPosition(x * 2, y), textCharacter[0]);
        }
    }

    protected void refreshScreen() {

        if (this.screen == null) {
            return;
        }

        try {
            this.screen.refresh();
            this.sleepNow(this.pauseForLevel());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setupMovements() {

        // top level is the current position
        this.getMovements().put(UP, this.getPositionsForUp());
        this.getMovements().put(RIGHT, this.getPositionsForRight());
        this.getMovements().put(DOWN, this.getPositionsForDown());
        this.getMovements().put(LEFT, this.getPositionsForLeft());
    }

    @Override
    public String toString() {
        return this.name + " (" + this.getClass().getSimpleName() + ")";
    }

    abstract int rowForKey(String key);

    abstract int colForKey(String key);

    protected void drawCharOnKeypadPosition(final char c, final int screenStart, final int position, final TextColor.ANSI foreground, final TextColor.ANSI background) {

        final int x = this.getXForPosition(screenStart, position);
        final int y = this.getYForPosition(screenStart, position);

        this.drawChar(c, x, y, foreground, background);
    }

    protected void drawString(final String s, final int infoStart, final TextColor foreground, final TextColor background) {

        final int x = 4;
        final int y = infoStart;

        for (int i = 0; i < s.length(); i++) {
            this.drawChar(s.charAt(i), x + i, y, foreground, background);
        }
    }

    private int getXForPosition(final int screenStart, final int position) {

        return ((position - 1) % 3) + screenStart;
    }

    private int getYForPosition(final int screenStart, final int position) {

        return 2 + ((position - 1) / 3);
    }

    private int pauseForLevel() {
        return (3 - this.level) * 500;
    }

    protected void sleepNow(final int millis) {

        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
