package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2021Day6 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 6: Lanternfish";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2021, 6);
    }

    @Override
    public String part1(final String[] input) {
        final long[] buckets = new long[9];
        this.loadFishIntoBuckets(buckets, input);
        final long result = this.breedFish(buckets, 80);
        return String.valueOf(result);
    }

    @Override
    public String part2(final String[] input) {
        final long[] buckets = new long[256];
        this.loadFishIntoBuckets(buckets, input);
        final long result = this.breedFish(buckets, 256);
        return String.valueOf(result);
    }

    private long breedFish(final long[] buckets, final int days) {
        for (int day = 0; day < days; day++) {
            final long breeders = buckets[0];
            for (int i = 0; i < 8; i++) {
                buckets[i] = buckets[i + 1];
            }
            buckets[6] = buckets[6] + breeders;
            buckets[8] = breeders;
        }
        long total = 0;
        for (int i = 0; i < 9; i++) {
            total += buckets[i];
        }
        return total;
    }

    private void loadFishIntoBuckets(final long[] buckets, final String[] lines) {
        final String[] ages = lines[0].split(",");
        for (final String age : ages) {
            final int fish = Integer.parseInt(age);
            buckets[fish] = buckets[fish] + 1;
        }
    }
}
