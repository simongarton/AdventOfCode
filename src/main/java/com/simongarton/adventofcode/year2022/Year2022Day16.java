package com.simongarton.adventofcode.year2022;

import com.google.common.collect.Sets;
import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;
import java.util.stream.Collectors;

public class Year2022Day16 extends AdventOfCodeChallenge {

    private List<Valve> valves;
    private int pressureReleased;
    private Map<String, Integer> travelCosts;
    private State bestState;

    private static final int ONE_TO_TURN_ON = 1;
    private static final boolean DEBUG = false;

    @Override
    public String title() {
        return "Day 16: Proboscidea Volcanium";
    }

    @Override
    public boolean run() {
        return this.runChallenge(2022, 16);
    }

    @Override
    public String part1(final String[] input) {
        this.loadNetwork(input);
//        this.graphViz();
        this.buildTravelCosts();
        this.exploreStates();
        return String.valueOf(this.bestState.pressureReleased);
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    private void exploreStates() {
        final List<State> availableStates = new ArrayList<>();
        final List<State> visitedStates = new ArrayList<>();
        final Set<Valve> useful = this.valves.stream().filter(v -> v.releaseRate > 0).collect(Collectors.toSet());
        State state = new State(this.valves.get(0), 0);
        state.pressureReleased = 0;
        this.pressureReleased = 0;
        state.addOpenedValve(this.valves.get(0));
        availableStates.add(state);
        int iterations = 0;
        while (!availableStates.isEmpty()) {
            state = availableStates.get(0);
            availableStates.remove(0);
            visitedStates.add(state);
            this.debugPrint(DEBUG, state.toString());
            availableStates.addAll(this.getNeighbours(state, useful, visitedStates));
            iterations++;
            if (iterations % 1000 == 0) {
                System.out.println(String.format("iter %s available %s visited %s best %s state %s",
                        iterations,
                        availableStates.size(),
                        visitedStates.size(),
                        this.bestState.pressureReleased,
                        state));
            }
        }
    }

    private List<State> getNeighbours(final State state, final Set<Valve> useful, final List<State> visitedStates) {
        final List<State> neighbours = new ArrayList<>();
        final List<Valve> possibles = useful.stream()
                .filter(v -> !state.valvesOpened.contains(v)).collect(Collectors.toList());
        for (final Valve possible : possibles) {
            final Journey journey = new Journey(state.valve, possible);
            final int timeToGetThere = this.travelCosts.get(journey.toString());
            final State neighbour = new State(possible, state.minutesElapsed + timeToGetThere + ONE_TO_TURN_ON);
            if (neighbour.minutesElapsed > 30) {
                continue; // no point, out of time.
            }
            neighbour.addOpenedValves(state.valvesOpened);
            neighbour.addOpenedValve(possible);
            final int newPressureReleased = (30 - neighbour.minutesElapsed) * possible.releaseRate;
            neighbour.pressureReleased = state.pressureReleased + newPressureReleased;
            if (this.bestState == null || this.bestState.pressureReleased < neighbour.pressureReleased) {
                this.bestState = neighbour;
            }
            final State existing = visitedStates.stream().filter(s -> s.key.equalsIgnoreCase(neighbour.key)).findFirst().orElse(null);
            if (existing != null) {
                if (neighbour.pressureReleased > existing.pressureReleased) {
                    existing.pressureReleased = neighbour.pressureReleased;
                    this.debugPrint(DEBUG, "  found existing, updating : " + existing);
                } else {
                    this.debugPrint(DEBUG, "  found existing, but not better : " + existing);
                }
            } else {
                neighbours.add(neighbour);
                this.debugPrint(DEBUG, "  adding " + neighbour);
            }
        }
        return neighbours;
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

    private void loadNetwork(final String[] input) {
        this.valves = new ArrayList<>();
        this.loadValves(input);
        this.digTunnels(input);
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

    public static final class State {

        private final Valve valve;

        private final int minutesElapsed;
        private int pressureReleased;
        private String key;

        private final List<Valve> valvesOpened;


        public State(final Valve valve, final int minutesElapsed) {
            this.valve = valve;
            this.minutesElapsed = minutesElapsed;
            this.valvesOpened = new ArrayList<>();
        }

        @Override
        public String toString() {
            return String.format("State at %s minutes is %s released with %s visited : %s",
                    this.minutesElapsed,
                    this.pressureReleased,
                    this.valvesOpened.size(),
                    this.valvesOpened.stream().map(v -> v.name).collect(Collectors.joining("->"))
            );
        }

        private void buildKey() {
            final List<String> values = this.valvesOpened.stream()
                    .map(v -> v.name)
                    .sorted(Comparator.comparing(String::valueOf))
                    .collect(Collectors.toList());
            this.key = String.join("->", values);
        }

        public void addOpenedValve(final Valve valve) {
            this.valvesOpened.add(valve);
            this.buildKey();
        }

        public void addOpenedValves(final List<Valve> valvesOpened) {
            this.valvesOpened.addAll(valvesOpened);
            this.buildKey();
        }
    }

}
