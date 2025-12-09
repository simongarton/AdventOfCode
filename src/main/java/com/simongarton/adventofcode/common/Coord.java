package com.simongarton.adventofcode.common;

import java.util.List;

public class Coord {

    private Integer x;
    private Integer y;

    public Coord(final String coordinate) {
        final String[] parts = coordinate.split(",");
        this.x = Integer.parseInt(parts[0]);
        this.y = Integer.parseInt(parts[1]);
    }

    public Coord(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return this.x + "," + this.y;
    }

    public Integer getX() {
        return this.x;
    }

    public void setX(final Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return this.y;
    }

    public void setY(final Integer y) {
        this.y = y;
    }

    public double euclideanDistance(final Coord other) {
        return Math.sqrt(
                Math.pow(this.x - other.x, 2) +
                        Math.pow(this.y - other.y, 2)
        );
    }

    public int manhattanDistance(final Coord other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    public static boolean isInsidePolygon(final Coord point, final List<Coord> polygon) {

        if (polygon.size() < 3) {
            return false; // Not a valid polygon
        }

        if (isOnBoundary(point, polygon)) {
            return true; // or false, depending on your needs
        }

        boolean inside = false;
        final int n = polygon.size();

        for (int i = 0, j = n - 1; i < n; j = i++) {
            final Coord vi = polygon.get(i);
            final Coord vj = polygon.get(j);

            // Check if the ray crosses this edge
            if ((vi.getY() > point.getY()) != (vj.getY() > point.getY()) &&
                    point.getX() < (vj.getX() - vi.getX()) * (point.getY() - vi.getY()) / (vj.getY() - vi.getY()) + vi.getX()) {
                inside = !inside;
            }
        }

        return inside;
    }

    private static boolean isOnBoundary(final Coord point, final List<Coord> polygon) {

        final int n = polygon.size();

        for (int i = 0, j = n - 1; i < n; j = i++) {
            final Coord vi = polygon.get(i);
            final Coord vj = polygon.get(j);

            // Check if point is on the line segment from vi to vj
            if (isOnSegment(point, vi, vj)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isOnSegment(final Coord p, final Coord a, final Coord b) {
        // Check if p is collinear with a and b
        final int crossProduct = (p.getY() - a.getY()) * (b.getX() - a.getX()) - (p.getX() - a.getX()) * (b.getY() - a.getY());

        if (crossProduct != 0) {
            return false; // Not collinear
        }

        // Check if p is within the bounding box of the segment
        if (p.getX() < Math.min(a.getX(), b.getX()) || p.getX() > Math.max(a.getX(), b.getX())) {
            return false;
        }
        if (p.getY() < Math.min(a.getY(), b.getY()) || p.getY() > Math.max(a.getY(), b.getY())) {
            return false;
        }

        return true;
    }
}
