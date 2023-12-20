package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

public class Year2023Day20 extends AdventOfCodeChallenge {

    /*

    Flip-flop modules (prefix %) are either on or off; they are initially off. If a flip-flop module receives a high
    pulse, it is ignored and nothing happens. However, if a flip-flop module receives a low pulse, it flips between
    on and off. If it was off, it turns on and sends a high pulse. If it was on, it turns off and sends a low pulse.

    Conjunction modules (prefix &) remember the type of the most recent pulse received from each of their connected
    input modules; they initially default to remembering a low pulse for each input. When a pulse is received, the
    conjunction module first updates its memory for that input. Then, if it remembers high pulses for all inputs,
    it sends a low pulse; otherwise, it sends a high pulse.

    How can the second one build up a set of inputs, but the first one is changing on each pulse ? Can flip flops only
    have one input ?

     */

    private Map<String, AoCModule> modules;
    private AoCModule button;

    @Override
    public String title() {
        return "Day 20: Pulse Propagation";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 20);
    }

    @Override
    public String part1(final String[] input) {

        this.loadModules(input);
        this.pulse();
        return String.valueOf(0);
    }

    private void pulse() {

        final List<AoCModule> modulesToProcess = new ArrayList<>();

        List<AoCModule> affectedModules = this.button.pulse(false);
        modulesToProcess.addAll(affectedModules);

        while (!modulesToProcess.isEmpty()) {
            final AoCModule currentModule = modulesToProcess.get(0);
            System.out.println("working on " + currentModule.getName() + " : " + modulesToProcess.stream().map(AoCModule::getDetails).collect(Collectors.joining(",")));
            modulesToProcess.remove(0);
            affectedModules = currentModule.pulse();
            modulesToProcess.addAll(affectedModules);
        }
    }

    private void loadModules(final String[] input) {

        this.modules = new HashMap<>();
        for (final String line : input) {
            final AoCModule module = this.createModule(line);
            this.modules.put(module.getName(), module);
        }

        for (final String line : input) {
            this.fixUpDependencies(line);
        }

        this.button = AoCModule.builder()
                .name("button")
                .type(ModuleType.BUTTON)
                .targets(List.of(this.modules.get("broadcaster")))
                .build();
    }

    private void fixUpDependencies(final String line) {
        final String[] parts = line.split(" -> ");
        final String name = parts[0].replace("%", "").replace("&", "");
        final List<String> children = Arrays.stream(parts[1].split(",")).map(String::trim).collect(Collectors.toList());
        final AoCModule parent = this.modules.get(name);
        for (final String childName : children) {
            final AoCModule child = this.modules.get(childName);
            parent.getTargets().add(child);
        }
    }

    private AoCModule createModule(final String line) {
        final String[] parts = line.split(" -> ");
        final String nameAndType = parts[0];
        if (nameAndType.equalsIgnoreCase("broadcaster")) {
            final AoCModule module = AoCModule.builder()
                    .name(nameAndType)
                    .type(ModuleType.BROADCAST)
                    .targets(new ArrayList<>())
                    .build();
            return module;
        }
        final String type = nameAndType.substring(0, 1);
        final String name = nameAndType.substring(1);
        if (type.equalsIgnoreCase("%")) {
            final AoCModule module = AoCModule.builder()
                    .name(name)
                    .type(ModuleType.FLIPFLOP)
                    .targets(new ArrayList<>())
                    .inputs(new ArrayList<>())
                    .sources(new ArrayList<>())
                    .state(false)
                    .build();
            return module;
        }
        if (type.equalsIgnoreCase("&")) {
            final AoCModule module = AoCModule.builder()
                    .name(name)
                    .type(ModuleType.CONJUNCTION)
                    .targets(new ArrayList<>())
                    .inputs(new ArrayList<>())
                    .sources(new ArrayList<>())
                    .inputMap(new HashMap<>())
                    .build();
            return module;
        }
        throw new RuntimeException("Unknown type " + type);
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    private enum ModuleType {
        BUTTON, BROADCAST, CONJUNCTION, FLIPFLOP
    }

    private enum SignalType {
        HIGH, LOW, NONE
    }


    @Data
    @Builder
    private static final class Cable {

        private AoCModule from;
        private AoCModule to;
        private SignalType signal;
    }

    @Data
    @Builder
    private static final class AoCModule {

        private String name;
        private ModuleType type;
        private List<Cable> incomingCables;
        private List<Cable> outgoingCables;
        private Boolean state;
        private List<AoCModule> targets;

        public void pulse(final SignalType signal) {
            System.out.println(this.name + " (" + this.type + ") pulse with " + signal);
            for (final Cable outgoing : this.outgoingCables) {
                outgoing.setSignal(signal);
            }
        }

        public void doBroadcast() {
            System.out.println(this.name + " (" + this.type + ") + doBroadcast()");
            if (this.getIncomingCables().size() != 1) {
                throw new RuntimeException("broadcast has " + this.getIncomingCables().size() + " incoming cables.");
            }
            final SignalType signal = this.incomingCables.get(0).getSignal();
            for (final Cable outgoing : this.outgoingCables) {
                outgoing.setSignal(signal);
            }
        }

        public void doFlipFlop() {
            System.out.println(this.name + " (" + this.type + ") flipFlop()");
            for (final Cable outgoing : this.outgoingCables) {
            }
        }

        public void doConjunction() {
            System.out.println(this.name + " (" + this.type + ") flipFlop()");
            for (final Cable outgoing : this.outgoingCables) {
            }
        }

        public void pulse() {
            switch (this.type) {
                case BUTTON:
                    throw new RuntimeException("shouldn't get here.");
                case BROADCAST:
                    this.doBroadcast();
                    break;
                case CONJUNCTION:
                    this.doConjunction();
                    break;
                case FLIPFLOP:
                    this.doFlipFlop();
                    break;
                default:
                    throw new RuntimeException("no option");
            }
        }

        public String getDetails() {
            return this.name;
        }
    }
}
