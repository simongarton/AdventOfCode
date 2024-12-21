package com.simongarton.adventofcode.year2024.day21;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.simongarton.adventofcode.year2024.Year2024Day21.*;

public class NumericKeypad implements Keypad {

    String name;
    String currentLetter = ACTIVATE;
    DirectionalKeypad controller;

    Map<String, Map<String, String>> movements;
    List<String> keysPressed;

    public NumericKeypad(final String name) {

        this.name = name;
        this.keysPressed = new ArrayList<>();

        this.setupMovements();
    }

    @Override
    public void setupMovements() {

        // this is coming from a DirectionalKeypad.

        this.movements = new HashMap<>();
        // top level is the current position
        this.movements.put(UP, this.getPositionsForUp());
        this.movements.put(RIGHT, this.getPositionsForRight());
        this.movements.put(DOWN, this.getPositionsForDown());
        this.movements.put(LEFT, this.getPositionsForLeft());
    }

    @Override
    public Map<String, String> getPositionsForUp() {

        return Map.of(
                "0", "2",
                ACTIVATE, "3",
                "1", "4",
                "2", "5",
                "3", "6",
                "4", "7",
                "5", "8",
                "6", "9"
        );
    }

    @Override
    public Map<String, String> getPositionsForDown() {

        return Map.of(
                "2", "0",
                "3", ACTIVATE,
                "4", "1",
                "5", "2",
                "6", "3",
                "7", "4",
                "8", "5",
                "9", "6"
        );
    }

    @Override
    public Map<String, String> getPositionsForRight() {

        return Map.of(
                "0", ACTIVATE,
                "1", "2",
                "2", "3",
                "4", "5",
                "5", "6",
                "7", "8",
                "8", "9"
        );
    }

    @Override
    public Map<String, String> getPositionsForLeft() {

        return Map.of(
                ACTIVATE, "0",
                "2", "1",
                "3", "2",
                "5", "4",
                "6", "5",
                "8", "7",
                "9", "8"
        );
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

    @Override
    public void activate() {

        this.keysPressed.add(this.currentLetter);
        System.out.println(this.name + " pressed " + this.currentLetter);
    }

    @Override
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
        System.out.println("I need to do this : " + commandsNeeded);
        final List<String> commands = this.buildCommandsRecursivelyFor(this.controller, commandsNeeded, status);
        return new Program(topLevel, commands);
    }

    private List<String> buildCommandsRecursivelyFor(final Keypad keypad,
                                                     final List<String> commandsNeeded,
                                                     final Map<Keypad, String> status) {

        if (keypad.getController() == null) {
            return keypad.damnItIllDoItMyself(commandsNeeded, status);
        }

        final List<String> commands = new ArrayList<>();
        for (final String commandNeeded : commandsNeeded) {
            // I need you, my controller, to tell me what you have to have pressed, to get this command.
            System.out.println("  I " + this.name + " need my controller " + this.controller.name + " to do this one key press: " + commandNeeded);
            commands.addAll(this.buildCommandsRecursivelyFor(this.controller, commandsNeeded, status));
        }
        return commands;
    }

    @Override
    public List<String> damnItIllDoItMyself(final List<String> commandsNeeded, final Map<Keypad, String> status) {

        throw new RuntimeException("you're joking.");
    }

    @Override
    public String toString() {
        return this.name + " (" + this.getClass().getSimpleName() + ")";
    }

    @Override
    public Keypad getController() {
        return this.controller;
    }

    @Override
    public List<String> keysPressed() {
        return this.keysPressed;
    }
}
