package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;

public class Year2024Day22 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 22: Monkey Market";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 22);
    }

    @Override
    public String part1(final String[] input) {

        long total = 0;
        for (final String line : input) {
            total += this.monkeyMagic(Long.valueOf(line)).getFinalNumber();
        }

        return String.valueOf(total);
    }

    private BananaTree monkeyMagic(Long number) {

        final BananaTree bananaTree = new BananaTree();
        bananaTree.numbers = new long[2001];
        bananaTree.deltas = new long[2001];
        bananaTree.prices = new long[2001];
        bananaTree.numbers[0] = number;
        bananaTree.prices[0] = number % 10;

        for (int i = 0; i < 2000; i++) {
            number = this.multiplyAndMixAndPrune(number, 64);
            number = this.divideAndMixAndPrune(number);
            number = this.multiplyAndMixAndPrune(number, 2048);
            bananaTree.numbers[i + 1] = number;
            bananaTree.prices[i + 1] = number % 10;
            bananaTree.deltas[i + 1] = (number % 10) - bananaTree.prices[i];
        }

        return bananaTree;
    }

    private long divideAndMixAndPrune(final long secret) {

        final long multiplied = secret / 32;
        final long mixed = this.mix(multiplied, secret);
        return this.prune(mixed);
    }

    private long multiplyAndMixAndPrune(final long secret, final long multiplect) {

        final long multiplied = secret * multiplect;
        final long mixed = this.mix(multiplied, secret);
        return this.prune(mixed);
    }

    private long prune(final long secret) {

        return secret % 16777216;
    }

    private long mix(final long nextNumber, final long number) {

        return nextNumber ^ number;
    }

    @Override
    public String part2(final String[] input) {

        final List<BananaTree> bananaTrees = new ArrayList<>();

        for (final String line : input) {
            bananaTrees.add(this.monkeyMagic(Long.valueOf(line)));
        }

        final Map<String, Long> map = new HashMap<>();
        for (final BananaTree bananaTree : bananaTrees) {
            final List<SequenceAndPrice> sequenceAndPriceList = this.getSequencesAndPrices(bananaTree);
            for (final SequenceAndPrice sequenceAndPrice : sequenceAndPriceList) {
                final String key = sequenceAndPrice.key;
                map.put(key, map.getOrDefault(key, 0L) + sequenceAndPrice.price);
            }
        }

        final OptionalLong max = map.values().stream().mapToLong(Long::longValue).max();
        if (max.isPresent()) {
            return String.valueOf(max.getAsLong());
        }
        throw new RuntimeException("oops");
    }

    private Map<String, Long> getSequencePriceMap(final List<List<SequenceAndPrice>> sequenceAndPrices) {

        final Map<String, Long> map = new HashMap<>();
        for (final List<SequenceAndPrice> sequenceAndPriceList : sequenceAndPrices) {
            for (final SequenceAndPrice sequenceAndPrice : sequenceAndPriceList) {
                final String key = sequenceAndPrice.key;
                map.put(key, map.getOrDefault(key, 0L) + sequenceAndPrice.price);
            }
        }
        return map;
    }

    private List<SequenceAndPrice> getSequencesAndPrices(final BananaTree bananaTree) {

        final List<SequenceAndPrice> sequenceAndPrices = new ArrayList<>();
        for (int i = 4; i < 2001; i++) {
            final long[] sequence = this.buildSequence(bananaTree, i);
            final SequenceAndPrice sequenceAndPrice = new SequenceAndPrice(sequence, bananaTree.prices[i]);
            if (this.alreadyGotSequence(sequenceAndPrice, sequenceAndPrices)) {
                continue;
            }
            sequenceAndPrices.add(sequenceAndPrice);
        }
        return sequenceAndPrices;
    }

    private boolean alreadyGotSequence(final SequenceAndPrice sequenceAndPrice, final List<SequenceAndPrice> sequenceAndPrices) {

        for (final SequenceAndPrice sequenceAndPriceTest : sequenceAndPrices) {
            if (
                    sequenceAndPriceTest.sequence[0] == sequenceAndPrice.sequence[0] &&
                            sequenceAndPriceTest.sequence[1] == sequenceAndPrice.sequence[1] &&
                            sequenceAndPriceTest.sequence[2] == sequenceAndPrice.sequence[2] &&
                            sequenceAndPriceTest.sequence[3] == sequenceAndPrice.sequence[3]
            ) {
                return true;
            }
        }
        return false;
    }

    private long[] buildSequence(final BananaTree bananaTree, final int i) {

        return new long[]{
                bananaTree.deltas[i - 3],
                bananaTree.deltas[i - 2],
                bananaTree.deltas[i - 1],
                bananaTree.deltas[i],
        };
    }

    static class BananaTree {

        private long[] numbers;
        private long[] prices;
        private long[] deltas;

        public long getFinalNumber() {
            return this.numbers[2000];
        }
    }

    static class SequenceAndPrice {

        private final long[] sequence;
        private final long price;
        private final String key;

        public SequenceAndPrice(final long[] sequence, final long price) {
            this.sequence = sequence;
            this.price = price;
            this.key = this.buildKey(this);
        }

        private String buildKey(final SequenceAndPrice sequenceAndPrice) {

            return sequenceAndPrice.sequence[0] + "|" +
                    sequenceAndPrice.sequence[1] + "|" +
                    sequenceAndPrice.sequence[2] + "|" +
                    sequenceAndPrice.sequence[3];
        }
    }
}
