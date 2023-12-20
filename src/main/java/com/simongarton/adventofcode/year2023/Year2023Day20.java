package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

public class Year2023Day20 extends AdventOfCodeChallenge {

    // I think I need to stack the inputs. I can see b being put on the list twice, but the first one removes the input

    private Map<String, AoCModule> modules;
    private AoCModule button;

    @Override
    public String title() {
        return "Day 20: Template code";
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
                .inputs(new ArrayList<>())
                .sources(new ArrayList<>())
                .build();
        this.button.inputs.add(false);
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
                    .inputs(new ArrayList<>())
                    .sources(new ArrayList<>())
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

    @Data
    @Builder
    private static final class AoCModule {

        private String name;
        private ModuleType type;
        private List<Boolean> inputs;
        private List<String> sources;
        private Boolean state;
        private Map<String, Boolean> inputMap;
        private List<AoCModule> targets;

        public List<AoCModule> pulse(final boolean highPulse) {
            System.out.println(this.name + " (" + this.type + ") got " + highPulse);
            for (final AoCModule target : this.targets) {
                target.setInputAndSource(highPulse, this);
                System.out.println("  " + target.name + " (" + target.type + ") was sent " + highPulse);
            }
            this.inputs.remove(0);
            return this.targets;
        }

        public List<AoCModule> pulse() {
            switch (this.type) {
                case BUTTON:
                    throw new RuntimeException("shouldn't get here.");
                case BROADCAST:
                    return this.doBroadcast();
                case CONJUNCTION:
                    return this.doConjunction();
                case FLIPFLOP:
                    return this.doFlipFlop();
                default:
                    throw new RuntimeException("no option");
            }
        }

        private List<AoCModule> doBroadcast() {
            System.out.println(this.name + " (" + this.type + ") got " + this.inputs);
            for (final AoCModule target : this.targets) {
                target.setInputAndSource(this.inputs.get(0), this);
                System.out.println("  " + target.name + " (" + target.type + ") was sent " + this.inputs);
            }
            this.inputs.remove(0);
            return this.targets;
        }

        private void setInputAndSource(final Boolean input, final AoCModule source) {
            this.inputs.add(input);
            this.sources.add(source.getName());
        }

        private List<AoCModule> doFlipFlop() {
            System.out.println(this.name + " (" + this.type + ") got " + this.inputs);
            if (this.inputs.get(0)) {
                // do nothing on high pulse
                this.inputs.remove(0);
                return new ArrayList<>();
            }
            this.state = !this.state;
            for (final AoCModule target : this.targets) {
                target.setInputAndSource(this.state, this);
                System.out.println("  " + target.name + " (" + target.type + ") was sent " + this.state);
            }
            this.inputs.remove(0);
            return this.targets;
        }

        private List<AoCModule> doConjunction() {
            // I'm worried there's a timing issue here
            // they remember the type of the most recent pulse received from each of their connected input modules;
            // but when I'm doing the pulse method, is that correct ?
            System.out.println(this.name + " (" + this.type + ") got " + this.inputs);
            this.inputMap.put(this.sources.get(0), this.inputs.get(0));
            boolean output = true;
            for (final boolean input : this.inputMap.values()) {
                if (!input) {
                    output = false;
                    break;
                }
            }
            for (final AoCModule target : this.targets) {
                target.setInputAndSource(this.state, this);
                System.out.println("  " + target.name + " (" + target.type + ") was sent " + output);
            }
            this.inputs.remove(0);
            this.sources.remove(0);
            return this.targets;
        }

        public String getDetails() {
            return this.name;
        }
    }
}
