package com.simongarton.adventofcode.year2024.day21;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.simongarton.adventofcode.year2024.Year2024Day21FiguringOutProblem.*;

public abstract class Keypad {

    String name;
    String currentLetter = ACTIVATE;
    DirectionalKeypad controller;

    Map<String, Map<String, String>> movements;
    List<String> keysPressed;

    public Keypad(final String name) {
        this.name = name;
        this.keysPressed = new ArrayList<>();

        this.setupMovements();
    }

    public Keypad getController() {
        return this.controller;
    }

    public void setupMovements() {

        this.movements = new HashMap<>();
        // top level is the current position
        this.movements.put(UP, this.getPositionsForUp());
        this.movements.put(RIGHT, this.getPositionsForRight());
        this.movements.put(DOWN, this.getPositionsForDown());
        this.movements.put(LEFT, this.getPositionsForLeft());
    }

    public void press(final String key) {

        if (key.equalsIgnoreCase(ACTIVATE)) {
            this.activate();
            return;
        }

        if (!this.movements.containsKey(key)) {
            throw new RuntimeException("bad key press " + key);
        }
        final Map<String, String> movement = this.movements.get(key);

        if (!movement.containsKey(this.currentLetter)) {
            throw new RuntimeException("invalid movement for key " + key + " from position " + this.currentLetter);
        }
        this.currentLetter = movement.get(this.currentLetter);
    }

    abstract Map<String, String> getPositionsForUp();

    abstract Map<String, String> getPositionsForRight();

    abstract Map<String, String> getPositionsForDown();

    abstract Map<String, String> getPositionsForLeft();

    public void activate() {

        this.keysPressed.add(this.currentLetter);
        System.out.println(this.name + " pressed " + this.currentLetter);
    }

    public List<String> keysPressed() {
        return this.keysPressed;
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

    protected String getName() {
        return this.name;
    }

    abstract int rowForKey(String key);

    abstract int colForKey(String key);
}
