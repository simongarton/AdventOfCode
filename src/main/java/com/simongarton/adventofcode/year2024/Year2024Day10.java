package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Year2024Day10 extends AdventOfCodeChallenge {

    private List<Trail> trails;
    private List<Trail> successfulTrails;
    private Map<Coord, List<Coord>> trailHeadScores; // from 0 -> [9]

    @Override
    public String title() {
        return "Day 10: Hoof It";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 10);
    }

    @Override
    public String part1(final String[] input) {

        this.loadChallengeMap(input);
        final List<Coord> trailHeads = this.findTrailHeads();

        this.trails = new ArrayList<>();
        this.trailHeadScores = new HashMap<>();
        for (final Coord c : trailHeads) {
            final Trail trail = new Trail(this.trails.size(), c);
            this.trails.add(trail);
            this.trailHeadScores.put(c, new ArrayList<>());
        }

        System.out.println("Trailheads = " + trailHeads.size());

        this.successfulTrails = new ArrayList<>();
        while (this.somethingHappened()) {
            System.out.println("tick");
        }

        for (final Trail trail : this.successfulTrails) {
            System.out.println(trail.breadCrumb());
        }

        int score = 0;
        for (final Map.Entry<Coord, List<Coord>> entry : this.trailHeadScores.entrySet()) {
            score += entry.getValue().size();
        }
        return String.valueOf(score);
    }

    private boolean somethingHappened() {

        boolean somethingHappened = true;
        while (somethingHappened) {
            somethingHappened = false;
            final List<Trail> trailsToTest = this.trails.stream().filter(t -> t.active).collect(Collectors.toList());
            System.out.println("\ntesting " + trailsToTest.size() + " trails ...");
            for (final Trail trail : trailsToTest) {
                System.out.println("  looking at " + trail);
                if (!trail.active) {
                    System.out.println("  not active.");
                    continue;
                }
                if (this.trailGotThere(trail)) {
                    continue;
                }
                if (!this.trailCanMove(trail)) {
                    System.out.println("  could not move.");
                }
                somethingHappened = true;
            }
        }

        return false;
    }

    private boolean trailGotThere(final Trail trail) {

        if (trail.height == 9) {
            System.out.println("    " + trail + " got there !");

            // do I have one yet ?
            final List<Coord> nineCoords = this.trailHeadScores.get(trail.getFirstCoord());

            boolean alreadyDone = false;
            for (final Coord c : nineCoords) {
                if (c.toString().equalsIgnoreCase(trail.getLastCoord().toString())) {
                    alreadyDone = true;
                }
            }
            if (!alreadyDone) {
                nineCoords.add(trail.getLastCoord());
            }
            this.successfulTrails.add(trail);
            // not sure I need this
            this.trailHeadScores.put(trail.getFirstCoord(), nineCoords);
            trail.active = false;
            return true;
        }
        return false;
    }

    private int countActiveTrails() {

        return (int) this.trails.stream().filter(t -> t.active).count();
    }

    private boolean trailCanMove(final Trail trail) {

        final Coord coord = trail.getLastCoord();
        final List<Coord> availableCoords = this.getAvailableCoords(trail.height, coord);
        if (availableCoords.isEmpty()) {
            trail.active = false;
            return false;

        }
        this.updateTrails(trail, availableCoords);

        return true;
    }

    private void updateTrails(final Trail trail, final List<Coord> availableCoords) {

        if (availableCoords.isEmpty()) {
            return;
        }

        if (availableCoords.size() == 1) {
            trail.height = trail.height + 1;
            trail.coords.add(availableCoords.get(0));
            System.out.println("    continuing on with trail " + trail);
            return;
        }

        for (final Coord c : availableCoords) {
            final Trail newTrail;
            newTrail = this.cloneTrail(trail);
            newTrail.height = trail.height + 1;
            newTrail.coords.add(c);
            System.out.println("    Cloned trail " + trail + " to " + newTrail);
        }
        trail.active = false;
    }

    private Trail cloneTrail(final Trail trail) {

        final Trail newTrail = new Trail(this.trails.size(), trail.coords.get(0));
        for (int i = 1; i < trail.coords.size(); i++) {
            newTrail.coords.add(trail.coords.get(i));
        }
        this.trails.add(newTrail);
        return newTrail;
    }

    private List<Coord> getAvailableCoords(final int height, final Coord coord) {

        final List<Coord> availableCoords = new ArrayList<>();
        this.maybeAdd(availableCoords, height, coord.x - 1, coord.y);
        this.maybeAdd(availableCoords, height, coord.x + 1, coord.y);
        this.maybeAdd(availableCoords, height, coord.x, coord.y + 1);
        this.maybeAdd(availableCoords, height, coord.x, coord.y - 1);

        return availableCoords;
    }

    private void maybeAdd(final List<Coord> availableCoords,
                          final int height,
                          final int x,
                          final int y) {

        final String mapLetter = this.getChallengeMapLetter(x, y);
        if (mapLetter == null) {
            return;
        }
        if (mapLetter.equalsIgnoreCase(".")) {
            return;
        }
        final int newHeight = Integer.parseInt(mapLetter);
        if (newHeight != (height + 1)) {
            return;
        }
        availableCoords.add(new Coord(x, y));
    }

    private List<Coord> findTrailHeads() {

        final List<Coord> trailHeads = new ArrayList<>();
        for (int x = 0; x < this.mapWidth; x++) {
            for (int y = 0; y < this.mapHeight; y++) {
                if (this.getChallengeMapLetter(x, y).equalsIgnoreCase("0")) {
                    trailHeads.add(new Coord(x, y));
                }
            }
        }
        return trailHeads;
    }

    @Override
    public String part2(final String[] input) {

        this.loadChallengeMap(input);
        final List<Coord> trailHeads = this.findTrailHeads();

        this.trails = new ArrayList<>();
        this.trailHeadScores = new HashMap<>();
        for (final Coord c : trailHeads) {
            final Trail trail = new Trail(this.trails.size(), c);
            this.trails.add(trail);
            this.trailHeadScores.put(c, new ArrayList<>());
        }

        System.out.println("Trailheads = " + trailHeads.size());

        this.successfulTrails = new ArrayList<>();
        while (this.somethingHappened()) {
            System.out.println("tick");
        }

        for (final Trail trail : this.successfulTrails) {
            System.out.println(trail.breadCrumb());
        }

        int score = 0;
        for (final Map.Entry<Coord, List<Coord>> entry : this.trailHeadScores.entrySet()) {
            score += entry.getValue().size();
        }
        return String.valueOf(this.successfulTrails.size());
    }

    static class Coord {

        private final int x;
        private final int y;

        public Coord(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {

            return this.x + "," + this.y;
        }
    }

    static class Trail {

        private final int id;
        private final List<Coord> coords;
        private boolean active;
        private int height;

        public Trail(final int id, final Coord coord) {
            this.id = id;
            this.coords = new ArrayList<>();
            this.coords.add(coord);
            this.active = true;
            this.height = 0;
        }

        public Coord getFirstCoord() {
            return this.coords.get(0);
        }

        public Coord getLastCoord() {
            return this.coords.get(this.coords.size() - 1);
        }

        @Override
        public String toString() {

            return this.id + " @ " + this.height +
                    " [" + this.getFirstCoord() +
                    "]->[" + this.getLastCoord() +
                    "] (" + this.coords.size() +
                    ")";
        }

        public String breadCrumb() {

            String breadCrumb = this.id + ": ";
            breadCrumb = breadCrumb + this.coords.stream().map(Coord::toString).collect(Collectors.joining(" "));
            return breadCrumb;
        }
    }
}
