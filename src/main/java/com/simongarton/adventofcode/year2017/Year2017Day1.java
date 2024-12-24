package com.simongarton.adventofcode.year2017;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2017Day1 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 1: Inverse Captcha";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2017, 1);
    }

    @Override
    public String part1(final String[] input) {

        int total = 0;
        for (int i = 1; i < input[0].length(); i++) {
            if (input[0].charAt(i - 1) == input[0].charAt(i)) {
                total += Integer.parseInt(input[0].charAt(i) + "");
            }
        }
        if (input[0].charAt(0) == input[0].charAt(input[0].length() - 1)) {
            total += Integer.parseInt(input[0].charAt(0) + "");
        }
        return String.valueOf(total);
    }

    @Override
    public String part2(final String[] input) {

        final String doubleString = input[0].repeat(2);
        final int lookAhead = input[0].length() / 2;
        int total = 0;
        for (int i = 0; i < input[0].length(); i++) {
            if (doubleString.charAt(i) == doubleString.charAt(i + lookAhead)) {
                total += Integer.parseInt(doubleString.charAt(i) + "");
            }
        }
        return String.valueOf(total);
    }
}
