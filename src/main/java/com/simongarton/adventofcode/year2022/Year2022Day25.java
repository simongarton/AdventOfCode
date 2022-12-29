package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.LinkedHashMap;
import java.util.Map;

public class Year2022Day25 extends AdventOfCodeChallenge {

    private Map<Integer, Integer> fivePowers;

    @Override
    public String title() {
        return "Day 25: Full of Hot Air";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2022, 25);
    }

    @Override
    public String part1(final String[] input) {
        long total = 0;
        for (final String line : input) {
            final long result = this.snafuToDecimal(line);
            total += result;
        }
        if (false) {
            System.out.println(this.decimalToSnafuBruteForce(total));
        }
        return this.decimalToSnafu(total);
    }

    @Override
    public String part2(final String[] input) {
        return this.part1(input);
    }

    public String decimalToSnafuBruteForce(final long total) {
        if (total == 0) {
            return "0";
        }

        // keyed as power : 1 = 5^0 so key = 0;
        final Map<Integer, String> map = new LinkedHashMap<>();
        map.put(0, "1");
        for (int i = 1; i < total; i++) {
            this.updateMap(map);
            //System.out.println(i + " " + this.mapToSnafu(map));
        }
        return this.mapToSnafu(map);
    }

    private String mapToSnafu(final Map<Integer, String> map) {
        final StringBuilder snafu = new StringBuilder();
        for (int key = map.size() - 1; key >= 0; key--) {
            snafu.append(map.get(key));
        }
        return snafu.toString();
    }

    private String mapToSnafuInteger(final Map<Integer, Integer> map) {
        final StringBuilder snafu = new StringBuilder();
        for (int key = map.size() - 1; key >= 0; key--) {
            snafu.append(map.get(key));
        }
        return snafu.toString();
    }

    private String mapToSnafuCode(final Map<Integer, Integer> map) {
        final StringBuilder snafu = new StringBuilder();
        for (int key = map.size() - 1; key >= 0; key--) {
            snafu.append(this.snafuCode(map.get(key)));
        }
        return snafu.toString();
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
        // System.out.println(this.mapToSnafuCode(this.fivePowers));
    }

    private void buildFivePowers(final long total) {
        this.fivePowers = new LinkedHashMap<>();
        long runningTotal = total;
        long buildingUp = 0;
        int power = 1;
        while (buildingUp < total) {
            final long powerOfFive = Math.round(Math.pow(5, power));
            final long powerOfFiveUnder = Math.round(Math.pow(5, power - 1));
            final long remainder = runningTotal % powerOfFive;
            this.fivePowers.put(power - 1, (int) (remainder / powerOfFiveUnder));
            buildingUp += remainder;
            runningTotal = runningTotal - remainder;
            power = power + 1;
        }
        // System.out.println(this.mapToSnafuInteger(this.fivePowers));
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
