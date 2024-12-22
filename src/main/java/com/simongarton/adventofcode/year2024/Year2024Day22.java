package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2024Day22 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 0: Template code";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 0);
    }

    @Override
    public String part1(final String[] input) {

        long total = 0;
        for (final String line : input) {
            total += this.monkeyMagic(Long.valueOf(line));
        }

        return String.valueOf(total);
    }

    private long monkeyMagic(Long number) {

        for (int i = 0; i < 2000; i++) {
            number = this.multiplyAndMixAndPrune(number, 64);
            number = this.divideAndMixAndPrune(number);
            number = this.multiplyAndMixAndPrune(number, 2048);
        }
        return number;
    }

    private long divideAndMixAndPrune(final long secret) {

        final long multiplied = secret / 32;
        final long mixed = this.mix(multiplied, secret);
        return this.prune(mixed);
    }

    private long multiplyAndMixAndPrune(final long secret, final long multiplect) {

        final long multiplied = secret * multiplect;
        final long mixed = this.mix(multiplied, secret);
        return this.prune(mixed);
    }

    private long prune(final long secret) {

        return secret % 16777216;
    }

    private long mix(final long nextNumber, final long number) {

        return nextNumber ^ number;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }
}
