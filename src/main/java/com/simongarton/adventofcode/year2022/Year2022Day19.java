package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class Year2022Day19 extends AdventOfCodeChallenge {

    private static final String GEODE = "G";
    private static final String OBSIDIAN = "B";
    private static final String CLAY = "C";
    private static final String ORE = "O";
    private static final String WAIT = "-";

    @Override
    public String title() {
        return "Day 19: Not Enough Minerals";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2022, 19);
    }

    @Override
    public String part1(final String[] input) {
        final Map<Factory, Integer> bestScores = new HashMap<>();
        for (final String blueprint : input) {
            final Factory factory = new Factory(blueprint, 24);
            final int bestScore = factory.bestScore();
            bestScores.put(factory, bestScore);
        }
        int quality = 0;
        for (final Map.Entry<Factory, Integer> entry : bestScores.entrySet()) {
            quality += entry.getKey().id * entry.getValue();
        }
        return String.valueOf(quality);
    }

    @Override
    public String part2(final String[] input) {
        // confirm that this gives 62 with sample2
        // then start thinking about more active pruning
        //   when can I stop adding waits ?
        //   when can I stop adding robots (I've got more than enough by now)
        // and write it up on my website.
        final Map<Factory, Integer> bestScores = new HashMap<>();
        for (final String blueprint : input) {
            final Factory factory = new Factory(blueprint, 32);
            final int bestScore = factory.bestScore();
            bestScores.put(factory, bestScore);
        }
        int total = 1;
        for (final Map.Entry<Factory, Integer> entry : bestScores.entrySet()) {
            total = total * entry.getValue();
        }
        return String.valueOf(total);
    }

    @Getter
    public static final class Factory implements Cloneable {

        private boolean factoryDebug = false;
        private static final boolean DEBUG = true;

        private String blueprintTitle;
        private int id;
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

        private int maxOreCost;
        private int maxClayCost;
        private int maxObsidianCost;


        private int time = 0;
        private final int maxTime;
        final Map<Integer, Integer> timeToMakeGeodes;

        public Factory(final String fullBlueprint, final int maxTime) {
            this.oreCollectingRobots = 1;
            this.loadBlueprint(fullBlueprint);
            this.timeToMakeGeodes = new HashMap<>();
            this.setupGeodeTimings();
            this.maxTime = maxTime;
        }

        private void setupGeodeTimings() {
            this.timeToMakeGeodes.put(1, 0);
            this.timeToMakeGeodes.put(2, 1);
            this.timeToMakeGeodes.put(3, 1);
            this.timeToMakeGeodes.put(4, 2);
            this.timeToMakeGeodes.put(5, 2);
            this.timeToMakeGeodes.put(6, 2);
            this.timeToMakeGeodes.put(7, 3);
            this.timeToMakeGeodes.put(8, 3);
            this.timeToMakeGeodes.put(9, 3);
            this.timeToMakeGeodes.put(10, 3);
            this.timeToMakeGeodes.put(11, 4);
            this.timeToMakeGeodes.put(12, 4);
            this.timeToMakeGeodes.put(13, 4);
            this.timeToMakeGeodes.put(14, 4);
        }

        public void setTitle(final String newTitle) {
            this.blueprintTitle = newTitle;
        }

        public void setFactoryDebug(final boolean factoryDebug) {
            this.factoryDebug = factoryDebug;
        }

        @Override
        public Factory clone() {
            try {
                return (Factory) super.clone();
            } catch (final CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        private void loadBlueprint(final String fullBlueprint) {
            final String[] parts = fullBlueprint.split(":");
            this.blueprintTitle = parts[0];
            this.id = Integer.parseInt(this.blueprintTitle.replace("Blueprint ", ""));
            final List<String> blueprint = Arrays.stream(parts[1].trim().split("\\. "))
                    .map(String::trim)
                    .collect(Collectors.toList());
            this.oreRobotCostOre = this.extractNumber(blueprint.get(0), 4);
            this.clayRobotCostOre = this.extractNumber(blueprint.get(1), 4);
            this.obsidianRobotCostOre = this.extractNumber(blueprint.get(2), 4);
            this.obsidianRobotCostClay = this.extractNumber(blueprint.get(2), 7);
            this.geodeRobotCostOre = this.extractNumber(blueprint.get(3), 4);
            this.geodeRobotCostObsidian = this.extractNumber(blueprint.get(3), 7);

            this.maxOreCost = Math.max(
                    Math.max(
                            Math.max(
                                    this.oreRobotCostOre, this.clayRobotCostOre),
                            this.obsidianRobotCostOre),
                    this.geodeRobotCostOre);
            this.maxClayCost = this.obsidianRobotCostClay;
            this.maxObsidianCost = this.geodeRobotCostObsidian;
        }

        private int extractNumber(final String s, final int i) {
            final String[] parts = s.split(" ");
            return Integer.parseInt(parts[i]);
        }

        private void factoryDebugPrint(final String s) {
            if (this.factoryDebug) {
                System.out.println(s);
            }
        }

        private void debugPrint(final String s) {
            if (DEBUG) {
                System.out.println(s);
            }
        }

        @Override
        public String toString() {
            return String.format("%s | @%3d o:%3d, c:%3d, O:%3d, g:%3d [%3d, %3d, %3d, %3d] {%3d, %3d, {%3d, %3d}, {%3d, %3d}}",
                    this.blueprintTitle,
                    this.time,
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

        // this is a core method. I have to collect ores AFTER deciding if I can make a robot.
        // I can inline the tests.
        private void makeRobot(final String nextRobot) {
            while (this.time < this.maxTime) {
                this.time++;
                switch (nextRobot) {
                    case ORE:
                        if (this.canMakeRobot(ORE)) {
                            this.collectOres();
                            this.oreCollectingRobots++;
                            this.ore -= this.oreRobotCostOre;
                            return;
                        }
                        break;
                    case CLAY:
                        if (this.canMakeRobot(CLAY)) {
                            this.collectOres();
                            this.clayCollectingRobots++;
                            this.ore -= this.clayRobotCostOre;
                            return;
                        }
                        break;
                    case OBSIDIAN:
                        if (this.canMakeRobot(OBSIDIAN)) {
                            this.collectOres();
                            this.obsidianCollectingRobots++;
                            this.ore -= this.obsidianRobotCostOre;
                            this.clay -= this.obsidianRobotCostClay;
                            return;
                        }
                        break;
                    case GEODE:
                        if (this.canMakeRobot(GEODE)) {
                            this.collectOres();
                            this.geodeCollectingRobots++;
                            this.ore -= this.geodeRobotCostOre;
                            this.obsidian -= this.geodeRobotCostObsidian;
                            return;
                        }
                        break;
                    case WAIT:
                        this.collectOres();
                        return;
                }
                this.collectOres();
            }
        }

        private boolean canMakeRobot(final String robot) {
            switch (robot) {
                case ORE:
                    return this.ore >= this.oreRobotCostOre;
                case CLAY:
                    return this.ore >= this.clayRobotCostOre;
                case OBSIDIAN:
                    return this.ore >= this.obsidianRobotCostOre && this.clay >= this.obsidianRobotCostClay;
                case GEODE:
                    return this.ore >= this.geodeRobotCostOre && this.obsidian >= this.geodeRobotCostObsidian;
                default:
                    throw new RuntimeException(robot);
            }
        }

        private void collectOres() {
            this.ore += this.oreCollectingRobots;
            this.clay += this.clayCollectingRobots;
            this.obsidian += this.obsidianCollectingRobots;
            this.geodes += this.geodeCollectingRobots;
        }

        public void testSequence(final String sequence) {
            System.out.println("\nTesting sequence " + sequence + "\n");
            for (int i = 0; i < sequence.length(); i++) {
                final String nextRobot = sequence.substring(i, i + 1);
                this.makeRobot(nextRobot);
            }
            while (this.time < this.maxTime) {
                this.collectOres();
                this.factoryDebugPrint("iterated : " + this);
                this.time++;
            }
        }

        public int bestScore() {

            final long start = System.currentTimeMillis();

            final List<Plan> availablePlans = new ArrayList<>();
            // no point in starting with a wait, an obsidian or a geode
            availablePlans.add(new Plan(ORE, this.clone()));
            availablePlans.add(new Plan(CLAY, this.clone()));

            int bestGeodes = 0;
            int firstGeodeFoundAtTime = Integer.MAX_VALUE;
            Plan bestPlan = null;

            long iteration = 0;

            while (!availablePlans.isEmpty()) {
                final Plan current = availablePlans.get(0);
                availablePlans.remove(0);
                iteration++;

                // pruning
                final int currentFactoryTime = current.factory.time;
                if (current.factory.geodes == 0 && currentFactoryTime > firstGeodeFoundAtTime) {
                    continue;
                }
                final int timeToGo = current.factory.maxTime - currentFactoryTime;
                final int minutesNeeded = this.timeToMakeGeodes.getOrDefault(bestGeodes, 0);
                if (current.factory.geodeCollectingRobots == 0 && timeToGo < minutesNeeded) {
                    continue;
                }

                final String nextRobot = current.plan.substring(current.plan.length() - 1);
                current.factory.makeRobot(nextRobot);
                if (current.factory.geodes > 0) {
                    if (currentFactoryTime < firstGeodeFoundAtTime) {
                        firstGeodeFoundAtTime = currentFactoryTime;
                    }
                }
                if (currentFactoryTime == this.maxTime) {
                    if ((bestPlan == null || bestGeodes < current.factory.geodes) &&
                            current.factory.geodes > 0) {
                        bestPlan = current;
                        bestGeodes = current.factory.geodes;
                        final long elapsedSeconds = (System.currentTimeMillis() - start);
                        this.debugPrint(
                                current.factory.blueprintTitle +
                                        " : " +
                                        bestGeodes +
                                        " geodes with " +
                                        bestPlan.plan +
                                        ", still got " +
                                        availablePlans.size() +
                                        " plans; done " +
                                        String.format("%,d", iteration) + " iterations, " +
                                        "having taken " + String.format("%1.2f", elapsedSeconds / 1000.0) + " seconds " +
                                        "@ rate " + String.format("%1.2f", iteration * 1.0 / elapsedSeconds) + "/ms; " +
                                        "currently working with " + current.factory);
                    }
                } else {
                    if (timeToGo > 1) {
                        availablePlans.add(0, this.newPlanFrom(current, GEODE));
                        availablePlans.add(0, this.newPlanFrom(current, WAIT));
                        // various optimisations gleaned from the subreddit.
                        if (timeToGo > 4) {
                            if (current.factory.obsidianCollectingRobots < current.factory.maxObsidianCost) {
                                availablePlans.add(0, this.newPlanFrom(current, OBSIDIAN));
                            }
                            if (timeToGo > 7) {
                                if (current.factory.clayCollectingRobots < current.factory.maxClayCost) {
                                    availablePlans.add(0, this.newPlanFrom(current, CLAY));
                                }
                                if (timeToGo > 16) {
                                    if (current.factory.oreCollectingRobots < current.factory.maxOreCost) {
                                        availablePlans.add(0, this.newPlanFrom(current, ORE));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            final long elapsedSeconds = (System.currentTimeMillis() - start);
            if (bestPlan != null) {
                this.debugPrint(
                        bestPlan.factory.blueprintTitle +
                                " : best is " +
                                bestGeodes +
                                " geodes with " +
                                bestPlan.plan +
                                "; done " +
                                String.format("%,d", iteration) + " iterations, " +
                                "having taken " + String.format("%1.2f", elapsedSeconds / 1000.0) + " seconds " +
                                "@ rate " + String.format("%1.2f", iteration * 1.0 / elapsedSeconds) + "/ms.");
            } else {
                this.debugPrint(
                        "Nothing found after " +
                                String.format("%,d", iteration) + " iterations, " +
                                "having taken " + String.format("%5.2f", elapsedSeconds / 1000.0) + " seconds " +
                                "@ rate " + String.format("%1.2f", iteration * 1.0 / elapsedSeconds) + "/ms.");
            }
            return bestGeodes;
        }

        private boolean worthChecking(final Plan current, final int bestGeodes, final int firstGeodeFoundAtTime) {
            if (current.factory.geodes == 0 && current.factory.time > firstGeodeFoundAtTime) {
                return false;
            }
            final int timeToGo = current.factory.maxTime - current.factory.time;
            final int minutesNeeded = this.timeToMakeGeodes.getOrDefault(bestGeodes, 0);
            if (current.factory.geodeCollectingRobots == 0 && timeToGo < minutesNeeded) {
                return false;
            }
            return true;
        }

        private Plan newPlanFrom(final Plan current, final String robot) {
            return new Plan(current.plan + robot, current.factory.clone());
        }
    }

    public static final class Plan {

        private final String plan;
        private final Factory factory;

        public Plan(final String plan, final Factory factory) {
            this.plan = plan;
            this.factory = factory;
        }
    }
}
