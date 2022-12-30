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

    /*
    This is going pretty well, but on the sample, I can get Blueprint 1 to match with 9 geodes,
    but Blueprint 2 is only giving me 10 geodes, not 12.

    Double checked the rules, had a look at one example. Might be dropping out too early ? Oh wait, I didn't have a
    "do nothing" step, which could be an option.

    2113 is too low

     */

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
            System.out.println("Best score for " + factory.id + " is " + bestScore);
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
            System.out.println("Best score for " + factory.id + " is " + bestScore);
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

        private boolean makeRobot(final String nextRobot) {
            while (this.time < this.maxTime) {
                this.time++;
                switch (nextRobot) {
                    case ORE:
                        if (this.canMakeRobot(ORE)) {
                            this.factoryDebugPrint("making " + nextRobot + " robot during minute " + this.time);
                            this.collectOres();
                            this.oreCollectingRobots++;
                            this.ore -= this.oreRobotCostOre;
                            this.factoryDebugPrint("iterated : " + this);
                            return true;
                        }
                        break;
                    case CLAY:
                        if (this.canMakeRobot(CLAY)) {
                            this.factoryDebugPrint("making " + nextRobot + " robot during minute " + this.time);
                            this.collectOres();
                            this.clayCollectingRobots++;
                            this.ore -= this.clayRobotCostOre;
                            this.factoryDebugPrint("iterated : " + this);
                            return true;
                        }
                        break;
                    case OBSIDIAN:
                        if (this.canMakeRobot(OBSIDIAN)) {
                            this.factoryDebugPrint("making " + nextRobot + " robot during minute " + this.time);
                            this.collectOres();
                            this.obsidianCollectingRobots++;
                            this.ore -= this.obsidianRobotCostOre;
                            this.clay -= this.obsidianRobotCostClay;
                            this.factoryDebugPrint("iterated : " + this);
                            return true;
                        }
                        break;
                    case GEODE:
                        if (this.canMakeRobot(GEODE)) {
                            this.factoryDebugPrint("making " + nextRobot + " robot during minute " + this.time);
                            this.collectOres();
                            this.geodeCollectingRobots++;
                            this.ore -= this.geodeRobotCostOre;
                            this.obsidian -= this.geodeRobotCostObsidian;
                            this.factoryDebugPrint("iterated : " + this);
                            return true;
                        }
                        break;
                    case WAIT:
                        this.factoryDebugPrint("waiting during minute " + this.time);
                        this.collectOres();
                        this.factoryDebugPrint("iterated : " + this);
                        return true;
                    default:
                        throw new RuntimeException(nextRobot);
                }
                this.collectOres();
                this.factoryDebugPrint("iterated : " + this);
            }
            return false;
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
                final boolean robotMade = this.makeRobot(nextRobot);
            }
            while (this.time < this.maxTime) {
                this.collectOres();
                this.factoryDebugPrint("iterated : " + this);
                this.time++;
            }
        }

        public Integer bestScore() {

            final long start = System.currentTimeMillis();

            final List<Plan> availablePlans = new ArrayList<>();
            availablePlans.add(new Plan(ORE, this.clone()));
            availablePlans.add(new Plan(CLAY, this.clone()));
            availablePlans.add(new Plan(OBSIDIAN, this.clone()));
            availablePlans.add(new Plan(GEODE, this.clone()));

            int bestGeodes = 0;
            int firstGeodeFoundAtTime = Integer.MAX_VALUE;
            Plan bestPlan = null;

            long iteration = 0;
            int loopIteration = 0;
            int availablePlanCount = availablePlans.size();

            long loopStart = System.currentTimeMillis();

            while (!availablePlans.isEmpty()) {
                final Plan current = availablePlans.get(0);
                availablePlans.remove(0);
                availablePlanCount--;
                if (iteration % 100000 == 0 && false) {
                    final long elapsedSeconds = (System.currentTimeMillis() - loopStart);
                    System.out.println("iteration " + iteration +
                            " : best " + bestGeodes +
                            " rate " + String.format("%5.2f", loopIteration * 1.0 / elapsedSeconds) + "/ms" +
                            " having taken " + String.format("%5.2f", elapsedSeconds / 1000.0) + " seconds;" +
                            " current plan is " + current.plan +
                            " and I have " + availablePlanCount +
                            " left : " + current.factory);
                    loopStart = System.currentTimeMillis();
                    loopIteration = 0;
                } else {
                    loopIteration++;
                }
                iteration++;
                if (!this.worthChecking(current, bestGeodes, firstGeodeFoundAtTime)) {
                    // this may have been too aggressive.
                    continue;
                }

                final String nextRobot = current.plan.substring(current.plan.length() - 1);
                final boolean robotMade = current.factory.makeRobot(nextRobot);
                if (current.factory.geodes > 0) {
                    if (current.factory.time < firstGeodeFoundAtTime) {
                        firstGeodeFoundAtTime = current.factory.time;
                    }
                }
                if (current.factory.time == this.maxTime) {
                    if (
                            (bestPlan == null || bestGeodes < current.factory.geodes) &&
                                    current.factory.geodes > 0) {
                        bestPlan = current;
                        bestGeodes = current.factory.geodes;
                        final long elapsedSeconds = (System.currentTimeMillis() - start);
                        System.out.println(
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
                    // not worth checking plans that won't add anything
                    final int timeToGo = current.factory.maxTime - current.factory.time;
                    availablePlans.add(0, this.newPlanFrom(current, WAIT));
                    availablePlanCount++;
                    if (timeToGo > 1) {
                        availablePlans.add(0, this.newPlanFrom(current, GEODE));
                        availablePlanCount++;
                    }
                    // these are magic numbers from Reddit.
                    if (timeToGo > 4) {
                        availablePlans.add(0, this.newPlanFrom(current, OBSIDIAN));
                        availablePlanCount++;
                    }
                    if (timeToGo > 7) {
                        availablePlans.add(0, this.newPlanFrom(current, CLAY));
                        availablePlanCount++;
                    }
                    if (timeToGo > 16) {
                        availablePlans.add(0, this.newPlanFrom(current, ORE));
                        availablePlanCount++;
                    }
                }
            }
            final long elapsedSeconds = (System.currentTimeMillis() - start);
            if (bestPlan != null) {
                System.out.println(
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
                System.out.println(
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
