package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class Year2023Day2 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 2: Cube Conundrum";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 2);
    }

    @Override
    public String part1(final String[] input) {
        final Hand testHand = new Hand(12, 13, 14);
        long score = 0;
        for (final String line : input) {
            final Game game = this.readGame(line);
            this.playGame(game, testHand);
            if (game.isPossible()) {
                score += game.getId();
            }
        }
        return String.valueOf(score);
    }

    private void playGame(final Game game, final Hand testHand) {
        boolean possible = true;
        for (final Hand hand : game.getHands()) {
            if (hand.getRed() > testHand.getRed()) {
                possible = false;
                break;
            }
            if (hand.getGreen() > testHand.getGreen()) {
                possible = false;
                break;
            }
            if (hand.getBlue() > testHand.getBlue()) {
                possible = false;
                break;
            }
        }
        game.setPossible(possible);
    }

    private Game readGame(final String line) {
        final String[] mainParts = line.split(":");
        final long id = Long.valueOf(mainParts[0].replace("Game ", ""));
        final String[] handLines = mainParts[1].split(";");
        final List<Hand> hands = new ArrayList<>();
        for (final String handLine : handLines) {
            final int red = this.countCubes("red", handLine);
            final int green = this.countCubes("green", handLine);
            final int blue = this.countCubes("blue", handLine);
            final Hand hand = new Hand(red, green, blue);
            hands.add(hand);
        }
        final Game game = new Game();
        game.setId(id);
        game.setHands(hands);
        return game;
    }

    private int countCubes(final String color, final String line) {
        final String[] cubeParts = line.split(",");
        int cubes = 0;
        for (final String cube : cubeParts) {
            final String[] bits = cube.trim().split(" ");
            if (bits[1].equalsIgnoreCase(color)) {
                cubes += Integer.parseInt(bits[0]);
            }
        }
        return cubes;
    }

    @Override
    public String part2(final String[] input) {
        long score = 0;
        for (final String line : input) {
            final Hand bestHand = new Hand(0, 0, 0);
            final Game game = this.readGame(line);
            this.calculateGame(game, bestHand);
            score = score + ((long) bestHand.getRed() * bestHand.getGreen() * bestHand.getBlue());
        }
        return String.valueOf(score);
    }

    private void calculateGame(final Game game, final Hand bestHand) {
        for (final Hand hand : game.getHands()) {
            bestHand.setRed(Math.max(hand.getRed(), bestHand.getRed()));
            bestHand.setGreen(Math.max(hand.getGreen(), bestHand.getGreen()));
            bestHand.setBlue(Math.max(hand.getBlue(), bestHand.getBlue()));
        }
    }

    @Data
    private static class Game {

        private long id;
        List<Hand> hands;
        private boolean possible;
    }

    @Data
    @AllArgsConstructor
    private static class Hand {

        private int red;
        private int green;
        private int blue;
    }
}
