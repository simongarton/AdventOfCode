package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Year2022Day20 extends AdventOfCodeChallenge {

    List<GPSNumber> numbers;
    Map<Integer, GPSNumber> map;

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
        final List<GPSNumber> rearranged = new ArrayList<>();
        rearranged.addAll(this.numbers);
        this.debugPrint(rearranged);
        for (final GPSNumber gpsNumber : this.numbers) {
            final int currentPosition = rearranged.indexOf(gpsNumber);
            int newPosition = (currentPosition + gpsNumber.value);
            while (newPosition >= (this.numbers.size() - 1)) {
                newPosition = newPosition - (this.numbers.size() - 1);
            }
            while (newPosition < 0) {
                newPosition += this.numbers.size() - 1;
            }
            if (newPosition == 0) {
                newPosition = this.numbers.size() - 1;
            }
            System.out.println("working on " + gpsNumber.value + "/" + gpsNumber.originalPosition +
                    " now at " + currentPosition + " going to " + newPosition);
            rearranged.remove(gpsNumber);
            rearranged.add(newPosition, gpsNumber);
            this.debugPrint(rearranged);
        }
        int zero = 0;
        for (int i = 0; i < rearranged.size(); i++) {
            if (rearranged.get(i).value == 0) {
                zero = i;
                break;
            }
        }
        final int a = rearranged.get((1000 + zero) % this.numbers.size()).value;
        final int b = rearranged.get((2000 + zero) % this.numbers.size()).value;
        final int c = rearranged.get((3000 + zero) % this.numbers.size()).value;
        return String.valueOf(a + b + c);
    }

    private void debugPrint(final List<GPSNumber> rearranged) {
        System.out.println(rearranged.stream().map(n -> String.valueOf(n.value)).collect(Collectors.joining(" ")));
    }

    private void loadList(final String[] input) {
        this.numbers = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            final GPSNumber gpsNumber = new GPSNumber(Integer.parseInt(input[i]), i);
            this.numbers.add(gpsNumber);
        }
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    public static final class GPSNumber {

        int value;
        int originalPosition;

        public GPSNumber(final int value, final int originalPosition) {
            this.value = value;
            this.originalPosition = originalPosition;
        }
    }
}
