package com.simongarton.adventofcode.year2025;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.Coord;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Year2025Day9 extends AdventOfCodeChallenge {

    List<Coord> coords;
    Map<Long, String> areas = new HashMap<>();

    @Override
    public String title() {
        return "Day 9: Movie Theater";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2025, 9);
    }

    @Override
    public String part1(final String[] input) {

        this.coords = Arrays.stream(input).map(Coord::new).toList();

        for (final Coord one : this.coords) {
            for (final Coord two : this.coords) {
                final long area = (long) (1 + Math.abs(one.getX() - two.getX())) * (1 + Math.abs(one.getY() - two.getY()));
                this.areas.put(area, one + ":" + two);
            }
        }

        String best = null;
        Long bestArea = 0L;
        for (final Map.Entry<Long, String> entry : this.areas.entrySet()) {
            if (entry.getKey() > bestArea) {
                best = entry.getValue();
                bestArea = entry.getKey();
            }
        }

        return String.valueOf(bestArea);
    }

    @Override
    public String part2(final String[] input) {

        this.coords = Arrays.stream(input).map(Coord::new).toList();

        /*

        Inspection of the map shows a horizontal indent covering most of the middle of the map.
        The big map is about 100k x 100k ... therefore any coord above the middle 50k won't be a valid rectangle
        if the other is below the 50k.



         */

        final long total = (long) this.coords.size() * this.coords.size();
        long checked = 0;
        Long bestArea = 0L;
        Coord bestOne = null;
        Coord bestTwo = null;
        for (final Coord one : this.coords) {
            final boolean above50k = one.getY() > 50000;
            for (final Coord two : this.coords) {
                if ((above50k && (two.getY() < 50000)) || (!above50k && (two.getY() > 50000))) {
                    continue;
                }
                if (one.toString().equalsIgnoreCase(two.toString())) {
                    continue;
                }
                final Coord otherCorner1 = new Coord(one.getX(), two.getY());
                final Coord otherCorner2 = new Coord(two.getX(), one.getY());
                if (!Coord.isInsidePolygon(otherCorner1, this.coords)) {
                    continue;
                }
                if (!Coord.isInsidePolygon(otherCorner2, this.coords)) {
                    continue;
                }
                final long area = (long) (1 + Math.abs(one.getX() - two.getX())) * (1 + Math.abs(one.getY() - two.getY()));
                if (area > bestArea) {
                    bestArea = area;
                    bestOne = one;
                    bestTwo = two;
                }
                checked++;
            }
        }

        this.drawMapWithCoords(bestOne, bestTwo);
        System.out.println("ended up checking " + checked + "/" + total + " with no ray tracing.");
        return String.valueOf(bestArea);
    }

    private void drawMapWithCoords(final Coord bestOne, final Coord bestTwo) {

        final BufferedImage bufferedImage = new BufferedImage(1000, 1000, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);

        graphics2D.setPaint(new Color(0, 255, 0));
        final int scale = 100;

        for (int i = 0; i < this.coords.size(); i++) {
            final Coord one = this.coords.get(i);
            final Coord two = this.coords.get((i + 1) % this.coords.size());
            graphics2D.drawLine(one.getX() / scale, one.getY() / scale, two.getX() / scale, two.getY() / scale);
        }

        graphics2D.setPaint(new Color(255, 0, 0));

        for (int i = 0; i < this.coords.size(); i++) {
            final Coord one = this.coords.get(i);
            graphics2D.drawOval(-1 + one.getX() / scale, -1 + one.getY() / scale, 1, 1);
        }

        graphics2D.setPaint(new Color(255, 255, 0));
        graphics2D.drawLine(bestOne.getX() / scale, bestOne.getY() / scale, bestTwo.getX() / scale, bestOne.getY() / scale);
        graphics2D.drawLine(bestTwo.getX() / scale, bestOne.getY() / scale, bestTwo.getX() / scale, bestTwo.getY() / scale);
        graphics2D.drawLine(bestTwo.getX() / scale, bestTwo.getY() / scale, bestOne.getX() / scale, bestTwo.getY() / scale);
        graphics2D.drawLine(bestOne.getX() / scale, bestTwo.getY() / scale, bestOne.getX() / scale, bestOne.getY() / scale);

        try {
            ImageIO.write(bufferedImage, "PNG", new File("2025.9.png"));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void drawMap() {

        final BufferedImage bufferedImage = new BufferedImage(1000, 1000, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);

        graphics2D.setPaint(new Color(0, 255, 0));
        final int scale = 100;

        for (int i = 0; i < this.coords.size(); i++) {
            final Coord one = this.coords.get(i);
            final Coord two = this.coords.get((i + 1) % this.coords.size());
            graphics2D.drawLine(one.getX() / scale, one.getY() / scale, two.getX() / scale, two.getY() / scale);
        }

        graphics2D.setPaint(new Color(255, 0, 0));

        for (int i = 0; i < this.coords.size(); i++) {
            final Coord one = this.coords.get(i);
            graphics2D.drawOval(-1 + one.getX() / scale, -1 + one.getY() / scale, 1, 1);
        }

        try {
            ImageIO.write(bufferedImage, "PNG", new File("2025.9.png"));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void drawTestMap() {

        final BufferedImage bufferedImage = new BufferedImage(15, 15, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);

        graphics2D.setPaint(new Color(0, 255, 0));
        final int scale = 1;

        for (int i = 0; i < this.coords.size(); i++) {
            final Coord one = this.coords.get(i);
            final Coord two = this.coords.get((i + 1) % this.coords.size());
            graphics2D.drawLine(one.getX() / scale, one.getY() / scale, two.getX() / scale, two.getY() / scale);
        }

        graphics2D.setPaint(new Color(255, 0, 0));

        for (int i = 0; i < this.coords.size(); i++) {
            final Coord one = this.coords.get(i);
            graphics2D.drawOval(-1 + one.getX() / scale, -1 + one.getY() / scale, 1, 1);
        }

        try {
            ImageIO.write(bufferedImage, "PNG", new File("2025.9.test.png"));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void drawTestMapFill() {

        final BufferedImage bufferedImage = new BufferedImage(15, 15, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);
        final int scale = 1;

        for (int col = 0; col < 15; col++) {
            for (int row = 0; row < 15; row++) {
                final Coord test = new Coord(col, row);
                if (Coord.isInsidePolygon(test, this.coords)) {
                    graphics2D.setPaint(new Color(0, 125, 0));
                } else {
                    graphics2D.setPaint(new Color(0, 0, 100));
                }
                graphics2D.drawOval(-1 + test.getX() / scale, -1 + test.getY() / scale, 1, 1);
            }
        }

        graphics2D.setPaint(new Color(255, 0, 0));

        for (int i = 0; i < this.coords.size(); i++) {
            final Coord one = this.coords.get(i);
            graphics2D.drawOval(-1 + one.getX() / scale, -1 + one.getY() / scale, 1, 1);
        }

        try {
            ImageIO.write(bufferedImage, "PNG", new File("2025.9.test.fill.png"));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void clearBackground(final Graphics2D graphics2D) {

        graphics2D.setPaint(Color.BLACK);
        graphics2D.fillRect(0, 0, 800, 600);
    }

    // this works but is far too slow for the big map
    private boolean completelyInside(final Coord one, final Coord two) {

        final boolean flipX = one.getX() > two.getX();
        final boolean flipY = one.getY() > two.getY();
        final int x1 = flipX ? two.getX() : one.getX();
        final int x2 = flipX ? one.getX() : two.getX();
        final int y1 = flipY ? two.getY() : one.getY();
        final int y2 = flipY ? one.getY() : two.getY();

        for (int x = x1; x <= x2; x += 1) {
            for (int y = y1; y <= y2; y += 1) {
                final Coord test = new Coord(x, y);
                if (!Coord.isInsidePolygon(test, this.coords)) {
                    return false;
                }
            }
        }
        return true;
    }
}
