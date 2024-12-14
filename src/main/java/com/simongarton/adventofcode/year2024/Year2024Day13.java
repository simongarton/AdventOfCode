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

    private Map<Button, Long> shouldUseA(final Scenario scenario) {

        final double slopeForA = (double) scenario.buttonA.deltaY / scenario.buttonA.deltaX;
        final double slopeForB = (double) scenario.buttonB.deltaY / scenario.buttonB.deltaX;
        final double averageSlope = (slopeForA + slopeForB) / 2.0;
        final long averageSteps = 0L; //this.pythag(scenario.prize.x, scenario.prize.y);

        // for part2, this will be very close to -1.0
        final double slopeForPrize = (double) -scenario.prize.y / scenario.prize.x;

        final double costedStepWithA = (3 * slopeForA);
        final double costedStepWithB = (1 * slopeForB);

        final double ratio = costedStepWithA / costedStepWithB;

        // I could get an accurate one - but for costs, I'll still need 3 times as many

        final Map<Button, Long> map = new HashMap<>();
        map.put(scenario.buttonA, Math.round(averageSteps * ratio / costedStepWithA));
        map.put(scenario.buttonB, Math.round(averageSteps * ratio / costedStepWithB));

        return map;
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

    private double goalSeek(final Scenario scenario,
                            final double targetSlope) {
        double mid;
        double low = 0;
        double high = 4;
        final double target = 0;
        final double tolerance = 1e-12;

        // I don't think this is working. Negative numbers ? For my 3rd example,
        /*
        low to high 0 -> 10
        testing 5.0 got slope 0.620137299771167 mapped -0.1994306798984644
        testing 7.5 got slope 0.5625965996908809 mapped -0.2569713799787505
        testing 8.75 got slope 0.5452127659574468 mapped -0.27435521371218463
        so it starts going up because fMid is decreasing ...
        but low to high 0 -> 2
        testing 2.0 got slope 0.8648648648648649 mapped 0.04529688519523345
        testing 1.0 got slope 1.2178217821782178 mapped 0.39825380250858633
        testing 0.5 got slope 1.7796610169491525 mapped 0.960093037279521
        so it starts going down because fMid is decreasing ...
        I think I need to hill climb
         */

        while (high - low > tolerance) {
            mid = (low + high) / 2;
            final double combinedSlope = this.slopeForCombinedButton(scenario, mid);
            final double fMid = combinedSlope - targetSlope;
            System.out.println("testing " + mid + " got slope " + combinedSlope + " mapped " + fMid);

            if (Math.abs(fMid - target) <= tolerance) {
                return mid; // Close enough to the target
            } else if (fMid < target) {
                low = mid; // Target is in the upper half
            } else {
                high = mid; // Target is in the lower half
            }
        }
        return (low + high) / 2; // Best approximation
    }


    private double slopeForCombinedButton(final Scenario scenario,
                                          final double bRatio) {

        final double deltaX = scenario.buttonA.deltaX + bRatio * scenario.buttonB.deltaX;
        final double deltaY = scenario.buttonA.deltaY + bRatio * scenario.buttonB.deltaY;

        return deltaY / deltaX;
    }

    private Long goDirectSmartly(final Scenario scenario) {

        final Map<Button, Long> counts = this.shouldUseA(scenario);

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
