package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Year2021Day14 extends AdventOfCodeChallenge {

    private List<Insertion> insertions;

    @Override
    public String title() {
        return "Day 14: Extended Polymerization";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2021, 14);
    }

    @Override
    public String part1(final String[] input) {
        final String template = input[0];
        this.loadInsertions(input);
        Map<String, Long> pairs = this.buildPairs(template);
        for (int step = 1; step <= 10; step++) {
            pairs = this.applyInsertions(pairs);
        }
        final long result = this.scorePolymer(pairs, template);
        return String.valueOf(result);
    }

    @Override
    public String part2(final String[] input) {
        final String template = input[0];
        this.loadInsertions(input);
        Map<String, Long> pairs = this.buildPairs(template);
        for (int step = 1; step <= 40; step++) {
            pairs = this.applyInsertions(pairs);
        }
//        final long length = this.measurePolymer(pairs, template); // 20,890,720,927,745
        final long result = this.scorePolymer(pairs, template);
        return String.valueOf(result);
    }

    private void debugPairs(final String s, final Map<String, Long> pairs) {
        final StringBuilder line = new StringBuilder();
        for (final Map.Entry<String, Long> entry : pairs.entrySet()) {
            line.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
        }
        System.out.println(s + " " + line);
    }

    protected Map<String, Long> applyInsertions(final Map<String, Long> pairs) {
        final Map<String, Long> newPairs = new HashMap<>(pairs);
        for (final Insertion insertion : this.insertions) {
            if (pairs.containsKey(insertion.pattern)) {
                // decrement the existing pair by how many we have of these.
                newPairs.put(insertion.pattern, newPairs.get(insertion.pattern) - pairs.get(insertion.pattern));
                // figure out the new ones
                final String new1 = insertion.left + insertion.charToInsert;
                final String new2 = insertion.charToInsert + insertion.right;
                newPairs.put(new1, newPairs.getOrDefault(new1, 0L) + pairs.get(insertion.pattern));
                newPairs.put(new2, newPairs.getOrDefault(new2, 0L) + pairs.get(insertion.pattern));
            }
        }
        final Map<String, Long> positivePairs = new HashMap<>();
        for (final Map.Entry<String, Long> entry : newPairs.entrySet()) {
            if (entry.getValue() > 0) {
                positivePairs.put(entry.getKey(), entry.getValue());
            }
        }
        return positivePairs;
    }

    private long scorePolymer(final String current) {
        final Map<String, Long> map = new HashMap<>();
        for (int i = 0; i < current.length(); i++) {
            final String element = current.charAt(i) + "";
            map.put(element, map.getOrDefault(element, 0L) + 1);
        }
        return this.scoreMap(map);
    }

    protected long scoreMapAfterCorrectingForDuplicates(final Map<String, Long> map, final String template) {
        // now halve them all
        map.replaceAll((k, v) -> v / 2);
        // and unhalve the first and last because they AREN'T duplicated
        final String first = template.substring(0, 1);
        map.put(first, map.get(first) + 1);
        final String last = template.substring(template.length() - 1);
        map.put(last, map.get(last) + 1);
        return this.scoreMap(map);
    }

    protected long scoreMap(final Map<String, Long> map) {
        long min = Long.MAX_VALUE;
        String minChar = "";
        long max = 0;
        String maxChar = "";
        for (final Map.Entry<String, Long> entry : map.entrySet()) {
            if (entry.getValue() < min) {
                min = entry.getValue();
                minChar = entry.getKey();
            }
            if (entry.getValue() > max) {
                max = entry.getValue();
                maxChar = entry.getKey();
            }
        }
        return map.get(maxChar) - map.get(minChar);
    }

    private Map<String, Long> buildCountMap(final Map<String, Long> pairs) {
        final Map<String, Long> map = new HashMap<>();
        for (final Map.Entry<String, Long> entry : pairs.entrySet()) {
            // this is over-counting ... each side of the pair will be used in another pair,
            // except for the first and last letters in the string
            String element = entry.getKey().charAt(0) + "";
            map.put(element, map.getOrDefault(element, 0L) + entry.getValue());
            element = entry.getKey().charAt(1) + "";
            map.put(element, map.getOrDefault(element, 0L) + entry.getValue());
        }
        return map;
    }

    private long scorePolymer(final Map<String, Long> pairs, final String template) {
        final Map<String, Long> map = this.buildCountMap(pairs);
        return this.scoreMapAfterCorrectingForDuplicates(map, template);
    }

    private String applyInsertions(final String current) {
        final List<Insertion> insertionsToApply = new ArrayList<>();
        for (final Insertion insertion : this.insertions) {
            for (int i = 0; i < current.length() - 1; i++) {
                final String match = current.substring(i, i + 2);
                if (match.equals(insertion.pattern)) {
                    final Insertion newInsertion = insertion.copy();
                    newInsertion.indexInTemplate = i + 1;
                    insertionsToApply.add(newInsertion);
                }
            }
        }
        return this.actuallyApplyInsertions(current, insertionsToApply);
    }

    private void updateRemainingInsertionsIfLater(final List<Insertion> insertionsToApply, final int i) {
        for (final Insertion insertion : insertionsToApply) {
            if (insertion.indexInTemplate >= i) {
                insertion.indexInTemplate++;
            }
        }
    }

    private String actuallyApplyInsertions(final String current, final List<Insertion> insertionsToApply) {
        String inserted = current;
        for (final Insertion insertion : insertionsToApply) {
            inserted = inserted.substring(0, insertion.indexInTemplate) + insertion.charToInsert + inserted.substring(insertion.indexInTemplate);
            this.updateRemainingInsertionsIfLater(insertionsToApply, insertion.indexInTemplate);
        }
        return inserted;
    }

    protected void loadInsertions(final String[] lines) {
        this.insertions = new ArrayList<>();
        for (int index = 2; index < lines.length; index++) {
            this.insertions.add(new Insertion(lines[index]));
        }
    }

    protected Map<String, Long> buildPairs(final String template) {
        final Map<String, Long> pairs = new HashMap<>();
        for (int i = 0; i < template.length() - 1; i++) {
            final String key = template.substring(i, i + 2);
            pairs.put(key, pairs.getOrDefault(key, 0L) + 1);
        }
        return pairs;
    }

    public static final class Insertion {
        final String left;
        final String right;
        final String pattern;
        final String charToInsert;
        final String insertion;
        int indexInTemplate;

        public Insertion(final String insertion) {
            this.insertion = insertion;
            final String[] parts = insertion.split(" -> ");
            this.pattern = parts[0];
            this.left = this.pattern.charAt(0) + "";
            this.right = this.pattern.charAt(1) + "";
            this.charToInsert = parts[1];
            this.indexInTemplate = 0;
        }

        public Insertion copy() {
            return new Insertion(this.insertion);
        }
    }
}
