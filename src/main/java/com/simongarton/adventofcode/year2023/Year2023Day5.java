package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.*;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Year2023Day5 extends AdventOfCodeChallenge {

    private final static boolean SHORTCUT = true;
    private final static boolean DEBUG = false;
    public static final int MAP_WIDTH = 800;
    public static final int MAP_HEIGHT = 350;

    private List<Long> seeds = new ArrayList<>();
    private List<SeedRange> seedRanges = new ArrayList<>();
    private List<AlmanacMap> maps;
    private long maxWidth;
    private final Random random = new Random();

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

        this.drawRanges();

        long lowestLocation = Long.MAX_VALUE;
        for (final Long seed : this.seeds) {
            final long thisLocation = this.location(seed);
            if (thisLocation < lowestLocation) {
                lowestLocation = thisLocation;
            }
        }

        return String.valueOf(lowestLocation);
    }

    private void drawRanges() {

        this.calculateMaxWidth();

        final BufferedImage bufferedImage = new BufferedImage(MAP_WIDTH, MAP_HEIGHT, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);
        this.paintRanges(graphics2D);
        try {
            ImageIO.write(bufferedImage, "PNG", new File("ranges.png"));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void calculateMaxWidth() {
        for (final AlmanacMap map : this.maps) {
            for (final AlmanacRange range : map.getRanges()) {
                this.maxWidth = Math.max(this.maxWidth, range.getSourceEnd());
            }
        }
    }

    private void paintRanges(final Graphics2D graphics2D) {
        for (int i = 0; i < this.maps.size(); i++) {
            this.paintRange(i, this.maps.get(i), graphics2D);
        }
    }

    private void paintRange(final int i, final AlmanacMap almanacMap, final Graphics2D graphics2D) {

        final int border = 1;
        final int depth = MAP_HEIGHT / this.maps.size();
        final int top = (i * depth) + border;
        final int bottom = ((i + 1) * depth) - border;

        for (int j = 0; j < almanacMap.getRanges().size(); j++) {
            final AlmanacRange almanacRange = almanacMap.getRanges().get(j);
            final int left = (int) (MAP_WIDTH * almanacRange.getSourceStart() / this.maxWidth) + border;
            final int right = (int) (MAP_WIDTH * almanacRange.getSourceEnd() / this.maxWidth) - border;
            graphics2D.setPaint(this.rangeColor(i));
            graphics2D.fillRect(left, top, right - left, bottom - top);
            graphics2D.setPaint(Color.WHITE);
            graphics2D.drawRect(left, top, right - left, bottom - top);
        }
    }

    private Paint rangeColor(final int i) {
        return new Color(this.random.nextInt(255),
                this.random.nextInt(255),
                this.random.nextInt(255)
        );
    }

    private void clearBackground(final Graphics2D graphics2D) {
        graphics2D.setPaint(Color.BLACK);
        graphics2D.fillRect(0, 0, 800, 600);
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

    // thanks Craig Goodspeed for explaining this part of the algorithm
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
            if (lookup < almanacRange.getDestinationStart() || lookup > almanacRange.getDestinationEnd()) {
                continue;
            }
            final long value = lookup - almanacRange.getDestinationStart() + almanacRange.getSourceStart();
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

        if (DEBUG) {
            System.out.println("Read " + this.maps.size() + " maps for " + this.seeds.size() + " seeds and " +
                    this.seedRanges.size() + " seed ranges.");
        }
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

        final long start = System.currentTimeMillis();

        this.loadSeedRanges(input);
        this.loadData(input);

        if (DEBUG) {
            this.validateSeedRanges();
            this.validateRanges();
            System.out.println("");
        }

        final long result = this.findLowestForSeedRanges();
        final long end = System.currentTimeMillis();
        if (DEBUG) {
            System.out.println("I took " + this.formatSeconds((int) ((end - start) / 1000.0)));
        }
        return String.valueOf(result);
    }

    private String formatSeconds(final int totalSecs) {
        final int hours = totalSecs / 3600;
        final int minutes = (totalSecs % 3600) / 60;
        final int seconds = totalSecs % 60;
        return (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes + ":"
                + (seconds < 10 ? "0" : "") + seconds;
    }

    // use for the sample NOT THE REAL ONE
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

    private long findLowestForSeedRanges() {
        final Map<SeedRange, StartAndEnd> lowestValues = new HashMap<>();
        long endPoint = 0;
        for (final AlmanacRange range : this.sortedLocationRanges()) {
            endPoint = Math.max(endPoint, range.getDestinationEnd());
        }
        // once I have a value for all of them, I don't need to keep going
        // and once I have a value for one of them, I don't need to check that one any more ...
        for (long endValue = 0; endValue <= endPoint; endValue++) {
            if (DEBUG && endValue % 50000000 == 0) {
                System.out.println(this.f(endValue) + " -> " + this.f(endPoint) + " = " + String.format("%3.2f%%", 100.0 * endValue / endPoint));
            }
            final long startValue = this.reverseLocation(endValue);
            for (final SeedRange seedRange : this.seedRanges) {
                if (lowestValues.containsKey(seedRange)) {
                    continue;
                }
                if (!(this.valueInRange(startValue, seedRange))) {
                    continue;
                }
                lowestValues.put(seedRange, StartAndEnd.builder().end(Long.MAX_VALUE).build());
                final StartAndEnd startAndEnd = lowestValues.get(seedRange);
                if (startAndEnd.getEnd() > endValue) {
                    startAndEnd.setStart(startValue);
                    startAndEnd.setEnd(endValue);
                    if (DEBUG) {
                        System.out.println("  put " + startAndEnd + " in for " + seedRange + " now got " + lowestValues.size());
                    }
                }
                // and in fact, this is guaranteed to be the lowest answer !
                if (SHORTCUT) {
                    return endValue;
                }
            }
        }
        long lowest = Long.MAX_VALUE;
        for (final Map.Entry<SeedRange, StartAndEnd> entry : lowestValues.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue().getStart() + "->" + entry.getValue().getEnd());
            lowest = Math.min(lowest, entry.getValue().getEnd());
        }
        return lowest;
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


