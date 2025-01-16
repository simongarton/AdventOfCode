package com.simongarton.adventofcode.year2016;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.HashMap;
import java.util.Map;

public class Year2016Day2 extends AdventOfCodeChallenge {

    private Map<String, Map<String, String>> moveMap;

    @Override

    public String title() {
        return "Day 2: Bathroom Security";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2016, 2);
    }

    @Override
    public String part1(final String[] input) {

        this.setupMap();
        String code = "";
        String position = "5";
        for (final String line : input) {
            for (int i = 0; i < line.length(); i++) {
                position = this.moveMap.get(position).get(line.substring(i, i + 1));
            }
            code += position;
        }
        return code;
    }

    private void setupMap() {

        this.moveMap = new HashMap<>();
        this.moveMap.put("1", Map.of(
                "U", "1",
                "D", "4",
                "L", "1",
                "R", "2")
        );
        this.moveMap.put("2", Map.of(
                "U", "2",
                "D", "5",
                "L", "1",
                "R", "3")
        );
        this.moveMap.put("3", Map.of(
                "U", "3",
                "D", "6",
                "L", "2",
                "R", "3")
        );
        this.moveMap.put("4", Map.of(
                "U", "1",
                "D", "7",
                "L", "4",
                "R", "5")
        );
        this.moveMap.put("5", Map.of(
                "U", "2",
                "D", "8",
                "L", "4",
                "R", "6")
        );
        this.moveMap.put("6", Map.of(
                "U", "3",
                "D", "9",
                "L", "5",
                "R", "6")
        );
        this.moveMap.put("7", Map.of(
                "U", "4",
                "D", "7",
                "L", "7",
                "R", "8")
        );
        this.moveMap.put("8", Map.of(
                "U", "5",
                "D", "8",
                "L", "7",
                "R", "9")
        );
        this.moveMap.put("9", Map.of(
                "U", "6",
                "D", "9",
                "L", "8",
                "R", "9")
        );
    }

    @Override
    public String part2(final String[] input) {

        this.setupMap2();
        String code = "";
        String position = "5";
        for (final String line : input) {
            for (int i = 0; i < line.length(); i++) {
                position = this.moveMap.get(position).get(line.substring(i, i + 1));
            }
            code += position;
        }
        return code;
    }

    private void setupMap2() {

        this.moveMap = new HashMap<>();
        this.moveMap.put("1", Map.of(
                "U", "1",
                "D", "3",
                "L", "1",
                "R", "1")
        );
        this.moveMap.put("2", Map.of(
                "U", "2",
                "D", "6",
                "L", "2",
                "R", "3")
        );
        this.moveMap.put("3", Map.of(
                "U", "1",
                "D", "7",
                "L", "2",
                "R", "4")
        );
        this.moveMap.put("4", Map.of(
                "U", "4",
                "D", "8",
                "L", "3",
                "R", "4")
        );
        this.moveMap.put("5", Map.of(
                "U", "5",
                "D", "5",
                "L", "5",
                "R", "6")
        );
        this.moveMap.put("6", Map.of(
                "U", "2",
                "D", "A",
                "L", "5",
                "R", "7")
        );
        this.moveMap.put("7", Map.of(
                "U", "3",
                "D", "B",
                "L", "6",
                "R", "8")
        );
        this.moveMap.put("8", Map.of(
                "U", "4",
                "D", "C",
                "L", "7",
                "R", "9")
        );
        this.moveMap.put("9", Map.of(
                "U", "9",
                "D", "9",
                "L", "8",
                "R", "9")
        );
        this.moveMap.put("A", Map.of(
                "U", "6",
                "D", "A",
                "L", "A",
                "R", "B")
        );
        this.moveMap.put("B", Map.of(
                "U", "7",
                "D", "D",
                "L", "A",
                "R", "C")
        );
        this.moveMap.put("C", Map.of(
                "U", "8",
                "D", "C",
                "L", "B",
                "R", "C")
        );
        this.moveMap.put("D", Map.of(
                "U", "B",
                "D", "D",
                "L", "D",
                "R", "D")
        );
    }
}
