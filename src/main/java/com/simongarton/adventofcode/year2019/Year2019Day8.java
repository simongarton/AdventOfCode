package com.simongarton.adventofcode.year2019;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;

public class Year2019Day8 extends AdventOfCodeChallenge {

    @Override
    public Outcome run() {
        return this.runChallenge(2019, 8);
    }

    @Override
    public String part1(final String[] input) {

        final int width = 25;
        final int height = 6;

        final List<String> layers = new ArrayList<>();
        int layerIndex = 0;
        while (layerIndex < input[0].length()) {
            layers.add(input[0].substring(layerIndex, layerIndex + width * height));
            layerIndex += width * height;
        }

        int minZeros = Integer.MAX_VALUE;
        int minZeroPosition = 0;
        int oneByTwo = 0;
        layerIndex = 0;
        for (final String layer : layers) {
            final int zeros = this.countCharacters("0", layer);
            if (zeros < minZeros) {
                minZeros = zeros;
                minZeroPosition = layerIndex;
                oneByTwo = this.countCharacters("1", layer) * this.countCharacters("2", layer);
            }
            layerIndex++;
        }

        System.out.printf("min zeros of %s at %s and found %s%n", minZeros, minZeroPosition, oneByTwo);

        return String.valueOf(oneByTwo);
    }

    private int countCharacters(final String number, final String line) {
        return line.length() - line.replace(number, "").length();
    }

    @Override
    public String part2(final String[] input) {

        final int width = 25;
        final int height = 6;

        final int dataLength = input[0].length();

        System.out.printf("I have an input of length %s%n", dataLength);
        System.out.printf("I am looking for images %s x %s = %s%n", width, height, width * height);
        System.out.printf("So I expect to get %s layers%n", dataLength / (width * height));

        final List<String> layers = new ArrayList<>();
        int layerIndex = 0;
        while (true) {
            final int start = layerIndex * (width * height);
            final int end = (layerIndex + 1) * (width * height);
            final String layerData = input[0].substring(start, end);
            layers.add(layerData);
            System.out.println(layerIndex + ":" + layerData);
            layerIndex++;
            if ((layerIndex * (width * height)) >= input[0].length()) {
                break;
            }
        }

        System.out.printf("I ended up with %s layers%n", layers.size());

        final StringBuilder finalString = new StringBuilder();
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                finalString.append(this.getFirstNon2Character(layers, w, h, width));
            }
        }

        this.displayLayer(finalString.toString(), width, height);

        return finalString.toString();
    }

    private void displayLayer(final String finalString, final int width, final int height) {
        for (int h = 0; h < height; h++) {
            System.out.println(finalString.substring(h * width, (h + 1) * width));
        }
    }

    private String getFirstNon2Character(final List<String> layers, final int w, final int h, final int width) {

        for (int layerIndex = 0; layerIndex < layers.size(); layerIndex++) {
            final String character = layers.get(layerIndex).charAt((h * width) + w) + "";
            if (!character.equals("2")) {
                return character;
            }
        }
        throw new RuntimeException("No non-2 character found");
    }
}
