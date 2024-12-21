package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Year2024Day21 extends AdventOfCodeChallenge {

    // there's a sneaky shortcut here where I could go back to A on each pad after each key press so I know where I am
    // but the question rules it out.

    // What I need to do is translate things ... so instead of explicitly typing out a sequence, I say what I want to happen.

    final static String UP = "^";
    final static String RIGHT = ">";
    final static String LEFT = "<";
    final static String DOWN = "v";
    final static String ACTIVATE = "A";

    @Override
    public String title() {
        return "Day 21: Keypad Conundrum";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 21);
    }

    @Override
    public String part1(final String[] input) {

        /* I want to press 1.

        I need to set up my keypads and then ask each of them what needs to be pressed.

         */

        final NumericKeypad main = new NumericKeypad("main");
        final DirectionalKeypad robot1 = new DirectionalKeypad("robot1", null, main);
        final DirectionalKeypad robot2 = new DirectionalKeypad("robot2", robot1, null);
        final DirectionalKeypad robot3 = new DirectionalKeypad("robot3", robot2, null);

        Map<String, String>

        // I want to press a 0

        return null;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    static class Keypad {
    }

    static class DirectionalKeypad extends Keypad {

        String name;
        String currentLetter = ACTIVATE;
        DirectionalKeypad nextDirectionalKeyPad;
        NumericKeypad numericKeypad;

        Map<String, Map<String, String>> movements;
        List<String> keysPressed;

        public DirectionalKeypad(final String name,
                                 final DirectionalKeypad nextDirectionalKeyPad,
                                 final NumericKeypad numericKeypad) {
            this.name = name;
            this.nextDirectionalKeyPad = nextDirectionalKeyPad;
            this.numericKeypad = numericKeypad;
            this.keysPressed = new ArrayList<>();

            this.setupMovements();
        }

        private void setupMovements() {

            // this is coming from a DirectionalKeypad.

            this.movements = new HashMap<>();
            // top level is the current position
            this.movements.put(UP, this.getPositionsForUp());
            this.movements.put(RIGHT, this.getPositionsForRight());
            this.movements.put(DOWN, this.getPositionsForDown());
            this.movements.put(LEFT, this.getPositionsForLeft());
        }

        private Map<String, String> getPositionsForUp() {

            return Map.of(
                    RIGHT, ACTIVATE,
                    DOWN, UP
            );
        }

        private Map<String, String> getPositionsForRight() {

            return Map.of(
                    LEFT, DOWN,
                    DOWN, RIGHT,
                    UP, ACTIVATE
            );
        }

        private Map<String, String> getPositionsForDown() {

            return Map.of(
                    UP, DOWN,
                    ACTIVATE, RIGHT
            );
        }

        private Map<String, String> getPositionsForLeft() {

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

        private void activate() {

            this.keysPressed.add(this.currentLetter);
            System.out.println(this.name + " pressed " + this.currentLetter);
            if (this.nextDirectionalKeyPad != null) {
                this.nextDirectionalKeyPad.press(this.currentLetter);
            }
            if (this.numericKeypad != null) {
                this.numericKeypad.press(this.currentLetter);
            }
        }
    }

    static class NumericKeypad {

        String name;
        String currentLetter = ACTIVATE;
        List<String> keysPressed;

        Map<String, Map<String, String>> movements;

        public NumericKeypad(final String name) {
            this.setupMovements();
            this.keysPressed = new ArrayList<>();
            this.name = name;
        }

        private void setupMovements() {

            // this is coming from a DirectionalKeypad.

            this.movements = new HashMap<>();
            // top level is the current position
            this.movements.put(UP, this.getPositionsForUp());
            this.movements.put(RIGHT, this.getPositionsForRight());
            this.movements.put(DOWN, this.getPositionsForDown());
            this.movements.put(LEFT, this.getPositionsForLeft());
        }

        private Map<String, String> getPositionsForUp() {

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

        private Map<String, String> getPositionsForDown() {

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

        private Map<String, String> getPositionsForRight() {

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

        private Map<String, String> getPositionsForLeft() {

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

        private void activate() {
            this.keysPressed.add(this.currentLetter);
            System.out.println(this.name + " pressed " + this.currentLetter);
        }
    }
}
