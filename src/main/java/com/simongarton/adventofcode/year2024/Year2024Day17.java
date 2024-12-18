package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Year2024Day17 extends AdventOfCodeChallenge {

    private final List<Integer> part2Program = List.of(2, 4, 1, 1, 7, 5, 4, 4, 1, 4, 0, 3, 5, 5, 3, 0);
    private final AtomicInteger bestDepthFound = new AtomicInteger(0);
    private long start;
    private final Random random = new Random();


    @Override
    public String title() {
        return "Day 17: Chronospatial Computer";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 17);
    }

    @Override
    public String part1(final String[] input) {

        final ChronospatialComputer computer = ChronospatialComputer.initializeFromLines(input);
        computer.run();

        return computer.getOutputString();
    }

    @Override
    public String part2(final String[] input) {

        this.start = System.currentTimeMillis();
        final ChronospatialComputer computer = ChronospatialComputer.initializeFromLines(input);

        final List<Genotype> pool = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            pool.add(new Genotype(this.randomOctalString(16)));
        }

        final Comparator<Genotype> genotypeComparator = Comparator.comparing(g -> g.score);
        final Comparator<Genotype> genotypeComparatorReversed = genotypeComparator.reversed();

        int bestScore = 0;
        Genotype bestGenotype = null;

        int iteration = 0;
        while (true) {

            int totalScore = 0;
            for (final Genotype genotype : pool) {
                this.score(computer, genotype);
                totalScore += genotype.score;
                if (genotype.score > bestScore) {
                    bestScore = genotype.score;
                    bestGenotype = genotype;

                    System.out.println(genotype.score + ":" +
                            genotype.dna +
                            " -> " +
                            genotype.output +
                            " =? " +
                            genotype.comparison
                    );
                }
            }

            if (iteration % 1000 == 0 && bestGenotype != null) {
                System.out.println(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS) + ": (" +
                        bestGenotype.score + ") " +
                        bestGenotype.output + " -> " +
                        bestGenotype.comparison
                );
            }

            if (bestScore == 16) {
                break;
            }

            if (totalScore == 0) {
                pool.clear();
                for (int i = 0; i < 8; i++) {
                    pool.add(new Genotype(this.randomOctalString(16)));
                }
                continue;
            }

//            if (iteration % 1000 == 0) {
//                System.out.println("\n iteration " + iteration + "\n");
//                for (final Genotype genotype : pool) {
//                    System.out.println(genotype);
//                }
//                System.out.println();
//            }

            // get the best
            final List<Genotype> sorted = pool.stream().sorted(genotypeComparatorReversed).collect(Collectors.toList());
            pool.clear();
            pool.add(sorted.get(0));

            // breed up the next few
            final List<Genotype> breeders = new ArrayList<>();
            for (int i = 0; i < 27; i++) {
                breeders.add(sorted.get(this.random.nextInt(sorted.size())));
            }
            pool.addAll(this.breed(breeders));

            // and some random ones
            while (pool.size() < 32) {
                pool.add(new Genotype(this.randomOctalString(16)));
            }

            iteration++;
        }

        return String.valueOf(Long.parseLong(bestGenotype.dna, 8));
    }

    private String compareStrings(final String outputString, final String programString) {

        final StringBuilder comparison = new StringBuilder();
        for (int i = 0; i < programString.length(); i++) {
            if (outputString.length() < i) {
                return comparison.toString();
            }
            try {
                if (outputString.charAt(i) == programString.charAt(i)) {
                    comparison.append(outputString.charAt(i));
                } else {
                    comparison.append("*");
                }
            } catch (final StringIndexOutOfBoundsException s) {
                // don't know why the check above is not catching this
//                System.out.println("o " + outputString + " " + outputString.length());
//                System.out.println("p " + programString + " " + programString.length());
                return comparison.toString();
            }
        }
        return comparison.toString();
    }

    private String outcome(final ChronospatialComputer computer, final Genotype genotype) {

        final long a = Long.parseLong(genotype.dna, 8);
        computer.reset();
        computer.setRegisterA(a);
        computer.run();

        return computer.getOutputString();
    }

    private List<Genotype> breed(final List<Genotype> breeders) {

        final List<Genotype> children = new ArrayList<>();
        while (children.size() < breeders.size()) {
            final StringBuilder dna = new StringBuilder();
            for (int j = 0; j < 16; j++) {
                final int index = this.random.nextInt(breeders.size());
                dna.append(breeders.get(index).dna.charAt(j));
            }
            if (!this.alreadyGotChild(dna.toString(), children)) {
                children.add(new Genotype(dna.toString()));
            }
        }
        return children;
    }

    private boolean alreadyGotChild(final String dna, final List<Genotype> children) {

        return children.stream().anyMatch(g -> g.dna.equalsIgnoreCase(dna));
    }

    private void score(final ChronospatialComputer computer, final Genotype genotype) {

        final long a = Long.parseLong(genotype.dna, 8);
        computer.reset();
        computer.setRegisterA(a);
        computer.run();

        int score = 0;
        final String outputString = computer.getOutputString();
        final String programString = computer.getProgramString();
        for (int i = 0; i < 31; i += 2) {
            try {
                if (outputString.charAt(i) == programString.charAt(i)) {
                    score++;
                }
            } catch (final StringIndexOutOfBoundsException e) {
            }
        }
//        System.out.println(genotype.dna + " " + outputString);
        genotype.score = score;
        genotype.output = computer.getOutputString();
        genotype.comparison = this.compareStrings(genotype.output, computer.getProgramString());
    }

    private String randomOctalString(final int length) {

        final Random random = new Random();
        final StringBuilder octal = new StringBuilder();
        for (int i = 0; i < length; i++) {
            octal.append(random.nextInt(8));
        }
        return octal.toString();
    }

    private long a16DigitOctalNumber() {

        final String value = "1234567071234567";
        System.out.println(value);
        System.out.println(Long.parseLong(value, 8));
        return Long.parseLong(value);
    }

    private void checkValue(final ChronospatialComputer computer, final long a) {

        computer.reset();
        computer.setRegisterA(a);
        computer.run();
        if (computer.getOutputString().equalsIgnoreCase(computer.getProgramString())) {
            System.out.println("a=" + a + " p " + computer.getProgramString() + " -> " + computer.getOutputString());
        }
    }

    private Integer evaluatePossibleA(final int index) {

        int a = index;
        int b = 0;
        int c = 0;

        b = a % 8;
        b = b ^ 1;
        c = Double.valueOf(Math.floor(1.0D * a / Math.pow(2, b))).intValue();
        b = b ^ c;
        b = b ^ 4;
        a = a / 8;
        if (b % 8 != 2) {
            return null;
        }
        return null;
    }

    private Long evaluatePossibleAForAll(final long index) {

        long a = index;
        long b = 0;
        long c = 0;

        final DecimalFormat formatter = new DecimalFormat("#,###");

        for (int iteration = 0; iteration < this.part2Program.size(); iteration++) {
            b = a % 8;
            b = b ^ 1;
            c = Double.valueOf(Math.floor(1.0D * a / Math.pow(2, b))).intValue();
            b = b ^ c;
            b = b ^ 4;
            a = a / 8;
            if (b % 8 != this.part2Program.get(iteration)) {
                final int currentBestDepth = this.bestDepthFound.get();
                if (iteration > currentBestDepth) {
                    this.bestDepthFound.set(iteration);
                    String best = "";
                    for (int i = 0; i < currentBestDepth; i++) {
                        best = best + this.part2Program.get(i) + ",";
                    }
                    System.out.println(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS) + "  (" + (System.currentTimeMillis() - this.start) / 1000 + " seconds) with index " + formatter.format(index) + " bestDepthFound " + currentBestDepth + "/" + this.part2Program.size() + " = " + best);
                }
                return null;
            }
        }
        return index;
    }

    static class OctalGenerator {

        private final int[] values = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        private long value;

        public OctalGenerator() {

        }

        public OctalGenerator(final String octalValue) {

            if (!(octalValue.length() == 16)) {
                throw new RuntimeException("must be 16 digits long.");
            }
            for (int i = 0; i < 16; i++) {
                this.values[i] = Integer.parseInt(octalValue.substring(i, i + 1));
            }
        }

        public String valueToString() {

            return Arrays.stream(this.values).mapToObj(String::valueOf).collect(Collectors.joining(""));
        }

        public Long yield() {

            this.next();
            return this.value;
        }

        private void next() {

            int index = 15;
            while (true) {
                final int current = this.values[index];
                if (current < 7) {
                    this.values[index] = current + 1;
                    break;
                }
                this.values[index] = 0;
                index--;
            }

            this.value = Long.parseLong(this.valueToString(), 8);
        }
    }

    static class Genotype {

        String dna;
        int score;
        String output;
        String comparison;

        public Genotype(final String dna) {

            this.dna = dna;
        }

        @Override
        public String toString() {

            return this.dna + " (" + this.score + ") " + this.output + " -> " + this.comparison;
        }
    }
}
