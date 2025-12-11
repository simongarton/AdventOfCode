package com.simongarton.adventofcode.year2025;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Year2025Day11 extends AdventOfCodeChallenge {

    private List<Device> devices;

    @Override
    public String title() {
        return "Day 11: Reactor";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2025, 11);
    }

    @Override
    public String part1(final String[] input) {

        this.devices = this.buildDeviceList(input);
        this.devices.add(new Device("out", new String[0]));
        final List<String> paths = this.explore("you", "out");
        return String.valueOf(paths.size());
    }

    private List<String> explore(final String startId, final String endId) {

        final List<DevicePath> pathsTried = new ArrayList<>();
        final List<DevicePath> completePaths = new ArrayList<>();

        DevicePath current = new DevicePath(List.of(this.getDevice(startId)));

        final List<DevicePath> pathsToTry = new ArrayList<>(this.pathsFromDevice(current));
        long iteration = 0;
        while (!pathsToTry.isEmpty()) {
            current = pathsToTry.removeFirst();
            pathsTried.add(current);
            if (current.getEnd().id.equalsIgnoreCase(endId)) {
                completePaths.add(current);
            }
            pathsToTry.addAll(this.findNewPaths(current, pathsTried));
            iteration++;
            if (iteration % 1000 == 0) {
                System.out.println("it " + iteration + " tried " + pathsTried.size() + " left " + pathsToTry.size());
            }
        }

        return completePaths.stream().map(DevicePath::toString).collect(Collectors.toList());
    }

    private List<String> exploreCarefully(final String startId) {

        final List<DevicePath> pathsTried = new ArrayList<>();
        final List<DevicePath> completePaths = new ArrayList<>();

        DevicePath current = new DevicePath(List.of(this.getDevice(startId)));

        final List<DevicePath> pathsToTry = new ArrayList<>(this.pathsFromDevice(current));
        while (!pathsToTry.isEmpty()) {
            current = pathsToTry.removeFirst();
            pathsTried.add(current);
            if (current.getEnd().id.equalsIgnoreCase("out")) {
                if (current.contains("dac") && current.contains("fft")) {
                    completePaths.add(current);
                }
            }
            pathsToTry.addAll(this.findNewPaths(current, pathsTried));
        }

        return completePaths.stream().map(DevicePath::toString).collect(Collectors.toList());
    }

    private List<DevicePath> findNewPaths(final DevicePath current, final List<DevicePath> pathsTried) {

        final List<DevicePath> pathsToTry = new ArrayList<>();
        final List<DevicePath> contenders = this.pathsFromDevice(current);
        // equals / hashcode
        for (final DevicePath contender : contenders) {
            if (!pathsTried.stream().anyMatch(p -> p.toString().equalsIgnoreCase(contender.toString()))) {
                pathsToTry.add(contender);
            }
        }
        return pathsToTry;
    }

    private List<DevicePath> pathsFromDevice(final DevicePath current) {

        final List<DevicePath> paths = new ArrayList<>();
        final Device end = current.getEnd();
        for (final String outputId : end.getOutputs()) {
            final Device output = this.getDevice(outputId);
            final DevicePath newPath = this.extend(current, output);
            paths.add(newPath);
        }
        return paths;
    }

    private DevicePath extend(final DevicePath current, final Device output) {

        final DevicePath devicePath = new DevicePath(current.getDevices());
        devicePath.addDevice(output);
        return devicePath;
    }

    public Device getDevice(final String id) {

        return this.devices.stream().filter(d -> d.id.equalsIgnoreCase(id)).findFirst().orElseThrow();
    }

    private List<Device> buildDeviceList(final String[] input) {

        return Arrays.stream(input).map(this::buildDevice).collect(Collectors.toList());
    }

    private Device buildDevice(final String s) {

        final String[] parts = s.split(":");
        final String id = parts[0];
        final String[] outputs = parts[1].trim().split(" ");
        return new Device(id, outputs);
    }

    @Override
    public String part2(final String[] input) {

        this.devices = this.buildDeviceList(input);
        this.devices.add(new Device("out", new String[0]));
        final List<String> paths = new ArrayList<>();
        final List<String> svrFftPaths = this.explore("svr", "fft");
        final List<String> svrDacPaths = this.explore("svr", "dac");
        final List<String> fftDacPaths = this.explore("fft", "dac");
        final List<String> dacFftPaths = this.explore("dac", "fft");
        final List<String> dacOutPaths = this.explore("dac", "out");
        final List<String> fftOutPaths = this.explore("fft", "out");
        System.out.println(svrFftPaths.size());
        System.out.println(svrDacPaths.size());
        System.out.println(fftDacPaths.size());
        System.out.println(dacFftPaths.size());
        System.out.println(dacOutPaths.size());
        System.out.println(fftOutPaths.size());

        return String.valueOf(paths.size());
    }

    public static final class Device {

        private final String id;
        private final String[] outputs;

        public Device(final String id, final String[] outputs) {

            this.id = id;
            this.outputs = outputs;
        }

        public String getId() {
            return this.id;
        }

        public String[] getOutputs() {
            return this.outputs;
        }
    }

    public static final class DevicePath {

        private final List<Device> devices;

        public DevicePath(final List<Device> start) {

            this.devices = new ArrayList<>(start);
        }

        public boolean hasVisited(final Device device) {

            return this.devices.stream().anyMatch(d -> d.id.equalsIgnoreCase(device.id));
        }

        public int addDevice(final Device device) {

            this.devices.add(device);
            return this.devices.size();
        }

        @Override
        public String toString() {

            return String.join("-", this.devices.stream().map(Device::getId).toList());
        }

        public Device getEnd() {

            return this.devices.getLast();
        }

        public List<Device> getDevices() {

            return this.devices;
        }

        public boolean contains(final String id) {

            return this.devices.stream().anyMatch(d -> d.id.equalsIgnoreCase(id));
        }
    }
}
