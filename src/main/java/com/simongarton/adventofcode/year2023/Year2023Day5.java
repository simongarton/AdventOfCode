package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Year2023Day5 extends AdventOfCodeChallenge {

    private List<Long> seeds = new ArrayList<>();
    private List<SeedRange> seedRanges = new ArrayList<>();
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

    private long location(final Long seed) {
        final long soil = this.map(seed, "seed-to-soil");
        final long fertilizer = this.map(soil, "soil-to-fertilizer");
        final long water = this.map(fertilizer, "fertilizer-to-water");
        final long light = this.map(water, "water-to-light");
        final long temperature = this.map(light, "light-to-temperature");
        final long humidity = this.map(temperature, "temperature-to-humidity");
        final long location = this.map(humidity, "humidity-to-location");
        return location;
    }

    private long reverseLocation(final Long location) {
        final long humidity = this.reverseMap(location, "humidity-to-location");
        final long temperature = this.reverseMap(humidity, "temperature-to-humidity");
        final long light = this.reverseMap(temperature, "light-to-temperature");
        final long water = this.reverseMap(light, "water-to-light");
        final long fertilizer = this.reverseMap(water, "fertilizer-to-water");
        final long soil = this.reverseMap(fertilizer, "soil-to-fertilizer");
        final long seed = this.reverseMap(soil, "seed-to-soil");
        return seed;
    }

    private long reverseMap(final Long lookup, final String mapName) {
        final AlmanacMap almanacMap = this.maps.stream().filter(m -> m.getName().equalsIgnoreCase(mapName))
                .findFirst().orElseThrow(() -> new RuntimeException(mapName));
        for (final AlmanacRange almanacRange : almanacMap.getRanges()) {
//            System.out.println("    " + mapName + "." + almanacRange.getDestinationStart());
            if (lookup < almanacRange.getDestinationStart() || lookup > almanacRange.getDestinationEnd()) {
                continue;
            }
            final long value = lookup - almanacRange.getDestinationStart() + almanacRange.getSourceStart();
//            System.out.println("    " + mapName + "." + almanacRange.getDestinationStart() + " " + lookup + " -> " + value);
            return value;
        }
        return lookup;
    }

    private long map(final Long lookup, final String mapName) {
        final AlmanacMap almanacMap = this.maps.stream().filter(m -> m.getName().equalsIgnoreCase(mapName))
                .findFirst().orElseThrow(() -> new RuntimeException(mapName));
        for (final AlmanacRange almanacRange : almanacMap.getRanges()) {
            if (lookup < almanacRange.getSourceStart() || lookup > almanacRange.getSourceEnd()) {
                continue;
            }
            final long value = lookup - almanacRange.getSourceStart() + almanacRange.getDestinationStart();
            return value;
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

        this.validateRanges();

        System.out.println("");

        final StartAndEnd overallResult = StartAndEnd.builder()
                .start(-1)
                .end(Long.MAX_VALUE)
                .build();
        for (final SeedRange seedRange : this.seedRanges) {
            final StartAndEnd thisResult = this.findLowestForSeedRange(seedRange);
            if (thisResult.getEnd() < overallResult.getEnd()) {
                overallResult.setStart(thisResult.getStart());
                overallResult.setEnd(thisResult.getEnd());
            }
        }

        return String.valueOf(overallResult.getEnd());
    }

    private void dumpMap() {
        final List<String> lines = new ArrayList<>();
        lines.add("seed,soil,fertilizer,water,light,temp,humidity,location");
        for (long seed = 0; seed < 100; seed++) {
            final long soil = this.map(seed, "seed-to-soil");
            final long fertilizer = this.map(soil, "soil-to-fertilizer");
            final long water = this.map(fertilizer, "fertilizer-to-water");
            final long light = this.map(water, "water-to-light");
            final long temperature = this.map(light, "light-to-temperature");
            final long humidity = this.map(temperature, "temperature-to-humidity");
            final long location = this.map(humidity, "humidity-to-location");
            lines.add(String.format("%d,%d,%d,%d,%d,%d,%d,%d", seed, soil, fertilizer, water, light, temperature, humidity, location));
        }
        final Path filePath = Path.of("map.csv");
        try {
            Files.deleteIfExists(filePath);
            Files.createFile(filePath);
            for (final String str : lines) {
                Files.writeString(filePath, str + System.lineSeparator(),
                        StandardOpenOption.APPEND);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private StartAndEnd findLowestForSeedRange(final SeedRange seedRange) {
        // I need to work backwards.
        // for each of the final maps, loop through the ranges starting with the lowest
        // and go back up through the maps until I hit a value in the incoming seedRange

        System.out.println("SeedRange is " + seedRange);
        long lowest = Long.MAX_VALUE;
        StartAndEnd result = null;
        long endPoint = 0;
        for (final AlmanacRange range : this.sortedLocationRanges()) {
            endPoint = Math.max(endPoint, range.getDestinationEnd());
        }
        for (long endValue = 0; endValue <= endPoint; endValue++) {
            if (endValue % 10000000 == 0) {
                System.out.println(this.f(endValue) + " -> " + this.f(endPoint) + " = " + String.format("%3.2f%%", 100.0 * endValue / endPoint));
            }
            final long startValue = this.reverseLocation(endValue);
            if (this.valueInRange(startValue, seedRange)) {
                System.out.println("  tried endValue " + endValue + " got back to " + startValue);
//                    return startValue;
                if (endValue < lowest) {
                    lowest = endValue;
                    result = StartAndEnd.builder()
                            .start(startValue)
                            .end(endValue)
                            .build();
                    // controversy
                    return result;
                }
            }
        }
        return result;
    }

    private List<AlmanacRange> sortedLocationRanges() {
        final String mapName = "humidity-to-location";
        final AlmanacMap almanacMap = this.maps.stream().filter(m -> m.getName().equalsIgnoreCase(mapName))
                .findFirst().orElseThrow(() -> new RuntimeException(mapName));
        final List<AlmanacRange> ranges = new ArrayList<>(almanacMap.getRanges());
        ranges.sort(Comparator.comparing(AlmanacRange::getDestinationStart));
        return ranges;
    }

    private boolean valueInRange(final long startValue, final SeedRange seedRange) {
        return (startValue >= seedRange.getStart() && startValue <= seedRange.getEnd());
    }

    private AlmanacRange findRangeForThisTargetInThisMap(final long target, final String mapName) {
        final AlmanacMap almanacMap = this.maps.stream().filter(m -> m.getName().equalsIgnoreCase(mapName))
                .findFirst().orElseThrow(() -> new RuntimeException(mapName));
        for (final AlmanacRange range : almanacMap.getRanges()) {
            if ((range.getDestinationStart() <= target) &&
                    (range.getDestinationEnd() >= target)) {
                return range;
            }
        }
        throw new RuntimeException("No destination in " + mapName + " for " + target);
    }

    private void validateSeedRanges() {
        for (final SeedRange seedRange : this.seedRanges) {
            System.out.println("SeedRange: " + this.f(seedRange.start) + "->" + this.f(seedRange.end));
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

    @Data
    @Builder
    private static final class StartAndEnd {
        private long start;
        private long end;
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
        private long end;
        private long length;

        public SeedRange complete() {
            this.end = this.start + this.length - 1;
            return this;
        }
    }

    @Data
    @Builder
    private static final class AlmanacRange {
        private long destinationStart;
        private long destinationEnd;
        private long sourceStart;
        private long sourceEnd;
        private long length;

        public AlmanacRange complete() {
            this.sourceEnd = this.sourceStart + this.length - 1;
            this.destinationEnd = this.destinationStart + this.length - 1;
            return this;
        }
    }
}


