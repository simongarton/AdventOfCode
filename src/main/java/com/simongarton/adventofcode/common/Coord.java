package com.simongarton.adventofcode.common;

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
}
