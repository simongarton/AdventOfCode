package com.simongarton.adventofcode.common;

import lombok.Builder;

import java.util.Objects;

@Builder(toBuilder = true)
public class ChallengeLongCoord {

    public final long x;
    public final long y;

    public ChallengeLongCoord(final long x, final long y) {

        this.x = x;
        this.y = y;
    }

    public ChallengeLongCoord(final String coordinate) {

        final String[] parts = coordinate.split(",");
        this.x = Long.parseLong(parts[0]);
        this.y = Long.parseLong(parts[1]);
    }


    public long getX() {
        return x;
    }

    public long getY() {
        return y;
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
        final ChallengeLongCoord coord = (ChallengeLongCoord) o;
        return this.x == coord.x && this.y == coord.y;
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.x, this.y);
    }
}