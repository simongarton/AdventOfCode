package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Year2024Day12 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = true;

    private List<Region> regions;

    @Override
    public String title() {
        return "Day 12: Garden Groups";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 12);
    }

    @Override
    public String part1(final String[] input) {

        this.buildRegions(input);
        this.regions.forEach(this::calculatePerimeter);

        return String.valueOf(this.regions.stream().mapToInt(Region::price).sum());
    }

    private void calculatePerimeter(final Region region) {

        int perimeter = 0;
        for (final Coord c : region.coords) {
            perimeter += this.checkPerimeter(c.x - 1, c.y, region);
            perimeter += this.checkPerimeter(c.x + 1, c.y, region);
            perimeter += this.checkPerimeter(c.x, c.y - 1, region);
            perimeter += this.checkPerimeter(c.x, c.y + 1, region);
        }
        region.perimeter = perimeter;
    }

    private int checkPerimeter(final int x, final int y, final Region region) {

        final String letter = this.getChallengeMapLetter(x, y);
        if (letter == null || !(letter.equalsIgnoreCase(region.plant))) {
            return 1;
        }
        return 0;
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

        // I now think this is buggy - well it could be better - but it gave me the right answer
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

        this.buildRegions(input);
        this.regions.forEach(this::calculatePerimeter);
        this.regions.forEach(this::calculateSides);

        if (DEBUG) {
            this.paintMap("2024-Day12.png");
        }

        return String.valueOf(this.regions.stream().mapToInt(Region::expensivePrice).sum());

    }

    private void paintMap(final String filename) {

        final int delta = 8;

        final BufferedImage bufferedImage = new BufferedImage(
                this.mapWidth * delta,
                this.mapHeight * delta,
                TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D, delta);

        final OptionalInt optMaxArea = this.regions.stream().mapToInt(Region::area).max();
        if (optMaxArea.isEmpty()) {
            throw new RuntimeException();
        }
        final int maxArea = optMaxArea.getAsInt();

        final List<Double> normalisedHeights = new ArrayList<>();
        for (final Region region : this.regions) {
            normalisedHeights.add(1.0 * region.area() / maxArea);
        }

        int regionIndex = 0;
        for (final Region region : this.regions) {
            graphics2D.setPaint(this.getColorForHeight(normalisedHeights.get(regionIndex++)));
            for (final Coord c : region.coords) {
                graphics2D.fillRect(c.x * delta, c.y * delta, delta, delta);
            }
        }

        graphics2D.setPaint(Color.BLACK);

        for (final Region region : this.regions) {
            for (final Coord c : region.coords) {
                final int x1 = c.x * delta;
                final int y1 = c.y * delta;
                final int x2 = (c.x + 1) * delta;
                final int y2 = (c.y + 1) * delta;
                if (region.hasNeighbour(c.x, c.y - 1)) {
                    graphics2D.drawLine(x1, y1, x2, y1);
                }
                if (region.hasNeighbour(c.x, c.y + 1)) {
                    graphics2D.drawLine(x1, y2, x2, y2);
                }
                if (region.hasNeighbour(c.x + 1, c.y)) {
                    graphics2D.drawLine(x2, y1, x2, y2);
                }
                if (region.hasNeighbour(c.x - 1, c.y)) {
                    graphics2D.drawLine(x1, y1, x1, y2);
                }
            }
        }

        // draw the bottom * right borders, because integers.
        final int x1 = 0;
        final int y1 = 0;
        final int x2 = -1 + (this.mapWidth) * delta;
        final int y2 = -1 + (this.mapHeight) * delta;

        graphics2D.drawLine(x2, y1, x2, y2);
        graphics2D.drawLine(x1, y2, x2, y2);

        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private Paint getShadedGrayColorForHeight(final Double height) {

        return this.interpolateColor(Color.BLACK, Color.WHITE, height);
    }

    private Paint getShadedBlueGreenColorForHeight(final Double height) {
        return this.interpolateColor(new Color(0, 0, 50), new Color(0, 255, 0), height);
    }

    private Color getColorForHeight(final double originalHeight) {

        final double height = 1.0 - originalHeight;

        if (height < 0.3) {
            // Low altitude (blue for water)
            return this.interpolateColor(Color.BLUE, Color.GREEN, height / 0.3);
        } else if (height < 0.6) {
            // Plains (green)
            return this.interpolateColor(Color.GREEN, new Color(139, 69, 19), (height - 0.3) / 0.3);
        } else {
            // Hills and mountains (brown to white)
            return this.interpolateColor(new Color(139, 69, 19), Color.WHITE, (height - 0.6) / 0.4);
        }
    }

    private Color interpolateColor(final Color c1, final Color c2, final double t) {

        final int red = (int) (c1.getRed() * (1 - t) + c2.getRed() * t);
        final int green = (int) (c1.getGreen() * (1 - t) + c2.getGreen() * t);
        final int blue = (int) (c1.getBlue() * (1 - t) + c2.getBlue() * t);
        return new Color(red, green, blue);
    }

    private Paint randomColor() {

        final Random random = new Random();
        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private void clearBackground(final Graphics2D graphics2D, final int delta) {

        graphics2D.setPaint(Color.BLACK);
        graphics2D.fillRect(0, 0, this.mapWidth * delta, this.mapHeight * delta);
    }


    private void calculateSides(final Region region) {

        for (final Coord c : region.coords) {
            this.addNeighbours(c, region);
        }

        boolean changedAny;
        int groupsSetOnRegion = 0;
        while (true) {
            changedAny = false;
            for (final Neighbour firstNeighbour : region.neighbours) {
                if (firstNeighbour.group >= 0) {
                    continue;
                }

                int touchedGroup = -1;
                String touchedDirection = "";
                for (final Neighbour secondNeighbour : region.neighbours) {
                    if (secondNeighbour.toString().equalsIgnoreCase(firstNeighbour.toString())) {
                        continue;
                    }
                    if (!secondNeighbour.touches(firstNeighbour)) {
                        continue;
                    }
                    if (secondNeighbour.group == -1) {
                        continue;
                    }
                    touchedGroup = secondNeighbour.group;
                    touchedDirection = secondNeighbour.direction;
                    final boolean sameDirection = this.isSameDirection(firstNeighbour.direction, touchedDirection);
                    if (sameDirection) {
                        break;
                    }
                }
                if (touchedGroup > -1) {
                    final boolean sameDirection = this.isSameDirection(firstNeighbour.direction, touchedDirection);
                    if (sameDirection) {
                        firstNeighbour.group = touchedGroup;
                        changedAny = true;
                    }
                }
            }

            if (!changedAny) {
                final Optional<Neighbour> firstNonSortedNeighbour = region.neighbours.stream().filter(n -> n.group == -1).findFirst();
                if (firstNonSortedNeighbour.isEmpty()) {
                    break;
                }
                firstNonSortedNeighbour.get().group = groupsSetOnRegion;
                groupsSetOnRegion += 1;
            }
        }

        final Set<Integer> sides = new HashSet<>();
        for (final Neighbour neighbour : region.neighbours) {
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

    }

    private boolean isSameDirection(final String a, final String b) {

        return a.equalsIgnoreCase(b);
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

        final String plant;
        List<Coord> coords;
        int perimeter;
        final List<Neighbour> neighbours;
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

        public boolean hasNeighbour(final int x, final int y) {

            final List<Coord> neighbourCoords = this.neighbours.stream().map(n -> n.c).collect(Collectors.toList());
            return neighbourCoords.stream().anyMatch(coord -> coord.onTop(x, y));
        }
    }

    static class Neighbour {

        Coord c;
        final String direction;
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

        public int x;
        public final int y;

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
            return (this.x == c.x) && (this.y == c.y + 1);
        }

        public boolean onTop(final int x, final int y) {

            return (this.x == x) && (this.y == y);
        }
    }
}
