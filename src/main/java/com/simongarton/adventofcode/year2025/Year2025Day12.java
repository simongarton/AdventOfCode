package com.simongarton.adventofcode.year2025;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2025Day12 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 0: Template code";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2025, 12);
    }

    @Override
    public String part1(final String[] input) {

        final List<Shape> shapes = new ArrayList<>();
        int i = 0;
        while (true) {
            final String idLine = input[i];
            if (idLine.contains("x")) {
                break;
            }
            final int id = Integer.parseInt(idLine.trim().substring(0, idLine.trim().length() - 1));
            final String dataLine = input[i + 1] + input[i + 2] + input[i + 3];
            shapes.add(new Shape(id, dataLine));
            i += 5;
        }

        final List<Region> regions = new ArrayList<>();
        while (i < input.length) {
            regions.add(new Region(input[i]));
            i++;
        }

        for (final Shape shape : shapes) {
            shape.draw();
        }

        for (final Region region : regions) {
            System.out.println(region);
        }

        return String.valueOf(0);
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    public static enum Orientation {
        NORMAL,
        FLIP_HORIZONTAL,
        FLIP_VERTICAL,
        ROTATE_90,
        ROTATE_180,
        ROTATE_270
    }

    public static final class Shape {

        private final int id;
        private final String data;
        private final int[] bits;

        public Shape(final int id, final String data) {
            this.id = id;
            this.data = data;
            // data should be 9 chars long
            assert (data.length() == 9);
            this.bits = new int[9];
            for (int i = 0; i < 9; i++) {
                this.bits[i] = data.charAt(i) == '#' ? 1 : 0;
            }
        }

        private int getBit(final int x, final int y) {

            final int index = y * 3 + x;
            return this.bits[index];
        }

        public int getBit(final int x, final int y, final Orientation orientation) {

            final int[] newBits = this.getBits(orientation);
            final int index = y * 3 + x;
            return newBits[index];
        }

        public int[] getBits(final Orientation orientation) {

            // yes I should learn how to do matrices
            switch (orientation) {
                case NORMAL -> {
                    return this.bits;
                }
                case FLIP_HORIZONTAL -> {
                    final int[] newBits = new int[9];
                    newBits[0] = this.getBit(2, 0);
                    newBits[1] = this.getBit(1, 0);
                    newBits[2] = this.getBit(0, 0);
                    newBits[3] = this.getBit(2, 1);
                    newBits[4] = this.getBit(1, 1);
                    newBits[5] = this.getBit(0, 1);
                    newBits[6] = this.getBit(2, 2);
                    newBits[7] = this.getBit(1, 2);
                    newBits[8] = this.getBit(0, 2);
                    return newBits;
                }
                case FLIP_VERTICAL -> {
                    final int[] newBits = new int[9];
                    newBits[0] = this.getBit(0, 2);
                    newBits[1] = this.getBit(1, 2);
                    newBits[2] = this.getBit(2, 2);
                    newBits[3] = this.getBit(0, 1);
                    newBits[4] = this.getBit(1, 1);
                    newBits[5] = this.getBit(2, 1);
                    newBits[6] = this.getBit(0, 0);
                    newBits[7] = this.getBit(1, 0);
                    newBits[8] = this.getBit(2, 0);
                    return newBits;
                }
                case ROTATE_90 -> {
                    final int[] newBits = new int[9];
                    newBits[0] = this.getBit(0, 2);
                    newBits[1] = this.getBit(0, 1);
                    newBits[2] = this.getBit(0, 0);
                    newBits[3] = this.getBit(1, 2);
                    newBits[4] = this.getBit(1, 1);
                    newBits[5] = this.getBit(1, 0);
                    newBits[6] = this.getBit(2, 2);
                    newBits[7] = this.getBit(2, 1);
                    newBits[8] = this.getBit(2, 0);
                    return newBits;
                }
                case ROTATE_180 -> {
                    final int[] newBits = new int[9];
                    newBits[0] = this.getBit(2, 2);
                    newBits[1] = this.getBit(1, 2);
                    newBits[2] = this.getBit(0, 2);
                    newBits[3] = this.getBit(2, 1);
                    newBits[4] = this.getBit(1, 1);
                    newBits[5] = this.getBit(0, 1);
                    newBits[6] = this.getBit(0, 0);
                    newBits[7] = this.getBit(1, 0);
                    newBits[8] = this.getBit(2, 0);
                    return newBits;
                }
                case ROTATE_270 -> {
                    final int[] newBits = new int[9];
                    newBits[0] = this.getBit(2, 0);
                    newBits[1] = this.getBit(2, 1);
                    newBits[2] = this.getBit(2, 2);
                    newBits[3] = this.getBit(1, 0);
                    newBits[4] = this.getBit(1, 1);
                    newBits[5] = this.getBit(1, 2);
                    newBits[6] = this.getBit(0, 0);
                    newBits[7] = this.getBit(0, 1);
                    newBits[8] = this.getBit(0, 2);
                    return newBits;
                }
                default -> {
                    throw new UnsupportedOperationException(orientation.toString());
                }
            }
        }

        public void draw() {

            this.draw(Orientation.NORMAL);
        }

        public void draw(final Orientation orientation) {

            final List<String> lines = this.getLines(orientation);
            lines.stream().forEach(System.out::println);
            System.out.println("");
        }

        private List<String> getLines(final Orientation orientation) {

            final List<String> lines = new ArrayList<>();
            for (int y = 0; y < 3; y++) {
                String line = "";
                for (int x = 0; x < 3; x++) {
                    line = line + (this.getBit(x, y, orientation) == 1 ? "#" : ".");
                }
                lines.add(line);
            }
            return lines;
        }
    }

    public static final class Region {

        private final int width;
        private final int height;
        private final List<Integer> presents;

        public Region(final String line) {
            final String[] parts = line.split(":");
            final String[] dimensions = parts[0].split("x");
            this.width = Integer.parseInt(dimensions[0]);
            this.height = Integer.parseInt(dimensions[1]);
            final String[] presentList = parts[1].trim().split(" ");
            this.presents = Arrays.stream(presentList).map(Integer::parseInt).toList();
        }

        @Override
        public String toString() {
            return this.width + "x" + this.height + " (" + this.presents.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
        }
    }
}
