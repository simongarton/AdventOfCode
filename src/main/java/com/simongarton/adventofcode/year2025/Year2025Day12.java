package com.simongarton.adventofcode.year2025;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2025Day12 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 0: Template code";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2025, 12);
    }

    @Override
    public String part1(final String[] input) {

        final List<Shape> shapes = new ArrayList<>();
        int i = 0;
        while (true) {
            final String idLine = input[i];
            if (idLine.contains("x")) {
                break;
            }
            final int id = Integer.parseInt(idLine.trim().substring(0, idLine.trim().length() - 1));
            final String dataLine = input[i + 1] + input[i + 2] + input[i + 3];
            shapes.add(new Shape(id, dataLine));
            i += 5;
        }

        final List<Region> regions = new ArrayList<>();
        while (i < input.length) {
            regions.add(new Region(input[i]));
            i++;
        }

        for (final Shape shape : shapes) {
            shape.draw();
        }

        int fittedRegions = 0;
        for (final Region region : regions) {
            System.out.println(region);
            if (this.canFitInRegion(region, shapes)) {
                fittedRegions++;
            }
            System.out.println();
        }

        return String.valueOf(fittedRegions);
    }

    private boolean canFitInRegion(final Region region, final List<Shape> shapes) {

        final String[] mapData = new String[region.height];
        for (int i = 0; i < region.height; i++) {
            final String data = ".".repeat(region.width);
            mapData[i] = data;
        }
        this.loadChallengeMap(mapData);
        this.drawChallengeMap();

        final List<Shape> shapesToFit = new ArrayList<>();
        for (int i = 0; i < region.presents.size(); i++) {
            for (int j = 0; j < region.presents.get(i); j++) {
                shapesToFit.add(shapes.get(i));
            }
        }
        System.out.println(shapesToFit.stream().map(Shape::getId).collect(Collectors.toList()));
        // now I need to iterate through this list of shapes to fit, and try it in every cell
        // for x between 0 and width-3 exclusive and y between 0 and height - 3 exclusive in
        // each of the orientations. If it could fit, add it and move on; if not, return false;
        // but of course I need to try every combination
        // write this up as pseudocode.
        // there are a gazillion permutations. can I get rid of some ?

        // 46,656 permutations, and I haven't even got the coordinates in yet.
        final List<List<Present>> permutations = this.generateAllPermutations(shapesToFit);

        // but this is good. I need to take each permutation, and starting from the first one, try and place it at 0,0;
        // if not, go to 1,0 and so on. If I can place it, then I move onto the second permutation and try again.
        // Drop out as early as possible.

        for (final List<Present> permutation : permutations) {
            try {
                this.loadChallengeMap(mapData);
                this.tryToPlacePresents(permutation);
                System.out.println("managed to place permutation " + permutation);
                this.drawChallengeMap();
                return true;
            } catch (final DidntFitException e) {
//                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    private void tryToPlacePresents(final List<Present> permutation) {

        // this is the original
        List<String> challengeMapLines = this.getMapLines();

        for (final Present present : permutation) {

            final Shape shape = present.shape;
            final Orientation orientation = present.orientation;
            boolean placedPresent = false;

            for (int y = 0; y < this.mapHeight - 2; y++) {
                if (placedPresent) {
                    break;
                }
                for (int x = 0; x < this.mapWidth - 2; x++) {
                    if (placedPresent) {
                        break;
                    }
                    int bitsPlaced = 0;
                    final int bitsToPlace = shape.bitsToPlace();
//                    System.out.println("Trying to place " + bitsToPlace + " bits of " + shape.id + ":" + orientation + " at " + x + "," + y);
                    // reset the map each time to how far I've got
                    this.loadChallengeMap(challengeMapLines.toArray(new String[0]));
//                    this.drawChallengeMap();
                    for (int j = 0; j < 3; j++) {
                        for (int i = 0; i < 3; i++) {
                            if (shape.getBit(i, j, orientation) == 0) {
                                continue;
                            }
                            final int mapX = x + i;
                            final int mapY = y + j;
//                            System.out.println("testing " + mapX + "," + mapY + " and got " + this.getChallengeMapSymbol(mapX, mapY));
                            if (!this.getChallengeMapSymbol(mapX, mapY).equalsIgnoreCase(".")) {
                                continue;
                            }
                            this.setChallengeMapLetter(mapX, mapY, String.valueOf(shape.getId()));
//                            System.out.println("set " + mapX + "," + mapY + " to " + shape.getId());
                            bitsPlaced++;
                        } // end of i
                    } // end of j
                    if (bitsPlaced == bitsToPlace) {
                        placedPresent = true;
                        break;
                    }
                } // end of x
            } // end of y

            if (!placedPresent) {
                throw new DidntFitException("Couldn't place " + shape.id);
            }
//            this.drawChallengeMap();
//            System.out.println("Placed " + shape.getId());
            // now I need to store this again
            challengeMapLines = this.getMapLines();
        } // end of permutation
    }

    private List<List<Present>> generateAllPermutations(final List<Shape> shapes) {
        final List<List<Present>> result = new ArrayList<>();
        final Orientation[] orientations = Orientation.values();

        // Generate all combinations recursively
        generatePermutationsRecursive(shapes, orientations, 0, new ArrayList<>(), result);

        return result;
    }

    private static void generatePermutationsRecursive(
            final List<Shape> originalShapes,
            final Orientation[] orientations,
            final int shapeIndex,
            final List<Present> current,
            final List<List<Present>> result) {

        // Base case: processed all shapes
        if (shapeIndex == originalShapes.size()) {
            result.add(new ArrayList<>(current));
            return;
        }

        // Try each orientation for the current shape
        final Shape originalShape = originalShapes.get(shapeIndex);
        for (final Orientation orientation : orientations) {
            final Present present = new Present(originalShape, orientation);
            current.add(present);

            // Recurse to next shape
            generatePermutationsRecursive(originalShapes, orientations, shapeIndex + 1, current, result);

            // Backtrack
            current.removeLast();
        }
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    public enum Orientation {
        NORMAL,
        FLIP_HORIZONTAL,
        FLIP_VERTICAL,
        ROTATE_90,
        ROTATE_180,
        ROTATE_270
    }

    public static final class DidntFitException extends RuntimeException {

        public DidntFitException(final String message) {
            super(message);
        }
    }

    public static final class Present {

        private final Shape shape;
        private final Orientation orientation;

        public Present(final Shape shape, final Orientation orientation) {
            this.shape = shape;
            this.orientation = orientation;
        }

        public Shape getShape() {
            return this.shape;
        }

        public Orientation getOrientation() {
            return this.orientation;
        }
    }

    public static final class Shape {

        private final int id;
        private final String data;
        private final int[] bits;

        public Shape(final int id, final String data) {
            this.id = id;
            this.data = data;
            // data should be 9 chars long
            assert (data.length() == 9);
            this.bits = new int[9];
            for (int i = 0; i < 9; i++) {
                this.bits[i] = data.charAt(i) == '#' ? 1 : 0;
            }
        }

        private int getBit(final int x, final int y) {

            final int index = y * 3 + x;
            return this.bits[index];
        }

        public int getBit(final int x, final int y, final Orientation orientation) {

            final int[] newBits = this.getBits(orientation);
            final int index = y * 3 + x;
            return newBits[index];
        }

        public int[] getBits(final Orientation orientation) {

            // yes I should learn how to do matrices
            switch (orientation) {
                case NORMAL -> {
                    return this.bits;
                }
                case FLIP_HORIZONTAL -> {
                    final int[] newBits = new int[9];
                    newBits[0] = this.getBit(2, 0);
                    newBits[1] = this.getBit(1, 0);
                    newBits[2] = this.getBit(0, 0);
                    newBits[3] = this.getBit(2, 1);
                    newBits[4] = this.getBit(1, 1);
                    newBits[5] = this.getBit(0, 1);
                    newBits[6] = this.getBit(2, 2);
                    newBits[7] = this.getBit(1, 2);
                    newBits[8] = this.getBit(0, 2);
                    return newBits;
                }
                case FLIP_VERTICAL -> {
                    final int[] newBits = new int[9];
                    newBits[0] = this.getBit(0, 2);
                    newBits[1] = this.getBit(1, 2);
                    newBits[2] = this.getBit(2, 2);
                    newBits[3] = this.getBit(0, 1);
                    newBits[4] = this.getBit(1, 1);
                    newBits[5] = this.getBit(2, 1);
                    newBits[6] = this.getBit(0, 0);
                    newBits[7] = this.getBit(1, 0);
                    newBits[8] = this.getBit(2, 0);
                    return newBits;
                }
                case ROTATE_90 -> {
                    final int[] newBits = new int[9];
                    newBits[0] = this.getBit(0, 2);
                    newBits[1] = this.getBit(0, 1);
                    newBits[2] = this.getBit(0, 0);
                    newBits[3] = this.getBit(1, 2);
                    newBits[4] = this.getBit(1, 1);
                    newBits[5] = this.getBit(1, 0);
                    newBits[6] = this.getBit(2, 2);
                    newBits[7] = this.getBit(2, 1);
                    newBits[8] = this.getBit(2, 0);
                    return newBits;
                }
                case ROTATE_180 -> {
                    final int[] newBits = new int[9];
                    newBits[0] = this.getBit(2, 2);
                    newBits[1] = this.getBit(1, 2);
                    newBits[2] = this.getBit(0, 2);
                    newBits[3] = this.getBit(2, 1);
                    newBits[4] = this.getBit(1, 1);
                    newBits[5] = this.getBit(0, 1);
                    newBits[6] = this.getBit(0, 0);
                    newBits[7] = this.getBit(1, 0);
                    newBits[8] = this.getBit(2, 0);
                    return newBits;
                }
                case ROTATE_270 -> {
                    final int[] newBits = new int[9];
                    newBits[0] = this.getBit(2, 0);
                    newBits[1] = this.getBit(2, 1);
                    newBits[2] = this.getBit(2, 2);
                    newBits[3] = this.getBit(1, 0);
                    newBits[4] = this.getBit(1, 1);
                    newBits[5] = this.getBit(1, 2);
                    newBits[6] = this.getBit(0, 0);
                    newBits[7] = this.getBit(0, 1);
                    newBits[8] = this.getBit(0, 2);
                    return newBits;
                }
                default -> {
                    throw new UnsupportedOperationException(orientation.toString());
                }
            }
        }

        public void draw() {

            this.draw(Orientation.NORMAL);
        }

        public void draw(final Orientation orientation) {

            final List<String> lines = this.getLines(orientation);
            lines.stream().forEach(System.out::println);
            System.out.println("");
        }

        private List<String> getLines(final Orientation orientation) {

            final List<String> lines = new ArrayList<>();
            for (int y = 0; y < 3; y++) {
                String line = "";
                for (int x = 0; x < 3; x++) {
                    line = line + (this.getBit(x, y, orientation) == 1 ? "#" : ".");
                }
                lines.add(line);
            }
            return lines;
        }

        public int getId() {
            return this.id;
        }

        public int bitsToPlace() {

            return Arrays.stream(this.bits).sum();
        }
    }

    public static final class Region {

        private final int width;
        private final int height;
        private final List<Integer> presents;

        public Region(final String line) {
            final String[] parts = line.split(":");
            final String[] dimensions = parts[0].split("x");
            this.width = Integer.parseInt(dimensions[0]);
            this.height = Integer.parseInt(dimensions[1]);
            final String[] presentList = parts[1].trim().split(" ");
            this.presents = Arrays.stream(presentList).map(Integer::parseInt).toList();
        }

        @Override
        public String toString() {
            return this.width + "x" + this.height + " (" + this.presents.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
        }
    }
}
