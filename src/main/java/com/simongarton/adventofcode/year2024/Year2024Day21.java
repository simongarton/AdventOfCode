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




        notes
        - always move up/down first rather than left/right first on the numpad - can't fail.
        - but the dir pad is the other way round
        - no more complicated than that. work out if it's up/down or left right
         */

        for (final String numericCode : input) {
            final String fullSequence = this.fullSequence(numericCode);
        }
        return null;
    }

    public String fullSequence(final String numericCode) {

        final StringBuilder fullSequence = new StringBuilder();

        State state = new State();
        state.initialLocation = null;
        state.finalLocation = "A";
        for (int i = 0; i < numericCode.length(); i++) {
            state = this.buildStateForNumber(state.finalLocation, numericCode.substring(i, i + 1));
            fullSequence.append(state.presses);
        }

        return fullSequence.toString();
    }

    public State buildStateForNumber(final String startingLocation, final String keyNeeded) {

        final State state = new State();

        state.requiredPress = keyNeeded;
        state.initialLocation = startingLocation;
        state.finalLocation = keyNeeded;
        state.presses = this.buildPressesForNumberMovement(startingLocation, keyNeeded);

        return state;
    }

    public String buildPressesForNumberMovement(final String startingLocation, final String keyNeeded) {

        final int startRow = this.findNumberRow(startingLocation);
        final int endRow = this.findNumberRow(keyNeeded);

        if (startRow > endRow) {
            return this.buildPressesForNumberMovementUpDownFirst(startingLocation, keyNeeded);
        } else {
            return this.buildPressesForNumberMovementLeftRightFirst(startingLocation, keyNeeded);
        }
    }

    private String buildPressesForNumberMovementLeftRightFirst(final String startingLocation, final String keyNeeded) {

        final StringBuilder sequence = new StringBuilder();

        sequence.append(this.buildPressesForNumberMovementLeftRight(startingLocation, keyNeeded));
        sequence.append(this.buildPressesForNumberMovementUpDown(startingLocation, keyNeeded));
        sequence.append("A");

        return sequence.toString();
    }

    private String buildPressesForNumberMovementLeftRight(final String startingLocation, final String keyNeeded) {

        final StringBuilder sequence = new StringBuilder();

        final int startCol = this.findNumberCol(startingLocation);
        final int endCol = this.findNumberCol(keyNeeded);

        final int deltaCol = Math.abs(startCol - endCol);
        final String deltaMoveCol = startCol > endCol ? "<" : ">";
        sequence.append(deltaMoveCol.repeat(deltaCol));

        return sequence.toString();
    }

    private String buildPressesForNumberMovementUpDown(final String startingLocation, final String keyNeeded) {

        final StringBuilder sequence = new StringBuilder();

        final int startRow = this.findNumberRow(startingLocation);
        final int endRow = this.findNumberRow(keyNeeded);

        final int deltaRow = Math.abs(startRow - endRow);
        final String deltaMoveRow = startRow > endRow ? "^" : "v";
        sequence.append(deltaMoveRow.repeat(deltaRow));

        return sequence.toString();
    }

    private String buildPressesForNumberMovementUpDownFirst(final String startingLocation, final String keyNeeded) {

        final StringBuilder sequence = new StringBuilder();

        sequence.append(this.buildPressesForNumberMovementUpDown(startingLocation, keyNeeded));
        sequence.append(this.buildPressesForNumberMovementLeftRight(startingLocation, keyNeeded));
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

    @Override
    public String part2(final String[] input) {
        return null;
    }

    public static class State {

        String requiredPress;
        String initialLocation;
        String finalLocation;
        String presses;

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
