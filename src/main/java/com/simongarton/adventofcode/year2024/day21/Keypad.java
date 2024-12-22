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
    @Getter
    protected String currentLetter = ACTIVATE;
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


    public Program getProgramFor(final List<String> commandsNeeded, final Map<Keypad, String> status) {

        // same as the other !

        // first I need to build my tree
        Keypad nestedKeypad = this;
        Keypad topLevel = null;
        System.out.println("I am " + nestedKeypad);
        while (nestedKeypad.getController() != null) {
            System.out.println(" and am controlled by " + nestedKeypad.getController());
            nestedKeypad = nestedKeypad.getController();
            topLevel = nestedKeypad;
        }

        // now I need to figure out some commands
        System.out.println("I (" + this.name + ") need to do this : " + commandsNeeded);
        final List<String> commands = new ArrayList();
        for (final String commandNeeded : commandsNeeded) {
            commands.addAll(this.buildCommandsRecursivelyFor(commandNeeded, status));
        }
        return new Program(topLevel, commands);
    }

    private List<String> buildCommandsRecursivelyFor(final String commandNeeded,
                                                     final Map<Keypad, String> status) {

        if (this.getController() == null) {
            return this.damnItIllDoItMyself(commandNeeded, status);
        }

        final List<String> commands = new ArrayList<>();
        // I need you, my controller, to tell me what you have to have pressed, to get this command.
        System.out.println("  I (" + this.name + ") need my controller (" + this.getController().name + ") to do this one key press: " + commandNeeded);
        commands.addAll(this.getController().buildCommandsRecursivelyFor(commandNeeded, status));
        return commands;
    }

    abstract List<String> damnItIllDoItMyself(final String commandNeeded, final Map<Keypad, String> status);

    @Override
    public String toString() {
        return this.name + " (" + this.getClass().getSimpleName() + ")";
    }

    abstract int rowForKey(String key);

    abstract int colForKey(String key);
}
