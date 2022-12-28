package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Year2022Day25 extends AdventOfCodeChallenge {

    private Map<Integer, String> snafuPowers;
    private Map<Integer, Integer> fivePowers;

    @Override
    public String title() {
        return "Day 25: Full of Hot Air";
    }

    @Override
    public boolean run() {
        return this.runChallenge(2022, 25);
    }

    @Override
    public String part1(final String[] input) {
        long total = 0;
        int index = 0;
        for (final String line : input) {
            final long result = this.snafuToDecimal(line);
            total += result;
            index++;
            System.out.printf("%s/%s %s -> %s\n",
                    index,
                    input.length,
                    line,
                    total);
        }
//        System.out.println(this.decimalToSnafuBruteForce(total));
        return this.decimalToSnafu(total);
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    public String decimalToSnafuBruteForce(final long total) {
        // Look at my test to see the pattern.
        // Starting from 1, I use the following digits (0) 1 2
        // then at the 2 the next counter starts at 1
        // the previous counter goes back to = then - then 0 1 2,
        // hitting 2 advances the next counter through the sequence to 2 and
        // then nudges the next next counter to 1, resetting the next counter to =
        // AND ALL PREVIOUS COUNTERS ?!
        // so I need to trap sequences knowing if I have a higher counter

        if (total == 0) {
            return "0";
        }

        // keyed as power : 1 = 5^0 so key = 0;
        final Map<Integer, String> map = new LinkedHashMap<>();
        map.put(0, "1");
        for (int i = 1; i < total; i++) {
            this.updateMap(map);
//            System.out.println(i + " " + this.mapToSnafu(map));
        }
        return this.mapToSnafu(map);
    }

    private String mapToSnafu(final Map<Integer, String> map) {
        String snafu = "";
        for (int key = map.size() - 1; key >= 0; key--) {
            snafu += map.get(key);
        }
        return snafu;
    }

    private String mapToSnafuInteger(final Map<Integer, Integer> map) {
        String snafu = "";
        for (int key = map.size() - 1; key >= 0; key--) {
            snafu += map.get(key);
        }
        return snafu;
    }

    private String mapToSnafuCode(final Map<Integer, Integer> map) {
        String snafu = "";
        for (int key = map.size() - 1; key >= 0; key--) {
            snafu += this.snafuCode(map.get(key));
        }
        return snafu;
    }

    private String snafuCode(final int value) {
        switch (value) {
            case -2:
                return "=";
            case -1:
                return "-";
            case 0:
                return "0";
            case 1:
                return "1";
            case 2:
                return "2";
            default:
                throw new RuntimeException("snafuCode(" + value + ")");
        }
    }

    private void updateMap(final Map<Integer, String> map) {
        switch (map.get(0)) {
            case "=":
                map.put(0, "-");
                return;
            case "-":
                map.put(0, "0");
                return;
            case "0":
                map.put(0, "1");
                return;
            case "1":
                map.put(0, "2");
                return;
            case "2":
                this.recursiveRippleMap(map, 1);
                return;
            default:
                throw new RuntimeException(map.get(0));
        }
    }

    private void recursiveRippleMap(final Map<Integer, String> map, final int powerToUpdate) {
        if (!map.containsKey(powerToUpdate)) {
            map.put(powerToUpdate, "0");
        }
        switch (map.get(powerToUpdate)) {
            case "=":
                map.put(powerToUpdate, "-");
                this.rippleDown(map, powerToUpdate - 1);
                return;
            case "-":
                map.put(powerToUpdate, "0");
                this.rippleDown(map, powerToUpdate - 1);
                return;
            case "0":
                map.put(powerToUpdate, "1");
                this.rippleDown(map, powerToUpdate - 1);
                return;
            case "1":
                map.put(powerToUpdate, "2");
                this.rippleDown(map, powerToUpdate - 1);
                return;
            case "2":
                this.recursiveRippleMap(map, powerToUpdate + 1);
                return;
            default:
                throw new RuntimeException(map.get(0));
        }
    }

    private void rippleDown(final Map<Integer, String> map, final int powerToReset) {
        for (int i = powerToReset; i >= 0; i--) {
            map.put(i, "=");
        }
    }

    public String decimalToSnafu(final long total) {
        this.buildFivePowers(total);
        this.redistributeFivePowers();
        return this.mapToSnafuCode(this.fivePowers);
    }

    private void redistributeFivePowers() {
        final int maxPower = this.fivePowers.size() - 1;
        while (true) {
            boolean movedAnything = false;
            for (int key = maxPower; key >= 0; key--) {
                final int value = this.fivePowers.get(key);
                if (value < 3) {
                    continue;
                }
                this.fivePowers.put(key + 1, this.fivePowers.getOrDefault(key + 1, 0) + 1);
                this.fivePowers.put(key, value - 5);
                movedAnything = true;
                break;
            }
            if (!movedAnything) {
                break;
            }
        }
        System.out.println(this.mapToSnafuCode(this.fivePowers));
    }

    private void buildFivePowers(final long total) {
        this.fivePowers = new LinkedHashMap<>();
        long runningTotal = total;
        long buildingUp = 0;
        int power = 1;
//        while (Math.pow(5, power) < total) {
        while (buildingUp < total) {
            final long powerOfFive = Math.round(Math.pow(5, power));
            final long powerOfFiveUnder = Math.round(Math.pow(5, power - 1));
            final long remainder = runningTotal % powerOfFive;
            this.fivePowers.put(power - 1, (int) (remainder / powerOfFiveUnder));
            buildingUp += remainder;
            runningTotal = runningTotal - remainder;
            System.out.printf("%s %s %s %s %s %s\n",
                    power,
                    powerOfFive,
                    powerOfFiveUnder,
                    runningTotal,
                    remainder,
                    this.mapToSnafuInteger(this.fivePowers)
            );
            power = power + 1;
        }
        System.out.println(this.mapToSnafuInteger(this.fivePowers));
    }

    public String decimalToSnafu2(final long total) {
        /*

        If we didn't have these negative numbers, it would be a matter of powers of 5.
        Since we do, we have to be more creative.
        7 decimal is 12 : 1 * 5^1 + 2
        8 decimal is 2= : 2 * 5^1 - 2
        So find the maximum power of 5 smaller than the total.
        If the remainder (of the total) is <= 2 * (5^(maxPower - 1)) then we go up 1 and subtract.
        If it's < then it's more normal.

        For part 1, I need to encode 32762853787275
        32,762,853,787,275
        5^20 is 95,367,431,640,625 which is bigger
        The remainder is 62,604,577,853,350 - which is about 3 times more than 5^19
        So we are going to start with 19,073,486,328,125 = 5^19

        This might be 1 or 2 - I need to get it above or equal to the total.

         */

        if (total == 0) {
            return "0";
        }
        int maxPower = 0;
        while (Math.pow(5, maxPower) < total) {
            maxPower = maxPower + 1;
        }

        // Ok, so I have a power of 5 > than my total.
        final long valueForMaxPower = Math.round(Math.pow(5, maxPower));
        // I need the next one down
        final long valueForNextPowerDown = Math.round(Math.pow(5, maxPower - 1));
        // whats the remainder from valueForMaxPower less total
        final long remainder = valueForMaxPower - total;
        final long multiples = remainder / valueForNextPowerDown;

        this.snafuPowers = new HashMap<>();
        if (multiples > 2) {
            this.snafuPowers.put(maxPower, "0");
            this.recursiveThingy(total, maxPower - 1);
        } else {
            this.snafuPowers.put(maxPower, String.valueOf(multiples));
            this.recursiveThingy(total - valueForMaxPower, maxPower - 1);
        }

        return this.mapToSnafu(this.snafuPowers);
    }

    private void recursiveThingy(final long runningTotal, final int power) {
        // This is different from earlier; I am calculating how many = - 0 1 2 of this power
        // I need to use.
        final long valueForMaxPower = Math.round(Math.pow(5, power));
        // I may need the next one down
        final long valueForNextPowerDown = Math.round(Math.pow(5, power - 1));
        // I may have been given a negative running total which means I now
        // need to add some of these - well, not so much add, as take away
        int multiplier = 1;
        if (runningTotal < 0) {
            multiplier = -1;
        }
        // whats the remainder from valueForMaxPower less total
        final long remainder = multiplier * (valueForMaxPower - runningTotal);
        // if it's zero, then I have got to the answer
        if (remainder == 0) {
            return;
        }
        // if it's less than 0, then running total is bigger than this,
        // which means I have to use 2 of these
        if (remainder < 0) {
            this.snafuPowers.put(power, this.afterMultiplier("2", multiplier));
            this.recursiveThingy((2 * valueForMaxPower) - (multiplier * runningTotal), power - 1);
        } else {
            final long multiples = remainder / valueForNextPowerDown;
            if (multiples > 2) {
                this.snafuPowers.put(power, "0");
                this.recursiveThingy(runningTotal, power - 1);
            } else {
                this.snafuPowers.put(power, String.valueOf(multiples));
                // not happy
                this.recursiveThingy(runningTotal - (2 * valueForMaxPower), power - 1);
            }
        }
    }

    private String afterMultiplier(final String s, final int multiplier) {
        switch (s) {
            case "2":
                return multiplier == 1 ? "2" : "=";
            case "1":
                return multiplier == 1 ? "1" : "-";
            case "0":
                return "0";
            default:
                throw new RuntimeException(s + " " + multiplier);
        }
    }

    public long snafuToDecimal(final String line) {
        int power = 0;
        long total = 0;
        for (int i = line.length() - 1; i >= 0; i--) {
            total += this.snafuDigit(line.substring(i, i + 1), power);
            power++;
        }
        return total;
    }

    public long snafuDigit(final String substring, final int power) {
        switch (substring) {
            case "2":
                return Math.round(2 * Math.pow(5, power));
            case "1":
                return Math.round(1 * Math.pow(5, power));
            case "0":
                return 0L;
            case "-":
                return Math.round(-1 * Math.pow(5, power));
            case "=":
                return Math.round(-2 * Math.pow(5, power));
            default:
                throw new RuntimeException(substring);
        }
    }
}
