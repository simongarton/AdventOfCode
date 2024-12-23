package com.simongarton.adventofcode.year2015;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Year2015Day1 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 1: Not Quite Lisp";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2015, 1);
    }

    @Override
    public String part1(final String[] input) {

        final int up = this.countFloors("\\(", input[0]);
        final int down = this.countFloors("\\)", input[0]);
        return String.valueOf(up - down);
    }

    private int countFloors(final String regex, final String input) {

        final Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        int index = 0;
        int matches = 0;
        while (matcher.find()) {
            matches++;
            index = index + matcher.end();
            final String restOfLine = input.substring(index);
            matcher = pattern.matcher(restOfLine);
        }

        return matches;
    }

    @Override
    public String part2(final String[] input) {

        int floor = 0;
        for (int i = 0; i < input[0].length(); i++) {
            floor = floor + (input[0].charAt(i) == '(' ? 1 : -1);
            if (floor == -1) {
                return String.valueOf(i + 1); // off by one
            }
        }

        throw new RuntimeException("oops");
    }
}
