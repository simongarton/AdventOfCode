package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2022Day2 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 2: Rock Paper Scissors";
    }

    @Override
    public AdventOfCodeChallenge.Outcome run() {
        return this.runChallenge(2022, 2);
    }

    @Override
    public String part1(final String[] input) {

        int score = 0;

        for (final String game : input) {
            final String[] moves = game.split(" ");
            final Move opponent = this.translateOpponent(moves[0]);
            final Move me = this.translateMe(moves[1]);
            final Outcome outcome = this.figureOutcome(opponent, me);
            score = score + this.outcomeValue(outcome) + this.moveValue(me);
        }

        return String.valueOf(score);
    }

    @Override
    public String part2(final String[] input) {

        int score = 0;
        for (final String game : input) {
            final String[] moves = game.split(" ");
            final Move opponent = this.translateOpponent(moves[0]);
            final Outcome requiredOutcome = this.translateOutcome(moves[1]);
            final Move me = this.findRequiredMoveForOutcome(requiredOutcome, opponent);
            score = score + this.outcomeValue(requiredOutcome) + this.moveValue(me);
        }

        return String.valueOf(score);
    }

    public enum Outcome {
        WIN,
        LOSS,
        DRAW
    }

    public enum Move {
        ROCK,
        PAPER,
        SCISSORS
    }

    private int moveValue(final Move move) {
        switch (move) {
            case ROCK:
                return 1;
            case PAPER:
                return 2;
            case SCISSORS:
                return 3;
            default:
                throw new RuntimeException("Invalid move " + move);
        }
    }

    private int outcomeValue(final Outcome outcome) {
        switch (outcome) {
            case WIN:
                return 6;
            case LOSS:
                return 0;
            case DRAW:
                return 3;
            default:
                throw new RuntimeException("Invalid outcome " + outcome);
        }
    }

    private Move translateOpponent(final String move) {
        switch (move) {
            case "A":
                return Move.ROCK;
            case "B":
                return Move.PAPER;
            case "C":
                return Move.SCISSORS;
            default:
                throw new RuntimeException("Invalid move " + move);
        }
    }

    private Move translateMe(final String move) {
        switch (move) {
            case "X":
                return Move.ROCK;
            case "Y":
                return Move.PAPER;
            case "Z":
                return Move.SCISSORS;
            default:
                throw new RuntimeException("Invalid move " + move);
        }
    }

    private Outcome figureOutcome(
            final Move opponent,
            final Move me
    ) {

        if (opponent.equals(me)) {
            return Outcome.DRAW;
        }
        if (opponent.equals(Move.ROCK) && me.equals(Move.PAPER)) {
            return Outcome.WIN;
        }
        if (opponent.equals(Move.PAPER) && me.equals(Move.ROCK)) {
            return Outcome.LOSS;
        }
        if (opponent.equals(Move.PAPER) && me.equals(Move.SCISSORS)) {
            return Outcome.WIN;
        }
        if (opponent.equals(Move.SCISSORS) && me.equals(Move.PAPER)) {
            return Outcome.LOSS;
        }
        if (opponent.equals(Move.SCISSORS) && me.equals(Move.ROCK)) {
            return Outcome.WIN;
        }
        if (opponent.equals(Move.ROCK) && me.equals(Move.SCISSORS)) {
            return Outcome.LOSS;
        }
        throw new RuntimeException("Missed combination");
    }

    private Outcome translateOutcome(final String outcome) {
        switch (outcome) {
            case "X":
                return Outcome.LOSS;
            case "Y":
                return Outcome.DRAW;
            case "Z":
                return Outcome.WIN;
            default:
                throw new RuntimeException("Invalid outcome " + outcome);
        }
    }

    private Move findRequiredMoveForOutcome(
            final Outcome outcome,
            final Move opponent
    ) {
        switch (outcome) {
            case LOSS:
                switch (opponent) {
                    case ROCK:
                        return Move.SCISSORS;
                    case PAPER:
                        return Move.ROCK;
                    case SCISSORS:
                        return Move.PAPER;
                    default:
                        throw new RuntimeException("Missed combination");
                }
            case DRAW:
                return opponent;
            case WIN:
                switch (opponent) {
                    case ROCK:
                        return Move.PAPER;
                    case PAPER:
                        return Move.SCISSORS;
                    case SCISSORS:
                        return Move.ROCK;
                    default:
                        throw new RuntimeException("Missed combination");
                }
            default:
                throw new RuntimeException("Invalid outcome " + outcome);
        }
    }
}
