package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Year2022Day13 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = true;

    @Override
    public boolean run() {
        return this.runChallenge(2022, 13);
    }

    @Override
    public String part1(final String[] input) {
        this.loadPackets(input);
        return null;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    private void loadPackets(final String[] input) {
        final Iterator<String> iterator = Arrays.stream(input).iterator();
        while (iterator.hasNext()) {
            final String packet1 = iterator.next();
            final String packet2 = iterator.next();
            if (iterator.hasNext()) {
                final String blankLine = iterator.next();
            }
            final Item item1 = this.parseItem(packet1);
            System.out.println(packet1);
            item1.displayOnTerminal(0);
            final Item item2 = this.parseItem(packet2);
            System.out.println(packet2);
            item2.displayOnTerminal(0);
        }
    }

    private void debugPrint(final String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

    private Item parseItem(final String string) {
        final Item recursedOneTooManyTimesItem = this.parseItem(string, null);
        if (recursedOneTooManyTimesItem.items.size() == 0) {
            final Item item = new Item();
            item.items = new ArrayList<>();
            return item;
        }
        return recursedOneTooManyTimesItem.items.get(0);
    }

    private Item parseItem(final String string, final Item parent) {
        this.debugPrint("parsing '" + string + "'");
        final Item item = new Item();
        item.items = new ArrayList<>();
        item.parent = parent;

        if (!(string.contains(",") || string.contains("["))) {
            this.debugPrint("just got '" + string + "' so must be an integer or empty");
            if (string.length() > 0) {
                item.value = Integer.parseInt(string);
            }
            this.debugPrint("returning " + item);
            return item;
        }
        this.debugPrint("got '" + string + "' so must be an item");

        int insideLists = 0;
        String workingString = "";
        for (int index = 0; index < string.length(); index++) {
            final char current = string.charAt(index);
            if (current == '[') {
                if (insideLists > 0) {
                    workingString = workingString + current;
                }
                insideLists++;
                this.debugPrint(index + ":" + current + " inside list " + insideLists);
                continue;
            }
            if (current == ']') {
                insideLists--;
                if (insideLists > 0) {
                    workingString = workingString + current;
                }
                this.debugPrint(index + ":" + current + " outside list " + insideLists);
                if (insideLists == 0) {
                    final Item childItem = this.parseItem(workingString, item);
                    if (childItem.isValid()) {
                        item.items.add(childItem);
                    }
                    this.debugPrint(index + ":" + current + " adding item " + childItem);
                    workingString = "";
                    this.debugPrint(index + ":" + current + " resetting workingString ]");
                }
                continue;
            }
            if (current == ',') {
                if (insideLists > 0) {
                    workingString = workingString + current;
                    this.debugPrint(index + ":" + current + " updating workingString " + workingString);
                    continue;
                }
                final Item childItem = this.parseItem(workingString, item);
                if (childItem.isValid()) {
                    item.items.add(childItem);
                }
                this.debugPrint(index + ":" + current + " adding item " + childItem);
                workingString = "";
                this.debugPrint(index + ":" + current + " resetting workingString ,");
                continue;
            }
            workingString = workingString + current;
            this.debugPrint(index + ":" + current + " updating workingString " + workingString);
        }
        if (workingString.length() > 0) {
            final Item childItem = this.parseItem(workingString, item);
            item.items.add(childItem);
            this.debugPrint(" adding end item " + childItem);
        }
        return item;
    }

    public static final class Item {

        Item parent;
        List<Item> items;
        Integer value;

        @Override
        public String toString() {
            return "Item of value " + this.value + " with " + this.items.size() + " child items"; // and parent " + this.parent;
        }

        public boolean isValid() {
            return this.items.size() > 0 || this.value != null;
        }

        public void displayOnTerminal(final int indent) {
            System.out.println(" ".repeat(indent) + this.toString());
            for (final Item item : this.items) {
                item.displayOnTerminal(indent + 2);
            }
        }
    }
}
