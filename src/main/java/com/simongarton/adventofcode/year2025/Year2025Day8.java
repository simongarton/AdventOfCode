package com.simongarton.adventofcode.year2025;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;
import java.util.stream.Collectors;

public class Year2025Day8 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;

    private final List<Point3d> points = new ArrayList<>();
    private final Map<String, Double> distances = new HashMap<>();
    private final Map<Double, String> reverseLookup = new HashMap<>();
    private final List<List<Integer>> chains = new ArrayList<>();
    private List<Double> sortedDistances = new ArrayList<>();

    @Override
    public String title() {
        return "Day 0: Template code";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2025, 0);
    }

    @Override
    public String part1(final String[] input) {

        this.buildUpPointsAndDistances(input);

        // distances
        for (int i = 0; i < 1000; i++) {
            this.mergeJunctionBoxes(i);
        }

        this.chains.sort(new ListLengthComparator());
        this.debug("");

        for (int i = 0; i < this.chains.size(); i++) {
            String line = "";
            for (final Integer id : this.chains.get(i)) {
                line = line + id + "-";
            }
            line = line.substring(0, line.length() - 1);
            this.debug("index " + i + " size " + this.chains.get(i).size() + ":" + line);
        }

        return String.valueOf(this.chains.get(0).size() *
                this.chains.get(1).size() *
                this.chains.get(2).size());
    }

    private void mergeJunctionBoxes(final int i) {

        final double sortedDistance = this.sortedDistances.get(i);
        final String ids = this.reverseLookup.get(sortedDistance);
        final String[] pts = ids.split(":");
        final Point3d first = this.points.get(Integer.parseInt(pts[0]));
        final Point3d second = this.points.get(Integer.parseInt(pts[1]));
        this.debug(i + " : got pair " + first.getId() + "," + second.getId());
        final Map<Integer, Integer> targets = new HashMap<>();
        for (int chainId = 0; chainId < this.chains.size(); chainId++) {
            final List<Integer> chain = this.chains.get(chainId);
            if (chain.contains(first.getId())) {
                targets.put(first.getId(), chainId);
            }
            if (chain.contains(second.getId())) {
                targets.put(second.getId(), chainId);
            }
        }
        final Integer firstChainId = targets.get(first.getId());
        final Integer secondChainId = targets.get(second.getId());
        if (firstChainId == null && secondChainId == null) {
            // neither are in an existing chain, so create a new one.
            final List<Integer> newChain = new ArrayList<>();
            newChain.add(first.getId());
            newChain.add(second.getId());
            this.debug("\tCreated new chain " + newChain.stream().map(Object::toString).collect(Collectors.joining(",")));
            this.chains.add(newChain);
            return;
        }
        if (Objects.equals(firstChainId, secondChainId)) {
            final List<Integer> chain = this.chains.get(firstChainId);
            this.debug("\tThey are both already in the same chain " + chain.stream().map(Object::toString).collect(Collectors.joining(",")));
            return;
        }
        if (firstChainId != null && secondChainId == null) {
            // add the second junction to the chain the first is already in
            final List<Integer> chain = this.chains.get(firstChainId);
            chain.add(second.getId());
            this.debug("\tadding second " + second.getId() + " to " + chain.stream().map(Object::toString).collect(Collectors.joining(",")));
            return;
        }
        if (firstChainId == null) {
            // add the first junction to the chain the second is already in
            final List<Integer> chain = this.chains.get(secondChainId);
            chain.add(first.getId());
            this.debug("\tadding first " + first.getId() + " to " + chain.stream().map(Object::toString).collect(Collectors.joining(",")));
            return;
        }
        // OK, they are both in different chains so I need to merge them.
        final List<Integer> firstChain = this.chains.get(firstChainId);
        final List<Integer> secondChain = this.chains.get(secondChainId);
        firstChain.addAll(secondChain);
        final List<List<Integer>> validChains = new ArrayList<>();
        for (int x = 0; x < this.chains.size(); x++) {
            if (x != secondChainId) {
                validChains.add(this.chains.get(x));
            }
        }
        this.chains.clear();
        this.chains.addAll(validChains);
        // this refused to work
        // this.chains.remove(secondChainId);
        this.debug("\tmerged into " + firstChain.stream().map(Object::toString).collect(Collectors.joining(",")));
    }


    private void debug(final String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

    private void buildUpPointsAndDistances(final String[] input) {

        this.points.clear();

        for (int i = 0; i < input.length; i++) {
            final String[] parts = input[i].split(",");
            this.points.add(new Point3d(i,
                            Double.parseDouble(parts[0]),
                            Double.parseDouble(parts[1]),
                            Double.parseDouble(parts[2])
                    )
            );
        }

        for (int i = 0; i < this.points.size(); i++) {
            for (int j = 0; j < this.points.size(); j++) {
                this.distances.put(i + ":" + j, this.points.get(i).distanceTo(this.points.get(j)));
            }
        }

        for (int i = 0; i < this.points.size(); i++) {
            for (int j = 0; j < i; j++) {
                this.reverseLookup.put(this.distances.get(i + ":" + j), i + ":" + j);
            }
        }

        this.sortedDistances = new ArrayList<>(this.reverseLookup.keySet());
        this.sortedDistances.sort(Comparator.naturalOrder());

        for (int i = 0; i < this.sortedDistances.size(); i++) {
            final double sortedDistance = this.sortedDistances.get(i);
            final String ids = this.reverseLookup.get(sortedDistance);
            final String[] pts = ids.split(":");
            final String text = this.points.get(Integer.parseInt(pts[0])).toString() + " -> " + this.points.get(Integer.parseInt(pts[1])).toString();
            this.debug(this.sortedDistances.get(i) + "=" + text);
        }
    }

    @Override
    public String part2(final String[] input) {

        this.buildUpPointsAndDistances(input);

        int i = 0;
        while (true) {
            this.mergeJunctionBoxes(i);
            if (this.chains.size() == 1) {
                if (this.chains.getFirst().size() == this.points.size()) {
                    return this.calculateDistanceFrom(i);
                }
            }
            i++;
            if (i >= this.sortedDistances.size()) {
                throw new RuntimeException("failed");
            }
        }
    }

    private String calculateDistanceFrom(final int i) {

        final double sortedDistance = this.sortedDistances.get(i);
        final String ids = this.reverseLookup.get(sortedDistance);
        final String[] pts = ids.split(":");
        final Point3d first = this.points.get(Integer.parseInt(pts[0]));
        final Point3d second = this.points.get(Integer.parseInt(pts[1]));

        return String.format("%.0f", first.getX() * second.getX());
    }

    public static final class Point3d {

        private final int id;
        private final double x;
        private final double y;
        private final double z;

        public Point3d(final int id, final double x, final double y, final double z) {

            this.id = id;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int getId() {
            return this.id;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }

        @Override
        public String toString() {
            return this.id + " (" + this.x + "," + this.y + "," + this.z + ")";
        }

        public double distanceTo(final Point3d otherPoint) {
            final double deltaX = this.x - otherPoint.getX();
            final double deltaY = this.y - otherPoint.getY();
            final double deltaZ = this.z - otherPoint.getZ();

            return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
        }
    }

    public static class ListLengthComparator implements Comparator<List<Integer>> {
        @Override
        public int compare(final List<Integer> list1, final List<Integer> list2) {
            return list2.size() - list1.size();
        }
    }

}
