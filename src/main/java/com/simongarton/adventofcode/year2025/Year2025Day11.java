package com.simongarton.adventofcode.year2025;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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
        this.graphViz("2025-11.1.dot", new HashMap<>());
        final List<String> paths = this.explore("you", "out");
        return String.valueOf(paths.size());
    }

    private void graphViz(final String fileName, final Map<String, Integer> routes) {

        final List<String> lines = new ArrayList<>();
        lines.add("digraph Reactor {");

        lines.add("\tsvr [style=\"filled\" color=\"red\"]");
        lines.add("\tout [style=\"filled\" color=\"green\"]");
        lines.add("\tfft [style=\"filled\" color=\"blue\"]");
        lines.add("\tdac [style=\"filled\" color=\"purple\"]");
        lines.add("\tyou [style=\"filled\" color=\"yellow\"]");

        for (final Device device : this.devices) {
            for (final String output : device.getOutputs()) {
                String key = device.getId() + "->" + output;
                if (routes.containsKey(key)) {
                    key = key + " [label=\"" + routes.get(key) + "\"]";
                }
                lines.add("\t" + key);
            }
        }
        lines.add("}");
        try {
            Files.writeString(Path.of(fileName), String.join("\n", lines));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }


    private List<String> explore(final String startId, final String endId) {

        final Set<String> pathsTried = new HashSet<>();
        final List<DevicePath> completePaths = new ArrayList<>();

        DevicePath current = new DevicePath(List.of(this.getDevice(startId)));

        final List<DevicePath> pathsToTry = new ArrayList<>(this.pathsFromDevice(current));
        long iteration = 0;
        while (!pathsToTry.isEmpty()) {
            current = pathsToTry.removeFirst();
            pathsTried.add(current.toString());
            if (current.getEnd().id.equalsIgnoreCase(endId)) {
                completePaths.add(current);
            }
            pathsToTry.addAll(this.findNewPaths(current, pathsTried));
            iteration++;
            if (iteration % 10000 == 0) {
                System.out.println("it " + iteration + " tried " + pathsTried.size() + " left " + pathsToTry.size());
            }
        }

        return completePaths.stream().map(DevicePath::toString).collect(Collectors.toList());
    }

    private List<String> exploreWithStops(final String startId, final String endId, final List<String> stops) {

        final Set<String> pathsTried = new HashSet<>();
        final List<DevicePath> completePaths = new ArrayList<>();

        DevicePath current = new DevicePath(List.of(this.getDevice(startId)));

        final List<DevicePath> pathsToTry = new ArrayList<>(this.pathsFromDevice(current));
        long iteration = 0;
        while (!pathsToTry.isEmpty()) {
            current = pathsToTry.removeFirst();
            pathsTried.add(current.toString());
            if (current.getEnd().id.equalsIgnoreCase(endId)) {
                completePaths.add(current);
            }
            if (!stops.contains(current.getEnd().getId()) && !current.getEnd().id.equalsIgnoreCase(endId)) {
                pathsToTry.addAll(this.findNewPaths(current, pathsTried));
            }
            iteration++;
            if (iteration % 10000 == 0) {
                System.out.println("it " + iteration + " tried " + pathsTried.size() + " left " + pathsToTry.size());
            }
        }

        return completePaths.stream().map(DevicePath::toString).collect(Collectors.toList());
    }

    private List<DevicePath> findNewPaths(final DevicePath current, final Set<String> pathsTried) {

        final List<DevicePath> pathsToTry = new ArrayList<>();
        final List<DevicePath> contenders = this.pathsFromDevice(current);
        // equals / hashcode
        for (final DevicePath contender : contenders) {
            if (!pathsTried.contains(contender.toString())) {
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

        // 608184 is too low

        this.devices = this.buildDeviceList(input);
        this.devices.add(new Device("out", new String[0]));

        // this is essential
        this.graphViz("2025-11.2.dot", new HashMap<>());

        final Map<String, Integer> routes = new HashMap<>();

        // layer for sanity checking
        final List<String> startz = List.of("mpf");
        final List<String> endz = List.of("fft");
        final List<String> gatez = List.of("rbq", "mzw", "clx", "roj", "ikn");
        this.traceLayer(startz, endz, gatez, routes);

        // layer 1
        List<String> starts = List.of("svr");
        List<String> ends = List.of("mpf", "xqw", "ycc");
        List<String> gates = List.of(); // additional gates to stop me going too far
        this.traceLayer(starts, ends, gates, routes);

        // layer 2.1
        starts = List.of("mpf", "xqw", "ycc");
        ends = List.of("fft");
        gates = List.of("rbq", "mzw", "clx", "roj", "ikn");
        this.traceLayer(starts, ends, gates, routes);

        // layer 2.2
        starts = List.of("fft");
        ends = List.of("rbq", "mzw", "clx", "roj", "ikn");
        gates = List.of();
        this.traceLayer(starts, ends, gates, routes);

        // layer 3
        starts = List.of("rbq", "mzw", "clx", "roj", "ikn");
        ends = List.of("tol", "dkl", "pmk");
        gates = List.of();
        this.traceLayer(starts, ends, gates, routes);

        // layer 3
        starts = List.of("tol", "dkl", "pmk");
        ends = List.of("sjn", "efk", "oqa");
        gates = List.of();
        this.traceLayer(starts, ends, gates, routes);

        // layer 4.1
        starts = List.of("sjn", "efk", "oqa");
        ends = List.of("dac");
        gates = List.of("you", "clh", "zpj");
        this.traceLayer(starts, ends, gates, routes);

        // layer 4.2
        starts = List.of("dac");
        ends = List.of("you", "clh", "zpj");
        gates = List.of();
        this.traceLayer(starts, ends, gates, routes);

        // layer 5
        starts = List.of("you", "clh", "zpj");
        ends = List.of("out");
        gates = List.of();
        this.traceLayer(starts, ends, gates, routes);

        this.devices.clear();
        this.devices.addAll(this.buildSummary(routes));
        this.devices.add(new Device("out", new String[0]));
        this.graphViz("2025-11.2-summary.dot", routes);
        final List<String> paths = this.explore("svr", "out");

        long totalCost = 0;
        for (final String path : paths) {
            totalCost += this.costOfPath(path, routes);
        }

        return String.valueOf(totalCost);
    }

    private long costOfPath(final String path, final Map<String, Integer> routes) {

        long totalCost = 1;
        final String[] stops = path.split("-");
        for (int i = 0; i < stops.length - 1; i++) {
            final String section = stops[i] + "->" + stops[i + 1];
            totalCost *= routes.get(section);
        }
        return totalCost;
    }

    private List<Device> buildSummary(final Map<String, Integer> routes) {

        final Map<String, List<String>> map = new HashMap<>();

        for (final Map.Entry<String, Integer> entry : routes.entrySet()) {
            final String key = entry.getKey();
            final String[] parts = key.split("->");
            if (!map.containsKey(parts[0])) {
                map.put(parts[0], new ArrayList<>());
            }
            map.get(parts[0]).add(parts[1]);
        }

        final List<Device> devices = new ArrayList<>();
        for (final Map.Entry<String, List<String>> entry : map.entrySet()) {
            final Device device = new Device(entry.getKey(), entry.getValue().toArray(new String[0]));
            devices.add(device);
        }
        return devices;
    }

    private void traceLayer(final List<String> starts, final List<String> ends, final List<String> gates, final Map<String, Integer> routes) {

        final List<String> blocks = new ArrayList<>(ends);
        blocks.addAll(gates);
        for (final String start : starts) {
            for (final String end : ends) {
                final List<String> blocksPlusEnd = new ArrayList<>(blocks);
                blocksPlusEnd.add(end);
                final Route route = new Route(start, end, blocksPlusEnd.toArray(new String[0]));
                final List<String> path = this.exploreWithStops(route.getStart(), route.getEnd(), Arrays.asList(route.getStops()));
                routes.put(start + "->" + end, path.size());
            }
        }
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

    public static final class Route {

        private final String start;
        private final String end;
        private final String[] stops;

        public Route(final String start, final String end, final String[] stops) {
            this.start = start;
            this.end = end;
            this.stops = stops;
        }

        public String getStart() {
            return this.start;
        }

        public String getEnd() {
            return this.end;
        }

        public String[] getStops() {
            return this.stops;
        }

        @Override
        public String toString() {

            return this.start + " -> " + this.end;
        }
    }
}
