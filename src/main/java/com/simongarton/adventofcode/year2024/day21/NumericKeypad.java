package com.simongarton.adventofcode.year2024.day21;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.simongarton.adventofcode.year2024.Year2024Day21FiguringOutProblem.ACTIVATE;

public class NumericKeypad extends Keypad {

    public NumericKeypad(final String name) {

        super(name);
    }

    @Override
    public Map<String, String> getPositionsForUp() {

        return Map.of(
                "0", "2",
                ACTIVATE, "3",
                "1", "4",
                "2", "5",
                "3", "6",
                "4", "7",
                "5", "8",
                "6", "9"
        );
    }

    @Override
    public Map<String, String> getPositionsForDown() {

        return Map.of(
                "2", "0",
                "3", ACTIVATE,
                "4", "1",
                "5", "2",
                "6", "3",
                "7", "4",
                "8", "5",
                "9", "6"
        );
    }

    @Override
    public Map<String, String> getPositionsForRight() {

        return Map.of(
                "0", ACTIVATE,
                "1", "2",
                "2", "3",
                "4", "5",
                "5", "6",
                "7", "8",
                "8", "9"
        );
    }

    @Override
    public Map<String, String> getPositionsForLeft() {

        return Map.of(
                ACTIVATE, "0",
                "2", "1",
                "3", "2",
                "5", "4",
                "6", "5",
                "8", "7",
                "9", "8"
        );
    }

    @Override
    public List<String> damnItIllDoItMyself(final String commandNeeded, final Map<Keypad, String> status) {

        throw new RuntimeException("you're joking.");
    }

    @Override
    int rowForKey(final String key) {
        final Map<String, Integer> data = new HashMap<>();
        data.put("1", 0);
        data.put("2", 0);
        data.put("3", 0);
        data.put("4", 1);
        data.put("5", 1);
        data.put("6", 1);
        data.put("7", 2);
        data.put("8", 2);
        data.put("9", 2);
        data.put("0", 3);
        data.put("A", 3);
        return data.get(key);
    }

    @Override
    int colForKey(final String key) {
        final Map<String, Integer> data = new HashMap<>();
        data.put("1", 0);
        data.put("2", 1);
        data.put("3", 2);
        data.put("4", 0);
        data.put("5", 1);
        data.put("6", 2);
        data.put("7", 0);
        data.put("8", 1);
        data.put("9", 2);
        data.put("0", 1);
        data.put("A", 2);
        return data.get(key);
    }
}
