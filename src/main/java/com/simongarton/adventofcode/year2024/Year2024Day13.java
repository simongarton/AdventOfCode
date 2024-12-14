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

    private Long goDirectSmartly(final Scenario scenario) {

        final boolean useA = this.shouldUseAAttempt2(scenario);
        final Button useButton = useA ? scenario.buttonA : scenario.buttonB;

        final long buttonPressesHorizontal = Math.round(Math.floor(1D * scenario.prize.x / useButton.deltaX));
        final long buttonPressesVertical = Math.round(Math.floor(1D * scenario.prize.y / useButton.deltaY));
        final long buttonPresses = Math.min(buttonPressesHorizontal, buttonPressesVertical);
        
        // undershoot is 34,2374
        // I need to backtrack on the main button presses 1 by 1, and then add other button presses
        // until I hit the target
        // AND I need to figure out if I have to stop.

        // I have backtracking working, I don't know how to stop
        // it will be some combination of negative main buttons and positive other buttons - which is the back tracking.
        // I'm trying to find some way of modding that but since I have different numbers, I don't know how

        // I might have to do some pos/neg stuff if I get the wrong button
        final Button useOtherButton = useA ? scenario.buttonB : scenario.buttonA;
        final Coord dropClaw = new Coord(buttonPresses * useButton.deltaX, buttonPresses * useButton.deltaY);
        final Coord undershoot = new Coord(scenario.prize.x - dropClaw.x, scenario.prize.y - dropClaw.y);
        System.out.println(" aiming for " + buttonPresses + " buttonA = " + useA + " gives undershoot " + undershoot);

        long mainButtonPresses = buttonPresses;

        int iterations = 0;
        while (true) {
            mainButtonPresses -= 1;
            // this is a brute force stop ... but won't work with the big numbers
            if (mainButtonPresses < 0) {
                return null;
            }
            if (++iterations > 100000) {
                return null;
            }
            long otherButtonPresses = 0;
            while (true) {
                final long x = mainButtonPresses * useButton.deltaX + otherButtonPresses * useOtherButton.deltaX;
                final long y = mainButtonPresses * useButton.deltaY + otherButtonPresses * useOtherButton.deltaY;
                final Coord newDropClaw = new Coord(x, y);
                final Coord newUndershoot = new Coord(scenario.prize.x - newDropClaw.x, scenario.prize.y - newDropClaw.y);
                if (newDropClaw.equals(scenario.prize)) {
                    System.out.println("gotcha with " + mainButtonPresses + " and " + otherButtonPresses + " when " + useA);
                    if (useA) {
                        return 3 * mainButtonPresses + otherButtonPresses;
                    } else {
                        return mainButtonPresses + 3 * otherButtonPresses;
                    }
                }
                otherButtonPresses += 1;
                if (newUndershoot.x < 0 || newUndershoot.y < 0) {
                    break;
                }
            }
        }
    }

    private Coord undershootFromButton(final Button useButton, final Scenario scenario) {

        final long buttonPressesHorizontal = Math.round(Math.floor(1D * scenario.prize.x / useButton.deltaX));
        final long buttonPressesVertical = Math.round(Math.floor(1D * scenario.prize.y / useButton.deltaY));
        final long buttonPresses = Math.min(buttonPressesHorizontal, buttonPressesVertical);

        final Coord dropClaw = new Coord(buttonPresses * useButton.deltaX, buttonPresses * useButton.deltaY);
        return new Coord(scenario.prize.x - dropClaw.x, scenario.prize.y - dropClaw.y);
    }

    private boolean shouldUseAAttempt2(final Scenario scenario) {

        final Coord undershootA = this.undershootFromButton(scenario.buttonA, scenario);
        final Coord undershootB = this.undershootFromButton(scenario.buttonB, scenario);

        if (undershootA.x >= 0 && undershootA.y >= 0) {
            return true;
        }
        if (undershootB.x >= 0 && undershootB.y >= 0) {
            return false;
        }
        System.out.println(undershootA);
        System.out.println(undershootB);
        throw new RuntimeException("oops-button");
    }

    private boolean shouldUseA(final Scenario scenario) {

        final double slopeForA = (double) -scenario.buttonA.deltaY / scenario.buttonA.deltaX;
        final double slopeForB = (double) -scenario.buttonB.deltaY / scenario.buttonB.deltaX;

        final double slopeForPrize = (double) -scenario.prize.y / scenario.prize.x;

        final double costedStepWithA = (3 * slopeForA);
        final double costedStepWithB = (1 * slopeForB);

        final double approxToPrizeA = Math.abs(slopeForPrize - costedStepWithA);
        final double approxToPrizeB = Math.abs(slopeForPrize - costedStepWithB);

        // this is a maths approach which ... doesn't always work. It gave me an overshoot occasionally

        return approxToPrizeA <= approxToPrizeB;
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
            final Long outcome = this.goDirectSmartly(scenario);
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

        public Button(final String name, final int deltaX, final int deltaY) {

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

    static class Node {

        final Coord c;
        final int cost;
        final Node cameFrom;
        final String buttonPress;

        public Node(final Coord c, final int cost, final Node cameFrom, final String buttonPress) {
            this.c = c;
            this.cost = cost;
            this.cameFrom = cameFrom;
            this.buttonPress = buttonPress;
        }

        @Override
        public String toString() {
            if (this.cameFrom == null) {
                return this.c + " cost=(" + this.cost + ")";
            } else {
                return this.c + " cost=(" + this.cost + ") " + this.cameFrom.c;
            }
        }
    }
}
