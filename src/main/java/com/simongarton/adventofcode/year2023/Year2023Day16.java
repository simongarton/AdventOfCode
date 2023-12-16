package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.simongarton.adventofcode.year2023.Year2023Day16.Direction.*;

public class Year2023Day16 extends AdventOfCodeChallenge {

    private String cave;
    private String energised;

    private List<Beam> beams;

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
        this.debugCave();

        this.beams = new ArrayList<>();

        this.beams.add(Beam.builder()
                .coord(Coord.builder()
                        .x(-1)
                        .y(0).build())
                .direction(Direction.EAST)
                .build());

        this.shineBeams();

        this.debugEnergised();

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

    private void blankLine() {
        System.out.println();
    }

    private void shineBeams() {

        while (!this.beams.isEmpty()) {
            final Beam beam = this.beams.get(0);
            while (true) {
                this.shine(beam);
                this.debugEnergised();
                this.checkOutOfBounds(beam);
                if (beam.isOutOfBounds()) {
                    this.beams.remove(beam);
                    break;
                }
            }
        }
    }

    private void shine(final Beam beam) {

        final Coord c = beam.getCoord();

        this.move(beam);
        if (!beam.isOutOfBounds()) {
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

        final Coord c = beam.getCoord();
        switch (beam.getDirection()) {
            case NORTH:
                beam.setDirection(WEST);
                beam.setCoord(Coord.builder().x(c.getX() - 1).y(c.getY()).build());
                break;
            case EAST:
                beam.setDirection(SOUTH);
                beam.setCoord(Coord.builder().x(c.getX()).y(c.getY() + 1).build());
                break;
            case SOUTH:
                beam.setDirection(EAST);
                beam.setCoord(Coord.builder().x(c.getX() + 1).y(c.getY()).build());
                break;
            case WEST:
                beam.setDirection(NORTH);
                beam.setCoord(Coord.builder().x(c.getX()).y(c.getY() - 1).build());
                break;
        }
        if (!this.checkOutOfBounds(beam)) {
            this.energize(beam.getCoord());
        }
        return true;
    }

    private boolean reflectForward(final Beam beam) {

        // /
        final Coord c = beam.getCoord();
        switch (beam.getDirection()) {
            case NORTH:
                beam.setDirection(EAST);
                beam.setCoord(Coord.builder().x(c.getX() + 1).y(c.getY()).build());
                break;
            case EAST:
                beam.setDirection(NORTH);
                beam.setCoord(Coord.builder().x(c.getX()).y(c.getY() - 1).build());
                break;
            case SOUTH:
                beam.setDirection(WEST);
                beam.setCoord(Coord.builder().x(c.getX() - 1).y(c.getY()).build());
                break;
            case WEST:
                beam.setDirection(SOUTH);
                beam.setCoord(Coord.builder().x(c.getX()).y(c.getY() + 1).build());
                break;
        }
        if (!this.checkOutOfBounds(beam)) {
            this.energize(beam.getCoord());
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
                .coord(Coord.builder().x(c.getX() - 1).y(c.getY()).build())
                .direction(WEST)
                .build();
        beam.setDirection(EAST);
        beam.setCoord(Coord.builder().x(c.getX() + 1).y(c.getY()).build());

        this.safeEnergize(beam.getCoord());
        this.safeEnergize(newBeam.getCoord());

        this.beams.add(newBeam);
        return true;
    }


    private boolean handleVerticalSplit(final Beam beam) {

        final Coord c = beam.getCoord();
        final Beam newBeam = Beam.builder()
                .coord(Coord.builder().x(c.getX()).y(c.getY() + 1).build())
                .direction(SOUTH)
                .build();
        beam.setDirection(NORTH);
        beam.setCoord(Coord.builder().x(c.getX()).y(c.getY() - 1).build());

        this.safeEnergize(beam.getCoord());
        this.safeEnergize(newBeam.getCoord());

        this.beams.add(newBeam);
        return true;
    }

    private void safeEnergize(Coord coord) {
        try {
            this.energize(coord);
        } catch (Exception e) {
            
        }
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

        this.energised = "";
        for (int i = 0; i < this.height; i++) {
            this.energised = this.energised + ".".repeat(this.width);
        }
    }

    @Override
    public String part2(final String[] input) {
        return null;
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

        private Coord coord;
        private Direction direction;

        public boolean outOfBounds = false;
    }

}
