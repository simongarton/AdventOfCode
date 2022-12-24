package com.simongarton.adventofcode.common;

import java.util.HashMap;
import java.util.Map;

public class InfiniteGrid {

    private final Map<String, String> grid;
    private String defaultResult;
    private final Bounds bounds;

    public InfiniteGrid() {
        this.grid = new HashMap<>();
        this.defaultResult = " ";
        this.bounds = new Bounds();
    }

    public void setDefaultResult(final String defaultResult) {
        this.defaultResult = defaultResult;
    }

    public String getCoord(final Coord c) {
        this.bounds.updateBounds(c);
        return this.grid.getOrDefault(c.toString(), this.defaultResult);
    }

    public void putCoord(final Coord c, final String value) {
        this.bounds.updateBounds(c);
        this.grid.put(c.toString(), value);
    }

    public String getXY(final int x, final int y) {
        this.bounds.updateBounds(new Coord(x, y));
        final String key = x + "," + y;
        return this.grid.getOrDefault(key, this.defaultResult);
    }

    public void putXY(final int x, final int y, final String value) {
        this.bounds.updateBounds(new Coord(x, y));
        final String key = x + "," + y;
        this.grid.put(key, value);
    }

    public String getKey(final String key) {
        return this.grid.getOrDefault(key, this.defaultResult);
    }

    public void putKey(final String key, final String value) {
        this.grid.put(key, value);
    }

    public InfiniteGrid copy() {
        final InfiniteGrid infiniteGrid = new InfiniteGrid();
        infiniteGrid.setDefaultResult(this.defaultResult);
        for (final Map.Entry<String, String> entry : this.grid.entrySet()) {
            infiniteGrid.putKey(entry.getKey(), entry.getValue());
        }
        return infiniteGrid;
    }

    public void drawOnTerminal() {
        for (int y = this.bounds.getMinY(); y <= this.bounds.getMaxY(); y++) {
            final StringBuilder line = new StringBuilder();
            for (int x = this.bounds.getMinX(); x <= this.bounds.getMaxX(); x++) {
                line.append(this.getXY(x, y));
            }
            System.out.println(line);
        }
        System.out.println("");
    }
}
