package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;

public class Year2024Day25 extends AdventOfCodeChallenge {

    private List<int[]> locks;
    private List<int[]> keys;

    @Override

    public String title() {
        return "Day 25: Code Chronicle";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 25);
    }

    @Override
    public String part1(final String[] input) {

        this.loadLocksAndKeys(input);

        int fits = 0;
        for (int lock = 0; lock < this.locks.size(); lock++) {
            for (int key = 0; key < this.keys.size(); key++) {
                if (this.keyFitsLock(this.keys.get(key), this.locks.get(lock))) {
                    fits++;
                }
            }
        }

        return String.valueOf(fits);
    }

    private boolean keyFitsLock(final int[] key, final int[] lock) {

        for (int i = 0; i < 5; i++) {
            if ((5 - lock[i]) < key[i]) {
                return false;
            }
        }
        return true;
    }

    private void loadLocksAndKeys(final String[] input) {

        this.locks = new ArrayList<>();
        this.keys = new ArrayList<>();

        // always 5x7

        final List<String> lines = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            if (input[i].isEmpty()) {
                this.handleLockOrKey(lines);
                lines.clear();
                continue;
            }
            lines.add(input[i]);
        }
        if (!lines.isEmpty()) {
            this.handleLockOrKey(lines);
        }
    }

    private void handleLockOrKey(final List<String> lines) {
        if (lines.get(0).equalsIgnoreCase("#####")) {
            this.handleLock(lines);
            return;
        }
        this.handleKey(lines);
    }

    private void handleKey(final List<String> lines) {

        final int[] key = new int[]{
                5, 5, 5, 5, 5
        };

        for (int row = 1; row < 7; row++) {
            for (int col = 0; col < 5; col++) {
                if (lines.get(row).charAt(col) == '.') {
                    key[col] = key[col] - 1;
                }
            }
        }
        this.keys.add(key);
    }

    private void handleLock(final List<String> lines) {

        final int[] lock = new int[5];
        for (int row = 1; row < 7; row++) {
            for (int col = 0; col < 5; col++) {
                if (lines.get(row).charAt(col) == '#') {
                    lock[col] = lock[col] + 1;
                }
            }
        }
        this.locks.add(lock);
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }
}
