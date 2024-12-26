package com.simongarton.adventofcode.year2024.day21;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class DirectionPlan {

    private DirectionHeading heading;
    private int firstMoves;
    private int secondMoves;

}
