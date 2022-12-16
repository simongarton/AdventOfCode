package com.simongarton.adventofcode.year2022;

import com.google.common.collect.Sets;
import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;
import java.util.stream.Collectors;

public class Year2022Day16 extends AdventOfCodeChallenge {

    /*
    I want to do some kind of network search for this. But instead of simply jumping between nodes,
    I have the option of staying at a node and turning the valve on. I think I have to be slightly
    clever for this and add pseudo-nodes, eg AA* which means effectively stay at AA with the same neighbours
    but turn it on. And now I think I can do this with extra nodes.

    Thought briefly about brute force search, but really ?

    OK, A* isn't going to work, because that's the fastest way to get to a destination.
    DepthFirst kind of assumes you don't revisit nodes, and we're going to have to.

    Every valve has a value, which is time dependent, depending on how fast you can get to it. Looking at the diagram,
    I clearly want to get to HH as soon as possible; it will go on during the 6th minute and therefore give me 23 minutes
    of 22 release = 506, before tracking back; but do I want to stop at DD on the way and turn it on, getting 28 of 20
    release = 560 at a cost of getting to HH one move later, so 484 not 506 (sounds like a bargain !) Only as it turns out
    I should go via (and open) BB and go through to JJ.

    So instead of wandering around, I should probably just target the valves that have value (6 out of 10); work out all
    the combinations of getting to them at different times; and that gives me a way of calculating the total value.

    6 nodes = 720 permutations.

    For each permutation I need to calculate how long it takes to get to each step, and then be able to work
    out the total cost. I probably need to look up a map of travel times - which will be a depth first, but from each
    bloody valve.

     */

    private List<Valve> valves;
    private List<List<Valve>> permutations;
    private List<String> permutationNames;
    private int pressureReleased;
    private Map<String, Integer> travelCosts;
    private List<Integer> permutationValues;

    @Override
    public boolean run() {
        return this.runChallenge(2022, 16);
    }

    @Override
    public String part1(final String[] input) {
        this.loadNetwork(input);
//        this.graphViz();
        this.buildTravelCosts();
        this.permutationNames = new ArrayList<>();
        final List<String> usefulNames = this.valves.stream()
                .filter(v -> v.releaseRate > 0)
                .map(v -> v.name)
                .collect(Collectors.toList());
        System.out.println(usefulNames.size());
        this.assemblePermutations(usefulNames.size(), usefulNames.toArray(new String[0]));
        this.buildPermutations();
        System.out.println(this.permutations.size());
        this.permutationValues = new ArrayList<>();
        this.calculatePermutationValues();
        this.permutationValues.sort(Comparator.comparing(Integer::intValue).reversed());
        this.pressureReleased = this.permutationValues.get(0);
        return String.valueOf(this.pressureReleased);
    }

    private void buildPermutations() {
        this.permutations = new ArrayList<>();
        for (final String nameList : this.permutationNames) {
            final List<String> names = Arrays.asList(nameList.split(","));
            final List<Valve> valves = names.stream().map(this::getValve).collect(Collectors.toList());
            this.permutations.add(valves);
        }
    }

    private void calculatePermutationValues() {
        for (final List<Valve> permutation : this.permutations) {
            this.calculatePermutationValue(permutation);
        }
    }

    private void calculatePermutationValue(final List<Valve> permutation) {
        // and here we go
        final Map<Valve, Integer> values = new HashMap<>();
        Valve startValve = this.valves.get(0);
        int ticks = 30;
        for (final Valve waypoint : permutation) {
            final Journey journey = new Journey(startValve, waypoint);
            try {
                ticks -= this.travelCosts.get(journey.toString()); // get there
            } catch (final NullPointerException e) {
                System.out.println(journey);
                throw e;
            }
            ticks -= 1; // turn it on;
            if (ticks < 0) {
                break; // out of time
            }
            values.put(waypoint, waypoint.releaseRate * ticks);
            startValve = waypoint;
        }
        final int totalValue = values.values().stream().reduce(0, Integer::sum);
        this.permutationValues.add(totalValue);
        System.out.println(permutation.stream().map(Valve::nodeName).collect(Collectors.joining(" ")) + " = " + totalValue);
    }

    private void buildTravelCosts() {
        this.travelCosts = new HashMap<>();
        final Set<Valve> useful = this.valves.stream().filter(v -> v.releaseRate > 0).collect(Collectors.toSet());
        useful.add(this.valves.get(0));
        final Set<Set<Valve>> combinations = Sets.combinations(useful, 2);
        for (final Set<Valve> combination : combinations) {
            final List<Valve> orderedCombination = new ArrayList<>(combination);
            orderedCombination.sort(Comparator.comparing(Valve::nodeName));
            final Journey journey = new Journey(orderedCombination.get(0), orderedCombination.get(1));
            final int distance = this.aStar(journey.one, journey.two).size() - 1;
            this.travelCosts.put(journey.toString(), distance);
            final Journey reverseJourney = new Journey(orderedCombination.get(1), orderedCombination.get(0));
            this.travelCosts.put(reverseJourney.toString(), distance);
        }
    }

    public void assemblePermutations(
            final int n, final String[] elements) {

        if (n == 1) {
            this.addPermutation(elements);
        } else {
            for (int i = 0; i < n - 1; i++) {
                this.assemblePermutations(n - 1, elements);
                if (n % 2 == 0) {
                    this.swap(elements, i, n - 1);
                } else {
                    this.swap(elements, 0, n - 1);
                }
            }
            this.assemblePermutations(n - 1, elements);
        }
    }

    private void swap(final String[] elements, final int a, final int b) {
        final String tmp = elements[a] + "";
        elements[a] = elements[b] + "";
        elements[b] = tmp;
    }

    private void addPermutation(final String[] elements) {
        this.permutationNames.add(Arrays.asList(elements)
                .stream().collect(Collectors.joining(",")));
    }

    private void graphViz() {
        System.out.println("digraph Volcano {");
        for (final Valve valve : this.valves) {
            System.out.println("\"" + valve.nodeName() + "\" [fillcolor = \"" + this.color(valve.releaseRate) + "\" style = \"filled\"]");
        }
        for (final Valve valve : this.valves) {
            for (final Valve tunnel : valve.tunnels) {
                System.out.println("\"" + valve.nodeName() + "\" -> \"" + tunnel.nodeName() + "\"");
            }
        }
        System.out.println("}");
    }

    private String color(final int releaseRate) {
        if (releaseRate == 0) {
            return "#FFFFFF";
        }
        return "#FFFF" + Integer.toHexString(255 - (releaseRate * 10));
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    private void loadNetwork(final String[] input) {
        this.valves = new ArrayList<>();
        this.loadValves(input);
        this.digTunnels(input);
//        this.loadSpecialValves();
    }

    private void loadSpecialValves() {
        final List<Valve> originals = new ArrayList<>();
        originals.addAll(this.valves);
        for (final Valve original : originals) {
            if (original.releaseRate == 0) {
                // no point
                continue;
            }
            final Valve alternate = new Valve(original.name + "+", 0);
            for (final Valve tunnel : original.tunnels) {
                alternate.tunnelOneWay(tunnel);
            }
            original.tunnelOneWay(alternate);
            this.valves.add(alternate);
        }
    }

    private void loadValves(final String[] input) {
        for (final String line : input) {
            this.loadValve(line);
        }
    }

    private void loadValve(final String line) {
        final String[] halves = line.split(";");
        final String[] parts = halves[0].split(" ");
        final String name = parts[1];
        final String[] rateParts = parts[4].split("=");
        final int rate = Integer.parseInt(rateParts[1]);
        final Valve valve = new Valve(name, rate);
        this.valves.add(valve);
    }

    private void digTunnels(final String[] input) {
        for (final String line : input) {
            this.digTunnel(line);
        }
    }

    private void digTunnel(final String line) {
        final String[] halves = line.split(";");
        final String[] parts = halves[0].split(" ");
        final String name = parts[1];
        final Valve from = this.getValve(name);

        final String search = halves[1].contains("valves") ? "valves " : "valve ";

        final String[] tunnelParts = halves[1].split(search);
        final String[] tunnels = tunnelParts[1].split(" ");
        for (final String tunnel : tunnels) {
            final String other = tunnel.replace(",", "");
            final Valve to = this.getValve(other);
            from.tunnel(to);
        }
    }

    private Valve getValve(final String name) {
        return this.valves.stream()
                .filter(v -> v.name.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public static final class Journey {

        private final Valve one;
        private final Valve two;

        public Journey(final Valve one, final Valve two) {
            this.one = one;
            this.two = two;
        }

        @Override
        public String toString() {
            return this.one.nodeName() + "->" + this.two.nodeName();
        }
    }

    public static final class Valve {

        private final String name;
        private final int releaseRate;
        private boolean open;
        private final List<Valve> tunnels;

        public Valve(final String name, final int releaseRate) {
            this.name = name;
            this.releaseRate = releaseRate;
            this.open = false;
            this.tunnels = new ArrayList<>();
        }

        public Valve tunnel(final Valve other) {
            final boolean exists = this.tunnels.contains(other);
            if (exists) {
                return this;
            }
            this.tunnels.add(other);
            other.tunnels.add(this);
            return this;
        }

        public Valve tunnelOneWay(final Valve other) {
            final boolean exists = this.tunnels.contains(other);
            if (exists) {
                return this;
            }
            this.tunnels.add(other);
            return this;
        }

        public void open() {
            this.open = true;
        }

        public String nodeName() {
            if (this.releaseRate == 0) {
                return this.name;
            }
            return this.name + " (" + this.releaseRate + ")";
        }
    }

    private List<Valve> aStar(final Valve start, final Valve end) {
        final boolean debug = false;

        final Set<Valve> openSet = new HashSet<>(Collections.singleton(start));
        final Map<Valve, Valve> cameFrom = new HashMap<>();
        final Map<Valve, Integer> gScore = new HashMap<>();
        gScore.put(start, 0);

        final Map<Valve, Integer> fScore = new HashMap<>();
        fScore.put(start, this.h(start, start));

        while (!openSet.isEmpty()) {
            final Valve current = this.bestOpenSetWithFScoreValue(openSet, fScore);
            if (current == end) {
                return this.reconstructPath(cameFrom, current);
            }
            this.debugPrint(debug, "working on / removing current " + current.toString() + " with openSet.size()=" + openSet.size());
            openSet.remove(current);
            for (final Valve neighbor : current.tunnels) {
                // d(current,neighbor) is the weight of the edge from current to neighbor
                // tentative_gScore is the distance from start to the neighbor through current
                final int tentative_gScore = gScore.get(current) + this.cost(current, neighbor);
                this.debugPrint(debug, "  checking neighbour " + neighbor.toString() + " tentative_gScore=" + tentative_gScore);
                if (tentative_gScore < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    // This path to neighbor is better than any previous one. Record it!
                    this.debugPrint(debug, "     using neighbour " + neighbor);
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentative_gScore);
                    fScore.put(neighbor, tentative_gScore + this.h(start, neighbor));
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                } else {
                    this.debugPrint(debug, "     ignoring neighbour " + neighbor);
                }
            }
        }

        // Open set is empty but goal was never reached
        throw new RuntimeException("AStar failed.");
    }

    private void debugPrint(final boolean debug, final String s) {
        if (debug) {
            System.out.println(s);
        }
    }

    private Valve bestOpenSetWithFScoreValue(final Set<Valve> openSet, final Map<Valve, Integer> fScore) {
        Valve best = null;
        int bestValue = 0;
        for (final Valve cell : openSet) {
            final int cost = fScore.get(cell); // may not be there ?
            if (best == null || bestValue > cost) {
                best = cell;
                bestValue = cost;
            }
        }
        return best;
    }

    private Integer h(final Valve start, final Valve end) {
        // no idea how to do this
        return 1;
    }


    private Integer cost(final Valve current, final Valve neighbor) {
        return 1;
    }

    private List<Valve> reconstructPath(final Map<Valve, Valve> cameFrom, final Valve end) {
        Valve current = end;
        final List<Valve> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        return path;
    }

}
