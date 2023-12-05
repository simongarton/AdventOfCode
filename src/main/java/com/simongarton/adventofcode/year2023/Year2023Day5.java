package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.*;

public class Year2023Day5 extends AdventOfCodeChallenge {

    private List<Long> seeds = new ArrayList<>();
    private List<SeedRange> seedRanges = new ArrayList<>();
    private List<AlmanacMap> maps;
    private final Map<String, Map<Long, Long>> cache = new HashMap<>();
    private final Map<Long, Long> locationCache = new HashMap<>();

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

        this.loadSeeds(input);
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

    private long location(final SeedRange seedRange) {
        long lowest = Long.MAX_VALUE;
        for (long seed = seedRange.start; seed < seedRange.start + seedRange.length; seed++) {
            final long location = this.location(seed);
            if (location < lowest) {
                lowest = location;
            }
        }
        System.out.println("for seedRange " + seedRange + " I got " + lowest);
        return lowest;
    }

    private long location(final Long seed) {
        if (this.locationCache.containsKey(seed)) {
            return this.locationCache.get(seed);
        }
        final long soil = this.map(seed, "seed-to-soil");
//        System.out.println(" seed " + seed + " maps to soil " + soil);
        final long fertilizer = this.map(soil, "soil-to-fertilizer");
//        System.out.println("  soil " + soil + " maps to fertilizer " + fertilizer);
        final long water = this.map(fertilizer, "fertilizer-to-water");
//        System.out.println("   fertilizer " + fertilizer + " maps to water " + water);
        final long light = this.map(water, "water-to-light");
//        System.out.println("    water " + water + " maps to light " + light);
        final long temperature = this.map(light, "light-to-temperature");
//        System.out.println("     light " + light + " maps to temperature " + temperature);
        final long humidity = this.map(temperature, "temperature-to-humidity");
//        System.out.println("      temperature " + humidity + " maps to humidity " + humidity);
        final long location = this.map(humidity, "humidity-to-location");
//        System.out.println("       seed " + seed + " maps to location " + location);
        this.locationCache.put(seed, location);
        return location;
    }

    private long map(final Long lookup, final String mapName) {
        if (!this.cache.containsKey(mapName)) {
            this.cache.put(mapName, new HashMap<>());
        }
        if (this.cache.get(mapName).containsKey(lookup)) {
            return this.cache.get(mapName).get(lookup);
        }
        final AlmanacMap almanacMap = this.maps.stream().filter(m -> m.getName().equalsIgnoreCase(mapName))
                .findFirst().orElseThrow(() -> new RuntimeException(mapName));
        for (final AlmanacRange almanacRange : almanacMap.getRanges()) {
            final long sourceStart = almanacRange.getSourceStart();
            final long sourceEnd = sourceStart + almanacRange.getRange();
            if (lookup < sourceStart || lookup > sourceEnd) {
                continue;
            }
            final long value = lookup - sourceStart + almanacRange.getDestinationStart();
            this.cache.get(mapName).put(lookup, value);
            return value;
        }
//        this.cache.get(mapName).put(lookup, lookup);
        return lookup;
    }

    private long mapBruteForce(final Long lookup, final String mapName) {
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

    private void loadSeeds(final String[] input) {
        final String firstLine = input[0];
        this.seeds = this.readSeeds(firstLine);
    }

    private void loadSeedRanges(final String[] input) {
        final String firstLine = input[0];
        this.seedRanges = this.readSeedRanges(firstLine);
    }

    private void loadData(final String[] input) {

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

        System.out.println("Read " + this.maps.size() + " maps for " + this.seeds.size() + " seeds and " +
                this.seedRanges.size() + " seed ranges.");
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

    private List<SeedRange> readSeedRanges(final String firstLine) {
        final String[] parts = firstLine.split(":");
        final String[] seedNumbers = parts[1].trim().split(" ");
        final List<Long> seeds = new ArrayList<>();
        final List<SeedRange> seedRanges = new ArrayList<>();
        for (final String seedNumber : seedNumbers) {
            seeds.add(Long.parseLong(seedNumber));
            if (seeds.size() == 2) {
                seedRanges.add(SeedRange.builder()
                        .start(seeds.get(0))
                        .length(seeds.get(1))
                        .build());
                seeds.clear();
            }
        }
        return seedRanges;
    }

    @Override
    public String part2(final String[] input) {

        this.loadSeedRanges(input);
        this.loadData(input);

        long lowest = Long.MAX_VALUE;
        for (final SeedRange seedRange : this.seedRanges) {
            final long lowestLocation = this.location(seedRange);
            if (lowestLocation < lowest) {
                lowest = lowestLocation;
            }
        }

        return String.valueOf(lowest);
    }

    @Data
    @Builder
    private static final class AlmanacMap {
        private String name;
        private List<AlmanacRange> ranges;
    }

    @Data
    @Builder
    private static final class SeedRange {
        private long start;
        private long length;
    }

    @Data
    @Builder
    private static final class AlmanacRange {
        private long destinationStart;
        private long sourceStart;
        private long range;
    }
}
