package com.simongarton.adventofcode.year2019;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.Arrays;
import java.util.List;

public class Year2019Day1 extends AdventOfCodeChallenge {

    @Override
    public boolean run() {
        return this.runChallenge(2019, 1);
    }

    @Override
    public String part1(final String[] input) {
        final List<String> result = Arrays.asList(input);
        final long fuelNeeded = this.calculateFuelForModule(result);
        return String.valueOf(fuelNeeded);
    }

    @Override
    public String part2(final String[] input) {
        final List<String> result = Arrays.asList(input);
        final long totalFuelNeeded = this.calculateTotalFuel(result);
        return String.valueOf(totalFuelNeeded);
    }


    private long calculateTotalFuel(final List<String> masses) {
        long total = 0;
        for (final String mass : masses) {
            final long actualMass = Long.parseLong(mass);
            final long fuelNeeded = this.calculateFuelForMass(actualMass);
            total += fuelNeeded;
        }
        return total;
    }

    private long calculateFuelForModule(final List<String> masses) {
        long total = 0;
        for (final String mass : masses) {
            final long actualMass = Long.parseLong(mass);
            final long fuelNeeded = (actualMass / 3) - 2;
            total += fuelNeeded;
        }
        return total;
    }

    private long calculateFuelForMass(final Long actualMass) {
        long total = 0;
        long fuelTotal;
        long mass = actualMass;
        do {
            fuelTotal = (mass / 3) - 2;
            if (fuelTotal < 0) {
                fuelTotal = 0;
            }
            total += fuelTotal;
            mass = fuelTotal;
        } while (fuelTotal > 0);
        return total;
    }
}
