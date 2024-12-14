package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;

public class Year2024Day13 extends AdventOfCodeChallenge {

    private List<Scenario> scenarios;

    @Override
    public String title() {
        return "Day 13: Claw Contraption";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 13);
    }

    @Override
    public String part1(final String[] input) {

        this.scenarios = this.readScenarios(input, false);

        long minimumTokenSpend = 0;
        for (final Scenario scenario : this.scenarios) {
            final Long outcome = this.goDirect(scenario);
            if (outcome != null) {
                minimumTokenSpend += outcome;
            }
        }

        return String.valueOf(minimumTokenSpend);
    }

    private Coord undershootFromButton(final Button useButton, final Scenario scenario) {

        final long buttonPressesHorizontal = Math.round(Math.floor(1D * scenario.prize.x / useButton.deltaX));
        final long buttonPressesVertical = Math.round(Math.floor(1D * scenario.prize.y / useButton.deltaY));
        final long buttonPresses = Math.min(buttonPressesHorizontal, buttonPressesVertical);

        final Coord dropClaw = new Coord(buttonPresses * useButton.deltaX, buttonPresses * useButton.deltaY);
        return new Coord(scenario.prize.x - dropClaw.x, scenario.prize.y - dropClaw.y);
    }

    private Long goDirect(final Scenario scenario) {

        // The buttons are always the same, and always go down and right
        // so if I can hit the target it will be N of one and M of the other

        // further more I have calculatable maxes for each presses that would take me
        // beyond the prize

        final long maxARight = Math.round(1.0 * scenario.prize.x / scenario.buttonA.deltaX) + 1;
        final long maxADown = Math.round(1.0 * scenario.prize.y / scenario.buttonA.deltaY) + 1;
        final long maxA = Math.min(maxARight, maxADown);

        final long maxBRight = Math.round(1.0 * scenario.prize.x / scenario.buttonB.deltaX) + 1;
        final long maxBDown = Math.round(1.0 * scenario.prize.y / scenario.buttonB.deltaY) + 1;
        final long maxB = Math.min(maxBRight, maxBDown);

        long minCost = Long.MAX_VALUE;

        for (long aPresses = 0; aPresses < maxA; aPresses++) {
            for (long bPresses = 0; bPresses < maxB; bPresses++) {
                final Coord endResult = new Coord(
                        aPresses * scenario.buttonA.deltaX + bPresses * scenario.buttonB.deltaX,
                        aPresses * scenario.buttonA.deltaY + bPresses * scenario.buttonB.deltaY
                );
                if (endResult.equals(scenario.prize)) {
                    final long cost = (aPresses * 3) + bPresses;
                    if (cost < minCost) {
                        minCost = cost;
                    }
                }
            }
        }
        if (minCost == Long.MAX_VALUE) {
            return null;
        }
        return minCost;
    }

    private Long useMathsAndGoalSeek(final Scenario scenario) {

        // I know I have to press button A x times and button B y times.
        // I also know that I have to aim for a specific target.
        // It's going to be a straight line with a mix of the two buttons
        // so I am going to make a combined button ... but set the ratio of presses
        // to 1 for A (x=1) and then I need to goal seek y so that the slope of the combined button
        // matches the slope of the target.
        // I can then work out the number or presses of the combined button by taking the total
        // pythag distance of the target and divide that by the pythag distance of the combined button.

        final double prizeDistance = this.pythagoras(scenario.prize.x, scenario.prize.y);
        final double prizeSlope = 1.0D * scenario.prize.y / scenario.prize.x;

        final double bRatio = this.hillClimb(scenario, prizeSlope);

        /*

        This is working (again !) for the simple set : but doesn't work
        for the large numbers. It overshoots massively ending up e.g. for the 2nd one
        at 10013682936824,10013685112896 but the target is 10000000012748,10000000012176

        I get to total presses 118,841,475,978 and a b ratio of 0.8695648193359374
        Google Sheets gives me 118,679,047,594.50 (!) and a b ratio of 0.8695652505
        (approximately)

        Have I got rounding errors ?

         */

        final Button combinedButton = new Button("Combined",
                scenario.buttonA.deltaX + Math.round(bRatio * scenario.buttonB.deltaX),
                scenario.buttonA.deltaY + Math.round(bRatio * scenario.buttonB.deltaY)
        );

        final double buttonDistance = this.pythagoras(combinedButton.deltaX, combinedButton.deltaY);

        final double totalPresses = prizeDistance / buttonDistance;

        // I am assuming that rounding will work here
        final long aPresses = Math.round(totalPresses);
        final long bPresses = Math.round(totalPresses * bRatio);

        // where did I get to ?
        final Coord endPoint = new Coord(
                aPresses * scenario.buttonA.deltaX + bPresses * scenario.buttonB.deltaX,
                aPresses * scenario.buttonA.deltaY + bPresses * scenario.buttonB.deltaY
        );

        System.out.println(endPoint);
        System.out.println(scenario.prize);

        if (endPoint.equals(scenario.prize)) {
            System.out.println("gotcha");
            System.out.println();
            return 3 * aPresses + bPresses;
        } else {
            System.out.println();
            return null;
        }
    }

    private double hillClimb(final Scenario scenario,
                             final double targetSlope) {

        double workingRatio = 1.0;
        double stepSize = 0.1;
        final List<Double> visited = new ArrayList<>();

        while (stepSize > 1e-6) {

            final double downRatio = workingRatio - stepSize;
            final double upRatio = workingRatio + stepSize;

            final double workingSlope = this.slopeForCombinedButton(scenario, workingRatio);
            if (Math.abs(workingSlope - targetSlope) < 1e-6) {
                return workingRatio;
            }

            final double downSlope = this.slopeForCombinedButton(scenario, downRatio);
            final double upSlope = this.slopeForCombinedButton(scenario, upRatio);

            if (Math.abs(downSlope - targetSlope) < 1e-6) {
                return downRatio;
            }
            if (Math.abs(upSlope - targetSlope) < 1e-6) {
                return upRatio;
            }

            final double absoluteDifferenceDown = Math.abs(downSlope - targetSlope);
            final double absoluteDifferenceUp = Math.abs(upSlope - targetSlope);

            workingRatio = absoluteDifferenceDown < absoluteDifferenceUp ? downRatio : upRatio;

            System.out.println(workingRatio + " @ " + stepSize + " target " + targetSlope + " down " + downSlope + " up " + upSlope + " working " + workingSlope);
            if (visited.contains(workingRatio)) {
                stepSize /= 2.0;
            }
            visited.add(workingRatio);
            if (visited.size() > 2) {
                visited.remove(0);
            }
        }

        System.out.println("returning " + workingRatio);
        return workingRatio;

    }

    private double slopeForCombinedButton(final Scenario scenario,
                                          final double bRatio) {

        final double deltaX = scenario.buttonA.deltaX + bRatio * scenario.buttonB.deltaX;
        final double deltaY = scenario.buttonA.deltaY + bRatio * scenario.buttonB.deltaY;

        return deltaY / deltaX;
    }

    private List<Scenario> readScenarios(final String[] input, final boolean part2) {

        final List<Scenario> scenarios = new ArrayList<>();

        final Iterator<String> iterator = Arrays.stream(input).iterator();

        while (iterator.hasNext()) {
            final Button buttonA = this.readButton(iterator.next());
            final Button buttonB = this.readButton(iterator.next());
            final Scenario scenario = this.readScenario(iterator.next(), buttonA, buttonB, part2);
            scenarios.add(scenario);

            if (iterator.hasNext()) {
                iterator.next();
            }
        }
        return scenarios;
    }

    private Scenario readScenario(final String line, final Button buttonA, final Button buttonB, final boolean part2) {

        final String[] parts = line.split(": ");
        final String[] coordDetails = parts[1].split(", ");

        final long x = Long.parseLong(coordDetails[0].replace("X=", ""));
        final long y = Long.parseLong(coordDetails[1].replace("Y=", ""));

        final long extra = 10000000000000L;
//        final long extra = 0;

        if (part2) {
            return new Scenario(buttonA, buttonB, new Coord(x + extra, y + extra));
        } else {
            return new Scenario(buttonA, buttonB, new Coord(x, y));
        }
    }

    private Button readButton(final String line) {

        final String[] parts = line.split(": ");
        final String[] deltas = parts[1].split(", ");

        final int deltaX = Integer.parseInt(deltas[0].replace("X", ""));
        final int deltaY = Integer.parseInt(deltas[1].replace("Y", ""));

        return new Button(parts[0].replace("Button ", ""), deltaX, deltaY);
    }

    @Override
    public String part2(final String[] input) {

        this.scenarios = this.readScenarios(input, true);

        long minimumTokenSpend = 0;
        for (final Scenario scenario : this.scenarios) {
            final Long outcome = this.useMathsAndGoalSeek(scenario);
            if (outcome != null) {
                minimumTokenSpend += outcome;
            }
        }

        return String.valueOf(minimumTokenSpend);
    }

    static class Coord {

        public final long x;
        public final long y;

        public Coord(final long x, final long y) {

            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {

            return this.x + "," + this.y;
        }

        @Override
        public boolean equals(final Object o) {

            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Coord coord = (Coord) o;
            return this.x == coord.x && this.y == coord.y;
        }

        @Override
        public int hashCode() {

            return Objects.hash(this.x, this.y);
        }
    }

    static class Button {

        final String name;
        final long deltaX;
        final long deltaY;

        public Button(final String name, final long deltaX, final long deltaY) {

            this.name = name;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }
    }

    static class Scenario {

        final Button buttonA;
        final Button buttonB;
        final Coord prize;

        public Scenario(final Button buttonA, final Button buttonB, final Coord prize) {

            this.buttonA = buttonA;
            this.buttonB = buttonB;
            this.prize = prize;
        }
    }
}
