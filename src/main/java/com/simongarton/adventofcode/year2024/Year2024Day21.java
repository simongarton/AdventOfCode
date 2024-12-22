package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.List;
import java.util.Objects;

public class Year2024Day21 extends AdventOfCodeChallenge {

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

        // 147756 too high

        /* I want to press this on the numeric keypad
        1968
        If I start from A - all sequences are dependent on starting point - then I need to do
        ^<<A ^^>>A vA ^<A [key presses on main]
        To do this from a directional keypad, again starting from A, then I need to go
        THIS IS DRIVING THE FINGER ON THE NUMPAD
        <A v<A A >>A A ^A (this just for the 1) [key presses on robot1]
        Next level is DRIVING THE FINGER ON THE DIRPAD starting from A
        v<<A >>^A <vA <A >>^A  (this just for <A v<A) [key presses on robot2]
        Next level is ... I think going to be the same]
        [robot 3

         */

        int total = 0;
        for (final String numericCode : input) {
            final String fullSequence = this.fullSequence(numericCode);
            final int numericPart = Integer.parseInt(numericCode.replace("A", ""));
            total += numericPart * fullSequence.length();
        }
        return String.valueOf(total);
    }

    public String fullSequence(final String numericCode) {

        final StringBuilder fullSequence = new StringBuilder();

        State numpadState = new State();
        State dirpadState1 = new State();
        State dirpadState2;
        for (int i = 0; i < numericCode.length(); i++) {
            // these are the numpad actions. I'm starting from the previous final location.
            numpadState = this.buildStateForNumpad(numpadState.finalLocation, numericCode.substring(i, i + 1));
            // I need to now drive a dirpad to produce this
            dirpadState1 = this.convertStateIntoDirpadPresses(numpadState, dirpadState1);
            // and again.
            dirpadState2 = this.convertStateIntoDirpadPresses(dirpadState1, dirpadState1);
            fullSequence.append(dirpadState2.presses);
        }

        return fullSequence.toString();
    }

    public State convertStateIntoDirpadPresses(final State numpadState, State dirpadState) {

        final StringBuilder fullSequence = new StringBuilder();

        // I need to produce this
        final String neededSequence = numpadState.presses;

        for (int i = 0; i < neededSequence.length(); i++) {
            // how do I produce this ^v<>A from a dirpad ? I'm starting from the previous final location.
            dirpadState = this.buildStateForDirpad(dirpadState.finalLocation, neededSequence.substring(i, i + 1));
            fullSequence.append(dirpadState.presses);
        }

        dirpadState.presses = fullSequence.toString();
        return dirpadState;
    }

    public State buildStateForDirpad(final String startingLocation, final String finalLocation) {

        final State state = new State();

        state.requiredPress = finalLocation;
        state.initialLocation = startingLocation;
        state.finalLocation = finalLocation;
        state.presses = this.buildPressesForDirpadMovement(startingLocation, finalLocation);

        return state;
    }

    private String buildPressesForDirpadMovementZigZag(final String startingLocation, final String finalLocation) {

        // let's try zig zagging - this more than doubled the length
        final StringBuilder sequence = new StringBuilder();

        int startRow = this.findDirpadRow(startingLocation);
        final int endRow = this.findDirpadRow(finalLocation);
        int startCol = this.findDirpadCol(startingLocation);
        final int endCol = this.findDirpadCol(finalLocation);

        while (startRow != endRow && startCol != endCol) {
            if (startRow > endRow) {
                if (this.dirPadSafeToMove(startRow, startCol, "^")) {
                    sequence.append("^");
                    startRow = startRow - 1;
                }
            }
            if (startRow < endRow) {
                if (this.dirPadSafeToMove(startRow, startCol, "v")) {
                    sequence.append("v");
                    startRow = startRow + 1;
                }
            }
            if (startCol > endCol) {
                if (this.dirPadSafeToMove(startCol, startCol, "<")) {
                    sequence.append("<");
                    startCol = startCol - 1;
                }
            }
            if (startCol < endCol) {
                if (this.dirPadSafeToMove(startCol, startCol, ">")) {
                    sequence.append(">");
                    startCol = startCol + 1;
                }
            }
        }

        return sequence.toString();
    }

    private boolean dirPadSafeToMove(final int x, final int y, final String direction) {

        // I can always move down or right
        if (direction.equalsIgnoreCase(">")) {
            return true;
        }
        if (direction.equalsIgnoreCase("v")) {
            return true;
        }
        if (direction.equalsIgnoreCase("<")) {
            if (x == 1 && y == 0) {
                return false;
            }
            return true;
        }
        if (direction.equalsIgnoreCase("^")) {
            if (x == 0 && y == 1) {
                return false;
            }
            return true;
        }
        throw new RuntimeException("oops");
    }

    private String buildPressesForDirpadMovement(final String startingLocation, final String finalLocation) {

        final int startRow = this.findDirpadRow(startingLocation);
        final int endRow = this.findDirpadRow(finalLocation);

        // other way round from numpad
        if (startRow < endRow) {
            return this.buildPressesForDirpadMovementUpDownFirst(startingLocation, finalLocation);
        } else {
            return this.buildPressesForDirpadMovementLeftRightFirst(startingLocation, finalLocation);
        }
    }

    public State buildStateForNumpad(final String startingLocation, final String finalLocation) {

        final State state = new State();

        state.requiredPress = finalLocation;
        state.initialLocation = startingLocation;
        state.finalLocation = finalLocation;
        state.presses = this.buildPressesForNumberMovement(startingLocation, finalLocation);

        return state;
    }

    public String buildPressesForNumberMovement(final String startingLocation, final String finalLocation) {

        final int startRow = this.findNumberRow(startingLocation);
        final int endRow = this.findNumberRow(finalLocation);
        final int startCol = this.findNumberCol(startingLocation);
        final int endCol = this.findNumberCol(finalLocation);

        if (startRow != 3 && endRow != 1) {
            return this.buildPressesForNumberMovementLeftRightFirst(startingLocation, finalLocation);
        } else {
            return this.buildPressesForNumberMovementUpDownFirst(startingLocation, finalLocation);
        }
    }

    private String buildPressesForNumberMovementLeftRightFirst(final String startingLocation, final String finalLocation) {

        final StringBuilder sequence = new StringBuilder();

        sequence.append(this.buildPressesForNumberMovementLeftRight(startingLocation, finalLocation));
        sequence.append(this.buildPressesForNumberMovementUpDown(startingLocation, finalLocation));
        sequence.append("A");

        return sequence.toString();
    }

    private String buildPressesForNumberMovementLeftRight(final String startingLocation, final String finalLocation) {

        final StringBuilder sequence = new StringBuilder();

        final int startCol = this.findNumberCol(startingLocation);
        final int endCol = this.findNumberCol(finalLocation);

        final int deltaCol = Math.abs(startCol - endCol);
        final String deltaMoveCol = startCol > endCol ? "<" : ">";
        sequence.append(deltaMoveCol.repeat(deltaCol));

        return sequence.toString();
    }

    private String buildPressesForNumberMovementUpDown(final String startingLocation, final String finalLocation) {

        final StringBuilder sequence = new StringBuilder();

        final int startRow = this.findNumberRow(startingLocation);
        final int endRow = this.findNumberRow(finalLocation);

        final int deltaRow = Math.abs(startRow - endRow);
        final String deltaMoveRow = startRow > endRow ? "^" : "v";
        sequence.append(deltaMoveRow.repeat(deltaRow));

        return sequence.toString();
    }

    private String buildPressesForNumberMovementUpDownFirst(final String startingLocation, final String finalLocation) {

        final StringBuilder sequence = new StringBuilder();

        sequence.append(this.buildPressesForNumberMovementUpDown(startingLocation, finalLocation));
        sequence.append(this.buildPressesForNumberMovementLeftRight(startingLocation, finalLocation));
        sequence.append("A");

        return sequence.toString();
    }

    private String buildPressesForDirpadMovementLeftRightFirst(final String startingLocation, final String finalLocation) {

        final StringBuilder sequence = new StringBuilder();

        sequence.append(this.buildPressesForDirpadMovementLeftRight(startingLocation, finalLocation));
        sequence.append(this.buildPressesForDirpadMovementUpDown(startingLocation, finalLocation));
        sequence.append("A");

        return sequence.toString();
    }

    private String buildPressesForDirpadMovementLeftRight(final String startingLocation, final String finalLocation) {

        final StringBuilder sequence = new StringBuilder();

        final int startCol = this.findDirpadCol(startingLocation);
        final int endCol = this.findDirpadCol(finalLocation);

        final int deltaCol = Math.abs(startCol - endCol);
        final String deltaMoveCol = startCol > endCol ? "<" : ">";
        sequence.append(deltaMoveCol.repeat(deltaCol));

        return sequence.toString();
    }

    private String buildPressesForDirpadMovementUpDown(final String startingLocation, final String finalLocation) {

        final StringBuilder sequence = new StringBuilder();

        final int startRow = this.findDirpadRow(startingLocation);
        final int endRow = this.findDirpadRow(finalLocation);

        final int deltaRow = Math.abs(startRow - endRow);
        final String deltaMoveRow = startRow > endRow ? "^" : "v";
        sequence.append(deltaMoveRow.repeat(deltaRow));

        return sequence.toString();
    }

    private String buildPressesForDirpadMovementUpDownFirst(final String startingLocation, final String finalLocation) {

        final StringBuilder sequence = new StringBuilder();

        sequence.append(this.buildPressesForDirpadMovementUpDown(startingLocation, finalLocation));
        sequence.append(this.buildPressesForDirpadMovementLeftRight(startingLocation, finalLocation));
        sequence.append("A");

        return sequence.toString();
    }

    private int findNumberRow(final String key) {

        // from top, left

        if (List.of("7", "8", "9").contains(key)) {
            return 0;
        }
        if (List.of("4", "5", "6").contains(key)) {
            return 1;
        }
        if (List.of("1", "2", "3").contains(key)) {
            return 2;
        }
        return 3;
    }

    private int findNumberCol(final String key) {

        if (List.of("1", "4", "7").contains(key)) {
            return 0;
        }
        if (List.of("0", "2", "5", "8").contains(key)) {
            return 1;
        }
        return 2;
    }

    private int findDirpadRow(final String key) {

        // from top, left

        if (List.of("^", "A").contains(key)) {
            return 0;
        }
        return 1;
    }

    private int findDirpadCol(final String key) {

        if (List.of("<").contains(key)) {
            return 0;
        }
        if (List.of("^", "v").contains(key)) {
            return 1;
        }
        return 2;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    public static class State {

        String requiredPress;
        String initialLocation;
        String finalLocation;
        String presses;


        public State() {
            // needed if I'm using this in a chain
            this.finalLocation = "A";
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final State state = (State) o;
            return Objects.equals(this.requiredPress, state.requiredPress) && Objects.equals(this.initialLocation, state.initialLocation) && Objects.equals(this.finalLocation, state.finalLocation) && Objects.equals(this.presses, state.presses);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.requiredPress, this.initialLocation, this.finalLocation, this.presses);
        }

        @Override
        public String toString() {
            return "State{" +
                    "requiredPress='" + this.requiredPress + '\'' +
                    ", initialLocation='" + this.initialLocation + '\'' +
                    ", finalLocation='" + this.finalLocation + '\'' +
                    ", presses='" + this.presses + '\'' +
                    '}';
        }
    }


}
