package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Year2022Day5 extends AdventOfCodeChallenge {

    private List<Stack<String>> stacks;
    private List<String> moves;

    @Override
    public boolean run() {
        return this.runChallenge(2022, 5);
    }

    @Override
    public String part1(final String[] input) {

        this.loadStacksAndMoves(input);

        for (final String move : this.moves) {
            this.moveCrates9000(move, this.stacks);
        }

        String result = "";
        for (final Stack<String> stack : this.stacks) {
            result = result + stack.peek();
        }
        return result;
    }

    @Override
    public String part2(final String[] input) {

        this.loadStacksAndMoves(input);

        for (final String move : this.moves) {
            this.moveCrates9001(move, this.stacks);
        }

        String result = "";
        for (final Stack<String> stack : this.stacks) {
            result = result + stack.peek();
        }
        return result;
    }

    private void moveCrates9000(final String move, final List<Stack<String>> stacks) {
        final String[] instructions = move.split(" ");
        final int count = Integer.parseInt(instructions[1]);
        final int from = Integer.parseInt(instructions[3]) - 1;
        final int to = Integer.parseInt(instructions[5]) - 1;

        for (int i = 0; i < count; i++) {
            stacks.get(to).push(stacks.get(from).pop());
        }
    }

    private void moveCrates9001(final String move, final List<Stack<String>> stacks) {
        final String[] instructions = move.split(" ");
        final int count = Integer.parseInt(instructions[1]);
        final int from = Integer.parseInt(instructions[3]) - 1;
        final int to = Integer.parseInt(instructions[5]) - 1;

        final List<String> crates = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            crates.add(stacks.get(from).pop());
        }
        Collections.reverse(crates);
        for (int i = 0; i < count; i++) {
            stacks.get(to).push(crates.get(i));
        }
    }

    private void loadStacksAndMoves(final String[] input) {
        final List<String> stackLines = new ArrayList<>();
        this.moves = new ArrayList<>();
        boolean doingStacks = true;
        for (final String line : input) {
            if (line.trim().length() == 0) {
                doingStacks = false;
                continue;
            }
            if (doingStacks) {
                stackLines.add(line);
            } else {
                this.moves.add(line);
            }
        }

        this.loadStacks(stackLines);
    }

    private void loadStacks(final List<String> inputLines) {

        // remove the last which is the stack numbers, and I can derive this
        inputLines.remove(inputLines.size() - 1);

        // reverse it so the stacks start at the bottom
        Collections.reverse(inputLines);

        // and use the first line to count the stacks
        final int stackCount = (inputLines.get(0) + " ").length() / 4;

        this.stacks = new ArrayList<>();
        for (int i = 0; i < stackCount; i++) {
            this.stacks.add(new Stack<>());
        }

        // now read in the lines
        for (final String line : inputLines) {
            for (int stackNo = 0; stackNo < stackCount; stackNo++) {
                final int position = (stackNo * 4) + 1;
                // hah, I assumed they weren't right padded
                if (position >= line.length()) {
                    break;
                }
                final String crate = line.substring(position, position + 1);
                if (crate.trim().length() > 0) {
                    this.stacks.get(stackNo).push(crate);
                }
            }
        }
    }
}
