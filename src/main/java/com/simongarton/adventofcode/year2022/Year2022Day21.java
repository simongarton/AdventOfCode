package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2022Day21 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;

    @Override
    public String title() {
        return "Day 21: Monkey Math";
    }

    @Override
    public boolean run() {
        return this.runChallenge(2022, 21);
    }

    @Override
    public String part1(final String[] input) {
        final List<Monkey> troop = this.loadMonkeys(input);
        troop.stream().forEach(m -> m.addDependencies(troop));
        final Monkey root = troop.stream().filter(m -> m.name.equalsIgnoreCase("root"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Didn't find root"));
        int iteration = 0;
        while (!root.hasYelled) {
            this.debugPrint(String.format("\niteration %s\n\n", iteration));
            troop.stream().forEach(Monkey::lookAroundAndYell);
            iteration++;
        }
        this.debugPrint("\n");
        return String.valueOf(root.numberYelled);
    }

    @Override
    public String part2(final String[] input) {
        final long val = 3373767893067L;
        for (int i = 1; i < 1000; i++) {
            final List<Monkey> troop = this.loadMonkeys(input);
            troop.stream().forEach(m -> m.addDependencies(troop));
            final Monkey root = troop.stream().filter(m -> m.name.equalsIgnoreCase("root"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Didn't find root"));
            final Monkey humn = troop.stream().filter(m -> m.name.equalsIgnoreCase("humn"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Didn't find humn"));
            humn.operation = String.valueOf(val);
            humn.numberYelled = val;
            root.operation = root.operation.replace("+", "=");
            int iteration = 0;
            while (!root.hasYelled) {
                this.debugPrint(String.format("\niteration %s\n\n", iteration));
                troop.stream().forEach(Monkey::lookAroundAndYell);
                iteration++;
            }
        }
        this.debugPrint("\n");
        return "";
    }

    private void debugPrint(final String s) {
        if (DEBUG) {
            System.out.printf(s);
        }
    }

    private List<Monkey> loadMonkeys(final String[] input) {
        return Arrays.stream(input).map(Monkey::new).collect(Collectors.toList());
    }

    public static final class Monkey {

        String name;
        boolean hasYelled = false;
        long numberYelled;
        String calculation;
        String operation;
        List<Monkey> dependencies;

        public Monkey(final String line) {
            final String[] parts = line.split(":");
            this.name = parts[0].trim();
            this.calculation = parts[1].trim();
        }

        public int addDependencies(final List<Monkey> troop) {
            this.dependencies = new ArrayList<>();
            if (this.calculation.contains(" ")) {
                final String[] parts = this.calculation.split(" ");
                this.operation = parts[1];
                this.dependencies.add(troop.stream()
                        .filter(m -> m.name.equalsIgnoreCase(parts[0]))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Didn't find " + parts[0])));
                this.dependencies.add(troop.stream()
                        .filter(m -> m.name.equalsIgnoreCase(parts[2]))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Didn't find " + parts[2])));
            } else {
                this.numberYelled = Long.parseLong(this.calculation);
                return 0;
            }
            if (this.dependencies.size() != 2) {
                throw new RuntimeException("Bad dependencies " + this.dependencies.size() + " for " + this.calculation);
            }
            return this.dependencies.size();
        }

        public void lookAroundAndYell() {
            if (this.hasYelled) {
                return;
            }
            if (this.numberYelled > 0) {
                this.debugPrint(String.format("%s yells out %s\n", this.name, this.numberYelled));
                this.hasYelled = true;
                return;
            }
            if (!(this.dependencies.get(0).hasYelled && this.dependencies.get(1).hasYelled)) {
                return;
            }
            final long value1 = this.dependencies.get(0).numberYelled;
            final long value2 = this.dependencies.get(1).numberYelled;
            switch (this.operation) {
                case "/":
                    this.numberYelled = value1 / value2;
                    break;
                case "+":
                    this.numberYelled = value1 + value2;
                    break;
                case "-":
                    this.numberYelled = value1 - value2;
                    break;
                case "*":
                    this.numberYelled = value1 * value2;
                    break;
                case "=":
                    this.numberYelled = 0;
                    System.out.printf("equalling %s - %s = %s\n", value1, value2, value1 - value2);
                    break;
                default:
                    throw new RuntimeException("Bad operation " + this.operation);
            }
            this.debugPrint(String.format("%s yells out %s\n", this.name, this.numberYelled));
            this.hasYelled = true;
        }

        private void debugPrint(final String s) {
            if (DEBUG) {
                System.out.printf(s);
            }
        }

    }
}
