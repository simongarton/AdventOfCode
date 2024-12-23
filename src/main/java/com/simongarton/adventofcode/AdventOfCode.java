package com.simongarton.adventofcode;

import com.simongarton.adventofcode.year2015.Year2015Day1;
import com.simongarton.adventofcode.year2019.Year2019Day1;
import com.simongarton.adventofcode.year2019.Year2019Day2;
import com.simongarton.adventofcode.year2019.Year2019Day3;
import com.simongarton.adventofcode.year2019.Year2019Day4;
import com.simongarton.adventofcode.year2020.*;
import com.simongarton.adventofcode.year2021.*;
import com.simongarton.adventofcode.year2022.*;
import com.simongarton.adventofcode.year2023.*;
import com.simongarton.adventofcode.year2024.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class AdventOfCode {

    private static final int XSCALE = 40;
    private static final int YSCALE = 40;

    private final List<AdventOfCodeChallenge> challenges;
    private final Map<Integer, Map<Integer, AdventOfCodeChallenge.Outcome>> complete = new HashMap<>();

    private int width;
    private int height;

    final int startYear = 2015;
    final int endYear = 2024;

    public static void main(final String[] args) {

        // can I load these by reflection / inspection ?
        final AdventOfCode adventOfCode = new AdventOfCode();
        adventOfCode.load2015();
        adventOfCode.load2019();
        adventOfCode.load2020();
        adventOfCode.load2021();
        adventOfCode.load2022();
        adventOfCode.load2023();
        adventOfCode.load2024();
        adventOfCode.run();
    }

    private void run() {

        this.complete.clear();

        for (final AdventOfCodeChallenge codeChallenge : this.challenges) {
            final AdventOfCodeChallenge.Outcome outcome = codeChallenge.run();
            final int year = codeChallenge.getYear();
            final int day = codeChallenge.getDay();
            if (!this.complete.containsKey(year)) {
                this.complete.put(year, new HashMap<>());
            }
            this.complete.get(year).put(day, outcome);
        }

        this.displayResults();
        this.writeResultsToFile("progress.md");
        this.paintMap("AdventOfCode.png");
        this.paintSpeedMap("AdventOfCodeSpeed.png");
        this.writeTimesToFile("times.csv");
    }

    private void writeTimesToFile(final String filename) {

        final List<String> times = new ArrayList<>();
        times.add("year,day,part,time_ms");
        for (int year = this.startYear; year <= this.endYear; year++) {
            for (int day = 1; day <= 25; day++) {
                String line = year + "," + day + ",";
                if (this.complete.containsKey(year)) {
                    if (this.complete.get(year).containsKey(day)) {
                        if (this.complete.get(year).get(day).part1) {
                            line = line + this.complete.get(year).get(day).timeInMs1 + ",";
                        } else {
                            line = line + ",";
                        }
                        if (this.complete.get(year).get(day).part2) {
                            line = line + this.complete.get(year).get(day).timeInMs2;
                        }
                        times.add(line);
                    }
                }
            }
        }

        this.writeStringsToFile(times, Path.of(filename).toFile());
    }

    private String textSymbolForDay(final int outcome) {

        switch (outcome) {
            case 2:
                return "✓";
            case 1:
                return ".";
            default:
                return "";
        }
    }

    private void displayResults() {

        System.out.println();

        final List<String> results = this.getResults();
        results.forEach(System.out::println);
    }

    private void writeResultsToFile(final String filename) {

        final List<String> lines = this.getResults();
        lines.add(0, "```");
        lines.add(lines.size(), "```");
        this.writeStringsToFile(lines, Path.of(filename).toFile());
    }

    private void writeStringsToFile(final List<String> lines, final File file) {

        try {
            final BufferedWriter br = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
            for (final String str : lines) {
                br.write(str + System.lineSeparator());
            }
            br.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }


    private List<String> getResults() {

        final List<String> results = new ArrayList<>();

        results.add("                1111111111222222  ");
        results.add("       1234567890123456789012345  ");
        for (int year = this.startYear; year <= this.endYear; year++) {
            final StringBuilder line = new StringBuilder(year + " : ");
            for (int day = 1; day <= 25; day++) {
                line.append(this.textSymbolForDay(this.getOutcomeForDay(year, day)));
            }
            results.add(line.toString() + "  ");
        }
        results.add("       1234567890123456789012345  ");
        results.add("                1111111111222222  ");

        return results;
    }

    private void load2015() {
        this.challenges.add(new Year2015Day1());
    }

    private void load2019() {
        this.challenges.add(new Year2019Day1());
        this.challenges.add(new Year2019Day2());
        this.challenges.add(new Year2019Day3());
        this.challenges.add(new Year2019Day4());
    }

    private void load2020() {
        this.challenges.add(new Year2020Day1());
        this.challenges.add(new Year2020Day2());
        this.challenges.add(new Year2020Day3());
        this.challenges.add(new Year2020Day4());
        this.challenges.add(new Year2020Day5());
        this.challenges.add(new Year2020Day6());
        this.challenges.add(new Year2020Day7());
        this.challenges.add(new Year2020Day8());
        this.challenges.add(new Year2020Day9());
        this.challenges.add(new Year2020Day10());
        this.challenges.add(new Year2020Day11());
        this.challenges.add(new Year2020Day12());
        this.challenges.add(new Year2020Day13());
        this.challenges.add(new Year2020Day14());
    }

    private void load2021() {
        this.challenges.add(new Year2021Day1());
        this.challenges.add(new Year2021Day2());
        this.challenges.add(new Year2021Day3());
        this.challenges.add(new Year2021Day4());
        this.challenges.add(new Year2021Day5());
        this.challenges.add(new Year2021Day6());
        this.challenges.add(new Year2021Day7());
        this.challenges.add(new Year2021Day8());
        this.challenges.add(new Year2021Day9());
        this.challenges.add(new Year2021Day10());
        this.challenges.add(new Year2021Day11());
        this.challenges.add(new Year2021Day12());
        this.challenges.add(new Year2021Day13());
        this.challenges.add(new Year2021Day14());
        this.challenges.add(new Year2021Day15());
        this.challenges.add(new Year2021Day16());
        this.challenges.add(new Year2021Day17());
        this.challenges.add(new Year2021Day18());
        this.challenges.add(new Year2021Day20());
        this.challenges.add(new Year2021Day21()); // slow
        this.challenges.add(new Year2021Day25());
    }

    private void load2022() {
        this.challenges.add(new Year2022Day1());
        this.challenges.add(new Year2022Day2());
        this.challenges.add(new Year2022Day3());
        this.challenges.add(new Year2022Day4());
        this.challenges.add(new Year2022Day5());
        this.challenges.add(new Year2022Day6());
        this.challenges.add(new Year2022Day7());
        this.challenges.add(new Year2022Day8());
        this.challenges.add(new Year2022Day9());
        this.challenges.add(new Year2022Day10());
        this.challenges.add(new Year2022Day11());
        this.challenges.add(new Year2022Day12());
        this.challenges.add(new Year2022Day13());
        this.challenges.add(new Year2022Day14());
        this.challenges.add(new Year2022Day15());
        this.challenges.add(new Year2022Day16());
        this.challenges.add(new Year2022Day17());
        this.challenges.add(new Year2022Day18());
        this.challenges.add(new Year2022Day19());
        this.challenges.add(new Year2022Day20());
//        this.challenges.add(new Year2022Day21()); // slow part 2 18 seconds
        this.challenges.add(new Year2022Day22());
        this.challenges.add(new Year2022Day23());
        this.challenges.add(new Year2022Day24()); // slow part 2 15 seconds
        this.challenges.add(new Year2022Day25());
    }

    private void load2023() {
        this.challenges.add(new Year2023Day1());
        this.challenges.add(new Year2023Day2());
        this.challenges.add(new Year2023Day3());
        this.challenges.add(new Year2023Day4());
        this.challenges.add(new Year2023Day5()); // slow part 2 63 seconds
        this.challenges.add(new Year2023Day6());
        this.challenges.add(new Year2023Day7()); // slow part 2 29 seconds
        this.challenges.add(new Year2023Day8());
        this.challenges.add(new Year2023Day9());
        this.challenges.add(new Year2023Day10());
        this.challenges.add(new Year2023Day11());
        this.challenges.add(new Year2023Day12());
        this.challenges.add(new Year2023Day13());
        this.challenges.add(new Year2023Day14());
        this.challenges.add(new Year2023Day15());
        this.challenges.add(new Year2023Day16());
        this.challenges.add(new Year2023Day17()); // slow part 2 26 seconds
        this.challenges.add(new Year2023Day18());
        this.challenges.add(new Year2023Day19());
        this.challenges.add(new Year2023Day20());
        this.challenges.add(new Year2023Day21());
        // this.challenges.add(new Year2023Day22()); // stupidly slow part 1 (!) 938 seconds
        this.challenges.add(new Year2023Day23());
        this.challenges.add(new Year2023Day24());
        // this.challenges.add(new Year2023Day25()); // stupidly slow
    }

    private void load2024() {
        this.challenges.add(new Year2024Day1());
        this.challenges.add(new Year2024Day2());
        this.challenges.add(new Year2024Day3());
        this.challenges.add(new Year2024Day4());
        this.challenges.add(new Year2024Day5());
        this.challenges.add(new Year2024Day6()); // slow
        this.challenges.add(new Year2024Day7());
        this.challenges.add(new Year2024Day8());
        this.challenges.add(new Year2024Day9());
        this.challenges.add(new Year2024Day10());
        this.challenges.add(new Year2024Day11());
        this.challenges.add(new Year2024Day12());
        this.challenges.add(new Year2024Day13()); // slow part 2 64 seconds
        this.challenges.add(new Year2024Day14());
        this.challenges.add(new Year2024Day15());
        this.challenges.add(new Year2024Day16()); // slow part 2 81 seconds
        this.challenges.add(new Year2024Day17());
        this.challenges.add(new Year2024Day18()); // slow part 1 (!) 180 seconds
        this.challenges.add(new Year2024Day19());
        this.challenges.add(new Year2024Day20()); // slow didn't finish
        this.challenges.add(new Year2024Day21());
        this.challenges.add(new Year2024Day22());
    }

    private BufferedImage getBufferedImage() {

        final int years = this.endYear - this.startYear;
        final int days = 25;

        this.width = (days + 2) * XSCALE;
        this.height = ((years + 15) * YSCALE) / 2;

        return new BufferedImage(this.width, this.height, TYPE_INT_RGB);
    }

    private void paintMap(final String filename) {

        final BufferedImage bufferedImage = this.getBufferedImage();
        final Graphics2D graphics2D = bufferedImage.createGraphics();

        this.clearBackground(graphics2D);
        this.drawStarsForOutcomes(graphics2D);

        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void paintSpeedMap(final String filename) {

        final BufferedImage bufferedImage = this.getBufferedImage();
        final Graphics2D graphics2D = bufferedImage.createGraphics();

        this.clearBackground(graphics2D);
        this.paintSpeedOutcomes(graphics2D);
        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void drawStarsForOutcomes(final Graphics2D graphics2D) {

        final String fontName = "Source Code Pro";

        final Font symbolFont = new Font(fontName, Font.BOLD, 40);
        graphics2D.setFont(symbolFont);
        for (int year = this.startYear; year <= this.endYear; year++) {
            for (int day = 1; day <= 25; day++) {

                final int x = 35 + day * XSCALE;
                final int y = -8 + ((2 + year - this.startYear) * YSCALE);

                graphics2D.setPaint(this.getColorForOutcome(this.getOutcomeForDay(year, day)));
                graphics2D.drawString("*", x, y);
            }
        }

        this.paintAxes(graphics2D);
    }

    private void paintAxes(final Graphics2D graphics2D) {

        final String fontName = "Source Code Pro";

        final Font textFont = new Font(fontName, Font.PLAIN, 20);
        graphics2D.setFont(textFont);
        graphics2D.setPaint(Color.WHITE);
        for (int year = this.startYear; year <= this.endYear; year++) {
            graphics2D.drawString(String.valueOf(year), 10, Math.round((1.65 * YSCALE)) + (long) (year - this.startYear) * YSCALE);
        }

        for (int day = 1; day <= 25; day++) {
            graphics2D.drawString(String.valueOf(day), 35 + day * XSCALE, YSCALE - 5);
        }
    }

    private void paintSpeedOutcomes(final Graphics2D graphics2D) {

        final int halfXScale = (XSCALE / 2) - 1;
        final int quarterYScale = (YSCALE / 4) - 2;
        final int threeQuarterYScale = (3 * YSCALE / 4) - 2;

        for (int year = this.startYear; year <= this.endYear; year++) {
            for (int day = 1; day <= 25; day++) {

                final int x = 32 + day * XSCALE;
                final int y = ((1 + year - this.startYear) * YSCALE);

                long time = this.getTimeForDay(year, day, 1);
                if (time != -1) {
                    if (this.complete.get(year).get(day).part1) {
                        graphics2D.setPaint(this.getColorForTime(time));
                        graphics2D.fillRect(x, y, halfXScale, threeQuarterYScale);
                    }
                }

                time = this.getTimeForDay(year, day, 2);
                if (time != -1) {
                    if (this.complete.get(year).get(day).part2) {
                        graphics2D.setPaint(this.getColorForTime(time));
                        graphics2D.fillRect(x + XSCALE / 2, y + quarterYScale, halfXScale, threeQuarterYScale);
                    }
                }
            }
        }

        this.paintAxes(graphics2D);
    }

    private Paint getColorForTime(final long time) {

        if (time <= 1000) {
            return Color.GREEN;
        }
        if (time <= 5000) {
            return Color.YELLOW;
        }
        if (time <= 10000) {
            return Color.ORANGE;
        }
        if (time <= 30000) {
            return Color.RED;
        }
        return Color.MAGENTA;
    }

    private Paint getColorForOutcome(final int outcomeForDay) {

        switch (outcomeForDay) {
            case 2:
                return Color.YELLOW;
            case 1:
                return Color.GRAY;
            default:
                return Color.BLACK;
        }
    }

    private long getTimeForDay(final int year, final int day, final int part) {

        final Optional<Integer> shortcut = this.getShortcutTimeForDay(year, day, part);
        if (shortcut.isPresent()) {
            return shortcut.get();
        }


        if (!this.complete.containsKey(year)) {
            return -1;
        }
        if (!this.complete.get(year).containsKey(day)) {
            return -1;
        }
        return part == 1 ? this.complete.get(year).get(day).timeInMs1 :
                this.complete.get(year).get(day).timeInMs2;
    }

    private Optional<Integer> getShortcutTimeForDay(final int year, final int day, final int part) {

        return this.getShortcutForDay(year, day, part, 1);
    }

    private Optional<Integer> getShortcutOutcomeForDay(final int year, final int day, final int part) {

        return this.getShortcutForDay(year, day, part, 0);
    }

    private Optional<Integer> getShortcutForDay(final int year, final int day, final int part, final int index) {

        final Map<String, List<Integer>> shorts = new HashMap<>();
        shorts.put("2022-21.1", List.of(2, 64));
        shorts.put("2022-21.2", List.of(2, 18790));

        final String key = year + "-" + day + "." + part;
        if (!shorts.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.of(shorts.get(key).get(index));
    }

    private int getOutcomeForDay(final int year, final int day) {

        final Optional<Integer> shortcut = this.getShortcutOutcomeForDay(year, day, 1);
        if (shortcut.isPresent()) {
            return shortcut.get();
        }

        if (this.complete.containsKey(year)) {
            if (this.complete.get(year).containsKey(day)) {
                if (this.complete.get(year).get(day).both()) {
                    return 2;
                }
                if (this.complete.get(year).get(day).part1) {
                    return 1;
                }
                return 0;
            }
        }
        return -1;
    }


    private void clearBackground(final Graphics2D graphics2D) {

        graphics2D.setPaint(Color.BLACK);
        graphics2D.fillRect(0, 0, this.width, this.height);
    }

    public AdventOfCode() {
        this.challenges = new ArrayList<>();
    }
}
