package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class Year2023Day20 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;

    private Map<String, AoCModule> modules;
    @Getter
    public List<Pulse> pulses;
    private long lowPulses;
    private long highPulses;
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
        for (int i = 0; i < 1000; i++) {
            this.pressButton();
        }
        return String.valueOf(this.highPulses * this.lowPulses);
    }

    private void handlePulses() {

        while (!this.pulses.isEmpty()) {
            final Pulse pulse = this.pulses.get(0);
            this.pulses.remove(0);
            this.handlePulse(pulse);
        }
    }

    private void handlePulse(final Pulse pulse) {
        final AoCModule to = pulse.getTo();

        to.handlePulse(pulse);
    }

    private void addPulse(final Pulse pulse) {
        this.getPulses().add(pulse);
        if (pulse.getLevel() == PulseType.HIGH) {
            this.highPulses++;
        } else {
            this.lowPulses++;
        }
    }

    private void pressButton() {

        this.pulses = new ArrayList<>();
        final AoCModule from = this.button;
        final AoCModule to = this.getModule("broadcaster");
        final Pulse outgoing = Pulse.builder().from(from).to(to).level(PulseType.LOW).build();
        this.addPulse(outgoing);
        from.announceTo(outgoing);

        this.handlePulses();

        if (DEBUG) {
            System.out.println();
        }
    }

    private AoCModule getModule(final String name) {
        return this.modules.get(name);
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
                .network(this)
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
            AoCModule child = this.modules.get(childName);
            if (child == null) {
                child = this.createDefaultModule(childName);
            }
            parent.getTargets().add(child);
            if (child.getType() == ModuleType.CONJUNCTION) {
                child.getInputs().put(parent.getName(), PulseType.LOW);
            }
        }

    }

    private AoCModule createDefaultModule(final String name) {

        final AoCModule defaultModule = AoCModule.builder()
                .name(name)
                .network(this)
                .type(ModuleType.DEFAULT)
                .finishState(false)
                .build();
        this.modules.put(name, defaultModule);
        return defaultModule;
    }

    private AoCModule createModule(final String line) {
        final String[] parts = line.split(" -> ");
        final String nameAndType = parts[0];
        if (nameAndType.equalsIgnoreCase("broadcaster")) {
            final AoCModule module = AoCModule.builder()
                    .name("broadcaster")
                    .network(this)
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
                    .network(this)
                    .type(ModuleType.FLIPFLOP)
                    .targets(new ArrayList<>())
                    .state(false)
                    .build();
            return module;
        }
        if (type.equalsIgnoreCase("&")) {
            final AoCModule module = AoCModule.builder()
                    .name(name)
                    .network(this)
                    .type(ModuleType.CONJUNCTION)
                    .targets(new ArrayList<>())
                    .inputs(new HashMap<>())
                    .build();
            return module;
        }
        throw new RuntimeException("Unknown type " + type);
    }

    @Override
    public String part2(final String[] input) {

        this.loadModules(input);
        final AoCModule rx = this.getModule("rx");
        long buttonPresses = 0;
        while (true) {
            this.pressButton();
            buttonPresses++;
            if (rx.getFinishState()) {
                break;
            }

            // I am not going to get this
            break;
        }

        return String.valueOf(buttonPresses);
    }

    private enum ModuleType {
        BUTTON, BROADCAST, CONJUNCTION, FLIPFLOP, DEFAULT
    }

    private enum PulseType {
        HIGH, LOW, NONE
    }

    @Data
    @Builder
    private static final class Pulse {

        private AoCModule from;
        private AoCModule to;
        private PulseType level;
    }

    @Data
    @Builder
    private static class AoCModule {

        protected Year2023Day20 network;
        protected String name;
        protected ModuleType type;
        protected List<AoCModule> targets;

        protected Boolean state; // for flipflops
        protected Boolean finishState = false; // for rx
        protected Map<String, PulseType> inputs; // for conjunctions

        public void handlePulse(final Pulse pulse) {
            switch (this.type) {
                case BUTTON:
                    this.handleButtonPulse(pulse);
                    break;
                case BROADCAST:
                    this.handleBroadcastPulse(pulse);
                    break;
                case CONJUNCTION:
                    this.handleConjunctionPulse(pulse);
                    break;
                case FLIPFLOP:
                    this.handleFlipFlopPulse(pulse);
                    break;
                case DEFAULT:
                    this.handleDefaultPulse(pulse);
                    break;
                default:
                    throw new RuntimeException(this.type.toString());
            }
        }

        @Override
        public String toString() {
            return this.getDescription();
        }

        public String getDescription() {
            return this.name + " (" + this.type + ")";
        }

        public void handleDefaultPulse(final Pulse pulse) {

            if (pulse.getLevel() == PulseType.LOW) {
                this.setFinishState(true);
            }

            this.announceFrom(pulse);
        }

        public void handleButtonPulse(final Pulse pulse) {
            // won't ever get one
        }

        public void handleBroadcastPulse(final Pulse pulse) {

            this.announceFrom(pulse);

            for (final AoCModule target : this.getTargets()) {
                final Pulse outgoing = Pulse.builder().from(this).to(target).level(pulse.getLevel()).build();
                this.announceTo(pulse);
                this.network.addPulse(outgoing);
            }
        }

        private void announceFrom(final Pulse pulse) {
//            System.out.println(this.getDescription() + " got a " + pulse.getLevel() + " from "
//                    + pulse.getFrom().getDescription());
        }

        private void announceTo(final Pulse pulse) {
            if (DEBUG) {
                System.out.println(this.getDescription() + " sends a " + pulse.getLevel() + " to "
                        + pulse.getTo().getDescription());
            }
        }

        public void handleFlipFlopPulse(final Pulse pulse) {

            this.announceFrom(pulse);

            if (pulse.getLevel() == PulseType.HIGH) {
                return;
            }

            this.setState(!this.getState());

            if (this.getState()) {
                for (final AoCModule target : this.getTargets()) {
                    final Pulse outgoing = Pulse.builder().from(this).to(target).level(PulseType.HIGH).build();
                    this.announceTo(outgoing);
                    this.getNetwork().addPulse(outgoing);
                }
            } else {
                for (final AoCModule target : this.getTargets()) {
                    final Pulse outgoing = Pulse.builder().from(this).to(target).level(PulseType.LOW).build();
                    this.announceTo(outgoing);
                    this.getNetwork().addPulse(outgoing);
                }
            }
        }

        public void handleConjunctionPulse(final Pulse pulse) {

            this.announceFrom(pulse);

            this.getInputs().put(pulse.getFrom().getName(), pulse.getLevel());

            boolean sendLow = true;
            for (final PulseType pulseType : this.getInputs().values()) {
                if (pulseType == PulseType.LOW) {
                    sendLow = false;
                    break;
                }
            }

            final PulseType output = sendLow ? PulseType.LOW : PulseType.HIGH;
            for (final AoCModule target : this.getTargets()) {
                final Pulse outgoing = Pulse.builder().from(this).to(target).level(output).build();
                this.announceTo(outgoing);
                this.getNetwork().addPulse(outgoing);
            }
        }
    }

}
