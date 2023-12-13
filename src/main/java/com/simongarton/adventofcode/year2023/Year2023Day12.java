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
            combos += this.arrangements(line);
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
            combos += this.longerArrangements(line);
        }

        return String.valueOf(combos);
    }

    private long longerArrangements(final String line) {

        // The recursion looks OK, but it's still taking too long.

        final String[] parts = line.split(" ");
//        final String row = parts[0];
//        final List<Integer> shortGroups = Arrays.stream(parts[1].split(",")).map(Integer::valueOf).collect(Collectors.toList());
//        final List<Integer> groups = new ArrayList<>();
//        for (int i = 0; i < 1; i++) {
//            groups.addAll(shortGroups);
//        }
        final String row = parts[0] + ("?" + parts[0]).repeat(4);
        final List<Integer> shortGroups = Arrays.stream(parts[1].split(",")).map(Integer::valueOf).collect(Collectors.toList());
        final List<Integer> groups = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            groups.addAll(shortGroups);
        }
        final List<Integer> unknownLocations = new ArrayList<>();
        for (int i = 0; i < row.length(); i++) {
            if (row.substring(i, i + 1).equalsIgnoreCase("?")) {
                unknownLocations.add(i);
            }
        }

        System.out.println(line + " -> " + row + " : " + groups.stream().map(String::valueOf).collect(Collectors.joining(",")));

        final long result = this.recursive(row, groups, unknownLocations);
        System.out.println(line + ":" + result);
        return result;
    }

    private long recursive(final String row, final List<Integer> groups, final List<Integer> unknownLocations) {
        return this.recursiveAccumulator(0L, 0, row, groups, unknownLocations, "#") +
                this.recursiveAccumulator(0L, 0, row, groups, unknownLocations, ".");
    }

    private long recursiveAccumulator(final long runningTotal,
                                      final int index,
                                      final String workingLine,
                                      final List<Integer> groups,
                                      final List<Integer> unknownLocations,
                                      final String replacement) {
        final String testLine = this.replaceCharacter(workingLine, unknownLocations.get(index), replacement);

//        System.out.println(testLine);
        final int definiteGroups = this.countDefiniteGroups(testLine);
        // go no further ...
        if (definiteGroups > groups.size()) {
            return 0;
        }
        if (index == (unknownLocations.size() - 1)) {
            return runningTotal + this.countValidGroups(testLine, groups);
        }
        return this.recursiveAccumulator(runningTotal, index + 1, testLine, groups, unknownLocations, "#") +
                this.recursiveAccumulator(runningTotal, index + 1, testLine, groups, unknownLocations, ".");

    }

    private int countDefiniteGroups(final String testLine) {
        // count definite groups - assume any ? is a .
        // no, this isn't valid.
        final String definiteLine = testLine.replace("?", ".");
        final String cleanerLine = this.splitIntoGroups(definiteLine);
        final String[] groups = cleanerLine.split("\\.");
        return groups.length;
    }

    private long countValidGroups(final String testLine, final List<Integer> desiredGroups) {
        final String cleanerLine = this.splitIntoGroups(testLine);
        final String[] groups = cleanerLine.split("\\.");
        final List<Integer> groupCounts = Arrays.stream(groups).map(String::length).collect(Collectors.toList());
        if (groupCounts.size() != desiredGroups.size()) {
            return 0;
        }
        for (int index = 0; index < desiredGroups.size(); index++) {
            if (!Objects.equals(groupCounts.get(index), desiredGroups.get(index))) {
                return 0;
            }
        }
        return 1;
    }

    private String splitIntoGroups(String testLine) {
        while (testLine.contains("..")) {
            testLine = testLine.replace("..", ".");
        }
        return testLine;
    }

    /*

    Five times as long is clearly not going to work.
    I either need to significantly reduce the numbers involved, or find a short cut from inspection.

    ???.### 1,1,3 does simplify to ??? 1,1 but still need to check same number
    ????.#...#... and ???.######..#####. would also simplify but also still as big
    .??..??...?##. 1,1,3 : that last one has to be a #

    ?#?#?#?#?#?#?#? 1,3,1,6 and ?###???????? 3,2,1 no idea.
    well, ?###???????? does go to .###.??????? which then goes to ??????? 2,1

    glimmerings : lookup. if I see ### and 1,1 I should be able to tell immediately, or memo it
    can I chop up the line into bits I know

    but i can't break the solid ones, no idea

    reading the reddit, there are algorithms for breaking it down.
    https://www.reddit.com/r/adventofcode/comments/18ghux0/2023_day_12_no_idea_how_to_start_with_this_puzzle/

    to figure out if I can fit S springs into N tiles, test S-1 springs in N-1 tiles ? Makes no sense.
    Can't break the long ranges into groups.

    Pick the first ? and make it a spring. now repeat, with the rest, recursively.
    Then make it not a spring. At each step, return 1 if it is solved, 0 if not, or recurse.

    But how do I remove the springs I've already used ? count possible groups, ? and # both count
    ???.###
  a #??.### can't tell yet
        ##?.### not going to work, only two groups, return 0
        #?#.### might work continue
            #.#.### might work continue works return 1
  b .#?.### can't tell yet
        .##.### not going to work, return 0
        ..#.### not going to work, return 0

   answer(xxx) = answer(xxx1) + answer(xxx2)

   only when I get to the end of the springs can I check to see if I got a match


     */
}
