package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;

public class Year2024Day9 extends AdventOfCodeChallenge {

    private int[] ids;

    @Override
    public String title() {
        return "Day 9: Disk Fragmenter";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 9);
    }

    @Override
    public String part1(final String[] input) {

        // 90779541117
        // too low

        // the first few examples didn't go into double digits.
        // do I need to keep track of this ?

        final String disk = this.parseLine(input[0]);
        System.out.println(disk.length());
        this.ids = new int[disk.length()];
        
        final String shuffledDisk = this.shuffleDisk(disk);
        //System.out.println(shuffledDisk);
        final long checkSum = this.checksum(shuffledDisk);
        return String.valueOf(checkSum);
    }

    private long checksum(final String shuffledDisk) {

        long total = 0;
        for (int i = 0; i < shuffledDisk.length(); i++) {

            if ((shuffledDisk.charAt(i) + "").equalsIgnoreCase(".")) {
                break;
            }
            total = total + (i * Long.parseLong(shuffledDisk.charAt(i) + ""));

        }
        return total;
    }

    private String shuffleDisk(final String disk) {

        int frontPointer = 0;
        int backPointer = disk.length() - 1;

        String shuffled = disk;

        while (true) {

            final String front = disk.charAt(frontPointer) + "";
            if (!front.equalsIgnoreCase(".")) {
                frontPointer++;
                // now I'm checking to see if I have hit the back pointer
                // if I drop out when I hit it ... I get a too low answer
                // if I don't check it, I overlap
                if (frontPointer == backPointer) {
                    break;
                }
                continue;
            }
            final String back = disk.charAt(backPointer) + "";
            shuffled = this.swapCharacter(shuffled, front, frontPointer, back, backPointer);
            frontPointer++;
            backPointer--;

            if (backPointer == frontPointer) {
                break;
            }
            while (true) {
                final String scan = disk.charAt(backPointer) + "";
                if (!scan.equalsIgnoreCase(".")) {
                    break;
                }
                backPointer--;
            }
        }

        return shuffled;
    }

    public String swapCharacter(final String shuffled,
                                final String front,
                                final int frontPointer,
                                final String back,
                                final int backPointer) {

        final int length = shuffled.length();

        final StringBuilder builder = new StringBuilder();
        // the good start
        builder.append(shuffled, 0, frontPointer);
        // now pull in the last character
        builder.append(back);
        // now skip the "." we're replacing, and bring in the rest
        builder.append(shuffled, frontPointer + 1, backPointer);
        // and a final "."
        builder.append(".".repeat(length - backPointer));
        return builder.toString();
    }

    private String parseLine(final String diskMap) {

        final List<FileThing> fileThings = new ArrayList<>();

        int fileThingIndex = 0;
        final StringBuilder disk = new StringBuilder();

        for (int index = 0; index < diskMap.length(); index += 2) {
            final String file = diskMap.charAt(index) + "";
            final int fileUsage = Integer.parseInt(file);
            disk.append(String.valueOf(fileThingIndex).repeat(fileUsage));

            fileThingIndex++;

            if (index == diskMap.length() - 1) {
                break;
            }
            final String empty = diskMap.charAt(index + 1) + "";
            final int emptyUsage = Integer.parseInt(empty);
            disk.append(".".repeat(emptyUsage));

            final FileThing fileThing = new FileThing(fileThingIndex, fileUsage, emptyUsage);
            fileThings.add(fileThing);

        }
        //System.out.println(disk.toString());
        return disk.toString();

    }


    @Override
    public String part2(final String[] input) {
        return null;
    }

    static class FileThing {

        int id;
        int length;
        int trailingSpaces;

        public FileThing(final int id, final int length, final int empty) {
            this.id = this.id;
            this.length = this.length;
            this.trailingSpaces = empty;
        }
    }
}
