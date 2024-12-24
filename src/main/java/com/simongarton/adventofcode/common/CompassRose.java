package com.simongarton.adventofcode.common;

import lombok.Getter;

public enum CompassRose {

    NORTH(0),
    EAST(1),
    SOUTH(2),
    WEST(3);

    @Getter
    final private int heading;

    CompassRose(final int heading) {
        this.heading = heading;
    }


}
