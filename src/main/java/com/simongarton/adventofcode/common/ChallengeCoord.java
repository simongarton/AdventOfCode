package com.simongarton.adventofcode.common;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Getter
@Builder(toBuilder = true)
public class ChallengeCoord {

    private final int x;
    private final int y;

    public ChallengeCoord(final int x, final int y) {

        this.x = x;
        this.y = y;
    }

    public ChallengeCoord(final String data) {

        this(
                Integer.parseInt(data.split(",")[0]),
                Integer.parseInt(data.split(",")[1])
        );
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
        final ChallengeCoord challengeCoord = (ChallengeCoord) o;
        return this.x == challengeCoord.x && this.y == challengeCoord.y;
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.x, this.y);
    }
}