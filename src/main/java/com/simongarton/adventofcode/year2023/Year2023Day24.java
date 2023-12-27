package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Year2023Day24 extends AdventOfCodeChallenge {

    private List<Hailstone> hailstones;
    private final long rangeLow = 200000000000000L;
    private final long rangeHigh = 400000000000000L;

    @Override
    public String title() {
        return "Day 24: Never Tell Me The Odds";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 24);
    }

    @Override
    public String part1(final String[] input) {

        this.loadHailstones(input);
        int scores = 0;
        for (int i = 0; i < this.hailstones.size(); i++) {
            for (int j = i + 1; j < this.hailstones.size(); j++) {
                if (i == j) {
                    continue;
                }
                final Hailstone a = this.hailstones.get(i);
                final Hailstone b = this.hailstones.get(j);
                final Optional<Coord> optionalCoord = this.collideInFuture(a, b);
                if (optionalCoord.isPresent()) {
                    final Coord coord = optionalCoord.get();
//                    System.out.println("collision for " + a.getId() + " -> " + b.getId() + " at " + coord);
                    scores++;
                } else {
//                    System.out.println("No collision for " + a.getId() + " -> " + b.getId());
                }
            }
        }

        return String.valueOf(scores);
    }

    private Optional<Coord> collideInFuture(final Hailstone a, final Hailstone b) {

        final Equation equationA = this.figureEquation(a);
        final Equation equationB = this.figureEquation(b);

        return this.solveForFuture(a, b, equationA, equationB);
    }

    private Optional<Coord> solveForFuture(final Hailstone hailstoneA, final Hailstone hailstoneB, final Equation equationA, final Equation equationB) {
        if (equationA.getSlope() == equationB.getSlope()) {
//            System.out.println("slopes parallel, won't collide");
            return Optional.empty();
        }

        // thanks ChatGPT.

        final double a = -equationA.getSlope();
        final double b = 1;
        final double e = equationA.getIntercept();
        final double c = -equationB.getSlope();
        final double d = 1;
        final double f = equationB.getIntercept();

        final double x = this.solveForX(a, b, c, d, e, f);
        final double y = this.solveForY(a, b, c, d, e, f);

        if (hailstoneA.getVx() == 0) {
            throw new RuntimeException("worried about verticals - not handling them");
        }

        if (!this.checkStuff(hailstoneA.getVx() > 0, x < hailstoneA.getPx(), "Already gone past a x (forwards)")) {
            return Optional.empty();
        }
        if (!this.checkStuff(hailstoneA.getVy() > 0, y < hailstoneA.getPy(), "Already gone past a y (forwards)")) {
            return Optional.empty();
        }
        if (!this.checkStuff(hailstoneB.getVx() > 0, x < hailstoneB.getPx(), "Already gone past b x (forwards)")) {
            return Optional.empty();
        }
        if (!this.checkStuff(hailstoneB.getVy() > 0, y < hailstoneB.getPy(), "Already gone past b y (forwards)")) {
            return Optional.empty();
        }
        if (!this.checkStuff(hailstoneA.getVx() < 0, x > hailstoneA.getPx(), "Already gone past a x (backwards)")) {
            return Optional.empty();
        }
        if (!this.checkStuff(hailstoneA.getVy() < 0, y > hailstoneA.getPy(), "Already gone past a y (backwards)")) {
            return Optional.empty();
        }
        if (!this.checkStuff(hailstoneB.getVx() < 0, x > hailstoneB.getPx(), "Already gone past b x (backwards)")) {
            return Optional.empty();
        }
        if (!this.checkStuff(hailstoneB.getVy() < 0, y > hailstoneB.getPy(), "Already gone past b y (backwards)")) {
            return Optional.empty();
        }

        if (x < this.rangeLow || x > this.rangeHigh || y < this.rangeLow || y > this.rangeHigh) {
//            System.out.println("Out of range");
            return Optional.empty();
        }

        final Coord coord = Coord.builder()
                .x(x)
                .y(y)
                .build();

//        System.out.println("Will collide at " + coord);
        return Optional.of(coord);
    }

    private boolean checkStuff(final boolean test1, final boolean test2, final String message) {
        if (test1 && test2) {
//            System.out.println(message);
            return false;
        }
        return true;
    }

    private double solveForX(final double a, final double b, final double c, final double d, final double e, final double f) {

        return (e * d - b * f) / (a * d - b * c);
    }

    private double solveForY(final double a, final double b, final double c, final double d, final double e, final double f) {

        return (a * f - e * c) / (a * d - b * c);
    }

    private Equation figureEquation(final Hailstone hailstone) {

        final Coord first = Coord.builder()
                .x(hailstone.getPx())
                .y(hailstone.getPy())
                .build();
        final Coord second = Coord.builder()
                .x(hailstone.getPx() + hailstone.getVx())
                .y(hailstone.getPy() + hailstone.getVy())
                .build();

        final double slope = (second.getY() - first.getY()) / (second.getX() - first.getX());
        final double intercept = hailstone.getPy() - (slope * hailstone.getPx());

        return Equation.builder()
                .slope(slope)
                .intercept(intercept)
                .build();
    }

    private void loadHailstones(final String[] input) {

        this.hailstones = new ArrayList<>();
        for (final String line : input) {
            this.loadHailstone(line);
        }
    }

    private void loadHailstone(final String line) {

        final String[] parts = line.split("@");
        final String[] positions = parts[0].trim().split(", ");
        final String[] velocities = parts[1].trim().split(", ");
        final Hailstone hailstone = Hailstone.builder()
                .id("" + (char) (65 + this.hailstones.size()))
                .px(Long.parseLong(positions[0].trim()))
                .py(Long.parseLong(positions[1].trim()))
                .pz(Long.parseLong(positions[2].trim()))
                .vx(Long.parseLong(velocities[0].trim()))
                .vy(Long.parseLong(velocities[1].trim()))
                .vz(Long.parseLong(velocities[2].trim()))
                .build();
        this.hailstones.add(hailstone);
    }

    @Override
    public String part2(final String[] input) {

        return String.valueOf(-1);
    }

    @Data
    @Builder
    private static final class Equation {

        double slope;
        double intercept;

        @Override
        public String toString() {
            return String.format("y=%3.2fx + %3.2f", this.slope, this.intercept);
        }

    }

    @Data
    @Builder
    private static final class Coord {

        private double x;
        private double y;

    }

    @Data
    @Builder
    private static final class Hailstone {

        private String id;

        private long px;
        private long py;
        private long pz;

        private long vx;
        private long vy;
        private long vz;
    }
}
