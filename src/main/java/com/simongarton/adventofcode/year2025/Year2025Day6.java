package com.simongarton.adventofcode.year2025;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Year2025Day6 extends AdventOfCodeChallenge {

    private List<List<Long>> problems;
    private List<String> operators;

    private List<String> problemLines;
    private List<Integer> columnWidths;
    private int rowCount;

    @Override
    public String title() {
        return "Day 6: Trash Compactor";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2025, 6);
    }

    @Override
    public String part1(final String[] input) {

        this.problems = new ArrayList<>();
        this.operators = new ArrayList<>();
        final int lines = input.length;

        for (int i = 0; i < lines - 1; i++) {
            this.updateProblems(input[i]);
        }
        this.operators = Arrays.stream(input[lines - 1].trim().replaceAll(" +", " ").split(" ")).toList();

        long total = 0;
        for (int i = 0; i < this.operators.size(); i++) {
            total = total + this.figureProblem(i);
        }
        return String.valueOf(total);
    }

    private long figureProblem(final int i) {

        final String operator = this.operators.get(i);
        if (operator.equalsIgnoreCase("+")) {
            return this.problems.get(i).stream().reduce(0L, Long::sum);
        }
        if (operator.equalsIgnoreCase("*")) {
            return this.problems.get(i).stream().reduce(1L, (a, b) -> a * b);
        }
        throw new RuntimeException("Found operator '" + operator + "'");
    }

    private void updateProblems(final String s) {

        final String cleanString = s.trim().replaceAll(" +", " ");

        final String[] numbers = cleanString.split(" ");
        final int problemSet = numbers.length;
        if (this.problems.isEmpty()) {
            for (int i = 0; i < problemSet; i++) {
                this.problems.add(new ArrayList<>());
            }
        }
        for (int i = 0; i < problemSet; i++) {
            this.problems.get(i).add(Long.parseLong(numbers[i]));
        }
    }

    @Override
    public String part2(final String[] input) {

        /*

        The format is:
        A block, the width of the longest number, plus a space
        The numbers are written starting far left, and so have 1,2,3 etc spaces after them.
        The operator is at the far left.

        I can read the operators to find out the width of the column
        I could read the whole string in, not trimmed or cleaned up.
        Then I can build up digits.
        Numbers can start anywhere - not necessarily at the top or reaching the bottom.

        There are only 4 rows plus the operators.

         */

        this.problems = new ArrayList<>();
        this.operators = new ArrayList<>();
        this.problemLines = new ArrayList<>();
        this.columnWidths = new ArrayList<>();

        final int problemCount = input[0].trim().replaceAll(" +", " ").split(" ").length;
        this.rowCount = input.length - 1;
        for (int i = 0; i < this.rowCount; i++) {
            this.problemLines.add(input[i]);
        }

        final String operatorLine = input[this.rowCount];
        int lastOperator = 0;
        for (int i = 0; i < operatorLine.length(); i++) {
            final String data = operatorLine.substring(i, i + 1);
            if (data.equalsIgnoreCase(" ")) {
                continue;
            }
            this.operators.add(data);
            if (this.operators.size() > 1) {
                this.columnWidths.add(i - lastOperator - 1); // -1 for the space
                lastOperator = i;
            }
        }
        this.columnWidths.add(operatorLine.length() - lastOperator);

        this.buildProblems();

        long total = 0;
        for (int i = 0; i < this.operators.size(); i++) {
            total = total + this.figureProblem(i);
        }
        return String.valueOf(total);

    }

    private void buildProblems() {

        for (int i = 0; i < this.operators.size(); i++) {
            this.problems.add(this.buildProblem(i));
        }

    }

    private List<Long> buildProblem(final int i) {

        final int colWidth = this.columnWidths.get(i);
        final int displacement = this.sumColumns(i);
        final List<Long> problem = new ArrayList<>();

        for (int numberToBuild = 0; numberToBuild < colWidth; numberToBuild++) {
            String data = "";
            for (int row = 0; row < this.rowCount; row++) {
                final int x = displacement + colWidth - numberToBuild - 1;
                data = data + this.problemLines.get(row).charAt(x);
            }
            problem.add(Long.parseLong(data.trim()));
        }
        return problem;
    }

    private int sumColumns(final int column) {

        int total = 0;
        for (int i = 0; i < column; i++) {
            total = total + this.columnWidths.get(i) + 1; // space
        }
        return total;
    }
}
