package com.simongarton.adventofcode.year2025;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;
import java.util.stream.Collectors;

public class Year2025Day8 extends AdventOfCodeChallenge {

    private final List<Point3d> points = new ArrayList<>();
    private final Map<String, Double> distances = new HashMap<>();
    private final Map<Double, String> reverseLookup = new HashMap<>();
    private final List<List<Integer>> chains = new ArrayList<>();

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

        final List<Double> sortedDistances = new ArrayList<>(this.reverseLookup.keySet());
        sortedDistances.sort(Comparator.naturalOrder());
        for (int i = 0; i < sortedDistances.size(); i++) {
            final double sortedDistance = sortedDistances.get(i);
            final String ids = this.reverseLookup.get(sortedDistance);
            final String[] pts = ids.split(":");
            final String text = this.points.get(Integer.parseInt(pts[0])).toString() + " -> " + this.points.get(Integer.parseInt(pts[1])).toString();
            System.out.println(sortedDistances.get(i) + "=" + text);
        }

        // distances
        for (int i = 0; i < 1000; i++) {
            final double sortedDistance = sortedDistances.get(i);
            final String ids = this.reverseLookup.get(sortedDistance);
            final String[] pts = ids.split(":");
            final Point3d first = this.points.get(Integer.parseInt(pts[0]));
            final Point3d second = this.points.get(Integer.parseInt(pts[1]));
            System.out.println(i + " : got pair " + first.getId() + "," + second.getId());
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
                System.out.println("\tCreated new chain " + newChain.stream().map(Object::toString).collect(Collectors.joining(",")));
                this.chains.add(newChain);
                continue;
            }
            if (Objects.equals(firstChainId, secondChainId)) {
                final List<Integer> chain = this.chains.get(firstChainId);
                System.out.println("\tThey are both already in the same chain " + chain.stream().map(Object::toString).collect(Collectors.joining(",")));
                continue;
            }
            if (firstChainId != null && secondChainId == null) {
                // add the second junction to the chain the first is already in
                final List<Integer> chain = this.chains.get(firstChainId);
                chain.add(second.getId());
                System.out.println("\tadding second " + second.getId() + " to " + chain.stream().map(Object::toString).collect(Collectors.joining(",")));
                continue;
            }
            if (firstChainId == null && secondChainId != null) {
                // add the first junction to the chain the second is already in
                final List<Integer> chain = this.chains.get(secondChainId);
                chain.add(first.getId());
                System.out.println("\tadding first " + first.getId() + " to " + chain.stream().map(Object::toString).collect(Collectors.joining(",")));
                continue;
            }
            // OK, they are both in different chains so I need to merge them.
            final List<Integer> firstChain = this.chains.get(firstChainId);
            final List<Integer> secondChain = this.chains.get(secondChainId);
            firstChain.addAll(secondChain);
            secondChain.clear();
//            this.chains.remove(secondChainId);
            System.out.println("\tmerged into " + firstChain.stream().map(Object::toString).collect(Collectors.joining(",")));
        }

        this.chains.sort(new ListLengthComparator());
        System.out.println();

        for (int i = 0; i < this.chains.size(); i++) {
            if (this.chains.get(i).isEmpty()) {
                continue;
            }
            String line = "";
            for (final Integer id : this.chains.get(i)) {
                line = line + id + "-";
            }
            line = line.substring(0, line.length() - 1);
            System.out.println("index " + i + " size " + this.chains.get(i).size() + ":" + line);
        }

        return String.valueOf(this.chains.get(0).size() *
                this.chains.get(1).size() *
                this.chains.get(2).size());
    }

    @Override
    public String part2(final String[] input) {
        return null;
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

    public class ListLengthComparator implements Comparator<List<Integer>> {
        @Override
        public int compare(final List<Integer> list1, final List<Integer> list2) {
            // For longest first, subtract list1.size() from list2.size()
            return list2.size() - list1.size();
        }
    }

}
