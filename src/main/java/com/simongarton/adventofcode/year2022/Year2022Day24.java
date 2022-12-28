package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.Coord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2022Day24 extends AdventOfCodeChallenge {

    private int width;
    private int height;
    private List<String> maps;
    private List<Blizzard> blizzards;

    @Override
    public boolean run() {
        return this.runChallenge(2022, 24);
    }

    @Override
    public String part1(final String[] input) {
        this.loadMaps(input);
        this.drawMap(0);
        for (int i = 0; i < 10; i++) {
            this.addMap();
            this.drawMap(this.maps.size() - 1);
        }
        return null;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    private void loadMaps(final String[] input) {
        this.maps = new ArrayList<>();
        this.maps.add(Arrays.asList(input).stream().collect(Collectors.joining()));
        this.width = input[0].length();
        this.height = input.length;
        this.loadBlizzards(this.maps.get(0));
    }

    private void loadBlizzards(final String map) {
        this.blizzards = new ArrayList<>();
        for (int col = 0; col < this.width; col++) {
            for (int row = 0; row < this.height; row++) {
                final String symbol = this.getSymbol(map, col, row);
                if (symbol.equalsIgnoreCase("#")) {
                    continue;
                }
                if (symbol.equalsIgnoreCase(".")) {
                    continue;
                }
                this.blizzards.add(new Blizzard(new Coord(col, row), symbol));
            }
        }
    }

    private String getSymbol(final String map, final int col, final int row) {
        return map.substring((row * this.width) + col, (row * this.width) + col + 1);
    }

    private String updateMapWithSymbol(final String map, final int col, final int row, final String symbol) {
        return map.substring(0, (row * this.width) + col) +
                symbol +
                map.substring((row * this.width) + col + 1);
    }

    private void drawMap(final int index) {
        this.drawMapString(this.maps.get(index));
    }

    private void drawMapString(final String map) {
        for (int row = 0; row < (map.length() / this.width); row++) {
            System.out.println(map.substring(row * this.width, (row + 1) * this.width));
        }
        System.out.println("");
    }

    private void addMap() {
        String newMap = this.maps.get(this.maps.size() - 1)
                .replace("v", ".")
                .replace("^", ".")
                .replace("<", ".")
                .replace(">", ".")
                .replace("*", ".");
        for (final Blizzard blizzard : this.blizzards) {
            newMap = this.moveBlizzardAndUpdateMap(newMap, blizzard);
        }
        this.maps.add(newMap);
    }

    private String moveBlizzardAndUpdateMap(String newMap, final Blizzard blizzard) {
        switch (blizzard.symbol) {
            case ">":
                blizzard.position.setX(blizzard.position.getX() + 1);
                if (blizzard.position.getX().equals(this.width - 1)) {
                    blizzard.position.setX(1);
                }
                break;
            case "<":
                blizzard.position.setX(blizzard.position.getX() - 1);
                if (blizzard.position.getX().equals(0)) {
                    blizzard.position.setX(this.width - 2);
                }
                break;
            case "^":
                blizzard.position.setY(blizzard.position.getY() - 1);
                if (blizzard.position.getY().equals(0)) {
                    blizzard.position.setY(this.height - 2);
                }
                break;
            case "v":
                blizzard.position.setY(blizzard.position.getY() + 1);
                if (blizzard.position.getY().equals(this.height - 1)) {
                    blizzard.position.setY(1);
                }
                break;
            default:
                throw new RuntimeException(blizzard.symbol);
        }
        switch (this.getSymbol(newMap, blizzard.position.getX(), blizzard.position.getY())) {
            case ".":
                newMap = this.updateMapWithSymbol(newMap, blizzard.position.getX(), blizzard.position.getY(), blizzard.symbol);
                return newMap;
            case "*":
                newMap = this.updateMapWithSymbol(newMap, blizzard.position.getX(), blizzard.position.getY(), blizzard.symbol);
                return newMap;
            case ">":
            case "<":
            case "^":
            case "v":
                newMap = this.updateMapWithSymbol(newMap, blizzard.position.getX(), blizzard.position.getY(), "*");
                return newMap;
            default:
                throw new RuntimeException(this.getSymbol(newMap, blizzard.position.getX(), blizzard.position.getY()));
        }
    }

    public static final class Blizzard {
        private final Coord position;
        private final String symbol;

        public Blizzard(final Coord coord, final String symbol) {
            this.position = coord;
            this.symbol = symbol;
        }
    }

    public static final class State {
        private final int iteration;
        private final List<Coord> stepsMade;
        private final String map;

        public State(final int iteration, final String map) {
            this.stepsMade = new ArrayList<>();
            this.iteration = iteration;
            this.map = map;
        }
    }
}
