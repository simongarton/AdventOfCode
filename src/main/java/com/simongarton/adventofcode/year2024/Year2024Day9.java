package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;

public class Year2024Day9 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;

    private List<FileRecord> fileRecords;

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

        final List<Integer> disk = this.parseLine(input[0]);
        this.displayDisk(disk);

        final List<Integer> shuffledDisk = this.shuffleDisk(disk);
        this.displayDisk(shuffledDisk);

        final long checkSum = this.checksum(shuffledDisk);
        return String.valueOf(checkSum);
    }

    private void displayDisk(final List<Integer> shuffledDisk) {

        if (!DEBUG) {
            return;
        }

        final StringBuilder line = new StringBuilder();
        for (final Integer i : shuffledDisk) {
            if (i < 0) {
                line.append(".");
            } else {
                line.append(i % 10);
            }
        }

        System.out.println(line);
    }

    private long checksum(final List<Integer> shuffledDisk) {

        long total = 0;
        for (int i = 0; i < shuffledDisk.size(); i++) {

            final Integer value = shuffledDisk.get(i);
            if (value >= 0) {
                total = total + ((long) i * shuffledDisk.get(i));
            }

        }
        return total;
    }

    private List<Integer> shuffleDisk(final List<Integer> disk) {

        int frontPointer = 0;
        int backPointer = disk.size() - 1;
        while (frontPointer <= backPointer) {
            final Integer frontValue = disk.get(frontPointer);
            if (frontValue >= 0) {
                frontPointer++;
                continue;
            }
            final int valueToMove = disk.get(backPointer);
            disk.add(frontPointer, valueToMove);
            disk.remove(frontPointer + 1);
            disk.add(backPointer, -1);
            disk.remove(backPointer + 1);
            backPointer--;
            while (disk.get(backPointer) < 0) {
                backPointer--;
            }
        }
        return disk;
    }

    private List<Integer> parseLine(final String diskMap) {

        final List<Integer> disk = new ArrayList<>();
        this.fileRecords = new ArrayList<>();

        int fileId = 0;
        int actualIndex = 0;

        for (int index = 0; index < diskMap.length(); index += 2) {
            final String file = diskMap.charAt(index) + "";
            final int fileUsage = Integer.parseInt(file);
            final int thisFileStartIndex = actualIndex;
            for (int sub = 0; sub < fileUsage; sub++) {
                disk.add(fileId);
                actualIndex++;
            }

            // have I got to the end of the file ?
            if (index == diskMap.length() - 1) {
                final FileRecord fileRecord = new FileRecord(fileId, thisFileStartIndex, fileUsage, 0);
                this.fileRecords.add(fileRecord);
                break;
            }
            // how many emptys do  I have ?
            final String empty = diskMap.charAt(index + 1) + "";
            final int emptyUsage = Integer.parseInt(empty);
            for (int sub = 0; sub < emptyUsage; sub++) {
                disk.add(-1);
                actualIndex++;
            }

            final FileRecord fileRecord = new FileRecord(fileId, thisFileStartIndex, fileUsage, emptyUsage);
            this.fileRecords.add(fileRecord);
            fileId++;
        }
        return disk;
    }

    private List<Integer> shuffleDiskAsFiles(final List<Integer> disk) {

        // don't move the first file, because it will already be there
        for (int fileRecordIndex = this.fileRecords.size() - 1; fileRecordIndex > 0; fileRecordIndex--) {
            this.displayDisk(disk);
            final FileRecord fileRecord = this.fileRecords.get(fileRecordIndex);

            final Integer position = this.findBlankPosition(fileRecord.fileLength, disk);
            // can't find a position to put it
            if (position == null) {
                continue;
            }

            // oooh .. better to stay where I am !
            if (position > fileRecord.startIndex) {
                continue;
            }

            for (int charToMove = 0; charToMove < fileRecord.fileLength; charToMove++) {
                disk.add(position + charToMove, fileRecord.fileId);
                disk.remove(position + 1 + charToMove);
                disk.add(fileRecord.startIndex + charToMove, -1);
                disk.remove(fileRecord.startIndex + 1 + charToMove);
            }

            this.displayDisk(disk);
        }

        return disk;
    }

    private Integer findBlankPosition(final int fileLength, final List<Integer> disk) {

        int startIndex = 0;
        int blankCount = 0;
        for (int index = 0; index < disk.size(); index++) {
            if (disk.get(index) >= 0) {
                startIndex = index + 1;
                blankCount = 0;
                continue;
            }
            blankCount++;
            if (blankCount == fileLength) {
                return startIndex;
            }
        }
        return null;
    }


    @Override
    public String part2(final String[] input) {

        final List<Integer> disk = this.parseLine(input[0]);
        this.displayDisk(disk);

        if (DEBUG) {
            for (final FileRecord fileRecord : this.fileRecords) {
                System.out.println(fileRecord);
            }
        }

        final List<Integer> shuffledDisk = this.shuffleDiskAsFiles(disk);
        this.displayDisk(shuffledDisk);

        final long checkSum = this.checksum(shuffledDisk);
        return String.valueOf(checkSum);
    }

    static class FileRecord {

        int fileId;
        int startIndex;
        int fileLength;
        int spaceLength;

        public FileRecord(final int fileId, final int startIndex, final int fileLength, final int spaceLength) {
            this.fileId = fileId;
            this.startIndex = startIndex;
            this.fileLength = fileLength;
            this.spaceLength = spaceLength;
        }

        @Override
        public String toString() {
            return this.fileId + " " + this.startIndex + "+" + this.fileLength + "[" + this.spaceLength + "]";
        }
    }
}
