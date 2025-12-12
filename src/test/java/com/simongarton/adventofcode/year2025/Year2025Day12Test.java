package com.simongarton.adventofcode.year2025;

import org.junit.jupiter.api.Test;

class Year2025Day12Test {

    @Test
    void testShapes() {

        // given
        final String data = "###.#..##";
        final Year2025Day12.Shape shape = new Year2025Day12.Shape(0, data);

        // when
        shape.draw();
        shape.draw(Year2025Day12.Orientation.FLIP_HORIZONTAL);
        shape.draw(Year2025Day12.Orientation.FLIP_VERTICAL);
        shape.draw(Year2025Day12.Orientation.ROTATE_90);
        shape.draw(Year2025Day12.Orientation.ROTATE_180);
        shape.draw(Year2025Day12.Orientation.ROTATE_270);

        // then
    }

}