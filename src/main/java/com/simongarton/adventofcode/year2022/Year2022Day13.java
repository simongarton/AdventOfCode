package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/*

There are 150 pairs in the main sample. I get 43 in order, 107 not in order, giving 3249 as the result -
which is too low.

The sample file works correctly, I get 13 (1,2 4 and 6 in order).

We've only got 3 basic rules; they seem to be working correctly.

Either I'm assembling the lists incorrectly (unlikely, no errors) or my logic is wrong.

I did notice

[[1,2,3,[4]],4]
[[1,2]

This should fail - it's not a valid second list - but doesn't.

Abandoning now, it's been 3 days. 16th Dec, 3am.

OK I pass this - it should fail

[[8,[[7]]]]
[[[[8]]]]

Also

[[8,[[7,10,10,5],[8,4,9]],3,5],[[[3,9,4],5,[7,5,5]],[[3,2,5],[10],[5,5],0,[8]]],[4,2,[],[[7,5,6,3,0],[4,4,10,7],6,[8,10,9]]],[[4,[],4],10,1]]
[[[[8],[3,10],[7,6,3,7,4],1,8]]]

shuld pass, and fails.

https://www.reddit.com/r/adventofcode/comments/zm20vb/2022_day_13_part_1_i_think_i_got_it_right_but_i/

 */

public class Year2022Day13 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = true;
    private boolean COMPARE_DEBUG = true;

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
            this.COMPARE_DEBUG = true;
            final boolean inOrder = this.inOrder(i, pairs.get(i));
            if (!inOrder) {
                this.COMPARE_DEBUG = true;
//                this.inOrder(i, pairs.get(i));
                notInOrder++;
            } else {
                countPairsInOrder++;
            }

            if (this.COMPARE_DEBUG) {
                System.out.println("\npair " + (i + 1) + " is" + this.isFalse(inOrder) + "in order.\n");
            }
            if (inOrder) {
                pairsInOrder += (i + 1);
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
            return this.itemsInOrder(left, right, 2);
        } catch (final OutOfOrderException e) {
            if (this.COMPARE_DEBUG) {
                System.out.println(e.getMessage());
            }
            return false;
        }
    }

    private boolean itemsInOrder(final Item left, final Item right, final int indentLevel) {
        this.compareDebugPrint(indentLevel, "Comparing " + left + " (" + left.items.size() + ") and " + right + " (" + right.items.size() + ")");
        final int maxItems = Math.max(left.items.size(), right.items.size());
        for (int index = 0; index < maxItems; index++) {
            if (left.items.size() <= index) {
                return true;
            }
            if (right.items.size() <= index) {
                throw new OutOfOrderException("Out of order at item " + index + " with right having no more items.");
            }
            final Item leftChild = left.items.get(index);
            final Item rightChild = right.items.get(index);
            this.compareDebugPrint(indentLevel, "Comparing children " + leftChild + " and " + rightChild);
            // compare two values
            if (leftChild.value != null && rightChild.value != null) {
                if (leftChild.value > rightChild.value) {
                    throw new OutOfOrderException("Out of order at item " + index + " with values out of order.");
                }
                if (leftChild.value < rightChild.value) {
                    return true;
                }
                continue;
            }
            // compare two lists
            if (leftChild.items.size() > 0 && rightChild.items.size() > 0) {
                this.itemsInOrder(leftChild, rightChild, indentLevel + 2);
                continue;
            }
            // Ok, convert the non list
            final Item a = leftChild.items.size() > 0 ? leftChild : this.convertValueToList(leftChild);
            final Item b = rightChild.items.size() > 0 ? rightChild : this.convertValueToList(rightChild);
            return this.itemsInOrder(a, b, indentLevel + 2);
        }
        return true;
    }

    private Item convertValueToList(final Item valueItem) {
        // I may not have a value - in which case I'm just an empty list
        if (valueItem.value == null) {
            return valueItem;
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
//            return this.source;
//            return this.source.substring(1, this.source.length() - 1);
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
}
