package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Year2022Day20 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;

    private List<GPSNumber> numbers;
    private Map<Integer, GPSNumber> map;

    @Override
    public String title() {
        return "Day 20: Grove Positioning System";
    }

    @Override
    public boolean run() {
        return this.runChallenge(2022, 20);
    }

    @Override
    public String part1(final String[] input) {
        this.loadList(input);
        final List<GPSNumber> rearranged = this.mix(this.numbers);
        return this.findMagicValue(rearranged);
    }

    @Override
    public String part2(final String[] input) {
        this.loadList(input);
        this.debugPrint(this.numbers);
        this.scaleUp();
        this.debugPrint(this.numbers);
        List<GPSNumber> rearranged = this.mix(this.numbers);
        this.debugPrint(rearranged);
        for (int i = 0; i < 9; i++) {
            rearranged = this.mix(rearranged);
            this.debugPrint(rearranged);
        }
        return this.findMagicValue(rearranged);
    }

    private void scaleUp() {
        this.numbers.stream().forEach(GPSNumber::scaleUpNumber);
    }

    private String findMagicValue(final List<GPSNumber> rearranged) {
        int zero = 0;
        for (int i = 0; i < rearranged.size(); i++) {
            if (rearranged.get(i).value == 0) {
                zero = i;
                break;
            }
        }
        final long a = rearranged.get((1000 + zero) % this.numbers.size()).value;
        final long b = rearranged.get((2000 + zero) % this.numbers.size()).value;
        final long c = rearranged.get((3000 + zero) % this.numbers.size()).value;
        return String.valueOf(a + b + c);
    }

    private List<GPSNumber> mix(final List<GPSNumber> numbers) {
        final List<GPSNumber> rearranged = new ArrayList<>(numbers);
        this.debugPrint(rearranged);
        for (final GPSNumber gpsNumber : this.numbers) {
            final int currentPosition = rearranged.indexOf(gpsNumber);
            long newPosition = (currentPosition + gpsNumber.value);
            if (newPosition >= (numbers.size() - 1)) {
                newPosition = newPosition % (numbers.size() - 1);
            }
            if (newPosition < 0) {
                final long a = (-newPosition / (numbers.size() - 1));
                newPosition = newPosition + ((a + 1) * (numbers.size() - 1));
                newPosition = newPosition % (numbers.size() - 1);
            }
            if (newPosition == 0) {
                newPosition = numbers.size() - 1;
            }
//            System.out.println("working on " + gpsNumber.value + "/" + gpsNumber.originalPosition +
//                    " now at " + currentPosition + " going to " + newPosition);
            rearranged.remove(gpsNumber);
            rearranged.add((int) newPosition, gpsNumber);
//            this.debugPrint(rearranged);
        }
        return rearranged;
    }

    private void debugPrint(final List<GPSNumber> rearranged) {
        if (DEBUG) {
            System.out.println(rearranged.stream().map(n -> String.valueOf(n.value)).collect(Collectors.joining(" ")));
        }
    }

    private void loadList(final String[] input) {
        this.numbers = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            final GPSNumber gpsNumber = new GPSNumber(Integer.parseInt(input[i]), i);
            this.numbers.add(gpsNumber);
        }
    }

    public static final class GPSNumber {

        long value;
        int originalPosition;

        public GPSNumber(final long value, final int originalPosition) {
            this.value = value;
            this.originalPosition = originalPosition;
        }

        private void scaleUpNumber() {
            this.value *= 811589153;
        }
    }
}
