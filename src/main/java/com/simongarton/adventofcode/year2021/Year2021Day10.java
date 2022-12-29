package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;

public class Year2021Day10 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 10: Syntax Scoring";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2021, 10);
    }

    @Override
    public String part1(final String[] input) {
        int result = 0;
        for (final String line : input) {
            result += this.scoreLine(line);
        }
        return String.valueOf(result);
    }

    @Override
    public String part2(final String[] input) {

        final List<String> incompleteLines = new ArrayList<>();
        for (final String line : input) {
            if (this.scoreLine(line) == 0) {
                incompleteLines.add(line);
            }
        }
        final List<Long> scores = new ArrayList<>();
        for (final String line : incompleteLines) {
            final String closingChars = this.close(line);
            scores.add(this.score(closingChars));
        }
        scores.sort(Comparator.naturalOrder());
        final int i = 0;
        final int index = scores.size() / 2;
        final long result = scores.get(index);
        return String.valueOf(result);
    }

    private int scoreLine(final String line) {
        final Stack<String> stack = new Stack<>();
        for (int index = 0; index < line.length(); index++) {
            final String instruction = line.charAt(index) + "";
            try {
                switch (instruction) {
                    case "{":
                    case "[":
                    case "(":
                    case "<":
                        stack.push(instruction);
                        break;
                    case "}":
                    case "]":
                    case ")":
                    case ">":
                        this.maybePop(instruction, stack);
                }
            } catch (final CorruptionException ce) {
                return this.valueOf(ce.getMessage());
            }
        }
        return 0;
    }

    private boolean maybePop(final String instruction, final Stack<String> stack) {
        final String last = stack.peek();
        switch (last) {
            case "{":
                return this.allow(stack, instruction, "}");
            case "[":
                return this.allow(stack, instruction, "]");
            case "(":
                return this.allow(stack, instruction, ")");
            case "<":
                return this.allow(stack, instruction, ">");
            default:
                throw new RuntimeException("unrecognised pop " + instruction);
        }
    }

    private boolean allow(final Stack<String> stack, final String instruction, final String allowed) {
        if (allowed.equalsIgnoreCase(instruction)) {
            stack.pop();
            return true;
        }
        throw new CorruptionException(instruction);
    }

    private int valueOf(final String instruction) {
        switch (instruction) {
            case ")":
                return 3;
            case "]":
                return 57;
            case "}":
                return 1197;
            case ">":
                return 25137;
            default:
                throw new RuntimeException("unrecognised pop " + instruction);
        }
    }

    private long score(final String closingChars) {
        long score = 0;
        final Map<String, Integer> closeScores = new HashMap<>();
        closeScores.put(")", 1);
        closeScores.put("]", 2);
        closeScores.put("}", 3);
        closeScores.put(">", 4);
        for (int i = 0; i < closingChars.length(); i++) {
            final String instruction = closingChars.charAt(i) + "";
            score = score * 5;
            score += closeScores.get(instruction);
        }
        return score;
    }

    private String close(final String line) {
        final Stack<String> stack = this.buildStack(line);
        final StringBuilder result = new StringBuilder();
        while (stack.size() > 0) {
            final String instruction = stack.pop();
            switch (instruction) {
                case "{":
                    result.append("}");
                    break;
                case "[":
                    result.append("]");
                    break;
                case "(":
                    result.append(")");
                    break;
                case "<":
                    result.append(">");
                    break;
                default:
                    throw new RuntimeException("I've got this wrong.");
            }
        }
        return result.toString();
    }

    private Stack<String> buildStack(final String line) {
        final Stack<String> stack = new Stack<>();
        for (int index = 0; index < line.length(); index++) {
            final String instruction = line.charAt(index) + "";
            switch (instruction) {
                case "{":
                case "[":
                case "(":
                case "<":
                    stack.push(instruction);
                    break;
                case "}":
                case "]":
                case ")":
                case ">":
                    this.maybePop(instruction, stack);
            }
        }
        return stack;
    }

    private static final class CorruptionException extends RuntimeException {

        private static final long serialVersionUID = 5723930208582470824L;

        public CorruptionException(final String instruction) {
            super(instruction);
        }
    }
}
