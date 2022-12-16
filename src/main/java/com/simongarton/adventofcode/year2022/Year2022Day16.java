package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;

public class Year2022Day16 extends AdventOfCodeChallenge {

    /*
    I want to do some kind of network search for this. But instead of simply jumping between nodes,
    I have the option of staying at a node and turning the valve on. I think I have to be slightly
    clever for this and add pseudo-nodes, eg AA* which means effectively stay at AA with the same neighbours
    but turn it on. And now I think I can do this with extra nodes.

    Thought briefly about brute force search, but really ?
     */

    private List<Valve> valves;
    private int pressureReleased;

    @Override
    public boolean run() {
        return this.runChallenge(2022, 16);
    }

    @Override
    public String part1(final String[] input) {
        this.loadNetwork(input);
        this.graphViz();
        this.pressureReleased = 0;
        this.getValve("BB").open();
        for (int minute = 0; minute < 30; minute++) {
            this.moveOpenOrStay();
            this.pressureReleased += this.pressureFromOpenValves();
        }
        return String.valueOf(this.pressureReleased);
    }

    private void graphViz() {
        System.out.println("digraph Volcano {");
        for (final Valve valve : this.valves) {
            for (final Valve tunnel : valve.tunnels) {
                System.out.println("\"" + valve.name + "\" -> \"" + tunnel.name + "\"");
            }
        }
        System.out.println("}");
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }


    private void moveOpenOrStay() {

    }

    private int pressureFromOpenValves() {
        return this.valves.stream().filter(v -> v.open).map(v -> v.releaseRate).mapToInt(Integer::intValue).sum();
    }

    private void loadNetwork(final String[] input) {
        this.valves = new ArrayList<>();
        this.loadValves(input);
        this.digTunnels(input);
        this.loadSpecialValves();
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
    }
}
