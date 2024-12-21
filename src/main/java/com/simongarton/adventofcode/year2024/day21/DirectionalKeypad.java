package com.simongarton.adventofcode.year2024.day21;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.simongarton.adventofcode.year2024.Year2024Day21FiguringOutProblem.*;

public class DirectionalKeypad extends Keypad {

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
        super(name);

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
    public List<String> damnItIllDoItMyself(final String commandNeeded, final Map<Keypad, String> status) {

        System.out.println("I  (" + this.getName() + ") need to do this myself : " + commandNeeded);

        if (this.numericKeypad != null) {
            return this.getCommandsForNumericKeypad(this.numericKeypad, commandNeeded, status);
        }

        return this.getCommandsForDirectionalKeypad(this.nextDirectionalKeyPad, commandNeeded, status);

    }

    @Override
    int rowForKey(final String key) {
        return 0;
    }

    @Override
    int colForKey(final String key) {
        return 0;
    }

    private List<String> getCommandsForDirectionalKeypad(
            final DirectionalKeypad nextDirectionalKeyPad,
            final String commandNeeded,
            final Map<Keypad, String> status) {
        return null;
    }

    private List<String> getCommandsForNumericKeypad(
            final NumericKeypad numericKeypad,
            final String commandNeeded,
            final Map<Keypad, String> status) {

        // ok where am I on the num pad
        final String myPosition = numericKeypad.currentLetter;

        // and where do I need to go to ?
        final String myNextPosition = commandNeeded;

        final List<String> movementsNeeded = this.plotCourseOnNumericKeypad(numericKeypad, myPosition, myNextPosition);

        final String key = myPosition + "->" + myNextPosition;

        final Map<String, List<String>> programs = new HashMap<>();

        return null;
    }

    private List<String> plotCourseOnNumericKeypad(final NumericKeypad numericKeypad, final String start, final String end) {

        final List<String> course = new ArrayList<>();

        final int startRow = numericKeypad.rowForKey(start);
        final int startCol = numericKeypad.colForKey(start);
        final int endRow = numericKeypad.rowForKey(end);
        final int endCol = numericKeypad.colForKey(end);

        if (startRow > endRow) {
            for (int i = startRow; i > endRow; i--) {
                course.add(DOWN);
            }
        }
        if (startRow < endRow) {
            for (int i = startRow; i < endRow; i++) {
                course.add(UP);
            }
        }
        if (startCol > endCol) {
            for (int i = startCol; i > endCol; i--) {
                course.add(LEFT);
            }
        }
        if (startCol < endCol) {
            for (int i = startCol; i < endCol; i++) {
                course.add(RIGHT);
            }
        }

        return null;

    }

}
