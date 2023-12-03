package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Year2023Day3 extends AdventOfCodeChallenge {

    private List<Symbol> symbols;
    private List<Number> numbers;

    // this looks inefficient, but it was a copy/paste from Day 1. And then I had to add zero.
    private static final Map<String, String> NUMBER_MAP = new LinkedHashMap<>();

    static {
        NUMBER_MAP.put("zero", "0");
        NUMBER_MAP.put("one", "1");
        NUMBER_MAP.put("two", "2");
        NUMBER_MAP.put("three", "3");
        NUMBER_MAP.put("four", "4");
        NUMBER_MAP.put("five", "5");
        NUMBER_MAP.put("six", "6");
        NUMBER_MAP.put("seven", "7");
        NUMBER_MAP.put("eight", "8");
        NUMBER_MAP.put("nine", "9");
    }

    @Override
    public String title() {
        return "Day 3: Gear Ratios";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 3);
    }

    @Override
    public String part1(final String[] input) {

        this.buildLists(input);

        long total = 0;
        for (final Number number : this.numbers) {
            if (this.isEnginePart(number)) {
                total = total + Long.valueOf(number.getNumber());
            }
        }

        return String.valueOf(total);
    }

    private void buildLists(final String[] input) {

        this.symbols = new ArrayList<>();
        this.numbers = new ArrayList<>();

        int row = 0;
        for (final String line : input) {
            this.symbols.addAll(this.findSymbols(line, row));
            this.numbers.addAll(this.findNumbers(line, row));
            row = row + 1;
        }
    }

    private boolean isEnginePart(final Number number) {

        for (final Coord coord : number.coords) {
            if (this.nextToSymbol(coord)) {
                return true;
            }
        }
        return false;
    }

    private boolean nextToSymbol(final Coord coord) {

        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (this.reallyNextToSymbol(new Coord(coord.getX() + x, coord.getY() + y))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean reallyNextToSymbol(final Coord coord) {

        for (final Symbol symbol : this.symbols) {
            if (symbol.getCoord().getX() == coord.getX()) {
                if (symbol.getCoord().getY() == coord.getY()) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Number> findNumbers(final String line, final int row) {
        final List<Number> numbers = new ArrayList<>();
        List<Coord> coords = new ArrayList<>();
        String currentNumber = null;
        boolean inNumber = false;
        for (int col = 0; col < line.length(); col++) {
            final String c = line.substring(col, col + 1);
            if (!this.isNumeric(c)) {
                if (inNumber) {
                    final Number number = Number.builder()
                            .number(currentNumber)
                            .coords(coords)
                            .build();
                    numbers.add(number);
                    inNumber = false;
                    continue; // I'm done with this number
                }
                continue; // I'm not in a number, and I'm not numeric
            }
            if (!inNumber) {
                coords = new ArrayList<>();
                currentNumber = "";
            }
            inNumber = true;
            coords.add(new Coord(row, col));
            currentNumber = currentNumber + c;
        }
        // don't forget the last one
        if (inNumber) {
            final Number number = Number.builder()
                    .number(currentNumber)
                    .coords(coords)
                    .build();
            numbers.add(number);
        }
        return numbers;
    }

    private List<Symbol> findSymbols(final String line, final int row) {

        final List<Symbol> symbols = new ArrayList<>();
        for (int col = 0; col < line.length(); col++) {
            final String c = line.substring(col, col + 1);
            if (this.isNumeric(c) || c.equalsIgnoreCase(".")) {
                continue;
            }
            symbols.add(Symbol.builder()
                    .symbol(c)
                    .coord(new Coord(row, col))
                    .build());
        }
        return symbols;
    }

    private boolean isNumeric(final String substring) {
        return NUMBER_MAP.containsValue(substring);
    }

    @Override
    public String part2(final String[] input) {

        this.buildLists(input);

        final List<Symbol> gears = this.symbols.stream()
                .filter(s -> s.symbol.equalsIgnoreCase("*"))
                .collect(Collectors.toList());

        long total = 0;
        for (final Symbol gear : gears) {
            final List<Number> pair = this.findPair(gear);
            if (pair.size() == 2) {
                total = total + (Long.parseLong(pair.get(0).getNumber()) * Long.parseLong(pair.get(1).getNumber()));
            }
        }

        return String.valueOf(total);
    }

    private List<Number> findPair(final Symbol gear) {

        final List<Number> numbers = new ArrayList<>();
        for (final Number number : this.numbers) {
            for (final Coord coord : number.getCoords()) {
                if (this.nextToMe(coord, gear.coord)) {
                    if (!numbers.contains(number)) {
                        numbers.add(number);
                    }
                }
            }
        }
        return numbers;
    }

    private boolean nextToMe(final Coord from, final Coord to) {

        // not dissimilar to previous methods so refactor
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (from.x == (to.x + x)) {
                    if (from.y == (to.y + y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Data
    @AllArgsConstructor
    private static class Coord {
        private int x;
        private int y;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class Symbol {

        private String symbol;
        private Coord coord;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class Number {

        private String number;
        private int length;
        private List<Coord> coords;
    }

}
