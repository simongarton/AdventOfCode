package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2024Day7 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 7: Bridge Repair";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 7);
    }

    @Override
    public String part1(final String[] input) {

        final List<Equation> equations = this.parseEquations(input);

        long score = 0;
        final List<String> operatorTypes = List.of("*", "+");
        for (final Equation equation : equations) {
            final int operatorLength = equation.numbers.size() - 1;
            final List<String> operatorCombinations = this.generateCombinations(operatorTypes, operatorLength);
            if (this.evaluateEquation(equation, operatorCombinations)) {
                score += equation.answer;
            }
        }
        return String.valueOf(score);
    }

    private boolean evaluateEquation(final Equation equation, final List<String> operatorCombinations) {

        for (final String combination : operatorCombinations) {
            if (this.evaluateEquationWithCombination(equation, combination)) {
                return true;
            }
        }
        return false;
    }

    private boolean evaluateEquationWithCombination(final Equation equation, final String combination) {

        long total = 0;
        for (int index = 1; index < equation.numbers.size(); index++) {
            if (index == 1) {
                total = this.applyOperator(equation.numbers.get(0),
                        equation.numbers.get(index),
                        combination.charAt(0));
            } else {
                total = this.applyOperator(total,
                        equation.numbers.get(index),
                        combination.charAt(index - 1));
            }
        }
        return total == equation.answer;
    }

    private Long applyOperator(final long a, final long b, final char c) {
        switch (c) {
            case '*':
                return a * b;
            case '+':
                return a + b;
            default:
                throw new RuntimeException(c + "");
        }
    }

    private List<String> generateCombinations(final List<String> operatorTypes,
                                              final int length) {
        final List<String> combinations = new ArrayList<>();
        final List<Integer> index = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            index.add(0);
        }
        while (true) {
            final String combination = this.buildCombination(operatorTypes, length, index);
            combinations.add(combination);
            if (!this.updateIndex(index, operatorTypes.size())) {
                break;
            }
        }
        return combinations;
    }

    private boolean updateIndex(final List<Integer> index, final int max) {

        for (int i = 0; i < index.size(); i++) {
            if (index.get(i) < (max - 1)) {
                index.add(i, index.get(i) + 1);
                index.remove(i + 1);
                return true;
            } else {
                index.add(i, 0);
                index.remove(i + 1);
            }
        }

        return false;
    }

    private String buildCombination(final List<String> operatorTypes, final int length, final List<Integer> index) {

        final StringBuilder combination = new StringBuilder();
        for (int i = 0; i < length; i++) {
            final int operatorTypeToGet = index.get(i);
            combination.append(operatorTypes.get(operatorTypeToGet));
        }
        return combination.toString();
    }

    private List<Equation> parseEquations(final String[] input) {

        final List<Equation> equations = new ArrayList<>();
        for (final String line : input) {
            equations.add(this.startEquation(line));
        }
        return equations;
    }

    private Equation startEquation(final String line) {

        final Equation equation = new Equation();
        final String[] parts = line.split(":");
        equation.answer = Long.parseLong(parts[0]);
        final String[] operands = parts[1].trim().split(" ");
        equation.numbers.addAll(Arrays.asList(operands).stream().map(Integer::parseInt).collect(Collectors.toList()));
        return equation;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    static class Equation {

        Long answer;
        List<Integer> numbers;
        List<String> operators;

        public Equation() {
            this.numbers = new ArrayList<>();
            this.operators = new ArrayList<>();
        }
    }
}
