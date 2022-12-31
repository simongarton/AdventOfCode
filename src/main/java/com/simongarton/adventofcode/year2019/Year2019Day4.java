package com.simongarton.adventofcode.year2019;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.HashMap;
import java.util.Map;

public class Year2019Day4 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 4: Secure Container";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2019, 4);
    }

    @Override
    public String part1(final String[] input) {

        final int low = 284639;
        final int high = 748759;

        int valid = 0;
        for (int i = low; i <= high; i++) {
            if (this.validPassword(i)) {
                valid++;
            }
        }

        return String.valueOf(valid);
    }

    @Override
    public String part2(final String[] input) {

        final int low = 284639;
        final int high = 748759;

        int valid = 0;
        for (int i = low; i <= high; i++) {
            if (this.validPassword2(i)) {
                valid++;
            }
        }

        return String.valueOf(valid);
    }

    private boolean validPassword(final int attempt) {
        final String password = String.valueOf(attempt);
        boolean dbl = false;
        boolean decrease = false;
        for (int i = 1; i < 6; i++) {
            final int a = Integer.parseInt(password.substring(i - 1, i));
            final int b = Integer.parseInt(password.substring(i, i + 1));
            if (a == b) {
                dbl = true;
            }
            if (b < a) {
                decrease = true;
                break;
            }
        }
        return dbl && !decrease;
    }

    private boolean validPassword2(final int attempt) {
        if (!this.validPassword(attempt)) {
            return false;
        }
        final String password = String.valueOf(attempt);
        final Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 6; i++) {
            final int val = Integer.parseInt(password.substring(i, i + 1));
            if (map.containsKey(val)) {
                map.put(val, map.get(val) + 1);
            } else {
                map.put(val, 1);
            }
        }
        for (final Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 2) {
                return true;
            }
        }
        return false;
    }
}
