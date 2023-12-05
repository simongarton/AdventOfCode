package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.*;

public class Year2023Day5 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = true;

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

        long lowestLocation = Long.MAX_VALUE;
        for (final Long seed : this.seeds) {
            final long thisLocation = this.location(seed);
            if (thisLocation < lowestLocation) {
                lowestLocation = thisLocation;
            }
        }

        return String.valueOf(lowestLocation);
    }

    // fails with brute force, too many values to check
    private long location(final SeedRange seedRange) {
        long lowestLocation = Long.MAX_VALUE;
        for (long seed = seedRange.start; seed < seedRange.start + seedRange.length; seed++) {
//            System.out.println("  testing seed " + seed);
            final long location = this.location(seed);
            if (location < lowestLocation) {
                lowestLocation = location;
            }
        }
        System.out.println("for seedRange " + seedRange + " I got " + lowestLocation);
        return lowestLocation;
    }

    private long location(final Long seed) {
        if (this.locationCache.containsKey(seed)) {
            return this.locationCache.get(seed);
        }
        final long soil = this.map(seed, "seed-to-soil");
        final long fertilizer = this.map(soil, "soil-to-fertilizer");
        final long water = this.map(fertilizer, "fertilizer-to-water");
        final long light = this.map(water, "water-to-light");
        final long temperature = this.map(light, "light-to-temperature");
        final long humidity = this.map(temperature, "temperature-to-humidity");
        final long location = this.map(humidity, "humidity-to-location");
        this.locationCache.put(seed, location);
        return location;
    }

    private void debugPrint(final String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

    private long map(final Long lookup, final String mapName) {
        if (!this.cache.containsKey(mapName)) {
            this.cache.put(mapName, new HashMap<>());
        }
        if (this.cache.get(mapName).containsKey(lookup)) {
            System.out.println("Found " + lookup + " for " + mapName + " in cache.");
            return this.cache.get(mapName).get(lookup);
        }
        final AlmanacMap almanacMap = this.maps.stream().filter(m -> m.getName().equalsIgnoreCase(mapName))
                .findFirst().orElseThrow(() -> new RuntimeException(mapName));
        // System.out.println("Checking " + mapName + "(" + almanacMap.getRanges().size() + ")" + " for " + lookup);
        for (final AlmanacRange almanacRange : almanacMap.getRanges()) {
            if (lookup < almanacRange.getSourceStart() || lookup > almanacRange.getSourceEnd()) {
                continue;
            }
            final long value = lookup - almanacRange.getSourceStart() + almanacRange.getDestinationStart();
            this.cache.get(mapName).put(lookup, value);
            return value;
        }
        this.cache.get(mapName).put(lookup, lookup);
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
            final long length = Long.parseLong(parts[2]);
            final AlmanacRange almanacRange = AlmanacRange.builder()
                    .destinationStart(destinationStart)
                    .sourceStart(sourceStart)
                    .length(length)
                    .build()
                    .complete();
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
                        .build()
                        .complete());
                seeds.clear();
            }
        }
        return seedRanges;
    }

    @Override
    public String part2(final String[] input) {

        this.loadSeedRanges(input);
        this.loadData(input);

        this.validateSeedRanges();

        System.out.println("As loaded ...");
        for (final AlmanacMap almanacMap : this.maps) {
            System.out.println("  " + almanacMap.getName() + ":" + almanacMap.getRanges().size());
        }

        this.validateRanges();

        // this.doCleanupOfRanges();

        // I want to hit 0.
        long aimPoint = 0;
        long delta = 0;
        AlmanacRange rangeToHit = null;
        final List<String> reverseMaps = List.of(
                "humidity-to-location",
                "temperature-to-humidity",
                "light-to-temperature",
                "water-to-light",
                "fertilizer-to-water",
                "soil-to-fertilizer",
                "seed-to-soil"
        );
        for (final String mapName : reverseMaps) {
            System.out.println("I'm looking for " + aimPoint + " in " + mapName);
            try {
                rangeToHit = this.findDestinationInRange(aimPoint, mapName);
                System.out.println("I found range " + rangeToHit);
                delta = aimPoint - rangeToHit.getDestinationStart();
                aimPoint = rangeToHit.getSourceStart() + delta;
                System.out.println("My delta is " + delta + " so my new aimpoint is " + aimPoint);
            } catch (final RuntimeException e) {
                System.out.println("no range found, so stay with aimPoint " + aimPoint);
            }
        }

        System.out.println("If I start with seed " + aimPoint + " I get to " + this.location(aimPoint));

        // this gives seed 70 and if I work through the tables both in
        // code and manually I get 0 as the location :shrug Ah : I can't start
        // with 70, it's not in a valid seed range.

        return String.valueOf(aimPoint);
    }

    private AlmanacRange findDestinationInRange(final long target, final String mapName) {
        final AlmanacMap almanacMap = this.maps.stream().filter(m -> m.getName().equalsIgnoreCase(mapName))
                .findFirst().orElseThrow(() -> new RuntimeException(mapName));
        for (final AlmanacRange range : almanacMap.getRanges()) {
            if ((range.getDestinationStart() <= target) &&
                    (range.getDestinationEnd() > target)) {
                return range;
            }
        }
        throw new RuntimeException("No destination in " + mapName + " for " + target);
    }

    private void validateSeedRanges() {
        for (final SeedRange seedRange : this.seedRanges) {
            System.out.println(this.f(seedRange.start) + "->" + this.f(seedRange.end));
        }
    }

    private void validateRanges() {
        for (final AlmanacMap almanacMap : this.maps) {
            System.out.println(almanacMap.getName());
            final List<AlmanacRange> ranges = new ArrayList<>(almanacMap.getRanges());
            if (false) {
                ranges.sort(Comparator.comparing(AlmanacRange::getDestinationStart));
            } else {
                ranges.sort(Comparator.comparing(AlmanacRange::getSourceStart));
            }
            for (final AlmanacRange range : ranges) {
                System.out.println(
                        "  source " +
                                this.f(range.getSourceStart()) + "->" + this.f(range.getSourceEnd()) +
                                " : destination " +
                                this.f(range.getDestinationStart()) + "->" + this.f(range.getDestinationEnd())
                );
            }
        }
    }

    private String f(final Long value) {
        return String.format("%,d", value);
    }

    private void doCleanupOfRanges() {
        final List<AlmanacRange> validRanges = new ArrayList<>();
        validRanges.add(this.lowestRange("humidity-to-location"));
        this.cleanUpRanges("temperature-to-humidity", validRanges, "humidity-to-location");
        this.cleanUpRanges("light-to-temperature", validRanges, "temperature-to-humidity");
        this.cleanUpRanges("water-to-light", validRanges, "light-to-temperature");
        this.cleanUpRanges("fertilizer-to-water", validRanges, "water-to-light");
        this.cleanUpRanges("soil-to-fertilizer", validRanges, "fertilizer-to-water");
        this.cleanUpRanges("seed-to-soil", validRanges, "soil-to-fertilizer");

        System.out.println("After cleanup ...");
        for (final AlmanacMap almanacMap : this.maps) {
            System.out.println("  " + almanacMap.getName() + ":" + almanacMap.getRanges().size());
        }
    }

    // have touched this, not sure if still valid
    private List<AlmanacRange> cleanUpRanges(final String mapName, final List<AlmanacRange> possibleRanges, final String sourceMapName) {
        final AlmanacMap almanacMap = this.maps.stream().filter(m -> m.getName().equalsIgnoreCase(mapName))
                .findFirst().orElseThrow(() -> new RuntimeException(mapName));
        final List<AlmanacRange> rangesToRemove = new ArrayList<>();
        for (final AlmanacRange range : almanacMap.getRanges()) {
            boolean validRange = true;
            for (final AlmanacRange possibleRange : possibleRanges) {
                if (range.getDestinationStart() <= possibleRange.getDestinationStart() &&
                        range.getDestinationEnd() >= possibleRange.getDestinationEnd()) {
                } else {
                    validRange = false;
                }
            }
            if (!validRange) {
                rangesToRemove.add(range);
            }
        }
        for (final AlmanacRange range : rangesToRemove) {
            almanacMap.getRanges().remove(range);
        }
        return almanacMap.getRanges();
    }

    private AlmanacRange lowestRange(final String mapName) {
        final AlmanacMap almanacMap = this.maps.stream().filter(m -> m.getName().equalsIgnoreCase(mapName))
                .findFirst().orElseThrow(() -> new RuntimeException(mapName));
        long lowestStart = Long.MAX_VALUE;
        AlmanacRange bestRange = almanacMap.getRanges().get(0);
        for (final AlmanacRange range : almanacMap.getRanges()) {
            if (range.getDestinationStart() < lowestStart) {
                bestRange = range;
                lowestStart = range.getDestinationStart();
            }
        }
        final List<AlmanacRange> badRanges = new ArrayList<>();
        for (final AlmanacRange range : almanacMap.getRanges()) {
            if (!range.toString().equalsIgnoreCase(bestRange.toString())) {
                badRanges.add(range);
            }
        }
        for (final AlmanacRange range : badRanges) {
            almanacMap.getRanges().remove(range);
        }
        return bestRange;
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
        private long end;

        public SeedRange complete() {
            this.end = this.start + this.length - 1;
            return this;
        }
    }

    @Data
    @Builder
    private static final class AlmanacRange {
        private long destinationStart;
        private long sourceStart;
        private long length;
        private long destinationEnd;
        private long sourceEnd;

        public AlmanacRange complete() {
            this.sourceEnd = this.sourceStart + this.length - 1;
            this.destinationEnd = this.destinationStart + this.length - 1;
            return this;
        }
    }
}


