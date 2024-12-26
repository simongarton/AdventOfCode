package com.simongarton.adventofcode.year2024.day21;

import com.googlecode.lanterna.TextColor;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class NumericKeypad extends Keypad {

    private final Map<String, Map<String, String>> movements;

    private String currentLetter;
    private String completeProgram = "";

    public NumericKeypad(final String name) {

        super(name, 0);

        this.movements = new HashMap<>();
        this.setupMovements();
        this.currentLetter = ACTIVATE;
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

    @Override
    void activate() {

        this.getKeysPressed().add(this.currentLetter);
        Radio.broadcast(this, this.currentLetter);

        final char c1 = this.currentLetter.charAt(0);
        this.drawCharOnKeypadPosition(c1, this.screenStart, this.positionForChar(c1), TextColor.ANSI.BLACK, TextColor.ANSI.GREEN_BRIGHT);
        this.drawString(String.join("", this.getKeysPressed()), this.infoStart, TextColor.ANSI.WHITE, TextColor.ANSI.BLACK);
        this.refreshScreen();
    }

    @Override
    int rowForKey(final String key) {
        final Map<String, Integer> data = new HashMap<>();
        data.put("1", 0);
        data.put("2", 0);
        data.put("3", 0);
        data.put("4", 1);
        data.put("5", 1);
        data.put("6", 1);
        data.put("7", 2);
        data.put("8", 2);
        data.put("9", 2);
        data.put("0", 3);
        data.put("A", 3);
        return data.get(key);
    }

    @Override
    int colForKey(final String key) {
        final Map<String, Integer> data = new HashMap<>();
        data.put("1", 0);
        data.put("2", 1);
        data.put("3", 2);
        data.put("4", 0);
        data.put("5", 1);
        data.put("6", 2);
        data.put("7", 0);
        data.put("8", 1);
        data.put("9", 2);
        data.put("0", 1);
        data.put("A", 2);
        return data.get(key);
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
        this.currentLetter = movement.get(this.currentLetter);
        final char c2 = this.currentLetter.charAt(0);
        this.drawCharOnKeypadPosition(c2, this.screenStart, this.positionForChar(c2), TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK);
        this.refreshScreen();
    }

    @Override
    protected void drawFullGrid() {

        if (this.screen == null) {
            return;
        }

        for (final char c : List.of('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'A')) {
            this.drawCharOnKeypadPosition(c, this.screenStart, this.positionForChar(c), TextColor.ANSI.WHITE, TextColor.ANSI.BLACK);
        }

        this.refreshScreen();
    }

    private int positionForChar(final char c) {
        return switch (c) {
            case '7' -> 1;
            case '8' -> 2;
            case '9' -> 3;
            case '4' -> 4;
            case '5' -> 5;
            case '6' -> 6;
            case '1' -> 7;
            case '2' -> 8;
            case '3' -> 9;
            case '0' -> 11;
            case 'A' -> 12;
            default -> throw new RuntimeException("oops");
        };
    }

}
