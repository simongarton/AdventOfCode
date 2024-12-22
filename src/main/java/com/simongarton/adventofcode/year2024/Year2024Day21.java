package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        // 142688 too high

        /*

        Expected :<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A
        Actual   :v<A<AA>>^AvAA<^A>Av<<A>>^AvA^Av<<A>>^AAv<A>A^A<A>Av<A<A>>^AAAvA<^A>A

        this is the first of mu fullSequencesIdentical() test.
        the As are in different places. it would be interesting to see where the keypad went.
        I know I get the right numbers in the right order - but how is it different ?

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

        if (false) {
            return this.hardcodedMovement(startingLocation, finalLocation);
        }

        final int startRow = this.findDirpadRow(startingLocation);
        final int endRow = this.findDirpadRow(finalLocation);

        // other way round from numpad
        if (startRow < endRow) {
            return this.buildPressesForDirpadMovementUpDownFirst(startingLocation, finalLocation);
        } else {
            return this.buildPressesForDirpadMovementLeftRightFirst(startingLocation, finalLocation);
        }
    }

    private String hardcodedMovement(final String startingLocation, final String finalLocation) {

        final Map<String, Map<String, String>> map = new HashMap<>();
        map.put("A", this.hardCodedA(startingLocation, finalLocation));
        map.put("<", this.hardCodedLeft(startingLocation, finalLocation));
        map.put(">", this.hardCodedRight(startingLocation, finalLocation));
        map.put("v", this.hardCodedDown(startingLocation, finalLocation));
        map.put("^", this.hardCodedUp(startingLocation, finalLocation));

        return map.get(startingLocation).get(finalLocation) + "A";
    }

    private Map<String, String> hardCodedA(final String startingLocation, final String finalLocation) {

        return Map.of(
                "^", "<",
                "<", "<v<",
                "v", "<v",
                ">", "v",
                "A", "" // don't know about this
        );
    }

    private Map<String, String> hardCodedLeft(final String startingLocation, final String finalLocation) {

        return Map.of(
                "^", ">^",
                "<", "",
                "v", ">",
                ">", ">>",
                "A", ">>^"
        );
    }

    private Map<String, String> hardCodedRight(final String startingLocation, final String finalLocation) {

        return Map.of(
                "^", "<^",
                "<", "<<",
                "v", "<",
                ">", "",
                "A", "^"
        );
    }

    private Map<String, String> hardCodedUp(final String startingLocation, final String finalLocation) {

        return Map.of(
                "^", "",
                "<", "v<",
                "v", "v",
                ">", "v>",
                "A", ">"
        );
    }

    private Map<String, String> hardCodedDown(final String startingLocation, final String finalLocation) {

        return Map.of(
                "^", "^",
                "<", "<",
                "v", "",
                ">", ">",
                "A", ">^"
        );
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

        // always go left first.
        if (startRow != 3 && startCol != 1) {
            return this.buildPressesForNumberMovementLeftRightFirst(startingLocation, finalLocation);
        } else {
            return this.buildPressesForNumberMovementUpDownFirst(startingLocation, finalLocation);
        }
    }

    private String buildPressesForNumberMovementLeftRightFirst(final String startingLocation, final String finalLocation) {

        final StringBuilder sequence = new StringBuilder();

        String actualStartingLocation = startingLocation;
        // this never happens ?!
        if (startingLocation.equalsIgnoreCase("0") && !finalLocation.equalsIgnoreCase("A")) {
            sequence.append("^");
            actualStartingLocation = "2";
        }

        sequence.append(this.buildPressesForNumberMovementLeftRight(actualStartingLocation, finalLocation));
        sequence.append(this.buildPressesForNumberMovementUpDown(actualStartingLocation, finalLocation));
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
