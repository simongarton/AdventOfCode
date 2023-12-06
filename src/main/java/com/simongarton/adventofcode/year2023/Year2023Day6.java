package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2023Day6 extends AdventOfCodeChallenge {

    private List<Long> times;
    private List<Long> distances;
    private List<Race> races;

    @Override
    public String title() {
        return "Day 6: Wait For It";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 6);
    }

    @Override
    public String part1(final String[] input) {

        this.loadData(input);

        long wins = 1;
        for (final Race race : this.races) {
            final long newWins = this.countWins(race);
            wins = wins * newWins;
        }

        return String.valueOf(wins);
    }

    private long countWinsSmartly(final Race race) {
        long wins = 0;
        for (long speed = 1; speed < race.distance; speed++) {
            final long timeAvailable = race.time - speed;
            if (timeAvailable < 0) {
                break;
            }
            final Result result = this.figureTimeTakenSmartly(speed, race.distance, timeAvailable);
            if (result.isSuccess()) {
                wins++;
            }
        }
        return wins;
    }

    private long countWins(final Race race) {
        long wins = 0;
        for (long speed = 1; speed < race.distance; speed++) {
            final long timeAvailable = race.time - speed;
            final Result result = this.figureTimeTakenSmartly(speed, race.distance, timeAvailable);
            if (result.isSuccess()) {
                wins++;
            }
        }
        return wins;
    }

    private Result figureTimeTakenSmartly(final long speed, final long distance, final long timeAvailable) {
        final long distanceCovered = timeAvailable * speed;
        final long timeTaken = distance / speed;
        if (distanceCovered > distance) {
            return Result.builder()
                    .speed(speed)
                    .distance(distance)
                    .timeAvailable(timeAvailable)
                    .distanceCovered(distanceCovered)
                    .timeTaken(timeTaken)
                    .success(true)
                    .build();
        } else {
            return Result.builder()
                    .speed(speed)
                    .distance(distance)
                    .timeAvailable(timeAvailable)
                    .distanceCovered(distanceCovered)
                    .timeTaken(timeTaken)
                    .success(false)
                    .build();
        }
    }

    private Result figureTimeTaken(final long speed, final long distance, final long timeAvailable) {
        long timeTaken = 0;
        long distanceCovered = 0;
        while (distanceCovered <= distance) {
            timeTaken++;
            distanceCovered += speed;
            if (timeTaken >= timeAvailable) {
                break;
            }
        }
        if (distanceCovered > distance) {
            return Result.builder()
                    .speed(speed)
                    .distance(distance)
                    .timeAvailable(timeAvailable)
                    .distanceCovered(distanceCovered)
                    .timeTaken(timeTaken)
                    .success(true)
                    .build();
        } else {
            return Result.builder()
                    .speed(speed)
                    .distance(distance)
                    .timeAvailable(timeAvailable)
                    .distanceCovered(distanceCovered)
                    .timeTaken(timeTaken)
                    .success(false)
                    .build();
        }
    }

    private void loadData(final String[] input) {
        this.loadTimes(input[0]);
        this.loadDistances(input[1]);
        this.buildRaces();
    }

    private void loadDataOneRace(final String[] input) {
        this.loadTimesAsOne(input[0]);
        this.loadDistancesAsOne(input[1]);
        this.buildRaces();
    }

    private void loadTimesAsOne(final String s) {
        final String[] bits = this.reallyCleanBits(s);
        this.times = Arrays.stream(bits).map(Long::valueOf).collect(Collectors.toList());
    }

    private void loadDistancesAsOne(final String s) {
        final String[] bits = this.reallyCleanBits(s);
        this.distances = Arrays.stream(bits).map(Long::valueOf).collect(Collectors.toList());
    }

    private void buildRaces() {
        this.races = new ArrayList<>();
        for (int i = 0; i < this.distances.size(); i++) {
            this.races.add(Race.builder()
                    .distance(this.distances.get(i))
                    .time(this.times.get(i))
                    .build());
        }
    }

    private void loadDistances(final String s) {
        final String[] bits = this.cleanBits(s);
        this.distances = Arrays.stream(bits).map(Long::valueOf).collect(Collectors.toList());
    }

    private String[] cleanBits(final String s) {
        final String[] parts = s.split(":");
        final String bitsString = this.cleanString(parts[1]);
        final String[] bits = bitsString.split(" ");
        return bits;
    }

    private String[] reallyCleanBits(final String s) {
        final String[] parts = s.split(":");
        final String bitsString = this.reallyCleanString(parts[1]);
        final String[] bits = bitsString.split(" ");
        return bits;
    }

    private void loadTimes(final String s) {
        final String[] bits = this.cleanBits(s);
        this.times = Arrays.stream(bits).map(Long::valueOf).collect(Collectors.toList());
    }

    private String cleanString(String example) {
        example = example.trim();
        while (example.contains("  ")) {
            example = example.replace("  ", " ");
        }
        return example;
    }

    private String reallyCleanString(final String example) {
        return example.trim().replace(" ", "");
    }

    @Override
    public String part2(final String[] input) {

        this.loadDataOneRace(input);

        long wins = 1;
        for (final Race race : this.races) {
            final long newWins = this.countWinsSmartly(race);
            wins = wins * newWins;
        }

        return String.valueOf(wins);
    }

    @Data
    @Builder
    private static final class Race {
        private long time;
        private long distance;
    }

    @Data
    @Builder
    private static final class Result {
        private long speed;
        private long distance;
        private long timeAvailable;
        private long timeTaken;
        private long distanceCovered;
        private boolean success;
    }
}


