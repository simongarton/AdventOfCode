package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Year2021Day4 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 4: Giant Squid";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2021, 4);
    }

    @Override
    public String part1(final String[] input) {
        final List<String> numbers = Arrays.asList(input[0].split(","));
        final List<Board> boards = this.constructBoards(input);
        final int winningBoard = this.scoreBoards(numbers, boards);
        return String.valueOf(winningBoard);
    }

    @Override
    public String part2(final String[] input) {
        final List<String> numbers = Arrays.asList(input[0].split(","));
        final List<Board> boards = this.constructBoards(input);
        final int winningBoard = this.scoreBoardsForLast(numbers, boards);
        return String.valueOf(winningBoard);
    }

    private int scoreBoards(final List<String> numbers, final List<Board> boards) {
        for (int i = 0; i < numbers.size(); i++) {
            final String move = numbers.get(i);
            for (final Board board : boards) {
                if (board.winningMove(move, i)) {
                    return board.score(Integer.parseInt(move));
                }
            }
        }
        throw new RuntimeException("No winner.");
    }

    private int scoreBoardsForLast(final List<String> numbers, final List<Board> boards) {
        int boardsWon = 0;
        for (int i = 0; i < numbers.size(); i++) {
            final String move = numbers.get(i);
            for (final Board board : boards) {
                if (!board.inPlay) {
                    continue;
                }
                if (board.winningMove(move, i)) {
                    boardsWon++;
                    if (boardsWon == boards.size()) {
                        return board.score(Integer.parseInt(move));
                    }
                }
            }
        }
        throw new RuntimeException("No winner.");
    }

    private List<Board> constructBoards(final String[] lines) {
        final List<Board> boards = new ArrayList<>();
        final int boardCount = (lines.length - 1) / 6;
        int startRow = 1;
        for (int boardId = 0; boardId < boardCount; boardId++) {
            final Board board = new Board(boardId);
            for (int row = 1; row < 6; row++) {
                board.addRow(lines[startRow + row], row - 1);
            }
            boards.add(board);
            startRow += 6;
        }
        return boards;
    }

    private static final class Board {

        int id;
        boolean inPlay;
        String[] squares = new String[25];
        boolean[] scores = new boolean[25];

        public Board(final int id) {
            this.id = id;
            this.inPlay = true;
        }

        public void addRow(final String line, final int row) {
            final String cleanLine = line.trim().replace("  ", " ");
            final String[] numbers = cleanLine.split(" ");
            for (int i = 0; i < 5; i++) {
                this.squares[(row * 5) + i] = numbers[i];
            }
        }

        public void printBoard() {
            for (int row = 0; row < 5; row++) {
                final StringBuilder line = new StringBuilder();
                for (int col = 0; col < 5; col++) {
                    line.append(this.padTo(this.squares[(row * 5) + col], 2)).append(" ");
                }
                System.out.println(line);
            }
        }

        public void printBoardScores() {
            for (int row = 0; row < 5; row++) {
                final StringBuilder line = new StringBuilder();
                for (int col = 0; col < 5; col++) {
                    if (this.scores[(row * 5) + col]) {
                        line.append(" * ");
                    } else {
                        line.append(" . ");
                    }
                }
                System.out.println(line);
            }
        }

        public boolean winningMove(final String move, final int moveId) {
            for (int i = 0; i < 25; i++) {
                if (this.squares[i].equalsIgnoreCase(move)) {
                    this.scores[i] = true;
                }
            }
            // no way we can win in less than 5 moves.
            if (moveId < 5) {
                return false;
            }
            for (int row = 0; row < 5; row++) {
                if (this.scores[(row * 5) + 0] &&
                        this.scores[(row * 5) + 1] &&
                        this.scores[(row * 5) + 2] &&
                        this.scores[(row * 5) + 3] &&
                        this.scores[(row * 5) + 4]) {
                    this.inPlay = false;
                    return true;
                }
            }
            for (int col = 0; col < 5; col++) {
                if (this.scores[col + 0] &&
                        this.scores[col + 5] &&
                        this.scores[col + 10] &&
                        this.scores[col + 15] &&
                        this.scores[col + 20]) {
                    this.inPlay = false;
                    return true;
                }
            }
            return false;
        }

        public int score(final int lastMove) {
            int total = 0;
            for (int i = 0; i < 25; i++) {
                if (!this.scores[i]) {
                    total += Integer.parseInt(this.squares[i]);
                }
            }
            return total * lastMove;
        }

        private String padTo(final String s, final int size) {
            if (s.length() > size) {
                return new String(new char[size]).replace("\0", "*");
            }
            return new String(new char[size - s.length()]).replace("\0", " ") + s;
        }
    }
}
