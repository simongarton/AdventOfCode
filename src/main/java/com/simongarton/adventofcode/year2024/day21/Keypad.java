package com.simongarton.adventofcode.year2024.day21;

import lombok.Getter;

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
}
