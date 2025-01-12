package com.simongarton.adventofcode.year2015;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Year2015Day2 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 2: I Was Told There Would Be No Math";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2015, 2);
    }

    @Override
    public String part1(final String[] input) {

        long total = 0;
        for (final String present : input) {
            total += this.calculatePaper(present);
        }

        return String.valueOf(total);
    }

    private long calculatePaper(final String present) {

        final String[] parts = present.split("x");
        final long length = Long.parseLong(parts[0]);
        final long width = Long.parseLong(parts[1]);
        final long height = Long.parseLong(parts[2]);
        final long facelw = length * width;
        final long facewh = width * height;
        final long facehl = height * length;
        final long smallFace = Stream.of(facelw, facewh, facehl).min(Long::compareTo).orElseThrow();
        System.out.println(present + ": " + length + "x" + width + "x" + height + " small " + smallFace);
        return (2 * facelw) + (2 * facewh) + (2 * facehl) + smallFace;
    }

    @Override
    public String part2(final String[] input) {

        long total = 0;
        for (final String present : input) {
            total += this.calculateRibbon(present);
        }

        return String.valueOf(total);
    }

    private long calculateRibbon(final String present) {

        final String[] parts = present.split("x");
        final long length = Long.parseLong(parts[0]);
        final long width = Long.parseLong(parts[1]);
        final long height = Long.parseLong(parts[2]);
        final List<Long> sides = Stream.of(length, width, height).sorted(Comparator.naturalOrder()).toList();
        return (2 * sides.get(0)) + (2 * sides.get(1)) + (length * width * height);
    }
}
