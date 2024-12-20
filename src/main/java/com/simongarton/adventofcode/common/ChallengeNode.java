package com.simongarton.adventofcode.common;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
public class ChallengeNode {

    @Getter
    private ChallengeCoord coord;
    @Getter
    private long cost;
    @Getter
    private ChallengeNode previous;

}
