package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;

public class Year2024Day12 extends AdventOfCodeChallenge {

    List<Region> regions;

    @Override
    public String title() {
        return "Day 12: Template code";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 12);
    }

    @Override
    public String part1(final String[] input) {

        this.buildRegions(input);
        this.regions.forEach(this::calculatePerimeter);

        this.regions.forEach(System.out::println);
        return String.valueOf(this.regions.stream().mapToInt(Region::price).sum());
    }

    private void calculatePerimeter(final Region region) {

        int perimeter = 0;
        for (final Coord c : region.coords) {
            String letter = this.getChallengeMapLetter(c.x - 1, c.y);
            if (letter == null || !(letter.equalsIgnoreCase(region.plant))) {
                perimeter++;
            }
            letter = this.getChallengeMapLetter(c.x + 1, c.y);
            if (letter == null || !(letter.equalsIgnoreCase(region.plant))) {
                perimeter++;
            }
            letter = this.getChallengeMapLetter(c.x, c.y - 1);
            if (letter == null || !(letter.equalsIgnoreCase(region.plant))) {
                perimeter++;
            }
            letter = this.getChallengeMapLetter(c.x, c.y + 1);
            if (letter == null || !(letter.equalsIgnoreCase(region.plant))) {
                perimeter++;
            }
        }
        region.perimeter = perimeter;
    }

    private void buildRegions(final String[] input) {

        this.loadChallengeMap(input);
        this.regions = new ArrayList<>();

        for (int x = 0; x < this.mapWidth; x++) {
            for (int y = 0; y < this.mapHeight; y++) {
                final Coord c = new Coord(x, y);
                if (this.regionAt(c)) {
                    continue;
                }
                final String letter = this.getChallengeMapLetter(c.x, c.y);
                final Region region = this.getOrCreateRegion(letter, c);
                this.growRegion(c, region);
            }
        }
    }

    private void growRegion(final Coord c, final Region region) {

        this.recursivelyExpand(c, region);

    }

    private void recursivelyExpand(final Coord c, final Region region) {

        Coord next = new Coord(c.x - 1, c.y);
        while (this.expandRegion(next, region)) {
            this.recursivelyExpand(next, region);
        }
        next = new Coord(c.x + 1, c.y);
        while (this.expandRegion(next, region)) {
            this.recursivelyExpand(next, region);
        }
        next = new Coord(c.x, c.y - 1);
        while (this.expandRegion(next, region)) {
            this.recursivelyExpand(next, region);
        }
        next = new Coord(c.x, c.y + 1);
        while (this.expandRegion(next, region)) {
            this.recursivelyExpand(next, region);
        }
    }

    private boolean expandRegion(final Coord next, final Region region) {

        if (this.regionAt(next)) {
            return false;
        }
        final String letter = this.getChallengeMapLetter(next.x, next.y);
        if (letter == null) {
            return false;
        }
        if (letter.equalsIgnoreCase(region.plant)) {
            region.coords.add(next);
            return true;
        }
        return false;
    }

    private boolean regionAt(final Coord c) {
        for (final Region region : this.regions) {
            if (region.containsCoord(c)) {
                return true;
            }
        }
        return false;
    }

    private Region getOrCreateRegion(final String letter, final Coord coord) {
        for (final Region region : this.regions) {
            if (region.containsCoord(coord)) {
                return region;
            }
        }
        final Region region = new Region(letter, coord);
        this.regions.add(region);
        return region;

    }

    @Override
    public String part2(final String[] input) {

        // 918577 too high

        this.buildRegions(input);
        this.regions.forEach(this::calculatePerimeter);
        this.regions.forEach(this::calculateSides);

        this.regions.forEach(System.out::println);
        return String.valueOf(this.regions.stream().mapToInt(Region::expensivePrice).sum());

    }

    private void calculateSides(final Region region) {

        // for every coord, calculate it's valid neighbours - not off the map, not the same letter
        // record it with it's coord. We can have more than one with the same coord
        // also record it's direction.
        // then loop over them, counting each one. if I touch another on one side, only count the first
        // i need to count the corner one twice.

        for (final Coord c : region.coords) {
            this.addNeighbours(c, region);
        }

        //System.out.println("Region " + region + " has " + region.neighbours.size() + " neighbours");
        //region.neighbours.forEach(System.out::println);

        // all the neighbours will start in group -1
        // for each neighbour, check all the others.
        // if it touches any of them, and they are not -1, join to that group
        // if not, make another group
        boolean changedAny;
        int groupsSetOnRegion = 0;
        while (true) {
            changedAny = false;
//            System.out.println();
            for (final Neighbour firstNeighbour : region.neighbours) {
                // if I have a group, I've been done
                if (firstNeighbour.group >= 0) {
                    //System.out.println("  already done " + firstNeighbour);
                    continue;
                }

                //System.out.println("  checking " + firstNeighbour);
                int touchedGroup = -1;
                String touchedDirection = "";
                for (final Neighbour secondNeighbour : region.neighbours) {
                    if (secondNeighbour.toString().equalsIgnoreCase(firstNeighbour.toString())) {
                        //System.out.println("    self " + secondNeighbour);
                        continue;
                    }
                    if (!secondNeighbour.touches(firstNeighbour)) {
                        //System.out.println("    no touch " + secondNeighbour);
                        continue;
                    }
                    //System.out.println("    maybe " + secondNeighbour);
                    if (secondNeighbour.group == -1) {
                        continue;
                    }
                    //System.out.println("    found sorted " + secondNeighbour);
                    touchedGroup = secondNeighbour.group;
                    touchedDirection = secondNeighbour.direction;
                    final boolean sameDirection = this.isSameDirection(firstNeighbour.direction, touchedDirection);
                    if (sameDirection) {
                        break;
                    }
                }
                if (touchedGroup > -1) {
                    // I think it has to to be the same direction
                    final boolean sameDirection = this.isSameDirection(firstNeighbour.direction, touchedDirection);
                    if (sameDirection) {
                        firstNeighbour.group = touchedGroup;
                        changedAny = true;
                    }
                }
            }

            // if I didnt change any, then I need to pick the first -1 and set it to
            // the next group, and check to see if I have set them all
            if (!changedAny) {
                //System.out.println("  looking for non sorted");
                final Optional<Neighbour> firstNonSortedNeighbour = region.neighbours.stream().filter(n -> n.group == -1).findFirst();
                if (firstNonSortedNeighbour.isEmpty()) {
                    // I must have done them all ?
                    //System.out.println("    done all ?!");
                    break;
                }
                firstNonSortedNeighbour.get().group = groupsSetOnRegion;
                //System.out.println("    set " + firstNonSortedNeighbour.get());
                groupsSetOnRegion += 1;
            }
        }

        System.out.println("looking again at region " + region);
        final Set<Integer> sides = new HashSet<>();
        for (final Neighbour neighbour : region.neighbours) {
            System.out.println("  " + neighbour);
            sides.add(neighbour.group);
        }
        this.drawRegionOnMap(region);
        region.sides = sides.size();
    }

    private void drawRegionOnMap(final Region region) {

        final List<String> lines = new ArrayList<>();
        final String line = ".".repeat(this.mapWidth + 2);
        for (int i = 0; i < this.mapHeight + 2; i++) {
            lines.add(line);
        }

        for (final Coord c : region.coords) {
            final String originalLine = lines.get(c.y + 1);
            final String newLine = originalLine.substring(0, c.x + 1) + region.plant + originalLine.substring(c.x + 2);
            lines.set(c.y + 1, newLine);
        }

        for (final Neighbour n : region.neighbours) {
            final Coord c = n.c;
            final String originalLine = lines.get(c.y + 1);
            final int group = n.group;
            String groupChar = String.valueOf(group);
            if (group >= 10) {
                groupChar = "*";
            }
            final String newLine = originalLine.substring(0, c.x + 1) + groupChar + originalLine.substring(c.x + 2);
            lines.set(c.y + 1, newLine);
        }

        lines.forEach(System.out::println);
    }

    private boolean isSameDirection(final String a, final String b) {
        if (a.equalsIgnoreCase(b)) {
            return true;
        }
        /*
        if (a.equalsIgnoreCase("N") && b.equalsIgnoreCase("S")) {
            return true;
        }
        if (a.equalsIgnoreCase("S") && b.equalsIgnoreCase("N")) {
            return true;
        }
        if (a.equalsIgnoreCase("E") && b.equalsIgnoreCase("W")) {
            return true;
        }
        if (a.equalsIgnoreCase("W") && b.equalsIgnoreCase("E")) {
            return true;
        }
        */
        return false;
    }

    private void addNeighbours(final Coord c, final Region region) {

        this.addNeighbour(c, region, -1, 0, "W");
        this.addNeighbour(c, region, 1, 0, "E");
        this.addNeighbour(c, region, 0, -1, "N");
        this.addNeighbour(c, region, 0, 1, "S");
    }

    private void addNeighbour(final Coord c, final Region region, final int deltaX, final int deltaY, final String direction) {

        final Coord next = new Coord(c.x + deltaX, c.y + deltaY);
        String letter = this.getChallengeMapLetter(next.x, next.y);
        if (letter == null) {
            letter = "^";
            if (direction.equalsIgnoreCase("E")) {
                letter = ">";
            }
            if (direction.equalsIgnoreCase("S")) {
                letter = "_";
            }
            if (direction.equalsIgnoreCase("W")) {
                letter = "<";
            }
        }
        if (letter.equalsIgnoreCase(region.plant)) {
            return;
        }

        final Neighbour neighbour = new Neighbour(next, direction);
        region.neighbours.add(neighbour);
    }

    static class Region {

        String plant;
        List<Coord> coords;
        int perimeter;
        List<Neighbour> neighbours;
        int sides;

        public Region(final String planted, final Coord c) {

            this.plant = planted;
            this.coords = new ArrayList<>();
            this.coords.add(c);
            this.neighbours = new ArrayList<>();
        }

        public boolean containsCoord(final Coord c) {

            return this.coords.contains(c);
        }

        public int area() {
            return this.coords.size();
        }

        public int price() {
            return this.area() * this.perimeter;
        }

        public int expensivePrice() {
            return this.area() * this.sides;
        }

        @Override
        public String toString() {
            return this.plant + " c ("
                    + this.coords.size()
                    + ") x p ["
                    + this.perimeter
                    + "] = "
                    + this.coords.size() * this.perimeter
                    + " -> s {"
                    + this.sides
                    + "}";
        }
    }

    static class Neighbour {

        Coord c;
        String direction;
        int group;

        public Neighbour(final Coord c, final String direction) {
            this.c = c;
            this.direction = direction;
            this.group = -1;
        }

        @Override
        public String toString() {
            return this.c + " dir " + this.direction + " group " + this.group;
        }

        public boolean touches(final Neighbour otherNeighbour) {
            return this.c.touches(otherNeighbour.c);
        }
    }


    static class Coord {

        int x;
        int y;

        public Coord(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return this.x + "," + this.y;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Coord coord = (Coord) o;
            return this.x == coord.x && this.y == coord.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.x, this.y);
        }

        public boolean touches(final Coord c) {
            if ((this.x == c.x - 1) && (this.y == c.y)) {
                return true;
            }
            if ((this.x == c.x + 1) && (this.y == c.y)) {
                return true;
            }
            if ((this.x == c.x) && (this.y == c.y - 1)) {
                return true;
            }
            if ((this.x == c.x) && (this.y == c.y + 1)) {
                return true;
            }
            return false;
        }
    }
}
