package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2022Day18 extends AdventOfCodeChallenge {

    private int[][][] cube;
    private int xMax = 0;
    private int yMax = 0;
    private int zMax = 0;

    // I have a 0 based cube, but will convert all coords to be 1 based

    /*

    For part 2, I tried just looking for completely enclosed - but I think I have to recursively drill in from the
    outer layer. Which will be fun.

     */


    @Override
    public String title() {
        return "Day 18: Boiling Boulders";
    }

    @Override
    public boolean run() {
        return this.runChallenge(2022, 18);
    }

    // 4482 is correct
    @Override
    public String part1(final String[] input) {
        this.loadCube(input);
//        this.debugCube();
        return String.valueOf(this.countSurfaceArea());
    }


    // 4194 is too high
    // 4826 is the possibles - the cubes on the inside
    @Override
    public String part2(final String[] input) {
        this.loadCube(input);
//        this.debugCube();
        final int surface = this.countSurfaceArea();
        final int completelyEnclosed = this.countCompletelyEnclosed();
        return String.valueOf(surface - completelyEnclosed);
    }


    private int countSurfaceArea() {
        int surfaceArea = 0;
        for (int x = 0; x <= this.xMax; x++) {
            for (int y = 0; y <= this.yMax; y++) {
                for (int z = 0; z <= this.zMax; z++) {
                    if (!this.cubeAt(x, y, z)) {
                        continue;
                    }
                    surfaceArea += (6 - this.countFacesBlocked(x, y, z));
                }
            }
        }
        return surfaceArea;
    }

    private int countCompletelyEnclosed() {
        int completelyEnclosed = 0;
        for (int x = 1; x < this.xMax; x++) {
            for (int y = 1; y < this.yMax; y++) {
                for (int z = 1; z < this.zMax; z++) {
                    // I think this is redundant
                    if (!this.cubeCouldBeInner(x, y, z)) {
                        continue;
                    }
                    final int facesBlocked = this.countFacesOpen(x, y, z);
                    if (facesBlocked == 6) {
                        completelyEnclosed += 6;
                    }
                }
            }
        }
        return completelyEnclosed;
    }

    private void debugCube() {
        int actualCubes = 0;
        int voids = 0;
        int voidsNotOnOutside = 0;
        for (int x = 1; x <= this.xMax; x++) {
            for (int y = 1; y <= this.yMax; y++) {
                for (int z = 1; z <= this.zMax; z++) {
                    if (this.cubeAt(x, y, z)) {
                        actualCubes++;
                    } else {
                        voids++;
                        if (this.cubeCouldBeInner(x, y, z)) {
                            voidsNotOnOutside++;
                        }
                    }
//                    System.out.printf("%s,%s,%s=%s [%s]\n", x, y, z,
//                            this.cubeAt(x, y, z),
//                            this.countFacesBlocked(x, y, z, true));
                }
            }
        }
        System.out.printf("outer : %s x %s x %s = %s\n", this.xMax, this.yMax, this.zMax, this.xMax * this.yMax * this.zMax);
        System.out.printf("one inside : %s x %s x %s = %s\n", this.xMax - 2, this.yMax - 2, this.zMax = 2, (this.xMax - 2) * (this.yMax - 2) * (this.zMax - 2));
        System.out.println("cubes : " + actualCubes);
        System.out.println("voids : " + voids);
        System.out.println("voidsNotOnOutside : " + voidsNotOnOutside);
    }

    private int countFacesBlocked(final int x, final int y, final int z) {
        if (!this.cubeAt(x, y, z)) {
            //System.out.println("No cube at " + this.d3(x, y, z));
            return 0;
        }
        //System.out.println("Counting blocked faces for " + this.d3(x, y, z));
        int count = 0;
        count = count + (this.cubeAt(x + 1, y, z) ? 1 : 0);
        count = count + (this.cubeAt(x - 1, y, z) ? 1 : 0);
        count = count + (this.cubeAt(x, y + 1, z) ? 1 : 0);
        count = count + (this.cubeAt(x, y - 1, z) ? 1 : 0);
        count = count + (this.cubeAt(x, y, z + 1) ? 1 : 0);
        count = count + (this.cubeAt(x, y, z - 1) ? 1 : 0);
        return count;
    }

    private int countFacesOpen(final int x, final int y, final int z) {
        if (this.cubeAt(x, y, z)) {
            //System.out.println("Is a cube at " + this.d3(x, y, z));
            return 0;
        }
        //System.out.println("Counting open faces for " + this.d3(x, y, z));
        int count = 0;
//        count = count + (this.cubeAt(x + 1, y, z) ? 1 : 0);
//        count = count + (this.cubeAt(x - 1, y, z) ? 1 : 0);
//        count = count + (this.cubeAt(x, y + 1, z) ? 1 : 0);
//        count = count + (this.cubeAt(x, y - 1, z) ? 1 : 0);
//        count = count + (this.cubeAt(x, y, z + 1) ? 1 : 0);
//        count = count + (this.cubeAt(x, y, z - 1) ? 1 : 0);
        count = count + (this.cubeAt(x + 1, y, z) ? 1 : this.countFacesOpen(x + 2, y, z) == 5 ? 1 : 0);
        count = count + (this.cubeAt(x - 1, y, z) ? 1 : this.countFacesOpen(x + 2, y, z) == 5 ? 1 : 0);
        count = count + (this.cubeAt(x, y + 1, z) ? 1 : this.countFacesOpen(x, y + 2, z) == 5 ? 1 : 0);
        count = count + (this.cubeAt(x, y - 1, z) ? 1 : this.countFacesOpen(x, y - 2, z) == 5 ? 1 : 0);
        count = count + (this.cubeAt(x, y, z + 1) ? 1 : this.countFacesOpen(x, y, z + 2) == 5 ? 1 : 0);
        count = count + (this.cubeAt(x, y, z - 1) ? 1 : this.countFacesOpen(x, y, z - 2) == 5 ? 1 : 0);
        return count;
    }

    private String d3(final int x, final int y, final int z) {
        return String.format("%s, %s, %s", x, y, z);
    }

    private boolean cubeCouldBeInner(final int x, final int y, final int z) {
        // x, y, z are 1 based.
        if (x <= 0 || x >= this.xMax) {
            //System.out.println("Not checking x : " + this.d3(x, y, z));
            return false;
        }
        if (y <= 0 || y >= this.yMax) {
            //System.out.println("Not checking y : " + this.d3(x, y, z));
            return false;
        }
        if (z <= 0 || z >= this.zMax) {
            //System.out.println("Not checking z : " + this.d3(x, y, z));
            return false;
        }
        return true;
    }

    private boolean cubeAt(final int x, final int y, final int z) {
        // x, y, z are 1 based.
        if (x < 0 || x > this.xMax) {
            //System.out.println("Not checking x : " + this.d3(x, y, z));
            return false;
        }
        if (y < 0 || y > this.yMax) {
            //System.out.println("Not checking y : " + this.d3(x, y, z));
            return false;
        }
        if (z < 0 || z > this.zMax) {
            //System.out.println("Not checking z : " + this.d3(x, y, z));
            return false;
        }
        //System.out.println("cubeAt " + this.d3(x, y, z) + " = " + this.cube[x][y][z]);
        return this.cube[x][y][z] > 0;
    }

    private void loadCube(final String[] input) {
        this.resizeCube(input);
//        System.out.println(input.length + " lines in file");
        for (final String line : input) {
            final String[] parts = line.split(",");
            final int x = Integer.parseInt(parts[0]);
            final int y = Integer.parseInt(parts[1]);
            final int z = Integer.parseInt(parts[2]);
            if (this.cubeAt(x, y, z)) {
                throw new RuntimeException("Already got.");
            }
            this.cube[x][y][z] = 1;

        }
    }

    private void resizeCube(final String[] input) {
        for (final String line : input) {
            final String[] parts = line.split(",");
            // scale up
            final int x = Integer.parseInt(parts[0]);
            final int y = Integer.parseInt(parts[1]);
            final int z = Integer.parseInt(parts[2]);
            if (this.xMax < x) {
                this.xMax = x;
            }
            if (this.yMax < y) {
                this.yMax = y;
            }
            if (this.zMax < z) {
                this.zMax = z;
            }
        }
        // scale up
        this.cube = new int[this.xMax + 1][this.yMax + 1][this.zMax + 1];
    }

}
