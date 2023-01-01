package com.simongarton.adventofcode.year2020;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Data;

import java.util.*;

public class Year2020Day7 extends AdventOfCodeChallenge {

    private final List<Rule> rules = new ArrayList<>();

    @Override
    public String title() {
        return "Day 7: Handy Haversacks";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2020, 7);
    }

    @Override
    public String part1(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        this.loadRules(lines);
        return String.valueOf(this.canHold("shiny gold"));
    }

    @Override
    public String part2(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        this.loadRules(lines);
        return String.valueOf(this.holds("shiny gold"));
    }

    private long canHold(final String type) {
        return this.rules.stream().filter(r -> r.canHold(type, 0)).count();
    }

    private int holds(final String type) {
        return this.getOrCreateRule(type).holdsOtherBags();
    }

    private void loadRules(final List<String> lines) {
        lines.forEach(this::addRule);
        lines.forEach(this::configureRule);
    }

    private void configureRule(final String line) {
        final String working = line.replace("bags", "")
                .replace("bag", "")
                .replace(".", "")
                .trim();
        final String[] parts = working.split("contain ");
        final String type = parts[0].trim();
        final String[] contains = parts[1].split(",");
        final Rule outer = this.getOrCreateRule(type);
        for (final String contain : contains) {
            final String trimContain = contain.trim();
            if (trimContain.equalsIgnoreCase("no other")) {
                continue;
            }
            // assuming none > 9
            final int count = Integer.parseInt(trimContain.substring(0, 1));
            final String containType = trimContain.substring(2);
            final Rule inner = this.getOrCreateRule(containType);
            outer.addCanContain(inner, count);
        }
    }

    private void addRule(final String line) {
        final String working = line.replace("bags", "")
                .replace("bag", "")
                .replace(".", "")
                .trim();
        final String[] parts = working.split("contain ");
        final String type = parts[0].trim();
        this.getOrCreateRule(type);
    }

    private Rule getOrCreateRule(final String type) {
        final Optional<Rule> optionalRule = this.rules.stream().filter(r -> r.getType().equalsIgnoreCase(type)).findFirst();
        if (optionalRule.isPresent()) {
            return optionalRule.get();
        }
        final Rule rule = new Rule(type);
        this.rules.add(rule);
        return rule;
    }

    @Data
    public static class Rule {
        private String type;
        private Map<Rule, Integer> canContain = new HashMap<>();

        public Rule(final String type) {
            this.type = type;
        }

        public void addCanContain(final Rule rule, final int count) {
            this.canContain.put(rule, count);
        }

        public boolean canHold(final String bagType, final int level) {
            if (bagType.equalsIgnoreCase(this.type) && level > 0) {
                return true;
            }
            for (final Map.Entry<Rule, Integer> entry : this.canContain.entrySet()) {
                if (entry.getKey().canHold(bagType, level + 1)) {
                    return true;
                }
            }
            return false;
        }

        public int holdsOtherBags() {
            int holds = 0;
            for (final Map.Entry<Rule, Integer> entry : this.canContain.entrySet()) {
                holds = holds + entry.getValue() + entry.getValue() * entry.getKey().holdsOtherBags();
            }
            return holds;
        }
    }
}
