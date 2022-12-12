package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;
import java.util.stream.Collectors;

public class Year2022Day11 extends AdventOfCodeChallenge {

    private List<Monkey> troop;

    private static final boolean DEBUG = false;
    private static final boolean SEMI_DEBUG = true;

    @Override
    public String title() {
        return "Day 11: Monkey in the Middle";
    }

    @Override
    public boolean run() {
        return this.runChallenge(2022, 11);
    }

    @Override
    public String part1(final String[] input) {
        this.loadTroop(input);
        for (int round = 1; round <= 20; round++) {
            for (final Monkey monkey : this.troop) {
                this.debugPrint(String.format("Monkey %s:", monkey.id));
                monkey.inspectAndThrowItems();
            }
            this.semiDebugPrint(String.format("After round %s, the monkeys are holding items with these worry levels.", round));
            for (final Monkey monkey : this.troop) {
                this.semiDebugPrint(monkey.itemList());
            }
        }
        final List<Integer> inspectionCounts = new ArrayList<>();
        for (final Monkey monkey : this.troop) {
            this.semiDebugPrint(String.format("Monkey %s inspected items %s times.", monkey.id, monkey.inspectionsMade));
            inspectionCounts.add(monkey.inspectionsMade);
        }
        inspectionCounts.sort(Comparator.comparing(Integer::intValue).reversed());
        return String.valueOf(inspectionCounts.get(0) * inspectionCounts.get(1));
    }

    private void debugPrint(final String line) {
        if (DEBUG) {
            System.out.println(line);
        }
    }

    private void semiDebugPrint(final String line) {
        if (SEMI_DEBUG) {
            System.out.println(line);
        }
    }

    private void loadTroop(final String[] input) {
        this.troop = new ArrayList<>();
        final Iterator<String> iterator = Arrays.asList(input).listIterator();
        while (iterator.hasNext()) {
            final String monkeyIdString = iterator.next();
            final String startingItemString = iterator.next();
            final String operationString = iterator.next();
            final String testString = iterator.next();
            final String monkeyTrueString = iterator.next();
            final String monkeyFalseString = iterator.next();
            if (iterator.hasNext()) {
                final String blankLine = iterator.next();
            }
            final int monkeyId = this.getMonkeyId(monkeyIdString);
            final List<Integer> startingItems = this.getStartingItems(startingItemString);
            final String operation = this.getOperation(operationString);
            final int divisible = this.getDivisible(testString);
            final int monkeyTrue = this.getMonkeyThrow(monkeyTrueString);
            final int monkeyFalse = this.getMonkeyThrow(monkeyFalseString);
            final Monkey monkey = new Monkey(monkeyId,
                    this.troop,
                    startingItems,
                    operation,
                    divisible,
                    monkeyTrue,
                    monkeyFalse
            );
            this.troop.add(monkey);
        }
    }

    private int getMonkeyThrow(final String monkeyString) {
        final String[] parts = monkeyString.split(":");
        final String[] things = parts[1].trim().split(" ");
        return Integer.parseInt(things[3]);
    }

    private int getDivisible(final String testString) {
        final String[] parts = testString.split(":");
        final String[] things = parts[1].trim().split(" ");
        return Integer.parseInt(things[2]);
    }

    private String getOperation(final String operationString) {
        final String[] parts = operationString.split(":");
        return parts[1].trim();
    }

    private List<Integer> getStartingItems(final String startingItemString) {
        final String[] parts = startingItemString.split(":");
        final String[] items = parts[1].split(",");
        final List<Integer> itemList = new ArrayList<>();
        for (final String item : items) {
            itemList.add(Integer.parseInt(item.trim()));
        }
        return itemList;
    }

    private int getMonkeyId(final String monkeyIdString) {
        final String[] parts = monkeyIdString.split(" ");
        return Integer.parseInt(parts[1].replace(":", ""));
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    public static final class Monkey {

        private final List<Monkey> troop;
        private final Integer id;
        private final List<Integer> items;
        private final String operation;
        private final Integer divisible;
        private final Integer monkeyTrue;
        private final Integer monkeyFalse;
        private int inspectionsMade;

        public Monkey(final int monkeyId,
                      final List<Monkey> troop,
                      final List<Integer> startingItems,
                      final String operation,
                      final int divisible,
                      final int monkeyTrue,
                      final int monkeyFalse) {
            this.id = monkeyId;
            this.troop = troop;
            this.items = new ArrayList<>(startingItems);
            this.operation = operation;
            this.divisible = divisible;
            this.monkeyTrue = monkeyTrue;
            this.monkeyFalse = monkeyFalse;
            this.inspectionsMade = 0;
        }

        public void inspectAndThrowItems() {
            final int size = this.items.size();
            for (int itemCount = 0; itemCount < size; itemCount++) {
                this.debugPrint(String.format("  Monkey inspects an item with a worry level of %s.", this.items.get(0)));
                this.inspectAndThrowItem();
                this.inspectionsMade++;
            }
        }

        private void inspectAndThrowItem() {
            // we're always looking at the first item
            final int newValue = this.inspectItem();
            final int reallyNewValue = this.reliefItsNotDamaged(newValue);
            this.throwItem(reallyNewValue);
        }

        private int reliefItsNotDamaged(final int oldValue) {
            final int newValue = (int) Math.floor(oldValue / 3.0);
            this.debugPrint(String.format("    Monkey gets bored with item. Worry level is divided by 3 to %s.", newValue));
            return newValue;
        }

        private void throwItem(final int reallyNewValue) {
            // decide
            if (reallyNewValue % this.divisible == 0) {
                this.troop.get(this.monkeyTrue).items.add(reallyNewValue);
                this.debugPrint(String.format("    Current worry level is divisible by %s.", this.divisible));
                this.debugPrint(String.format("    Item with worry level %s is thrown to monkey %s.", reallyNewValue, this.troop.get(this.monkeyTrue).id));
            } else {
                this.debugPrint(String.format("    Current worry level is not divisible by %s.", this.divisible));
                this.debugPrint(String.format("    Item with worry level %s is thrown to monkey %s.", reallyNewValue, this.troop.get(this.monkeyFalse).id));
                this.troop.get(this.monkeyFalse).items.add(reallyNewValue);
            }

            // and empty
            this.items.remove(0);
        }

        private int inspectItem() {
            final String thisOperation = this.operation.replace("old", String.valueOf(this.items.get(0)));
            final String[] sides = thisOperation.split("=");
            final String[] right = sides[1].trim().split(" ");
            final int operand1 = Integer.parseInt(right[0]);
            final int operand2 = Integer.parseInt(right[2]);
            final String operator = right[1];
            int outcome = 0;
            switch (operator) {
                case "+":
                    outcome = operand1 + operand2;
                    break;
                case "*":
                    outcome = operand1 * operand2;
                    this.debugPrint(String.format("    Worry level is multiplied by %s to %s.", operand2, outcome));
                    break;
                default:
                    throw new RuntimeException("Unhandled operator " + operator);
            }
            return outcome;
        }

        private void debugPrint(final String line) {
            if (DEBUG) {
                System.out.println(line);
            }
        }

        public String itemList() {
            return "Monkey " + this.id + ": " + this.items.stream().map(String::valueOf).collect(Collectors.joining(", "));
        }
    }
}
