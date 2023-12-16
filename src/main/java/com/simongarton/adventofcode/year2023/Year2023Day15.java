package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class Year2023Day15 extends AdventOfCodeChallenge {

    private List<List<String>> boxes;

    @Override
    public String title() {
        return "Day 15: Lens Library";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 15);
    }

    @Override
    public String part1(final String[] input) {

        final String[] parts = input[0].split(",");
        long total = 0;
        for (final String part : parts) {
            total = total + hash(part);
        }
        return String.valueOf(total);
    }

    public static long hash(final String part) {

        int current = 0;
        for (int index = 0; index < part.length(); index++) {
            current = current + ascii(part.substring(index, index + 1));
            current = current * 17;
            current = current % 256;
        }
        return current;
    }

    public static int ascii(final String substring) {

        return substring.charAt(0);
    }

    @Override
    public String part2(final String[] input) {

        this.boxes = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            this.boxes.add(new ArrayList<>());
        }
        final String[] parts = input[0].split(",");

        for (final String part : parts) {
            final Instruction instruction = new Instruction(part);
            this.carryOut(instruction);
        }

        if (false) {
            this.debugBoxes();
        }

        final int score = this.scoreLenses();
        return String.valueOf(score);
    }

    private void debugBoxes() {

        for (int i = 0; i < 256; i++) {
            final List<String> box = this.boxes.get(i);
            if (!box.isEmpty()) {
                System.out.println(i + " : " + String.join(",", box) + " : " + this.score(i, box));
            }
        }
    }

    private int score(final int i, final List<String> box) {

        int score = 0;
        for (int j = 0; j < box.size(); j++) {
            final String[] parts = box.get(j).split(" ");
            score = score + ((i + 1) * (j + 1) * Integer.parseInt(parts[1]));
        }
        return score;
    }

    private int scoreLenses() {
        
        int score = 0;
        for (int i = 0; i < 256; i++) {
            final List<String> box = this.boxes.get(i);
            score = score + this.score(i, box);
        }
        return score;
    }

    private void carryOut(final Instruction instruction) {
        if (instruction.getOperation().equalsIgnoreCase("-")) {
            final List<String> box = this.boxes.get(instruction.getBox());
            final int index = this.findIndex(box, instruction.getLabel());
            if (index > -1) {
                box.remove(index);
            }
            return;
        }
        if (instruction.getOperation().equalsIgnoreCase("=")) {
            final List<String> box = this.boxes.get(instruction.getBox());
            final int index = this.findIndex(box, instruction.getLabel());
            if (index > -1) {
                box.remove(index);
                box.add(index, instruction.getLabel() + " " + instruction.getMirror());
            } else {
                box.add(instruction.getLabel() + " " + instruction.getMirror());
            }
            return;
        }
        throw new RuntimeException(instruction.toString());
    }

    private int findIndex(final List<String> box, final String label) {

        for (int i = 0; i < box.size(); i++) {
            if (box.get(i).startsWith(label)) {
                return i;
            }
        }
        return -1;
    }

    @Data
    private static final class Instruction {

        private String code;
        private String label;
        private int box;
        private int mirror;
        private String operation;

        public Instruction(final String code) {

            this.code = code;
            final String[] parts;
            if (code.contains("=")) {
                parts = code.split("=");
                this.label = parts[0];
                this.box = (int) Year2023Day15.hash(parts[0]);
                this.operation = "=";
                this.mirror = Integer.parseInt(parts[1]);
            } else {
                parts = code.split("-");
                this.label = parts[0];
                this.box = (int) Year2023Day15.hash(parts[0]);
                this.operation = "-";
            }
        }
    }
}
