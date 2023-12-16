package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Year2023Day12 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 12: Hot Springs";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 12);
    }

    @Override
    public String part1(final String[] input) {

        long combos = 0;
        for (final String line : input) {
            final String[] parts = line.split(" ");
            final String row = parts[0] + ".";
            final List<Integer> groups = Arrays.stream(parts[1].split(",")).map(Integer::valueOf).collect(Collectors.toList());
            final long result = this.countWithRecursion(row, groups);
            combos = combos + result;
        }

        return String.valueOf(combos);
    }

    private long arrangements(final String line) {
        final String[] parts = line.split(" ");
        final String row = parts[0];
        final List<Integer> groups = Arrays.stream(parts[1].split(",")).map(Integer::valueOf).collect(Collectors.toList());

        // I could do brute force
        return this.bruteForce(row, groups);
    }

    private long bruteForce(final String row, final List<Integer> groups) {
        final List<Integer> unknownLocations = new ArrayList<>();
        for (int i = 0; i < row.length(); i++) {
            if (row.substring(i, i + 1).equalsIgnoreCase("?")) {
                unknownLocations.add(i);
            }
        }
        final int combos = (int) Math.pow(2, unknownLocations.size());
        final int maxLength = Integer.toBinaryString(combos).length();

        int validCombos = 0;

        for (int combo = 0; combo < combos; combo++) {
            final String binary = this.leftPad("0", Integer.toBinaryString(combo), maxLength - 1);
            final String testRow = this.makeTestRow(row, binary, unknownLocations);
            final boolean testGroups = this.testGroups(testRow, groups);
            if (testGroups) {
                validCombos++;
            }
        }

        return validCombos;
    }

    private boolean testGroups(final String row, final List<Integer> groupsToTest) {
        int groups = 0;
        final List<Integer> foundGroups = new ArrayList<>();
        boolean inGroup = false;
        for (int i = 0; i < row.length(); i++) {
            if (row.substring(i, i + 1).equalsIgnoreCase(".")) {
                if (inGroup) {
                    foundGroups.add(groups);
                    groups = 0;
                }
                inGroup = false;
            } else {
                if (!inGroup) {
                    inGroup = true;
                }
                groups++;
            }
        }
        if (inGroup) {
            foundGroups.add(groups);
        }
        if (foundGroups.size() != groupsToTest.size()) {
            return false;
        }
        for (int i = 0; i < foundGroups.size(); i++) {
            if (!Objects.equals(foundGroups.get(i), groupsToTest.get(i))) {
                return false;
            }
        }
        return true;
    }

    private String makeTestRow(final String row, final String binary, final List<Integer> unknownLocations) {
        final String mappedBinary = binary.replace("0", ".").replace("1", "#");
        String testRow = row;
        for (int i = 0; i < unknownLocations.size(); i++) {
            testRow = this.replaceCharacter(testRow, unknownLocations.get(i), mappedBinary.substring(i, i + 1));
        }
        return testRow;
    }

    private String replaceCharacter(final String original, final Integer index, final String replacement) {
        return original.substring(0, index) + replacement + original.substring(index + 1, original.length());
    }

    private String leftPad(final String replace, final String binaryString, final int length) {
        final int rest = length - binaryString.length();
        return replace.repeat(rest) + binaryString;
    }

    @Override
    public String part2(final String[] input) {

        long combos = 0;
        for (final String line : input) {
            final String[] parts = line.split(" ");
            final String row = parts[0] + ".";
            final List<Integer> groups = Arrays.stream(parts[1].split(",")).map(Integer::valueOf).collect(Collectors.toList());
            final long result = this.countWithRecursion(row, groups);
            combos = combos + result;
        }

        return String.valueOf(combos);
    }

    private long countWithRecursion(final String cfg, final List<Integer> nums) {

        long result = 0;

        if (cfg.isEmpty()) {
            result = (nums.isEmpty()) ? 1 : 0;
            return result;
        }

        if (nums.isEmpty()) {
            result = cfg.contains("#") ? 0 : 1;
            return result;
        }

        final String firstSpring = cfg.substring(0, 1);
        if (".?".contains(firstSpring)) {
            result = result + this.countWithRecursion(cfg.substring(1), nums);
        }

        if ("#?".contains(firstSpring)) {

            /*
            if cfg[0] in '#?':
                if nums[0] <= len(cfg) and '.' not in cfg[:nums[0]] and (nums[0] == len(cfg) or cfg[nums[0]] != '#'):
                    result += count(cfg[nums[0] + 1:], nums[1:])
             */

            final String part1 = this.pythonPart1(cfg, nums.get(0));
            final String part2 = this.pythonPart2(cfg, nums.get(0));
            final String part3 = this.pythonPart3(cfg, nums.get(0));

            if (nums.get(0) <= cfg.length() &&
                    !(part1.contains(".")) &&
                    (nums.get(0) == cfg.length() || !(part2.equalsIgnoreCase("#")))
            ) {
                result = result + this.countWithRecursion(part3, this.restOfGroups(nums, 1));
            }
        }

        // https://www.youtube.com/watch?v=g3Ms5e7Jdqo&t=602s
        return result;
    }

    private String pythonPart1(final String cfg, final int group) {
        // cfg[:nums[0]]
        try {
            return cfg.substring(0, group);
        } catch (final StringIndexOutOfBoundsException e) {
            return cfg.substring(0);
        }
    }

    private String pythonPart2(final String cfg, final int group) {
        // cfg[nums[0]]
        if (group >= cfg.length()) {
            return "";
        }
        return cfg.substring(group, group + 1);
    }

    private String pythonPart3(final String cfg, final int group) {
        // cfg[nums[0] + 1:]
        if (group >= cfg.length()) {
            return "";
        }
        return cfg.substring(group + 1);
    }

    private List<Integer> restOfGroups(final List<Integer> groups, final int start) {
        final List<Integer> restOfGroups = new ArrayList<>();
        for (int i = start; i < groups.size(); i++) {
            restOfGroups.add(groups.get(i));
        }
        return restOfGroups;
    }
}
