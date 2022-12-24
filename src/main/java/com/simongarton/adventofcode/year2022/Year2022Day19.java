package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2022Day19 extends AdventOfCodeChallenge {

    private List<Factory> factories;

    /*

    I fear.

    Blueprint 1 shows the trouble.  Clay robots are cheaper in ore than ore robots, so the simplest
    algorithm keeps making more clay robots, so we don't get enough ore robots to harvest enough ore to
    make an obsidian robot.

    Clever girl.

    Have considered various options :

    - tree searching : set up State, and for each option at each minute, create a new state and explore. I think there
      will be too many options, e.g. if I have 10 ore robots, I will have 10 ore, and could make 1,2,3,4,5,6,7,8,9 or 10
      new ore robots; then also use that ore for better robots (in different quantities, again); or save it for later ...
    - set up a plan - I want to make n of ore robots, then m of clay, etc; that will be a smaller space I think
    - use the numbers to work out the fastest way to get to 1 geode robot; this probably has tradeoffs between making
      the better robots earlier and letting them work vs later and in more quantity; though I suspect its make them as
      early as possible.

    So in blueprint 1 I need 2 ore and 7 obsidian for a geode; to get 1 obsidian I need 3 ore, 14 clay; for 1 clay = 2 ore
    and 1 ore = 4 ore. So I need to get to 98 clay for the geode. Do I alternate clay and ore to get clay earlier, or
    focus on ore to get more ore robots so I can then build others faster ?


     */

    @Override
    public String title() {
        return "Day 19: Not Enough Minerals";
    }

    @Override
    public boolean run() {
        return this.runChallenge(2022, 19);
    }

    @Override
    public String part1(final String[] input) {
        this.loadFactories(input);
        return null;
    }

    private void loadFactories(final String[] input) {
        this.factories = new ArrayList<>();
        for (final String blueprint : input) {
            this.factories.add(new Factory(blueprint));
        }
        for (final Factory factory : this.factories) {
            for (int minute = 1; minute <= 24; minute++) {
                factory.mustGrow();
                System.out.printf("%3d : %s\n", minute, factory);
            }
        }
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    public static final class Factory {

        private String blueprintTitle;
        private int ore;
        private int clay;
        private int obsidian;
        private int geodes;

        private int oreRobotCostOre;
        private int clayRobotCostOre;
        private int obsidianRobotCostOre;
        private int obsidianRobotCostClay;
        private int geodeRobotCostOre;
        private int geodeRobotCostObsidian;

        private int oreCollectingRobots;
        private int clayCollectingRobots;
        private int obsidianCollectingRobots;
        private int geodeCollectingRobots;

        private Factory(final String fullBlueprint) {
            this.oreCollectingRobots = 1;
            this.loadBlueprint(fullBlueprint);
        }

        private void loadBlueprint(final String fullBlueprint) {
            final String[] parts = fullBlueprint.split(":");
            this.blueprintTitle = parts[0];
            final List<String> blueprint = Arrays.stream(parts[1].trim().split("\\. "))
                    .map(String::trim)
                    .collect(Collectors.toList());
            this.oreRobotCostOre = this.extractNumber(blueprint.get(0), 4);
            this.clayRobotCostOre = this.extractNumber(blueprint.get(1), 4);
            this.obsidianRobotCostOre = this.extractNumber(blueprint.get(2), 4);
            this.obsidianRobotCostClay = this.extractNumber(blueprint.get(2), 7);
            this.geodeRobotCostOre = this.extractNumber(blueprint.get(3), 4);
            this.geodeRobotCostObsidian = this.extractNumber(blueprint.get(3), 7);
        }

        private int extractNumber(final String s, final int i) {
            final String[] parts = s.split(" ");
            return Integer.parseInt(parts[i]);
        }

        @Override
        public String toString() {
            return String.format("%s | o:%3d, c:%3d, O:%3d, g:%3d [%3d, %3d, %3d, %3d] {%3d, %3d, {%3d, %3d}, {%3d, %3d}}",
                    this.blueprintTitle,
                    this.ore,
                    this.clay,
                    this.obsidian,
                    this.geodes,
                    this.oreCollectingRobots,
                    this.clayCollectingRobots,
                    this.obsidianCollectingRobots,
                    this.geodeCollectingRobots,
                    this.oreRobotCostOre,
                    this.clayRobotCostOre,
                    this.obsidianRobotCostOre,
                    this.obsidianRobotCostClay,
                    this.geodeRobotCostOre,
                    this.geodeRobotCostObsidian
            );
        }

        public void mustGrow() {
            this.collectOres();
            this.buildNewRobots();
        }

        private void buildNewRobots() {
            // I am assuming we go for the most expensive first.
            // But I fear.
            this.buildGeodeRobots();
            this.buildObsidianRobots();
            this.buildClayRobots();
            this.buildOreRobots();
        }

        private void buildGeodeRobots() {
            final int possibleGeodeRobotsFromOre = this.ore / this.geodeRobotCostOre;
            final int possibleGeodeRobotsFromObsidian = this.obsidian / this.geodeRobotCostObsidian;
            final int newRobots = Math.min(possibleGeodeRobotsFromOre, possibleGeodeRobotsFromObsidian);
            this.geodeCollectingRobots += newRobots;
            this.ore -= (newRobots * this.geodeRobotCostOre);
            this.obsidian -= (newRobots * this.geodeRobotCostObsidian);
        }

        private void buildObsidianRobots() {
            final int possibleObsidianRobotsFromOre = this.ore / this.obsidianRobotCostOre;
            final int possibleObsidianRobotsFromClay = this.clay / this.obsidianRobotCostClay;
            final int newRobots = Math.min(possibleObsidianRobotsFromOre, possibleObsidianRobotsFromClay);
            this.obsidianCollectingRobots += newRobots;
            this.ore -= (newRobots * this.obsidianRobotCostOre);
            this.clay -= (newRobots * this.obsidianRobotCostClay);
        }

        private void buildClayRobots() {
            final int newRobots = this.ore / this.clayRobotCostOre;
            this.clayCollectingRobots += newRobots;
            this.ore -= (newRobots * this.clayRobotCostOre);
        }

        private void buildOreRobots() {
            final int newRobots = this.ore / this.oreRobotCostOre;
            this.oreCollectingRobots += newRobots;
            this.ore -= (newRobots * this.oreRobotCostOre);
        }

        private void collectOres() {
            this.ore += this.oreCollectingRobots;
            this.clay += this.clayCollectingRobots;
            this.obsidian += this.obsidianCollectingRobots;
            this.geodes += this.geodeCollectingRobots;
        }
    }

}
