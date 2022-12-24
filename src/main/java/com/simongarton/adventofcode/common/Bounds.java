package com.simongarton.adventofcode.common;

public class Bounds {
    private Integer minX;
    private Integer minY;
    private Integer maxX;
    private Integer maxY;

    @Override
    public String toString() {
        return this.minX + "," + this.minY + " -> " + this.maxX + "," + this.maxY;
    }

    public int getWidth() {
        return 1 + this.maxX - this.minX;
    }

    public int getHeight() {
        return 1 + this.maxY - this.minY;
    }

    public Integer getMinX() {
        return this.minX;
    }

    public void setMinX(final Integer minX) {
        this.minX = minX;
    }

    public Integer getMinY() {
        return this.minY;
    }

    public void setMinY(final Integer minY) {
        this.minY = minY;
    }

    public Integer getMaxX() {
        return this.maxX;
    }

    public void setMaxX(final Integer maxX) {
        this.maxX = maxX;
    }

    public Integer getMaxY() {
        return this.maxY;
    }

    public void setMaxY(final Integer maxY) {
        this.maxY = maxY;
    }
}
