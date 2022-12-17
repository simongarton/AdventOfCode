package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.simongarton.adventofcode.year2022.Year2022Day13.Outcome.KEEP_CHECKING;

public class Year2022Day13 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;
    private final boolean COMPARE_DEBUG = true;

    public enum Outcome {
        IN_ORDER,
        OUT_OF_ORDER,
        KEEP_CHECKING
    }

    @Override
    public boolean run() {
        return this.runChallenge(2022, 13);
    }

    @Override
    public String part1(final String[] input) {
        final List<ItemPair> pairs = this.loadPackets(input);
        int pairsInOrder = 0;
        int notInOrder = 0;
        int countPairsInOrder = 0;
        for (int i = 0; i < pairs.size(); i++) {
            final boolean inOrder = this.inOrder(i, pairs.get(i));
            if (!inOrder) {
                notInOrder++;
            } else {
                countPairsInOrder++;
                pairsInOrder += (i + 1);
            }

            if (this.COMPARE_DEBUG) {
                System.out.println("\npair " + (i + 1) + " is" + this.isFalse(inOrder) + "in order.\n");
            }
        }
        System.out.println("total in order " + countPairsInOrder + " not in order : " + notInOrder);
        return String.valueOf(pairsInOrder);
    }

    private String isFalse(final boolean inOrder) {
        return inOrder ? " " : " not ";
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    private boolean inOrder(final int index, final ItemPair itemPair) {
        final Item left = itemPair.item1;
        final Item right = itemPair.item2;
        this.compareDebugPrint(0, "Comparing pair " + (index + 1) + "...\n" + left.source + "\n" + right.source);
        try {
            this.itemsInOrder(left, right, 2);
        } catch (final OutOfOrderException e) {
            if (this.COMPARE_DEBUG) {
                System.out.println(e.getMessage());
            }
            return false;
        } catch (final InOrderException e) {
            if (this.COMPARE_DEBUG) {
                System.out.println(e.getMessage());
            }
            return true;
        }
        throw new RuntimeException("No answer ?");
    }

    private void itemsInOrder(final Item left, final Item right, final int indentLevel) {
        // we have three cases :
        // 1. They can both be values, in which case it's trivial.
        // 2. They can both be lists, in which case we work pairwise through the lists. if left runs out first, in order; if
        //  right runs out first, out of order; if same, check next input
        // 3. One is a value, one is a list. Convert the value to a list and repeat
        this.compareDebugPrint(indentLevel, " ".repeat(indentLevel) + String.format("%s %s", left, right));
        // this now validated on round #17
        if ((left.value != null) && (right.value != null)) {
            this.valueItemsInOrder(left, right, indentLevel);
            return;
        }
        if ((left.items.size() > 0) && (right.items.size() > 0)) {
            this.listItemsInOrder(left, right, indentLevel);
            return;
        }
        if (left.value != null) {
            final Item newValue = this.convertValueToList(left);
            this.listItemsInOrder(newValue, right, indentLevel);
            return;
        }
        if (right.value != null) {
            final Item newValue = this.convertValueToList(right);
            this.listItemsInOrder(left, newValue, indentLevel);
            return;
        }
        if (left.items.size() == 0 && right.items.size() > 0) {
            throw new InOrderException("left no items, right has");
        }
        if (left.items.size() > 0 && right.items.size() == 0) {
            throw new OutOfOrderException("left has items, right no");
        }
        // I think I am now two empty lists
    }

    private void listItemsInOrder(final Item left, final Item right, final int indentLevel) {
        final int itemCount = Math.max(left.items.size(), right.items.size());
        for (int i = 0; i < itemCount; i++) {
            if (i >= left.items.size()) {
                this.compareDebugPrint(indentLevel, "Left side ran out of items, so inputs are in the right order");
                throw new InOrderException("Left side ran out of items, so inputs are in the right order");
            }
            if (i >= right.items.size()) {
                this.compareDebugPrint(indentLevel, "Right side ran out of items, so inputs are not in the right order");
                throw new OutOfOrderException("Right side ran out of items, so inputs are not in the right order");
            }
            this.itemsInOrder(left.items.get(i), right.items.get(i), indentLevel + 1);
        }
    }

    private Outcome valueItemsInOrder(final Item left, final Item right, final int indentLevel) {
        final int leftValue = left.value;
        final int rightValue = right.value;
        if (leftValue < rightValue) {
            this.compareDebugPrint(indentLevel, "left < right, in order");
            throw new InOrderException("left < right, in order");
//            return IN_ORDER;
        }
        if (leftValue == rightValue) {
            this.compareDebugPrint(indentLevel, "left = right, keep checking");
            return KEEP_CHECKING;
        }
        this.compareDebugPrint(indentLevel, "left > right, out of order");
        throw new OutOfOrderException("left > right, out of order");
    }

    private Item convertValueToList(final Item valueItem) {
        // I may not have a value - in which case I'm just an empty list
        if (valueItem.value == null) {
            throw new RuntimeException("whoops");
        }
        final Item item = new Item("*" + valueItem.source);
        item.parent = valueItem.parent;
        item.items = new ArrayList<>();
        item.items.add(this.itemFromInteger(String.valueOf(valueItem.value), item));
        return item;
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
        if (this.COMPARE_DEBUG) {
            System.out.println(" ".repeat(indentLevel) + s);
        }
    }

    private Item parseItemNew(final String string, final Item parent) {
        this.debugPrint("parsing '" + string + "'");
        final Item item = new Item(string);
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
        final Item item = new Item(String.valueOf(integerValue));
        item.parent = parent;
        item.items = new ArrayList<>();
        item.value = Integer.parseInt(integerValue);
        return item;
    }

    public static final class Item {

        private final String source;
        private Item parent;
        private List<Item> items;
        private Integer value;

        public Item(final String source) {
            this.source = source;
        }

        @Override
        public String toString() {
            if (this.value != null) {
                return String.valueOf(this.value);
            }
            return this.displayItems();
        }

        private String displayItems() {
            String line = "[";
            for (final Item item : this.items) {
                if (item.value != null) {
                    line += item.value + ",";
                    continue;
                }
                if (item.items.isEmpty()) {
                    line += "[],";
                } else {
                    line += "[*],";
                }
            }
            if (line.contains(",")) {
                line = line.substring(0, line.length() - 1);
            }
            line = line + "]";
            return line;
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

    public static final class InOrderException extends RuntimeException {

        public InOrderException(final String message) {
            super(message);
        }
    }
}
