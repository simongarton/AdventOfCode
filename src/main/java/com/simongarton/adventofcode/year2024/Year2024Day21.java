package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.year2024.day21.DirectionHeading;
import com.simongarton.adventofcode.year2024.day21.DirectionPlan;

import java.util.List;

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
        // 72966 too low

        // Dec 27 : 145240 wrong but sample is still working at 126384
        // other code, I know the answer is 138764
        // how do I make my code get to that answer ?

        /*

        This is from part 1.

        140A: <v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>>^AAvA<^A>A<vA>^A<A>A
        180A: <v<A>>^A<vA<A>>^AAvAA<^A>A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A<vA>^A<A>A
        176A: <v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<v<A>>^AvA<^A>A<v<A>A>^AAvA<^A>A
        805A: <vA<AA>>^AvA<^A>AAAvA^A<v<A>A>^AAAvA<^A>A<v<A>>^AAvA^A<vA>^A<v<A>>^AAvA<^A>A
        638A: <v<A>>^AAvA^A<v<A>A>^AvA<^A>A<vA<AA>>^AvA<^A>AAvA^A<vA>^A<v<A>>^AAAvA<^A>A

        I've looked at the Numpad sequences and they look sane.
        I don't yet get why the Dirpad sequences make a difference.
        And given the sample is perfect ...

        "t doesn't matter. The last digit of every code is A. Making the final robot press A involves every other robot in the chain pressing A. So at the point you finish entering any one of the codes, every single robot is back in the starting position. Therefore you can treat the codes entirely independently."

         */

        int total = 0;
        for (final String numericCode : input) {
            final String fullSequence = this.fullSequence(numericCode);
            final int numericPart = Integer.parseInt(numericCode.replace("A", ""));
            total += numericPart * fullSequence.length();
            System.out.println(fullSequence);
            System.out.println("  " + numericCode + ": " + numericPart + " * " + fullSequence.length() + " = " + numericPart * fullSequence.length());
        }
        return String.valueOf(total);
    }

    private void generateMovesForTesting() {

        final List<String> keys = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "A");
        for (final String from : keys) {
            for (final String to : keys) {
                if (from.equalsIgnoreCase(to)) {
                    continue;
                }
                final DirectionPlan directionPlan = this.figureDirectionPlanForNumpad(from, to);
                System.out.println(from + "->" + to + " " + directionPlan + " " + this.dirpadPressesForPlan(directionPlan));
            }
        }

        final List<String> dirpadKeys = List.of(">", "<", "^", "v", "A");
        for (final String from : dirpadKeys) {
            for (final String to : dirpadKeys) {
                if (from.equalsIgnoreCase(to)) {
                    continue;
                }
                final DirectionPlan directionPlan = this.figureDirectionPlanForDirpad(from, to);
                System.out.println(from + "->" + to + " " + directionPlan + " " + this.dirpadPressesForPlan(directionPlan));
            }
        }
    }

    private String dirpadPressesForPlan(final DirectionPlan directionPlan) {

        String presses = this.getDirpadPressesFirst(directionPlan.getHeading(), directionPlan.getFirstMoves());
        presses = presses + this.getDirpadPressesSecond(directionPlan.getHeading(), directionPlan.getSecondMoves());
        presses = presses + "A";
        return presses;
    }

    private String getDirpadPressesFirst(final DirectionHeading heading, final int firstMoves) {

        return switch (heading) {
            case LEFT, LEFT_UP, LEFT_DOWN -> "<".repeat(firstMoves);
            case RIGHT, RIGHT_UP, RIGHT_DOWN -> ">".repeat(firstMoves);
            case UP, UP_LEFT, UP_RIGHT -> "^".repeat(firstMoves);
            case DOWN, DOWN_LEFT, DOWN_RIGHT -> "v".repeat(firstMoves);
        };
    }

    private String getDirpadPressesSecond(final DirectionHeading heading, final int secondMoves) {

        return switch (heading) {
            case UP, DOWN, LEFT, RIGHT -> "";
            case UP_LEFT, DOWN_LEFT -> "<".repeat(secondMoves);
            case UP_RIGHT, DOWN_RIGHT -> ">".repeat(secondMoves);
            case LEFT_DOWN, RIGHT_DOWN -> "v".repeat(secondMoves);
            case LEFT_UP, RIGHT_UP -> "^".repeat(secondMoves);
        };
    }

    private DirectionPlan figureDirectionPlanForNumpad(final String startingLocation, final String finalLocation) {

        // origin top left

        final int startRow = this.findNumberRow(startingLocation);
        final int endRow = this.findNumberRow(finalLocation);
        final int startCol = this.findNumberCol(startingLocation);
        final int endCol = this.findNumberCol(finalLocation);

        final int verticalMoves = Math.abs(startRow - endRow);
        final int horizontalMoves = Math.abs(startCol - endCol);

        if (startRow == endRow) {
            final DirectionHeading heading = this.leftOrRight(startCol, endCol); // LEFT or RIGHT
            return new DirectionPlan(heading, horizontalMoves, 0);
        }

        if (startCol == endCol) {
            final DirectionHeading heading = this.upOrDown(startRow, endRow); // UP or DOWN
            return new DirectionPlan(heading, verticalMoves, 0);
        }

        final boolean horizontalFirst = !(startRow == 3 && endRow < 3 && endCol == 0);

        if (startCol > endCol) { // must go left
            if (startRow < endRow) { // must go down
                if (horizontalFirst) {
                    return new DirectionPlan(DirectionHeading.LEFT_DOWN, horizontalMoves, verticalMoves);
                } else {
                    return new DirectionPlan(DirectionHeading.DOWN_LEFT, verticalMoves, horizontalMoves);
                }
            } else { // must go up
                if (horizontalFirst) {
                    return new DirectionPlan(DirectionHeading.LEFT_UP, horizontalMoves, verticalMoves);
                } else {
                    return new DirectionPlan(DirectionHeading.UP_LEFT, verticalMoves, horizontalMoves);
                }
            }
        }

        // I will be going right, but could be 0 moves

        if (startRow < endRow) { // must go down
            if (horizontalFirst) {
                return new DirectionPlan(DirectionHeading.RIGHT_DOWN, horizontalMoves, verticalMoves);
            } else {
                return new DirectionPlan(DirectionHeading.DOWN_RIGHT, verticalMoves, horizontalMoves);
            }
        } else { // must go up
            if (horizontalFirst) {
                return new DirectionPlan(DirectionHeading.RIGHT_UP, horizontalMoves, verticalMoves);
            } else {
                return new DirectionPlan(DirectionHeading.UP_RIGHT, verticalMoves, horizontalMoves);
            }
        }
    }

    private DirectionPlan figureDirectionPlanForDirpad(final String startingLocation, final String finalLocation) {

        // origin top left

        final int startRow = this.findDirpadRow(startingLocation);
        final int endRow = this.findDirpadRow(finalLocation);
        final int startCol = this.findDirpadCol(startingLocation);
        final int endCol = this.findDirpadCol(finalLocation);
        final int verticalMoves = Math.abs(startRow - endRow);
        final int horizontalMoves = Math.abs(startCol - endCol);

        if (startRow == endRow) {
            if (startCol == endCol) {
                // is this even valid ?
                return new DirectionPlan(DirectionHeading.DOWN, 0, 0);
            }
            final DirectionHeading heading = this.leftOrRight(startCol, endCol); // LEFT or RIGHT
            return new DirectionPlan(heading, horizontalMoves, 0);
        }

        if (startCol == endCol) {
            final DirectionHeading heading = this.upOrDown(startRow, endRow); // UP or DOWN
            return new DirectionPlan(heading, verticalMoves, 0);
        }

        // I want to go horizontal first.
        // I can do that unless I am on the top row AND moving left as far as 0
        final boolean horizontalFirst = !((startRow == 0) && (endCol < startCol) && (endCol == 0));

        if (startCol > endCol) {
            if (startRow < endRow) {
                if (horizontalFirst) {
                    return new DirectionPlan(DirectionHeading.LEFT_DOWN, horizontalMoves, verticalMoves);
                } else {
                    return new DirectionPlan(DirectionHeading.DOWN_LEFT, verticalMoves, horizontalMoves);
                }
            } else {
                if (horizontalFirst) {
                    return new DirectionPlan(DirectionHeading.LEFT_UP, horizontalMoves, verticalMoves);
                } else {
                    return new DirectionPlan(DirectionHeading.UP_LEFT, verticalMoves, horizontalMoves);
                }
            }
        }

        if (startRow < endRow) {
            if (horizontalFirst) {
                return new DirectionPlan(DirectionHeading.RIGHT_DOWN, horizontalMoves, verticalMoves);
            } else {
                return new DirectionPlan(DirectionHeading.DOWN_RIGHT, verticalMoves, horizontalMoves);
            }
        } else {
            if (horizontalFirst) {
                return new DirectionPlan(DirectionHeading.RIGHT_UP, horizontalMoves, verticalMoves);
            } else {
                return new DirectionPlan(DirectionHeading.UP_RIGHT, verticalMoves, horizontalMoves);
            }
        }
    }

    private DirectionHeading figureDirectionHeading(final String startingLocation, final String finalLocation) {

        // origin top left

        final int startRow = this.findNumberRow(startingLocation);
        final int endRow = this.findNumberRow(finalLocation);
        final int startCol = this.findNumberCol(startingLocation);
        final int endCol = this.findNumberCol(finalLocation);

        if (startRow == endRow) {
            return this.leftOrRight(startCol, endCol); // LEFT or RIGHT
        }

        if (startCol == endCol) {
            return this.upOrDown(startRow, endRow); // UP or DOWN
        }

        if (startCol < endCol) {
            if (startRow < endRow) {
                return DirectionHeading.LEFT_DOWN; // or it could be DOWN_LEFT ?!
            } else {
                return DirectionHeading.LEFT_UP; // or it could be UP_LEFT ?!
            }
        }

        if (startRow < endRow) {
            return DirectionHeading.DOWN_RIGHT; // or it could be RIGHT_DOWN ?!
        } else {
            return DirectionHeading.UP_RIGHT; // or it could be RIGHT_UP ?!
        }
    }

    private DirectionHeading upOrDown(final int startRow, final int endRow) {

        if (startRow < endRow) {
            return DirectionHeading.DOWN;
        }
        if (startRow > endRow) {
            return DirectionHeading.UP;
        }
        throw new RuntimeException("oops");
    }

    private DirectionHeading leftOrRight(final int startCol, final int endCol) {

        if (startCol < endCol) {
            return DirectionHeading.RIGHT;
        }
        if (startCol > endCol) {
            return DirectionHeading.LEFT;
        }
        throw new RuntimeException("oops");
    }

    public String fullSequence(final String numericCode) {

        final StringBuilder fullSequence = new StringBuilder();

        String currentNumpadLocation = "A";
        for (int i = 0; i < numericCode.length(); i++) {
            final String neededKey = numericCode.substring(i, i + 1);
            final String numpadPresses = this.buildPressesForNumpadSimple(currentNumpadLocation, neededKey);
            fullSequence.append(numpadPresses);
            currentNumpadLocation = neededKey;
        }

        return fullSequence.toString();
    }

    public String buildPressesForNumpadSimple(final String startingLocation, final String finalLocation) {

        // these are the moves I need to make around the numpad
        final DirectionPlan directionPlan = this.figureDirectionPlanForNumpad(startingLocation, finalLocation);
        final String numpadMoves = this.dirpadPressesForPlan(directionPlan);

        final StringBuilder fullSequence = new StringBuilder();

        // now if I have just made a move, the robot will have been on A
        String currentRobotLocation = "A";
        for (int i = 0; i < numpadMoves.length(); i++) {
            final String neededKey = numpadMoves.substring(i, i + 1);
            final String numpadPresses = this.buildPressesForDirpadRobot1(currentRobotLocation, neededKey);
//            System.out.println("for numpad " + neededKey + " I got " + numpadPresses);
            fullSequence.append(numpadPresses);
            currentRobotLocation = neededKey;
        }

        return fullSequence.toString();
    }

    public String buildPressesForDirpadRobot1(final String startingLocation, final String finalLocation) {

        // these are the moves I need to make around the current dirpad
        final DirectionPlan directionPlan = this.figureDirectionPlanForDirpad(startingLocation, finalLocation);
        final String dirpadMoves = this.dirpadPressesForPlan(directionPlan);

        final StringBuilder fullSequence = new StringBuilder();

        // now if I have just made a move, THIS robot will have been on A
        String currentRobotLocation = "A";
        for (int i = 0; i < dirpadMoves.length(); i++) {
            final String neededKey = dirpadMoves.substring(i, i + 1);
            final String dirpadPresses = this.buildPressesForDirpadRobot2(currentRobotLocation, neededKey);
//            System.out.println("  for dirpad " + neededKey + " I got " + dirpadPresses);
            fullSequence.append(dirpadPresses);
            currentRobotLocation = neededKey;
        }

        return fullSequence.toString();
    }

    public String buildPressesForDirpadRobot2(final String startingLocation, final String finalLocation) {

        // these are the moves I need to make around the current dirpad
        final DirectionPlan directionPlan = this.figureDirectionPlanForDirpad(startingLocation, finalLocation);
//        System.out.println("    " + directionPlan);

        // hack
        if (startingLocation.equalsIgnoreCase("A") && finalLocation.equalsIgnoreCase("<")) {
            return "<v<A";
        }
        return this.dirpadPressesForPlan(directionPlan);
    }

    public String buildPressesForNumberMovement(final String startingLocation, final String finalLocation) {

        final int startRow = this.findNumberRow(startingLocation);
        final int startCol = this.findNumberCol(startingLocation);

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
}
