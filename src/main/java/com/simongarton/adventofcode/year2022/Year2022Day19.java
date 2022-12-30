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

    private static final int MAX_TIME = 24;

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
            bestScores.put(factory, factory.bestScore());
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

        private static final boolean FACTORY_DEBUG = false;

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
            if (FACTORY_DEBUG) {
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
                            this.collectOres();
                            this.factoryDebugPrint("making " + nextRobot + " robot during minute " + this.time);
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
                final boolean success = this.makeRobot(nextRobot);
//                System.out.println(this);
            }
            while (this.time < MAX_TIME) {
                this.collectOres();
                this.factoryDebugPrint("iterated : " + this);
                this.time++;
            }
        }

        public Integer bestScore() {
            /*
             I need to make best use of this blueprint. To this, I need to try out all reasonable sequences of
             robot plans. Starting with an empty string, I will set up "O", "C", "B" or "G" to make one of these as fast
             as possible. This will mean waiting - nothing smart - until I have enough resources to make that one robot -
             so I know ahead of time I won't be able to make a B or a G at all, as I won't initially have any clay or
             obsidian, respectively. So I need to stop after 24 minutes.
             
             If I do manage to make the first one, then I need to add on all the variations as a second robot; etc. Each time 
             I run out of time, work out how many geodes I mined, and store that, as well as the factory (?).
             
             */

            final List<Plan> availablePlans = new ArrayList<>();
            availablePlans.add(new Plan(ORE, this.clone()));
            availablePlans.add(new Plan(CLAY, this.clone()));
            availablePlans.add(new Plan(OBSIDIAN, this.clone()));
            availablePlans.add(new Plan(GEODE, this.clone()));

            int geodes = 0;
            Plan bestPlan = null;

            int iteration = 0;

            while (!availablePlans.isEmpty()) {
                final Plan current = availablePlans.get(0);
                availablePlans.remove(0);
                if (iteration % 100000 == 0) {
                    System.out.println("iteration " + iteration + " : " + "current plan is " + current.plan + " and I have " + availablePlans.size() + " left : " + current.factory);
                }
                iteration++;

                final String nextRobot = current.plan.substring(current.plan.length() - 1);
//                System.out.println("current plan is " + current.plan + " and I have " + availablePlans.size() + " left : " + current.factory);
                final boolean success = current.factory.makeRobot(nextRobot);
//                System.out.println("Outcome " + success + " at time " + current.factory.time + " and geodes " + current.factory.geodes);
                if (current.factory.time >= MAX_TIME) {
                    if ((bestPlan == null || geodes < current.factory.geodes) &&
                            current.factory.geodes > 0 &&
                            current.factory.time == MAX_TIME) {
                        bestPlan = current;
                        geodes = current.factory.geodes;
                        System.out.println("New best plan : geodes " + geodes + " with " + bestPlan.plan + " still got " + availablePlans.size());
                    }
                } else {
                    availablePlans.add(this.newPlanFrom(current, ORE));
                    availablePlans.add(this.newPlanFrom(current, CLAY));
                    availablePlans.add(this.newPlanFrom(current, OBSIDIAN));
                    availablePlans.add(this.newPlanFrom(current, GEODE));
                }
            }

            return geodes;
        }

        private Plan newPlanFrom(final Plan current, final String robot) {
            final Plan plan = new Plan(current.plan + robot, current.factory.clone());
            return plan;
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
