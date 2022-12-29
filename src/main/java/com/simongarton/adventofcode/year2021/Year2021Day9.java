package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.Coord;

import java.util.*;

public class Year2021Day9 extends AdventOfCodeChallenge {

    private String map;
    private int width;
    private int height;

    @Override
    public String title() {
        return "Day 9: Smoke Basin";
    }

    // not my original code ! Class missing from repo, rebuilt 2022.

    @Override
    public Outcome run() {
        return this.runChallenge(2021, 9);
    }

    @Override
    public String part1(final String[] input) {
        this.loadMap(input);
        int lowPoints = 0;
        for (int col = 0; col < this.width; col++) {
            for (int row = 0; row < this.height; row++) {
                lowPoints += this.riskLevel(col, row);
            }
        }
        return String.valueOf(lowPoints);
    }

    @Override
    public String part2(final String[] input) {
        this.loadMap(input);
        final List<Basin> basins = new ArrayList<>();
        final Map<String, Basin> coordBasinMap = new HashMap<>();
        for (int col = 0; col < this.width; col++) {
            for (int row = 0; row < this.height; row++) {
                if (this.riskLevel(col, row) > 0) {
                    final Coord coord = new Coord(col, row);
                    final Basin basin = new Basin(coord);
                    basins.add(basin);
                    coordBasinMap.put(coord.toString(), basin);
                }
            }
        }

        this.flowBasins(basins, coordBasinMap);

        basins.sort(Comparator.comparing(Basin::size).reversed());

        return String.valueOf(basins.get(0).size() * basins.get(1).size() * basins.get(2).size());
    }

    private void flowBasins(final List<Basin> basins, final Map<String, Basin> coordBasinMap) {
        boolean anythingChanged = true;
        while (anythingChanged) {
            anythingChanged = false;
            for (int col = 0; col < this.width; col++) {
                for (int row = 0; row < this.height; row++) {
                    final String spot = this.getMap(col, row);
                    if (spot.equalsIgnoreCase("9")) {
                        continue;
                    }
                    final Coord spotCoord = new Coord(col, row);
                    if (coordBasinMap.containsKey(spotCoord.toString())) {
                        continue;
                    }
                    if (this.addCoordToBasin(coordBasinMap, spotCoord)) {
                        anythingChanged = true;
                    }
                }
            }
        }
    }

    private boolean addCoordToBasin(final Map<String, Basin> coordBasinMap, final Coord spotCoord) {
        if (this.addCoordToBasin(spotCoord, coordBasinMap, spotCoord.getX() - 1, spotCoord.getY())) {
            return true;
        }
        if (this.addCoordToBasin(spotCoord, coordBasinMap, spotCoord.getX() + 1, spotCoord.getY())) {
            return true;
        }
        if (this.addCoordToBasin(spotCoord, coordBasinMap, spotCoord.getX(), spotCoord.getY() - 1)) {
            return true;
        }
        if (this.addCoordToBasin(spotCoord, coordBasinMap, spotCoord.getX(), spotCoord.getY() + 1)) {
            return true;
        }
        return false;
    }

    private boolean addCoordToBasin(final Coord spotCoord, final Map<String, Basin> coordBasinMap, final Integer col, final Integer row) {
        if (col < 0 || col >= this.width) {
            return false;
        }
        if (row < 0 || row >= this.height) {
            return false;
        }
        final String spot = this.getMap(col, row);
        if (spot.equalsIgnoreCase("9")) {
            return false;
        }
        final Coord neighbour = new Coord(col, row);
        if (!coordBasinMap.containsKey(neighbour.toString())) {
            return false;
        }
        final Basin basin = coordBasinMap.get(neighbour.toString());
        basin.coords.add(spotCoord);
        coordBasinMap.put(spotCoord.toString(), basin);
        return true;
    }

    private void loadMap(final String[] input) {
        this.width = input[0].length();
        this.height = input.length;
        this.map = String.join("", Arrays.asList(input));
    }

    private String getMap(final int col, final int row) {
        if (row < 0 || row >= this.height) {
            return "";
        }
        if (col < 0 || col >= this.width) {
            return "";
        }
        final int index = (row * this.width) + col;
        return this.map.substring(index, index + 1);
    }

    private int riskLevel(final int col, final int row) {
        final int targetLevel = Integer.parseInt(this.getMap(col, row));
        if (!this.neighbourHigher(targetLevel, col - 1, row)) {
            return 0;
        }
        if (!this.neighbourHigher(targetLevel, col + 1, row)) {
            return 0;
        }
        if (!this.neighbourHigher(targetLevel, col, row - 1)) {
            return 0;
        }
        if (!this.neighbourHigher(targetLevel, col, row + 1)) {
            return 0;
        }
        return targetLevel + 1;
    }

    private boolean neighbourHigher(final int targetLevel, final int col, final int row) {
        final String point = this.getMap(col, row);
        if (point.isEmpty()) {
            return true;
        }
        final int testLevel = Integer.parseInt(point);
        return testLevel > targetLevel;
    }

    public static final class Basin {
        private final Coord center;
        private final List<Coord> coords;

        public Basin(final Coord center) {
            this.center = center;
            this.coords = new ArrayList<>();
            this.coords.add(center);
        }

        public int size() {
            return this.coords.size();
        }
    }
}
