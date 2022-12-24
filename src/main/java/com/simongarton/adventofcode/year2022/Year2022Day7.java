package com.simongarton.adventofcode.year2022;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Year2022Day7 extends AdventOfCodeChallenge {

    private CommunicatorDirectory root;
    private CommunicatorDirectory current;
    private boolean listingFiles = false;
    private List<CommunicatorDirectory> all;

    @Override
    public String title() {
        return "Day 7: No Space Left On Device";
    }

    @Override
    public boolean run() {
        return this.runChallenge(2022, 7);
    }

    @Override
    public String part1(final String[] input) {
        this.parse(input);
        this.buildAll();
        final int totalOfLimitedSize = this.all.stream()
                .map(CommunicatorDirectory::getFileSize)
                .filter(fileSize -> fileSize < 100000)
                .mapToInt(Integer::intValue)
                .sum();

        return String.valueOf(totalOfLimitedSize);
    }

    @Override
    public String part2(final String[] input) {
        this.parse(input);
        this.buildAll();
        final int totalDiskSize = 70000000;
        final int needed = 30000000;
        final int used = this.root.getFileSize();
        final int unused = totalDiskSize - used;
        final int needToDelete = needed - unused;

        final List<CommunicatorDirectory> possibles = this.all.stream()
                .filter(c -> c.getFileSize() > needToDelete)
                .collect(Collectors.toList());
        possibles.sort(Comparator.comparing(CommunicatorDirectory::getFileSize));

        return String.valueOf(possibles.get(0).getFileSize());
    }

    private void buildAll() {
        this.all = new ArrayList<>();
        this.buildFrom(this.root);
    }

    private void buildFrom(final CommunicatorDirectory working) {
        this.all.add(working);
        for (final CommunicatorDirectory directory : working.subDirectories) {
            this.buildFrom(directory);
        }
    }

    private void parse(final String[] input) {

        for (final String line : input) {
            this.parseLine(line);
        }
    }

    private void parseLine(final String line) {

        if (line.startsWith("$")) {
            this.listingFiles = false;
            this.handleCommandLine(line);
            return;
        }

        if (line.startsWith("dir")) {
            this.addNewDirectory(line);
            return;
        }

        if (!this.listingFiles) {
            throw new RuntimeException("Unhandled line " + line);
        }

        final String[] parts = line.split(" ");
        final int size = Integer.parseInt(parts[0]);
        final String name = parts[1];
        final CommunicatorFile communicatorFile = new CommunicatorFile(name, size);
        this.current.files.add(communicatorFile);
    }

    private void addNewDirectory(final String line) {
        final CommunicatorDirectory communicatorDirectory = new CommunicatorDirectory();
        communicatorDirectory.name = line.substring(4);
        communicatorDirectory.parent = this.current;
        this.current.subDirectories.add(communicatorDirectory);
    }

    private void handleCommandLine(final String line) {
        if (line.equalsIgnoreCase("$ cd /")) {
            this.resetToRoot();
            return;
        }

        if (line.equalsIgnoreCase("$ ls")) {
            this.listingFiles = true;
            return;
        }

        if (line.startsWith("$ cd")) {
            this.changeDirectoryDown(line);
            return;
        }

        throw new RuntimeException("Unhandled command " + line);
    }

    private void changeDirectoryDown(final String line) {
        final String[] parts = line.split(" ");
        final String directoryName = parts[2];

        if (directoryName.equals("..")) {
            this.current = this.current.parent;
            return;
        }

        final CommunicatorDirectory communicatorDirectory = this.current
                .subDirectories
                .stream()
                .filter(c -> c.name.equalsIgnoreCase(directoryName))
                .findFirst()
                .orElse(null);

        if (communicatorDirectory == null) {
            throw new RuntimeException("did not change down " + line);
        }

        this.current = communicatorDirectory;
    }

    private void resetToRoot() {
        this.root = new CommunicatorDirectory();
        this.root.name = "/";
        this.current = this.root;
    }

    public static final class CommunicatorFile {

        private final int fileSize;
        private final String fileName;

        public CommunicatorFile(final String fileName, final int size) {
            this.fileName = fileName;
            this.fileSize = size;
        }
    }

    public static final class CommunicatorDirectory {

        private String name;
        private CommunicatorDirectory parent;
        private final List<CommunicatorDirectory> subDirectories = new ArrayList<>();
        private final List<CommunicatorFile> files = new ArrayList<>();

        public int getFileSize() {
            return this.files.stream().map(f -> f.fileSize).mapToInt(Integer::intValue).sum() +
                    this.subDirectories.stream().map(CommunicatorDirectory::getFileSize).mapToInt(Integer::intValue).sum();
        }
    }
}
