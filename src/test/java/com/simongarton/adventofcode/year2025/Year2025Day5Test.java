package com.simongarton.adventofcode.year2025;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Year2025Day5Test {

    @Test
    void checkRangeInside() {

        assertTrue(new Year2025Day5.Range(1, 5).inside(new Year2025Day5.Range(0, 6)));
        assertTrue(new Year2025Day5.Range(1, 5).inside(new Year2025Day5.Range(1, 6)));
        assertFalse(new Year2025Day5.Range(0, 5).inside(new Year2025Day5.Range(1, 6)));
        assertFalse(new Year2025Day5.Range(0, 5).inside(new Year2025Day5.Range(6, 10)));
        assertFalse(new Year2025Day5.Range(0, 5).inside(new Year2025Day5.Range(3, 10)));
    }

    @Test
    void checkRangeOverlaps() {

        assertFalse(new Year2025Day5.Range(1, 5).overlaps(new Year2025Day5.Range(0, 6)));
        assertFalse(new Year2025Day5.Range(1, 5).overlaps(new Year2025Day5.Range(1, 6)));
        assertTrue(new Year2025Day5.Range(0, 5).overlaps(new Year2025Day5.Range(1, 6)));
        assertFalse(new Year2025Day5.Range(0, 5).overlaps(new Year2025Day5.Range(6, 10)));
        assertTrue(new Year2025Day5.Range(0, 5).overlaps(new Year2025Day5.Range(3, 10)));
    }

}