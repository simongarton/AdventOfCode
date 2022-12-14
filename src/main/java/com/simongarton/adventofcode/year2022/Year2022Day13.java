package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Year2022Day13 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;
    private static final boolean COMPARE_DEBUG = true;

    @Override
    public boolean run() {
        return this.runChallenge(2022, 13);
    }

    @Override
    public String part1(final String[] input) {
        final List<ItemPair> pairs = this.loadPackets(input);
        int pairsInOrder = 0;
        for (int i = 0; i < pairs.size(); i++) {
            if (this.inOrder(pairs.get(i))) {
                pairsInOrder += (i + 1);
            }
        }
        return String.valueOf(pairsInOrder);
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    private boolean inOrder(final ItemPair itemPair) {
        final Item left = itemPair.item1;
        final Item right = itemPair.item2;
        this.compareDebugPrint(0, "Comparing " + left + " and " + right);
        try {
            return this.itemsInOrder(left, right, 2);
        } catch (final OutOfOrderException e) {
            System.out.println(e);
            return false;
        }
    }

    private boolean itemsInOrder(final Item left, final Item right, final int indentLevel) {
        final int maxItems = Math.max(left.items.size(), right.items.size());
        for (int index = 0; index < maxItems; index++) {
            final Item leftChild = left.items.get(index);
            final Item rightChild = left.items.get(index);
            this.compareDebugPrint(indentLevel, "Comparing " + leftChild + " and " + rightChild);
            if (leftChild.value != null && rightChild.value != null) {
                if (leftChild.value > rightChild.value) {
                    throw new OutOfOrderException("Out of order at item " + index + " with values out of order.");
                }
                continue;
            }
        }
        return true;
    }

    private List<ItemPair> loadPackets(final String[] input) {
        final Iterator<String> iterator = Arrays.stream(input).iterator();
        final List<ItemPair> itemPairs = new ArrayList<>();
        while (iterator.hasNext()) {
            final String packet1 = iterator.next();
            final String packet2 = iterator.next();
            if (iterator.hasNext()) {
                final String blankLine = iterator.next();
            }
            final Item item1 = this.parseItemNew(packet1, null);
            if (DEBUG) {
                System.out.println(packet1);
                item1.displayOnTerminal(0);
            }
            final Item item2 = this.parseItemNew(packet2, null);
            if (DEBUG) {
                System.out.println(packet2);
                item2.displayOnTerminal(0);
            }
            itemPairs.add(new ItemPair(item1, item2));
        }
        return itemPairs;
    }

    private void debugPrint(final String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

    private void compareDebugPrint(final int indentLevel, final String s) {
        if (COMPARE_DEBUG) {
            System.out.println(" ".repeat(indentLevel) + s);
        }
    }

    private Item parseItemNew(final String string, final Item parent) {
        this.debugPrint("parsing '" + string + "'");
        final Item item = new Item();
        item.items = new ArrayList<>();
        item.parent = parent;

        // take a look at the string. The first will always have [] around it, and then will have either values 1,2,3 or other lists.
        // regardless of how deep it goes can I iterate over this top level and return each thing as an item to belong to this parent
        // of course, I then need to take any lists and turn these into items

        int index = 1;
        StringBuilder outerWorking = new StringBuilder();
        while (index < string.length() - 1) {
            char current = string.charAt(index);
            if (current == '[') {
                int nesting = 1;
                String working = "" + current;
                while (true) {
                    index++;
                    current = string.charAt(index);
                    working = working + current;
                    if (current == '[') {
                        nesting++;
                    }
                    if (current == ']') {
                        nesting--;
                    }
                    if (nesting == 0) {
                        break;
                    }
                }
                final Item child = this.parseItemNew(working, item);
                item.items.add(child);
                outerWorking = new StringBuilder();
                continue;
            }
            if (current == ',' || current == ']') {
                if (outerWorking.length() > 0) {
                    final Item child = this.itemFromInteger(outerWorking.toString(), item);
                    item.items.add(child);
                    outerWorking = new StringBuilder();
                    continue;
                }
                index++;
                continue;
            }
            outerWorking.append(current);
            index++;
        }
        if (outerWorking.length() > 0) {
            final Item child = this.itemFromInteger(outerWorking.toString(), item);
            item.items.add(child);
        }
        return item;
    }

    private Item itemFromInteger(final String integerValue, final Item parent) {
        final Item item = new Item();
        item.parent = parent;
        item.items = new ArrayList<>();
        item.value = Integer.parseInt(integerValue);
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

    public static final class ItemPair {

        private final Item item1;
        private final Item item2;

        public ItemPair(final Item item1, final Item item2) {
            this.item1 = item1;
            this.item2 = item2;
        }
    }

    public static final class OutOfOrderException extends RuntimeException {
        private static final long serialVersionUID = -2028560193758538554L;

        public OutOfOrderException(final String message) {
            super(message);
        }
    }
}
