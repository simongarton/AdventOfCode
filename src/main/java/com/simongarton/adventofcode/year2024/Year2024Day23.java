package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.*;
import java.util.stream.Collectors;

public class Year2024Day23 extends AdventOfCodeChallenge {

    private Map<String, Computer> computers;

    @Override
    public String title() {
        return "Day 23: LAN Party";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 23);
    }

    @Override
    public String part1(final String[] input) {

        this.computers = new HashMap<>();
        for (final String line : input) {
            this.handleLine(line);
        }

        final List<String> sets = this.findSetsOf3();

        final List<String> tsets = sets.stream().filter(this::oneStartsWithT).collect(Collectors.toList());
        return String.valueOf(tsets.size());
    }

    private boolean oneStartsWithT(final String s) {
        final String[] addresses = s.split("-");
        return addresses[0].charAt(0) == 't' ||
                addresses[1].charAt(0) == 't' ||
                addresses[2].charAt(0) == 't';
    }

    private List<String> findSetsOf3() {

        /*
        a-b
        b-c
        c-a

        build all the pairs, and sort them before storing.

        get a and all it's connections a [b,c,d,e]
        for each connection, check to see if any of them are connected to each other

         */

        final Set<String> triplets = new HashSet<>();

        for (final Map.Entry<String, Computer> entry : this.computers.entrySet()) {

            final String level1Address = entry.getKey();
            final Computer level1Computer = entry.getValue();
            for (final String level2Address : level1Computer.others) {
                final Computer level2Computer = this.computers.get(level2Address);
                for (final String level3Address : level2Computer.others) {
                    if (level3Address.equalsIgnoreCase(level1Address)) {
                        continue;
                    }
                    final Computer level3Computer = this.computers.get(level3Address);
                    for (final String level4Address : level3Computer.others) {
                        if (level4Address.equalsIgnoreCase(level1Address)) {
                            final List<String> triple = new ArrayList<>(List.of(level1Address, level2Address, level3Address));
                            triple.sort(Comparator.naturalOrder());
                            final String key = String.join("-", triple);
                            triplets.add(key);
                        }
                    }
                }
            }
        }
        return new ArrayList<>(triplets);
    }

    private void handleLine(final String line) {

        final String[] pcs = line.split("-");
        final Computer pc1 = this.getOrCreate(pcs[0], pcs[1]);
        final Computer pc2 = this.getOrCreate(pcs[1], pcs[0]);
    }

    private Computer getOrCreate(final String pc, final String pc1) {

        final Computer computer;
        if (!this.computers.containsKey(pc)) {
            computer = new Computer(pc);
            this.computers.put(pc, computer);
        } else {
            computer = this.computers.get(pc);
        }
        computer.others.add(pc1);
        return computer;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    static class Computer {

        String address;
        Set<String> others;

        public Computer(final String address) {
            this.address = address;
            this.others = new HashSet<>();
        }
    }
}
