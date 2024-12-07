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

    private boolean evaluateEquationVariations(final Equation equation,
                                               final List<String> operatorTypes) {

        // I need to build new equations : I need to get the original one, plus then I need to
        // join all the possible pairs of numbers.
        final List<Equation> equations = new ArrayList<>();
        equations.add(equation);

        for (int i = 1; i < equation.numbers.size(); i++) {
            final Equation newEquation = new Equation(equation.line);
            newEquation.answer = equation.answer;
            for (int n = 0; n < equation.numbers.size(); n++) {
                if (n == i) {
                    continue;
                }
                if (n == (i - 1)) {
                    final int newValue = Integer.parseInt(
                            (equation.numbers.get(n) + "") +
                                    (equation.numbers.get(n + 1) + "")
                    );
                    newEquation.numbers.add(newValue);
                    continue;
                }
                newEquation.numbers.add(equation.numbers.get(n));
            }
            equations.add(newEquation);
        }

        for (final Equation equation1 : equations) {
            final int operatorLength = equation1.numbers.size() - 1;
            final List<String> operatorCombinations = this.generateCombinations(operatorTypes, operatorLength);
            if (this.evaluateEquation(equation1, operatorCombinations)) {
                return true;
            }
        }

        return false;
    }

    private boolean evaluateEquation(final Equation equation, final List<String> operatorCombinations) {

        System.out.println("evaluating " + equation + " " + equation.numbers);
        for (final String combination : operatorCombinations) {
            if (this.evaluateEquationWithCombination(equation, combination)) {
                return true;
            }
        }
        return false;
    }

    private boolean evaluateEquationWithCombination(final Equation equation, final String combination) {

        long total = 0;
        if (equation.numbers.size() == 1) {
            total = equation.numbers.get(0);
            return total == equation.answer;
        }
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

        final Equation equation = new Equation(line);
        final String[] parts = line.split(":");
        equation.answer = Long.parseLong(parts[0]);
        final String[] operands = parts[1].trim().split(" ");
        equation.numbers.addAll(Arrays.stream(operands).map(Integer::parseInt).collect(Collectors.toList()));
        return equation;
    }

    @Override
    public String part2(final String[] input) {

        final List<Equation> equations = this.parseEquations(input);

        long score = 0;
        final List<String> operatorTypes = List.of("*", "+");
        for (final Equation equation : equations) {
            if (this.evaluateEquationVariations(equation, operatorTypes)) {
                System.out.println(equation + " worked so adding " + equation.answer);
                score += equation.answer;
            } else {
                System.out.println(equation + " failed");
            }
        }

        return String.valueOf(score);
    }

    static class Equation {

        String line;
        Long answer;
        List<Integer> numbers;

        public Equation(final String line) {
            this.line = line;
            this.numbers = new ArrayList<>();
        }

        @Override
        public String toString() {
            return this.line + " = " + this.numbers;
        }
    }
}
