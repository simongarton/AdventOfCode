package com.simongarton.adventofcode.year2024.day21;

import com.googlecode.lanterna.TextColor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectionalKeypad extends Keypad {

    @Getter
    private Map<String, Map<String, String>> movements;

    private String currentLetter;
    private String completeProgram = "";

    final DirectionalKeypad nextDirectionalKeyPad;
    final NumericKeypad numericKeypad;

    public DirectionalKeypad(final String name,
                             final int level,
                             final DirectionalKeypad nextDirectionalKeyPad,
                             final NumericKeypad numericKeypad) {
        super(name, level);

        this.movements = new HashMap<>();
        this.setupMovements();
        this.currentLetter = ACTIVATE;

        this.nextDirectionalKeyPad = nextDirectionalKeyPad;
        if (nextDirectionalKeyPad != null) {
            nextDirectionalKeyPad.controller = this;
        }

        this.numericKeypad = numericKeypad;
        if (numericKeypad != null) {
            numericKeypad.controller = this;
        }
    }

    public void press(final String key) {

        Radio.tick();

        this.completeProgram += key;
        this.drawString(String.join("", this.completeProgram), this.infoStart + 4 + this.level, TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK);

        if (key.equalsIgnoreCase(ACTIVATE)) {
            this.activate();
            return;
        }

        if (!this.getMovements().containsKey(key)) {
            throw new RuntimeException("bad key press " + key);
        }
        final Map<String, String> movement = this.getMovements().get(key);

        if (!movement.containsKey(this.currentLetter)) {
            Radio.broadcast(this, "invalid movement for key " + key + " from position " + this.currentLetter);
            if (this.screen != null) {
                this.sleepNow(30000);
            }
            throw new RuntimeException("invalid movement for key " + key + " from position " + this.currentLetter);
        }
        final char c1 = this.currentLetter.charAt(0);
        this.drawCharOnKeypadPosition(c1, this.screenStart, this.positionForChar(c1), TextColor.ANSI.WHITE, TextColor.ANSI.BLACK);
        final String oldPosition = this.currentLetter;
        this.currentLetter = movement.get(this.currentLetter);
        final char c2 = this.currentLetter.charAt(0);
        this.drawCharOnKeypadPosition(c2, this.screenStart, this.positionForChar(c2), TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK);
        Radio.broadcast(this, "press " + key + ": " + oldPosition + " -> " + this.currentLetter);
        this.refreshScreen();
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

        this.getKeysPressed().add(this.currentLetter);

        final char c1 = this.currentLetter.charAt(0);
        this.drawCharOnKeypadPosition(c1, this.screenStart, this.positionForChar(c1), TextColor.ANSI.BLACK, TextColor.ANSI.GREEN_BRIGHT);
        this.drawString(String.join("", this.getKeysPressed()), this.infoStart, TextColor.ANSI.WHITE, TextColor.ANSI.BLACK);
        this.refreshScreen();

        final String key = "A";
        Radio.broadcast(this, "press " + key + ": " + this.currentLetter + " activated");
        if (this.nextDirectionalKeyPad != null) {
            this.nextDirectionalKeyPad.press(this.currentLetter);
        }
        if (this.numericKeypad != null) {
            this.numericKeypad.press(this.currentLetter);
        }
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
        final String myPosition = numericKeypad.getCurrentLetter();

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

    public void run(final String program) {

        for (int i = 0; i < program.length(); i++) {
            this.press(program.substring(i, i + 1));
        }
    }

    @Override
    protected void drawFullGrid() {

        if (this.screen == null) {
            return;
        }

        for (final char c : List.of('^', 'A', '<', 'v', '>')) {
            this.drawCharOnKeypadPosition(c, this.screenStart, this.positionForChar(c), TextColor.ANSI.WHITE, TextColor.ANSI.BLACK);
        }

        this.refreshScreen();
    }

    private int positionForChar(final char c) {
        return switch (c) {
            case '^' -> 2;
            case 'A' -> 3;
            case '<' -> 4;
            case 'v' -> 5;
            case '>' -> 6;
            default -> throw new RuntimeException("oops");
        };
    }
}
