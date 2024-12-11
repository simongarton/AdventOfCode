package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;
import java.util.stream.Collectors;

public class Year2024Day11 extends AdventOfCodeChallenge {

    private final Map<String, Long> memo = new HashMap<>();
    private List<String> nodes;
    private long tests = 0;
    private long cacheHits = 0;

    private static final boolean DEBUG = false;

    @Override
    public String title() {
        return "Day 11: Plutonian Pebbles";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 11);
    }

    @Override
    public String part1(final String[] input) {

        List<Long> stones = this.parseStones(input[0]);
        final int blinks = 25;
        for (int blink = 0; blink < blinks; blink++) {
            stones = this.dontBlink(stones);
            if (DEBUG) {
                System.out.println(blink + "/" + blinks + "=" + stones.size());
            }
        }

        return String.valueOf(stones.size());
    }

    private List<Long> dontBlink(final List<Long> stones) {

        final List<Long> newStones = new ArrayList<>();
        for (final Long stone : stones) {
            newStones.addAll(this.blinkStone(stone));
        }
        return newStones;
    }

    private List<Long> blinkStone(final Long stone) {

        if (stone == 0) {
            return List.of(1L);
        }
        final String stoneValue = String.valueOf(stone);
        if ((stoneValue.length() % 2) == 0) {
            final Long left = Long.parseLong(stoneValue.substring(0, stoneValue.length() / 2));
            final Long right = Long.parseLong(stoneValue.substring(stoneValue.length() / 2));
            return List.of(left, right);
        }
        return List.of(stone * 2024);
    }

    private List<Long> parseStones(final String input) {

        final String[] parts = input.split(" ");
        return Arrays.stream(parts).map(Long::parseLong).collect(Collectors.toList());
    }

    @Override
    public String part2(final String[] input) {

        // my first idea was to break it up and do one number at a time
        // but that ran into problems about 45 stones in - like the full list.

        // I am now thinking I need to memoize it somehow.  Instead of a Long,
        // I need to store a structure that is a Long, a position, and a total for what
        // it would turn into

        // So in this list ..

        /*

        0:[1] (1)
        1:[2024] (1)
        2:[20, 24] (2)
        3:[2, 0, 2, 4] (4)
        4:[4048, 1, 4048, 8096] (4)
        5:[40, 48, 2024, 40, 48, 80, 96] (7)

         */

        // I can tell that a 2 at level 3 will turn into 4048 at 4, and a pair of 40 and 48 at 5
        // and a 0 turns into a 2024 and so on

        // but I need to start from the bottom.
        // basically my key needs to be levelX-ValueY and my value should be the size of the list that would result from it.
        // I have to start top down ... but we'll do it recursively and memoize it for speed.

        this.nodes = new ArrayList<>();

        final List<Long> stones = this.parseStones(input[0]);
        long total = 0;
        final int blinks = 75;
        for (final Long stone : stones) {
            total += this.recursiveDepth(stone, 0, blinks);
        }

        if (DEBUG) {
            this.dumpNodesAsGraph();
            System.out.println("tested " + this.tests + " and cache hits " + this.cacheHits);
        }

        return String.valueOf(total);
    }

    private void dumpNodesAsGraph() {

        final List<String> lines = new ArrayList<>();
        lines.add("digraph {");
        lines.add("rankdir=\"LR\"");
        lines.addAll(this.nodes);
        lines.add("}");

        this.dumpGraphToFile(String.format("src/graphs/%s.dot", this.getClass().getSimpleName()), lines);

    }

    private long recursiveDepth(final Long stone, final int level, final int blinks) {

        this.tests++;
        final String key = level + ":" + stone;

        if (this.memo.containsKey(key)) {
            this.cacheHits++;
            return this.memo.get(key);
        }

        if (DEBUG && (level < blinks)) {
            for (final Long next : this.blinkStone(stone)) {
                final String nextKey = (level + 1) + ":" + next;
                if (!this.memo.containsKey("\"" + nextKey + "\"")) {
                    this.nodes.add("\"" + key + "\"" + "->" + "\"" + nextKey + "\"");
                }
            }

            final String simpleKey = "\"" + key + "\" [style=filled color=wheat label=\"\"]";
            if (this.memo.containsKey(key)) {
                final String extra = " [style=filled color=green label=\"\"] ";
                this.nodes.add("\"" + key + "\"" + extra);
                this.nodes.remove(simpleKey);
            } else {
                this.nodes.add(simpleKey);
            }
        }

        if (level == blinks) {
            this.memo.put(key, 1L);
            return 1L;
        }

        final List<Long> nextLevel = this.dontBlink(List.of(stone));
        long total = 0;
        for (final Long next : nextLevel) {
            total += this.recursiveDepth(next, level + 1, blinks);
        }
        this.memo.put(key, total);
        return total;
    }
}
