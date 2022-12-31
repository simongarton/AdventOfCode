package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.HashMap;
import java.util.Map;

public class Year2021Day21 extends AdventOfCodeChallenge {

    private static final int TRACK_END = 10;
    private static final int PART2_WIN = 21;

    private static final Map<Integer, Integer> combinedRolls = new HashMap<>();
    private final Map<Integer, Long> wins = new HashMap<>();

    static {
        combinedRolls.put(3, 1);
        combinedRolls.put(4, 3);
        combinedRolls.put(5, 6);
        combinedRolls.put(6, 7);
        combinedRolls.put(7, 6);
        combinedRolls.put(8, 3);
        combinedRolls.put(9, 1);
    }

    @Override
    public String title() {
        return "Day 21: Dirac Dice";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2021, 21);
    }

    @Override
    public String part1(final String[] input) {
        final Player one = new Player(1, this.getPosition(input[0]));
        final Player two = new Player(2, this.getPosition(input[1]));
        final Die die = new DeterministicDie();
        final long loser;
        while (true) {
            if (one.roll3Times(die)) {
                loser = two.score;
                break;
            }
            if (two.roll3Times(die)) {
                loser = one.score;
                break;
            }
        }
        final long result = (3L * (one.moves + two.moves)) * loser;
        return String.valueOf(result);
    }

    @Override
    public String part2(final String[] input) {
        final Player one = new Player(1, this.getPosition(input[0]));
        final Player two = new Player(2, this.getPosition(input[1]));
        this.wins.clear();
        this.wins.put(1, 0L);
        this.wins.put(2, 0L);
        this.playAllTheGames(one, two);
        final long result = Math.max(this.wins.get(1), this.wins.get(2));
        return String.valueOf(result);
    }

    private int getPosition(final String line) {
        final String[] parts = line.split(": ");
        return Integer.parseInt(parts[1]);
    }
    
    private void playAllTheGames(final Player one, final Player two) {
        this.playPlayerOne(one, two, 1);
    }

    private void playPlayerOne(final Player one, final Player two, final long gameCount) {
        if (two.score >= PART2_WIN) {
            this.wins.put(two.id, this.wins.get(two.id) + gameCount);
            return;
        }
        for (final Map.Entry<Integer, Integer> entry : combinedRolls.entrySet()) {
            final Player newPlayer = new Player(one.id, one.position, one.moves, one.score, one.sequence);
            newPlayer.moveAndScore(entry.getKey());
            this.playPlayerTwo(newPlayer, two, gameCount * entry.getValue());
        }
    }

    private void playPlayerTwo(final Player one, final Player two, final long gameCount) {
        if (one.score >= PART2_WIN) {
            this.wins.put(one.id, this.wins.get(one.id) + gameCount);
            return;
        }
        for (final Map.Entry<Integer, Integer> entry : combinedRolls.entrySet()) {
            final Player newPlayer = new Player(two.id, two.position, two.moves, two.score, two.sequence);
            newPlayer.moveAndScore(entry.getKey());
            this.playPlayerOne(one, newPlayer, gameCount * entry.getValue());
        }
    }

    public static final class Player {
        final int id;
        int score;
        int position;
        int moves;
        String sequence;

        public Player(final int id, final int position) {
            this.id = id;
            this.score = 0;
            this.moves = 0;
            this.position = position;
            this.sequence = "";
        }

        public Player(final int id, final int position, final int moves, final int score, final String sequence) {
            this.id = id;
            this.score = score;
            this.moves = moves;
            this.position = position;
            this.sequence = sequence;
        }

        public boolean roll3Times(final Die die) {
            final int roll1 = die.roll();
            final int roll2 = die.roll();
            final int roll3 = die.roll();
            this.moveTo(roll1 + roll2 + roll3);
            this.score += this.position;
            this.moves += 1;
            return (this.score >= 1000);
        }

        private void moveTo(final int roll) {
            this.position = this.position + roll;
            this.sequence = this.sequence + roll;
            while (this.position > TRACK_END) {
                this.position -= TRACK_END;
            }
        }

        @Override
        public String toString() {
            return "Player " + this.id + " (moves " + this.moves + " score " + this.score + " position " + this.position + " sequence " + this.sequence + ")";
        }

        public void moveAndScore(final Integer roll) {
            this.moveTo(roll);
            this.moves++;
            this.score += this.position;
        }
    }

    public interface Die {

        int roll();
    }

    public static final class DeterministicDie implements Die {

        int value;

        public DeterministicDie() {
            this.value = 1;
        }

        @Override
        public int roll() {
            final int result = this.value;
            this.value++;
            if (this.value > 100) {
                this.value = 1;
            }
            return result;
        }
    }
}
