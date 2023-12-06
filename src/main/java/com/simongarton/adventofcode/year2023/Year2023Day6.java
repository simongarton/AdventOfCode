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
        return "Day 6: XXX";
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
//            System.out.println(race + " " + newWins + "=" + wins);
        }

        return String.valueOf(wins);
    }

    private long countWins(final Race race) {
        long wins = 0;
        for (long speed = 1; speed < race.distance; speed++) {
            final long timeAvailable = race.time - speed;
            final Result result = this.figureTimeTaken(speed, race.distance, timeAvailable);
            if (result.isSuccess()) {
//                System.out.println("  At speed " + speed + " timeTaken was " + result.getTimeTaken() + " distance covered " + result.getDistanceCovered() + " and race was " + race.getTime() + " and won");
                wins++;
            } else {
//                System.out.println("  At speed " + speed + " timeTaken was " + result.getTimeTaken() + " distance covered " + result.getDistanceCovered() + " and race was " + race.getTime() + " but lost");
            }
//            System.out.println(result);
        }
        return wins;
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
        final String[] parts = s.split(":");
        final String distancesString = this.cleanString(parts[1]);
        final String[] distances = distancesString.split(" ");
        this.distances = Arrays.stream(distances).map(Long::valueOf).collect(Collectors.toList());
    }

    private void loadTimes(final String s) {
        final String[] parts = s.split(":");
        final String timesString = this.cleanString(parts[1]);
        final String[] times = timesString.split(" ");
        this.times = Arrays.stream(times).map(Long::valueOf).collect(Collectors.toList());
    }

    private String cleanString(String example) {
        example = example.trim();
        while (example.contains("  ")) {
            example = example.replace("  ", " ");
        }
        return example;
    }

    @Override
    public String part2(final String[] input) {

        return null;
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


