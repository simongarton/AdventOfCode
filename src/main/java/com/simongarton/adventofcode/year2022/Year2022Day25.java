package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.LinkedHashMap;
import java.util.Map;

public class Year2022Day25 extends AdventOfCodeChallenge {

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
        return this.decimalToSnafu(total);
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    public String decimalToSnafu(final long total) {
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
