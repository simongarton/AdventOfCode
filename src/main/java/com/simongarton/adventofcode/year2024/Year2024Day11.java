package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2024Day11 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 11: Template code";
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
            System.out.println(blink + "/" + blinks + "=" + stones.size());
        }

        System.out.println(stones);
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

        final List<List<Long>> stoneLists = new ArrayList<>();

        final List<Long> stones = this.parseStones(input[0]);
        for (final Long stone : stones) {
            List<Long> shortList = List.of(stone);
            final int blinks = 75;
            for (int blink = 0; blink < blinks; blink++) {
                shortList = this.dontBlink(shortList);
                System.out.println(blink + "/" + blinks + "=" + shortList.size());
            }
            stoneLists.add(shortList);
        }
        long total = 0;
        for (final List<Long> shortList : stoneLists) {
            total += shortList.size();
        }
        return String.valueOf(total);
    }

    static class StoneState {

        long value;
        StoneState nextState1;
        StoneState nextState2;
    }

}
