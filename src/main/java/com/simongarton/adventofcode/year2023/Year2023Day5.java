package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Year2023Day5 extends AdventOfCodeChallenge {

    private List<Long> seeds;
    private List<AlmanacMap> maps;


    @Override
    public String title() {
        return "Day 5: If You Give A Seed A Fertilizer";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 5);
    }

    @Override
    public String part1(final String[] input) {

        this.loadData(input);

        long lowest = Long.MAX_VALUE;
        for (final Long seed : this.seeds) {
            final long lowestLocation = this.location(seed);
            if (lowestLocation < lowest) {
                lowest = lowestLocation;
            }
        }

        return String.valueOf(lowest);
    }

    private long location(final Long seed) {
        final long soil = this.map(seed, "seed-to-soil");
        System.out.println(" seed " + seed + " maps to soil " + soil);
        final long fertilizer = this.map(soil, "soil-to-fertilizer");
        System.out.println("  soil " + soil + " maps to fertilizer " + fertilizer);
        final long water = this.map(fertilizer, "fertilizer-to-water");
        System.out.println("   fertilizer " + fertilizer + " maps to water " + water);
        final long light = this.map(water, "water-to-light");
        System.out.println("    water " + water + " maps to light " + light);
        final long temperature = this.map(light, "light-to-temperature");
        System.out.println("     light " + light + " maps to temperature " + temperature);
        final long humidity = this.map(temperature, "temperature-to-humidity");
        System.out.println("      temperature " + humidity + " maps to humidity " + humidity);
        final long location = this.map(humidity, "humidity-to-location");
        System.out.println("       seed " + seed + " maps to location " + location);
        return location;
    }

    private long map(final Long lookup, final String mapName) {
        final AlmanacMap almanacMap = this.maps.stream().filter(m -> m.getName().equalsIgnoreCase(mapName))
                .findFirst().orElseThrow(() -> new RuntimeException(mapName));
        for (final AlmanacRange almanacRange : almanacMap.getRanges()) {
            for (long source = almanacRange.getSourceStart();
                 source < (almanacRange.getSourceStart() + almanacRange.getRange());
                 source++) {
                if (source == lookup) {
                    return almanacRange.getDestinationStart() + source - almanacRange.getSourceStart();
                }
            }
        }
        return lookup;
    }

    private void loadData(final String[] input) {
        final String firstLine = input[0];
        this.seeds = this.readSeeds(firstLine);

        this.maps = new ArrayList<>();
        String mapName = null;
        final List<AlmanacRange> ranges = new ArrayList<>();

        final List<String> lines = new ArrayList<>(Arrays.asList(input));
        lines.add("");

        for (int i = 2; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.isEmpty()) {
                this.maps.add(AlmanacMap.builder()
                        .name(mapName)
                        .ranges(new ArrayList<>(ranges))
                        .build());
                mapName = null;
                ranges.clear();
                continue;
            }
            if (line.contains("map:")) {
                final String[] parts = line.split(" ");
                mapName = parts[0];
                continue;
            }
            final String[] parts = line.split(" ");
            final long destinationStart = Long.parseLong(parts[0]);
            final long sourceStart = Long.parseLong(parts[1]);
            final long range = Long.parseLong(parts[2]);
            final AlmanacRange almanacRange = AlmanacRange.builder()
                    .destinationStart(destinationStart)
                    .sourceStart(sourceStart)
                    .range(range)
                    .build();
            ranges.add(almanacRange);
        }

        System.out.println("Read " + this.maps.size() + " maps for " + this.seeds.size() + " seeds.");
    }

    private List<Long> readSeeds(final String firstLine) {
        final String[] parts = firstLine.split(":");
        final String[] seedNumbers = parts[1].trim().split(" ");
        final List<Long> seeds = new ArrayList<>();
        for (final String seedNumber : seedNumbers) {
            seeds.add(Long.parseLong(seedNumber));
        }
        return seeds;
    }

    @Override
    public String part2(final String[] input) {

        return null;
    }

    @Data
    @Builder
    private static final class AlmanacMap {
        private String name;
        private List<AlmanacRange> ranges;
    }

    @Data
    @Builder
    private static final class AlmanacRange {
        private long destinationStart;
        private long sourceStart;
        private long range;
    }
}
