package com.simongarton.adventofcode.year2024.day21;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.simongarton.adventofcode.year2024.Year2024Day21.*;

public class DirectionalKeypad implements Keypad {

    String name;
    String currentLetter = ACTIVATE;
    DirectionalKeypad controller;

    Map<String, Map<String, String>> movements;
    List<String> keysPressed;

    final DirectionalKeypad nextDirectionalKeyPad;
    final NumericKeypad numericKeypad;

    public DirectionalKeypad(final String name,
                             final DirectionalKeypad nextDirectionalKeyPad,
                             final NumericKeypad numericKeypad) {
        this.name = name;
        this.keysPressed = new ArrayList<>();

        this.setupMovements();

        this.nextDirectionalKeyPad = nextDirectionalKeyPad;
        if (nextDirectionalKeyPad != null) {
            nextDirectionalKeyPad.controller = this;
        }

        this.numericKeypad = numericKeypad;
        if (numericKeypad != null) {
            numericKeypad.controller = this;
        }
    }

    @Override
    public Keypad getController() {
        return this.controller;
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
                RIGHT, ACTIVATE,
                DOWN, UP
        );
    }

    @Override
    public Map<String, String> getPositionsForRight() {

        return Map.of(
                LEFT, DOWN,
                DOWN, RIGHT,
                UP, ACTIVATE
        );
    }

    @Override
    public Map<String, String> getPositionsForDown() {

        return Map.of(
                UP, DOWN,
                ACTIVATE, RIGHT
        );
    }

    @Override
    public Map<String, String> getPositionsForLeft() {

        return Map.of(
                RIGHT, DOWN,
                DOWN, LEFT,
                ACTIVATE, UP
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
        if (this.nextDirectionalKeyPad != null) {
            this.nextDirectionalKeyPad.press(this.currentLetter);
        }
        if (this.numericKeypad != null) {
            this.numericKeypad.press(this.currentLetter);
        }
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

        System.out.println("I need to do this myself : " + commandsNeeded);

        final List<String> commands = new ArrayList<>();

        return commands;
    }

    @Override
    public List<String> keysPressed() {
        return this.keysPressed;
    }

    @Override
    public String toString() {
        return this.name + " (" + this.getClass().getSimpleName() + ")";
    }
}
