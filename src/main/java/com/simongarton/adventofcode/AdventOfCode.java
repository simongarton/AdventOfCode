package com.simongarton.adventofcode;

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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class AdventOfCode {

    private static final int XSCALE = 40;
    private static final int YSCALE = 40;

    private final List<AdventOfCodeChallenge> challenges;
    private final Map<Integer, Map<Integer, AdventOfCodeChallenge.Outcome>> complete = new HashMap<>();

    private int width;
    private int height;

    final int startYear = 2019;
    final int endYear = 2024;


    public static void main(final String[] args) {

        final AdventOfCode adventOfCode = new AdventOfCode();
        adventOfCode.load2019();
        adventOfCode.load2020();
//        adventOfCode.load2021();
//        adventOfCode.load2022();
//        adventOfCode.load2023();
//        adventOfCode.load2024();
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
        this.paintMap("AdventOfCode.png");
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
        System.out.println("                1111111111222222");
        System.out.println("       1234567890123456789012345");
        for (int year = this.startYear; year <= this.endYear; year++) {
            final StringBuilder line = new StringBuilder(year + " : ");
            for (int day = 1; day <= 25; day++) {
                line.append(this.textSymbolForDay(this.getOutcomeForDay(year, day)));
            }
            System.out.println(line);
        }
        System.out.println("       1234567890123456789012345");
        System.out.println("                1111111111222222");
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
        // this.challenges.add(new Year2021Day21()); // slow
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
//        this.challenges.add(new Year2022Day21()); // slow
        this.challenges.add(new Year2022Day22());
        this.challenges.add(new Year2022Day23());
//        this.challenges.add(new Year2022Day24()); // slow
        this.challenges.add(new Year2022Day25());
    }

    private void load2023() {
        this.challenges.add(new Year2023Day1());
        this.challenges.add(new Year2023Day2());
        this.challenges.add(new Year2023Day3());
        this.challenges.add(new Year2023Day4());
//        this.challenges.add(new Year2023Day5()); // slow
        this.challenges.add(new Year2023Day6());
//        this.challenges.add(new Year2023Day7()); // slow
        this.challenges.add(new Year2023Day8());
        this.challenges.add(new Year2023Day9());
        this.challenges.add(new Year2023Day10());
        this.challenges.add(new Year2023Day11());
        this.challenges.add(new Year2023Day12());
        this.challenges.add(new Year2023Day13());
        this.challenges.add(new Year2023Day14());
        this.challenges.add(new Year2023Day15());
        this.challenges.add(new Year2023Day16());
//        this.challenges.add(new Year2023Day17()); // slow
        this.challenges.add(new Year2023Day18());
        this.challenges.add(new Year2023Day19());
        this.challenges.add(new Year2023Day20());
        this.challenges.add(new Year2023Day21());
//        this.challenges.add(new Year2023Day22()); // slow
        this.challenges.add(new Year2023Day23());
        this.challenges.add(new Year2023Day24());
//        this.challenges.add(new Year2023Day25());
    }

    private void load2024() {
        this.challenges.add(new Year2024Day1());
        this.challenges.add(new Year2024Day2());
        this.challenges.add(new Year2024Day3());
        this.challenges.add(new Year2024Day4());
        this.challenges.add(new Year2024Day5());
//        this.challenges.add(new Year2024Day6()); slow
        this.challenges.add(new Year2024Day7());
        this.challenges.add(new Year2024Day8());
        this.challenges.add(new Year2024Day9());
        this.challenges.add(new Year2024Day10());
        this.challenges.add(new Year2024Day11());
        this.challenges.add(new Year2024Day12());
        this.challenges.add(new Year2024Day13());
        this.challenges.add(new Year2024Day14());
        this.challenges.add(new Year2024Day15());
        this.challenges.add(new Year2024Day16());
        this.challenges.add(new Year2024Day17());
        this.challenges.add(new Year2024Day18());
        this.challenges.add(new Year2024Day19());
        this.challenges.add(new Year2024Day20());
    }

    private void paintMap(final String filename) {

        // do this but with text

        final int years = this.endYear - this.startYear;
        final int days = 25;

        this.width = (days + 10) * XSCALE;
        this.height = ((years + 10) * YSCALE) / 2;

        final BufferedImage bufferedImage = new BufferedImage(this.width, this.height, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);
        this.paintOutcomes(graphics2D);
        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void paintOutcomes(final Graphics2D graphics2D) {

        for (int year = this.startYear; year <= this.endYear; year++) {
            for (int day = 1; day <= 25; day++) {

                final int x = 10 + day * XSCALE;
                final int y = 10 + (year - this.startYear) * YSCALE;

                graphics2D.setPaint(this.getColorForOutcome(this.getOutcomeForDay(year, day)));
                graphics2D.fillRect(x + 4, y + 4, XSCALE - 4, YSCALE - 4);

                graphics2D.setPaint(Color.BLUE);
                graphics2D.drawRect(x + 2, y + 2, XSCALE - 2, YSCALE - 2);
            }
        }
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

    private int getOutcomeForDay(final int year, final int day) {

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
