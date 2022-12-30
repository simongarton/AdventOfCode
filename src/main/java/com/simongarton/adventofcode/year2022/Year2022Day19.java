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

    private static final int MAX_TIME = 24;

    /*
    This is going pretty well, but on the sample, I can get Blueprint 1 to match with 9 geodes,
    but Blueprint 2 is only giving me 10 geodes, not 12.

    Double checked the rules, had a look at one example. Might be dropping out too early ? Oh wait, I didn't have a
    "do nothing" step, which could be an option.

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
            final Factory factory = new Factory(blueprint);
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
        return null;
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

        public Factory(final String fullBlueprint) {
            this.oreCollectingRobots = 1;
            this.loadBlueprint(fullBlueprint);
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
            while (this.time < MAX_TIME) {
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
            for (int i = 0; i < sequence.length(); i++) {
                final String nextRobot = sequence.substring(i, i + 1);
                final boolean robotMade = this.makeRobot(nextRobot);
            }
            while (this.time < MAX_TIME) {
                this.collectOres();
                this.factoryDebugPrint("iterated : " + this);
                this.time++;
            }
        }

        public Integer bestScore() {

            final List<Plan> availablePlans = new ArrayList<>();
            availablePlans.add(new Plan(WAIT, this.clone()));
            availablePlans.add(new Plan(ORE, this.clone()));
            availablePlans.add(new Plan(CLAY, this.clone()));
            availablePlans.add(new Plan(OBSIDIAN, this.clone()));
            availablePlans.add(new Plan(GEODE, this.clone()));

            int bestGeodes = 0;
            Plan bestPlan = null;

            int iteration = 0;
            int loopIteration = 0;
            int availablePlanCount = availablePlans.size();

            long loopStart = System.currentTimeMillis();

            // would a stack be faster than a list ? apparently not, but there is also ArrayDequeue.
            while (!availablePlans.isEmpty()) {
                final Plan current = availablePlans.get(loopIteration);
//                availablePlans.remove(0);
                availablePlanCount--;
                if (iteration % 1000000 == 0) {
                    final long elapsedSeconds = (System.currentTimeMillis() - loopStart);
                    System.out.println("iteration " + iteration +
                            " : best " + bestGeodes +
                            " rate " + String.format("%3.2f", loopIteration * 1.0 / elapsedSeconds) + "/ms" +
                            " current plan is " + current.plan +
                            " and I have " + availablePlanCount +
                            " left : " + current.factory);
                    loopStart = System.currentTimeMillis();
                    for (int i = 0; i < loopIteration; i++) {
                        availablePlans.remove(i);
                    }
                    loopIteration = 0;
                } else {
                    loopIteration++;
                }
                iteration++;
                if (!this.worthChecking(current, bestGeodes)) {
                    continue;
                }

                final String nextRobot = current.plan.substring(current.plan.length() - 1);
                final boolean robotMade = current.factory.makeRobot(nextRobot);
                if (current.factory.time == MAX_TIME) {
                    if (
                            (bestPlan == null || bestGeodes < current.factory.geodes) &&
                                    current.factory.geodes > 0) {
                        bestPlan = current;
                        bestGeodes = current.factory.geodes;
                        System.out.println("New best plan : geodes " +
                                bestGeodes +
                                " with " +
                                bestPlan.plan +
                                " still got " +
                                availablePlans.size() +
                                " using " +
                                current.factory);
                    }
                } else {
                    // not worth checking plans that won't add anything
                    if (current.factory.time <= (MAX_TIME - 1)) {
                        availablePlans.add(this.newPlanFrom(current, GEODE));
                        availablePlanCount++;
                    }
                    if (current.factory.time <= (MAX_TIME - 2)) {
                        availablePlans.add(this.newPlanFrom(current, WAIT));
                        availablePlans.add(this.newPlanFrom(current, OBSIDIAN));
                        availablePlans.add(this.newPlanFrom(current, CLAY));
                        availablePlans.add(this.newPlanFrom(current, ORE));
                        availablePlanCount += 4;
                    }
                }
            }
            return bestGeodes;
        }

        private boolean worthChecking(final Plan current, final int bestGeodes) {
            if (current.factory.geodeCollectingRobots < 2) {
                if (current.factory.time > (MAX_TIME - (bestGeodes - current.factory.geodes))) {
                    return false;
                }
            }
            if (current.factory.time > (MAX_TIME - this.geodes)) {
                if (current.factory.geodeCollectingRobots == 0 ||
                        current.factory.obsidianCollectingRobots < 2
                ) {
                    return false;
                }
            }
            // more cunning pruning here - count up different types of robots, existing ore.
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
