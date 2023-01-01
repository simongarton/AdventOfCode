package com.simongarton.adventofcode.year2020;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Year2020Day1 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 1: Report Repair";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2020, 1);
    }

    @Override
    public String part1(final String[] input) {
        final List<Long> longList = Arrays.stream(input).map(Long::valueOf).collect(Collectors.toList());
        final Result result = this.part1AsList(longList);
        return String.valueOf(result.multiplied());
    }

    @Override
    public String part2(final String[] input) {
        final List<Long> longList = Arrays.stream(input).map(Long::valueOf).collect(Collectors.toList());
        final Result result = this.part2AsList(longList);
        return String.valueOf(result.multiplied());
    }

    private Result part1AsList(final List<Long> longList) {
        Collections.sort(longList);
        for (final Long first : longList) {
            for (final Long second : longList) {
                if (first + second == 2020) {
                    return new Result(first, second, null);
                }
            }
        }
        throw new RuntimeException("no luck.");
    }

    private Result part2AsList(final List<Long> longList) {
        Collections.sort(longList);
        for (final Long first : longList) {
            for (final Long second : longList) {
                for (final Long third : longList) {
                    if (first + second + third == 2020) {
                        return new Result(first, second, third);
                    }
                }
            }
        }
        throw new RuntimeException("no luck.");
    }


    @Data
    @AllArgsConstructor
    private static class Result {
        private Long first;
        private Long second;
        private Long third;

        public long multiplied() {
            if (this.third == null) {
                return this.first * this.second;
            } else {
                return this.first * this.second * this.third;
            }
        }

        @Override
        public String toString() {
            return "Result{" +
                    "first=" + this.first +
                    ", second=" + this.second +
                    (this.third == null ? "" : " third=" + this.third) +
                    ", multiplied=" + this.multiplied() +
                    '}';
        }
    }
}
