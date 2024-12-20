package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.ChallengeCoord;
import com.simongarton.adventofcode.common.ChallengeNode;

import java.util.List;

public class Year2024Day20 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 20: Race Condition";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 20);
    }

    @Override
    public String part1(final String[] input) {

        this.loadChallengeMap(input);
        final ChallengeCoord start = this.findChallengeCoord("S");
        final ChallengeCoord end = this.findChallengeCoord("E");
        final List<ChallengeNode> shortestPath = this.getShortestPathAStar(start, end);

        final List<String> lines = this.updateMapWithNode(shortestPath.get(shortestPath.size() - 1), PATH);
        this.drawMapFromLines(lines);
        return null;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }
}
