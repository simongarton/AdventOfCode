package com.simongarton.adventofcode.year2020;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

public class Year2020Day10 extends AdventOfCodeChallenge {

     /*

    The second part caused issues with large files - recursion takes too long. I struggled for
    a bit and then went and read through some of the solutions in the subreddit for ideas.
    There were big clues being given by the challenge : the gaps were either 1 or 3 jolts, never 2.
    There are various approaches being taken in the subreddit. One of them - which I've now abandoned -
    looked at breaking the entire chain into chunks where the gap was 3 jolts - all paths will converge at these gaps.
    So if you can break the whole chain into 2 chunks; you need to find the number of unique paths in each chunk; and then
    multiply the two together.
    However, a more elegant solution using dynamic programming and memos to cache the results. The number of paths to any adapter
    will be the sum of the paths to up to 3 previous adapters that it can be reached by.
    So if I have this chain of adapters :

    0  1  2  3  5  8  9  10
    1  1                        # I can get to 1 from 0, so I store 1 against 1
    1  1  2                     # I can get to 2 from 0 or 1, so I store 2 against 2
    1  1  2  4                  # I can get to 3 once from 0 one from 1 and two ways from 2, so I store 4 against 3
          2  4  6               # I can get to 5 two ways from 2 and four ways from 3, so I store 6 against 5
                6  6            # I can only get to 8 the same six ways as I got to 5 ...
                   6  6         # ... and only to 9 the same six ways again ...
                   6  6  12     # ... but I can get to 10 six ways from 8 and six ways from 9, hence 12.

     */

    private List<Adapter> adapters;
    private long combinations = 0;

    @Override
    public String title() {
        return "Day 10: Adapter Array";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2020, 10);
    }

    @Override
    public String part1(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        this.adapters = lines.stream().map(Adapter::new)
                .sorted(Comparator.comparing(Adapter::getRating))
                .collect(Collectors.toList());
        final Map<Integer, Integer> differences = this.figureRatingOrder(this.adapters);
        final int deviceAdapter = 3;
        if (!differences.containsKey(deviceAdapter)) {
            differences.put(deviceAdapter, 0);
        }
        differences.put(deviceAdapter, differences.get(deviceAdapter) + 1);
        int result = 1;
        for (final Map.Entry<Integer, Integer> entry : differences.entrySet()) {
            result = result * entry.getValue();
        }
        return String.valueOf(result);
    }

    @Override
    public String part2(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        this.adapters = lines.stream().map(Adapter::new)
                .sorted(Comparator.comparing(Adapter::getRating))
                .collect(Collectors.toList());
        final Map<Integer, Integer> differences = this.figureRatingOrder(this.adapters);
        final int deviceAdapter = 3;
        if (!differences.containsKey(deviceAdapter)) {
            differences.put(deviceAdapter, 0);
        }
        differences.put(deviceAdapter, differences.get(deviceAdapter) + 1);
        int result = 1;
        for (final Map.Entry<Integer, Integer> entry : differences.entrySet()) {
            result = result * entry.getValue();
        }
        final int maxRating = this.adapters.get(this.adapters.size() - 1).getRating();
        final List<Adapter> usedAdapters = new ArrayList<>();
        this.combinations = 0;
        if (this.adapters.size() <= 20 && false) {
            System.out.println("Recursive solution : " + this.solveRecursively(usedAdapters, maxRating, 0));
        }
        return String.valueOf(this.solveWithMemos());
    }

    private long solveWithMemos() {
        final Map<Integer, Long> memo = new HashMap<>();
        memo.put(0, 1L);
        for (final Adapter adapter : this.adapters) {
            final int rating = adapter.getRating();
            final long a = memo.containsKey(rating - 1) ? memo.get(rating - 1) : 0;
            final long b = memo.containsKey(rating - 2) ? memo.get(rating - 2) : 0;
            final long c = memo.containsKey(rating - 3) ? memo.get(rating - 3) : 0;
            memo.put(rating, a + b + c);
        }
        return memo.get(this.adapters.get(this.adapters.size() - 1).getRating());
    }

    private long solveRecursively(final List<Adapter> usedAdapters, final int maxRating, final int rating) {
        final List<Adapter> possibleAdapters = this.adapters.stream().filter(a -> a.canAccept(rating)).collect(Collectors.toList());
        int workingRating = rating;
        for (final Adapter possibleAdapter : possibleAdapters) {
            final int oldRating = workingRating;
            if (usedAdapters.contains(possibleAdapter)) {
                continue;
            }
            if (!possibleAdapter.canAccept(workingRating)) {
                continue;
            }
            usedAdapters.add(possibleAdapter);
            workingRating = possibleAdapter.getRating();
            if (this.testForFinish(usedAdapters, maxRating)) {
                this.combinations++;
            }
            this.solveRecursively(usedAdapters, maxRating, workingRating);
            usedAdapters.remove(possibleAdapter);
            workingRating = oldRating;
        }
        return this.combinations;
    }

    private boolean testForFinish(final List<Adapter> usedAdapters, final int maxRating) {
        final int lastRating = usedAdapters.get(usedAdapters.size() - 1).getRating();
        if (lastRating == maxRating) {
            //System.out.println("(0)," + usedAdapters.stream().map(a -> Integer.toString(a.getRating())).collect(Collectors.joining(",")) + ",(" + (maxRating + 3) + ")");
            return true;
        }
        return false;
    }

    private Map<Integer, Integer> figureRatingOrder(final List<Adapter> adapters) {
        int currentRating = 0;
        final Map<Integer, Integer> difference = new HashMap<>();
        for (final Adapter adapter : adapters) {
            if (!adapter.canAccept(currentRating)) {
                throw new RuntimeException("Can't add adapter " + adapter.getRating() + " at rating " + currentRating);
            }
            final int diff = adapter.rating - currentRating;
            if (!difference.containsKey(diff)) {
                difference.put(diff, 0);
            }
            difference.put(diff, difference.get(diff) + 1);
            currentRating = adapter.rating;
        }
        return difference;
    }

    @Data
    private static class Adapter {

        private int rating;

        public Adapter(final String rating) {
            this.rating = Integer.parseInt(rating);
        }

        public boolean canAccept(final int currentRating) {
            final int diff = this.rating - currentRating;
            return (diff >= 1 && diff <= 3);
        }
    }
}
