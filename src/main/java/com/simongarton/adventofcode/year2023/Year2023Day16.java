package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.simongarton.adventofcode.year2023.Year2023Day16.Direction.*;

public class Year2023Day16 extends AdventOfCodeChallenge {

    private static int id = 0;

    private String cave;
    private String energised;

    private List<Beam> beams;

    private Set<String> cache;

    private int width;
    private int height;

    @Override
    public String title() {
        return "Day 16: The Floor Will Be Lava";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 16);
    }

    @Override
    public String part1(final String[] input) {

        this.loadCave(input);

        this.beams = new ArrayList<>();
        this.cache = new HashSet<>();

        this.beams.add(Beam.builder()
                .id(Year2023Day16.id++)
                .coord(Coord.builder()
                        .x(-1)
                        .y(0).build())
                .direction(Direction.EAST)
                .build());

        this.shineBeams();

        return String.valueOf(this.countEnergised());
    }

    private void debugEnergised() {

        for (int row = 0; row < this.height; row++) {
            final String line = this.energised.substring(row * this.width, (row + 1) * this.width);
            System.out.println(line);
        }
        this.blankLine();
    }

    private long countEnergised() {

        return this.energised.chars().filter(ch -> ch == '#').count();
    }

    private void debugCave() {

        for (int row = 0; row < this.height; row++) {
            final String line = this.cave.substring(row * this.width, (row + 1) * this.width);
            System.out.println(line);
        }
        this.blankLine();
    }

    private void shineBeams() {

        while (!this.beams.isEmpty()) {
            final Beam beam = this.beams.get(0);
            while (true) {
                this.shine(beam);
                this.checkOutOfBounds(beam);
                if (beam.isOutOfBounds()) {
                    this.beams.remove(beam);
                    break;
                }
                final String key = this.buildKey(beam);
                if (this.cache.contains(key)) {
                    this.beams.remove(beam);
                    break;
                }
                this.cache.add(key);
            }
        }
    }

    private String buildKey(final Beam beam) {

        return beam.getCoord() + "->" + beam.getDirection();
    }

    private void shine(final Beam beam) {

        this.move(beam);
        if (!beam.isOutOfBounds()) {
            this.energize(beam.getCoord());
            this.handle(beam);
        }
    }

    private boolean handle(final Beam beam) {

        final String floor = this.getFloor(beam.getCoord());
        if (floor.equalsIgnoreCase(".")) {
            return false;
        }
        if (floor.equalsIgnoreCase("\\")) {
            return this.reflectBack(beam);
        }
        if (floor.equalsIgnoreCase("/")) {
            return this.reflectForward(beam);
        }
        if (floor.equalsIgnoreCase("-") || floor.equalsIgnoreCase("|")) {
            return this.split(beam, floor);
        }
        throw new RuntimeException("lost");
    }

    private boolean reflectBack(final Beam beam) {

        return this.reflect(beam, false);
    }

    private boolean reflectForward(final Beam beam) {
        return this.reflect(beam, true);
    }

    private boolean reflect(final Beam beam, final boolean forward) {

        switch (beam.getDirection()) {
            case NORTH:
                if (forward) {
                    beam.setDirection(EAST);
                } else {
                    beam.setDirection(WEST);
                }
                break;
            case EAST:
                if (forward) {
                    beam.setDirection(NORTH);
                } else {
                    beam.setDirection(SOUTH);
                }
                break;
            case SOUTH:
                if (forward) {
                    beam.setDirection(WEST);
                } else {
                    beam.setDirection(EAST);
                }
                break;
            case WEST:
                if (forward) {
                    beam.setDirection(SOUTH);
                } else {
                    beam.setDirection(NORTH);
                }
                break;
        }
        return true;
    }

    private boolean split(final Beam beam, final String floor) {

        if (floor.equalsIgnoreCase("|")) {
            if (List.of(NORTH, SOUTH).contains(beam.getDirection())) {
                return false;
            }
            return this.handleVerticalSplit(beam);
        }
        if (floor.equalsIgnoreCase("-")) {
            if (List.of(EAST, WEST).contains(beam.getDirection())) {
                return false;
            }
            return this.handleHorizontalSplit(beam);
        }

        throw new RuntimeException(floor);
    }

    private boolean handleHorizontalSplit(final Beam beam) {

        final Coord c = beam.getCoord();
        final Beam newBeam = Beam.builder()
                .id(Year2023Day16.id++)
                .coord(Coord.builder().x(c.getX()).y(c.getY()).build())
                .direction(WEST)
                .build();
        beam.setDirection(EAST);
//        System.out.println(beam.getId() + " horizontal coming in from " + beam.getDirection() + " at " + c + " created " + newBeam.getId());

        this.beams.add(newBeam);
        return true;
    }


    private boolean handleVerticalSplit(final Beam beam) {

        final Coord c = beam.getCoord();
        final Beam newBeam = Beam.builder()
                .id(Year2023Day16.id++)
                .coord(Coord.builder().x(c.getX()).y(c.getY()).build())
                .direction(SOUTH)
                .build();
        beam.setDirection(NORTH);
//        System.out.println(beam.getId() + " vertical coming in from " + beam.getDirection() + " at " + c + " created " + newBeam.getId());

        this.beams.add(newBeam);
        return true;
    }

    private String getFloor(final Coord coord) {

        final int index = (coord.getY() * this.height) + coord.getX();
        return this.cave.substring(index, index + 1);
    }

    private void move(final Beam beam) {

        final Coord c = beam.getCoord();
        switch (beam.getDirection()) {
            case NORTH:
                beam.setCoord(Coord.builder().x(c.getX()).y(c.getY() - 1).build());
                break;
            case EAST:
                beam.setCoord(Coord.builder().x(c.getX() + 1).y(c.getY()).build());
                break;
            case SOUTH:
                beam.setCoord(Coord.builder().x(c.getX()).y(c.getY() + 1).build());
                break;
            case WEST:
                beam.setCoord(Coord.builder().x(c.getX() - 1).y(c.getY()).build());
                break;
        }
        if (!this.checkOutOfBounds(beam)) {
            this.energize(beam.getCoord());
        }
    }

    private boolean checkOutOfBounds(final Beam beam) {

        boolean outOfBounds = false;
        if (beam.getCoord().getX() < 0 || beam.getCoord().getX() >= this.width) {
            outOfBounds = true;
        }
        if (beam.getCoord().getY() < 0 || beam.getCoord().getY() >= this.height) {
            outOfBounds = true;
        }
        beam.setOutOfBounds(outOfBounds);
        return outOfBounds;
    }

    private void energize(final Coord coord) {

        this.energised = this.replaceCharacter(this.energised, coord);
    }

    private String replaceCharacter(final String energised, final Coord coord) {

        final int index = (coord.getY() * this.height) + coord.getX();
        return this.replaceCharacter(energised, index, "#");
    }

    private String replaceCharacter(final String original, final Integer index, final String replacement) {

        return original.substring(0, index) + replacement + original.substring(index + 1);
    }

    private void loadCave(final String[] input) {

        this.cave = String.join("", input);
        this.width = input[0].length();
        this.height = input.length;

        this.buildEnergised();
    }

    private void buildEnergised() {

        this.energised = "";
        for (int i = 0; i < this.height; i++) {
            this.energised = this.energised + ".".repeat(this.width);
        }
    }

    @Override
    public String part2(final String[] input) {

        this.loadCave(input);

        long maxScore = 0;
        long score;

        for (int i = 0; i < this.width; i++) {
            score = this.getScore(i, -1, SOUTH);
            maxScore = Math.max(score, maxScore);
            score = this.getScore(i, this.height, NORTH);
            maxScore = Math.max(score, maxScore);
        }
        score = this.getScore(-1, 0, EAST);
        maxScore = Math.max(score, maxScore);
        score = this.getScore(this.width, 0, WEST);
        maxScore = Math.max(score, maxScore);

        for (int j = 1; j < this.height - 1; j++) {
            score = this.getScore(-1, j, EAST);
            maxScore = Math.max(score, maxScore);
            score = this.getScore(this.width, j, WEST);
            maxScore = Math.max(score, maxScore);
        }

        return String.valueOf(maxScore);
    }

    private long getScore(final int i, final int j, final Direction direction) {

        this.beams = new ArrayList<>();
        this.cache = new HashSet<>();
        this.buildEnergised();

        this.beams.add(Beam.builder()
                .id(Year2023Day16.id++)
                .coord(Coord.builder()
                        .x(i)
                        .y(j).build())
                .direction(direction)
                .build());

        this.shineBeams();

        return this.countEnergised();
    }

    @Data
    @Builder
    private static final class Coord {

        private int x;
        private int y;
    }

    public enum Direction {
        NORTH, EAST, SOUTH, WEST
    }

    @Data
    @Builder
    private static final class Beam {

        private int id;
        private Coord coord;
        private Direction direction;

        public boolean outOfBounds;
    }
}
