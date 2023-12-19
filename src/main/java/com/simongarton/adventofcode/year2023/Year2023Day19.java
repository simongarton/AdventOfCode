package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

public class Year2023Day19 extends AdventOfCodeChallenge {

    List<Workflow> workflows;
    List<Part> parts;
    Map<String, Workflow> workflowMap;
    Path root;
    Map<String, Long> endpoints;

    @Override
    public String title() {
        return "Day 19: Aplenty";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 19);
    }

    @Override
    public String part1(final String[] input) {

        this.loadWorkflowsAndParts(input);
        int total = 0;
        for (final Part part : this.parts) {
            final boolean accepted = this.accept(part);
            if (accepted) {
                total += this.sum(part);
            }
        }
        return String.valueOf(total);
    }

    private int sum(final Part part) {
        return part.getX() + part.getM() + part.getA() + part.getS();
    }

    private boolean accept(final Part part) {

        return this.recursiveAccept("in", part);
    }

    private boolean recursiveAccept(final String in, final Part part) {
        final Workflow workflow = this.workflowMap.get(in);
        final Rule applicableRule = this.getApplicableRule(workflow, part);
        if (applicableRule.getWorkflowName().equalsIgnoreCase("A")) {
            return true;
        }
        if (applicableRule.getWorkflowName().equalsIgnoreCase("R")) {
            return false;
        }
        return this.recursiveAccept(applicableRule.getWorkflowName(), part);
    }

    private Rule getApplicableRule(final Workflow workflow, final Part part) {
        for (final Rule rule : workflow.getRules()) {
            if (this.test(rule, part)) {
                return rule;
            }
        }
        throw new RuntimeException("no applicable rules.");
    }

    private boolean test(final Rule rule, final Part part) {
        if (rule.getField() == null) {
            return true;
        }
        if (rule.getField().equalsIgnoreCase("x")) {
            return this.testX(rule, part);
        }
        if (rule.getField().equalsIgnoreCase("m")) {
            return this.testM(rule, part);
        }
        if (rule.getField().equalsIgnoreCase("a")) {
            return this.testA(rule, part);
        }
        if (rule.getField().equalsIgnoreCase("s")) {
            return this.testS(rule, part);
        }
        throw new RuntimeException("no");
    }

    private boolean testX(final Rule rule, final Part part) {
        final int value = part.getX();
        return this.testValue(rule, value, part);
    }

    private boolean testM(final Rule rule, final Part part) {
        final int value = part.getM();
        return this.testValue(rule, value, part);
    }

    private boolean testA(final Rule rule, final Part part) {
        final int value = part.getA();
        return this.testValue(rule, value, part);
    }

    private boolean testS(final Rule rule, final Part part) {
        final int value = part.getS();
        return this.testValue(rule, value, part);
    }

    private boolean testValue(final Rule rule, final int value, final Part part) {
        if (rule.getCriteria().equalsIgnoreCase("<")) {
            if (value < rule.getValue()) {
                return true;
            }
        }
        if (rule.getCriteria().equalsIgnoreCase(">")) {
            if (value > rule.getValue()) {
                return true;
            }
        }
        return false;
    }

    private void loadWorkflowsAndParts(final String[] input) {

        boolean doingParts = false;
        this.workflows = new ArrayList<>();
        this.parts = new ArrayList<>();

        for (final String line : input) {
            if (line.isEmpty()) {
                doingParts = true;
                continue;
            }
            if (doingParts) {
                this.parts.add(new Part(line));
                continue;
            }
            this.workflows.add(new Workflow(line));
        }

        this.workflowMap = new HashMap<>();
        for (final Workflow workflow : this.workflows) {
            this.workflowMap.put(workflow.getName(), workflow);
        }
    }

    @Override
    public String part2(final String[] input) {

        this.loadWorkflowsAndParts(input);
        this.endpoints = new HashMap<>();
        this.root = Path.builder()
                .parent(null)
                .source("in")
                .destinations(new ArrayList<>())
                .xLow(1)
                .xHigh(4000)
                .mLow(1)
                .mHigh(4000)
                .aLow(1)
                .aHigh(4000)
                .sLow(1)
                .sHigh(4000)
                .build();
        this.buildPathMap(this.root);
        this.debugPathMap(this.root, 0);
        System.out.println();
        for (final Map.Entry<String, Long> entry : this.endpoints.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
        final long total = 0;
        return String.valueOf(total);
    }

    private void debugPathMap(final Path node, final int i) {
        System.out.println(" ".repeat(i) + node.getSource() + " " + this.getRange(node));
        for (final Path destination : node.destinations) {
            this.debugPathMap(destination, i + 1);
        }
    }

    private String getRange(final Path node) {
        return node.getXLow() + ":" + node.getXHigh() + " " +
                node.getMLow() + ":" + node.getMHigh() + " " +
                node.getALow() + ":" + node.getAHigh() + " " +
                node.getSLow() + ":" + node.getSHigh() + " ";
    }

    private void buildPathMap(final Path node) {
        // I have arrived at this node, which will be in initially, and I have these many options
        // Work out from my rules where I can go
        final Workflow workflow = this.workflowMap.get(node.getSource());
        for (final Rule rule : workflow.getRules()) {
            final Path next = Path.builder()
                    .parent(node)
                    .source(rule.getWorkflowName())
                    .destinations(new ArrayList<>())
                    .xLow(this.figureOutLow("x", node, rule))
                    .xHigh(this.figureOutHigh("x", node, rule))
                    .mLow(this.figureOutLow("m", node, rule))
                    .mHigh(this.figureOutHigh("m", node, rule))
                    .aLow(this.figureOutLow("a", node, rule))
                    .aHigh(this.figureOutHigh("a", node, rule))
                    .sLow(this.figureOutLow("s", node, rule))
                    .sHigh(this.figureOutHigh("s", node, rule))
                    .build();
            node.destinations.add(next);
        }
        for (final Path destination : node.getDestinations()) {
            if (List.of("A", "R").contains(destination.getSource())) {
                continue;
            }
            this.buildPathMap(destination);
        }
    }

    private int figureOutLow(final String field, final Path start, final Rule rule) {
        if (List.of("A", "R").contains(rule.getWorkflowName())) {
            this.handleEndOfLine(start, rule);
            return 0;
        }
        if (rule.getField() == null) {
            return this.getLow(start, field);
        }
        if (!rule.getField().equalsIgnoreCase(field)) {
            return this.getLow(start, field);
        }
        final String criteria = rule.getCriteria();
        final int value = this.getLow(start, field);
        if (criteria.equalsIgnoreCase("<")) {
            if (rule.getValue() < value) {
                return value;
            } else {
                return rule.getValue();
            }
        }
        if (criteria.equalsIgnoreCase(">")) {
            if (rule.getValue() < value) {
                return rule.getValue();
            } else {
                return value;
            }
        }
        throw new RuntimeException("gargh!");
    }

    private int figureOutHigh(final String field, final Path start, final Rule rule) {
        if (List.of("A", "R").contains(rule.getWorkflowName())) {
            this.handleEndOfLine(start, rule);
            return 0;
        }
        if (rule.getField() == null) {
            return this.getHigh(start, field);
        }
        if (!rule.getField().equalsIgnoreCase(field)) {
            return this.getHigh(start, field);
        }
        final String criteria = rule.getCriteria();
        final int value = this.getHigh(start, field);
        if (criteria.equalsIgnoreCase(">")) {
            if (rule.getValue() < value) {
                return value;
            } else {
                return rule.getValue();
            }
        }
        if (criteria.equalsIgnoreCase("<")) {
            if (rule.getValue() < value) {
                return rule.getValue();
            } else {
                return value;
            }
        }
        throw new RuntimeException("gargh!");
    }

    private void handleEndOfLine(final Path node, final Rule rule) {
        final long result = this.recursiveTotal(node);
        // some kind of recursive thing going up from node to each parent, until parent is null.
        // where each level multiplies each of the four ranges by each other.
        this.endpoints.put(rule.workflowName, this.endpoints.getOrDefault(rule.workflowName, 0L) + result);
    }

    private long recursiveTotal(final Path node) {
        long total = this.totalForNode(node);
        if (node.parent != null) {
            total = total + this.recursiveTotal(node.parent);
        }
        return total;
    }

    private long totalForNode(final Path node) {
        final long xRange = node.getXHigh() + node.getXLow();
        final long mRange = node.getMHigh() + node.getMLow();
        final long aRange = node.getAHigh() + node.getALow();
        final long sRange = node.getSHigh() + node.getSLow();
        return xRange * mRange * aRange * sRange;
    }

    private int getLow(final Path start, final String field) {
        if (field.equalsIgnoreCase("x")) {
            return start.getXLow();
        }
        if (field.equalsIgnoreCase("m")) {
            return start.getMLow();
        }
        if (field.equalsIgnoreCase("a")) {
            return start.getALow();
        }
        if (field.equalsIgnoreCase("s")) {
            return start.getSLow();
        }
        throw new RuntimeException("urk " + field);
    }

    private int getHigh(final Path start, final String field) {
        if (field.equalsIgnoreCase("x")) {
            return start.getXHigh();
        }
        if (field.equalsIgnoreCase("m")) {
            return start.getMHigh();
        }
        if (field.equalsIgnoreCase("a")) {
            return start.getAHigh();
        }
        if (field.equalsIgnoreCase("s")) {
            return start.getSHigh();
        }
        throw new RuntimeException("urkk ! " + field);
    }


    @Data
    private static final class Workflow {

        private String name;
        private List<Rule> rules;

        public Workflow(final String data) {

            final int firstBracket = data.indexOf("{");
            this.name = data.substring(0, firstBracket);
            String ruledata = data.substring(firstBracket + 1);
            ruledata = ruledata.substring(0, ruledata.length() - 1);
            final String[] actualRules = ruledata.split(",");
            this.rules = Arrays.stream(actualRules).map(this::mapToRule).collect(Collectors.toList());

        }

        private Rule mapToRule(final String s) {
            if (s.contains("=")) {
                throw new RuntimeException("not expecting =");
            }
            if (s.contains("<")) {
                return this.mapLessThanRule(s);
            }
            if (s.contains(">")) {
                return this.mapGreaterThanRule(s);
            }
            return Rule.builder()
                    .workflowName(s)
                    .build();
        }

        private Rule mapLessThanRule(final String s) {
            final String[] parts = s.split(":");
            final String[] criteriaParts = parts[0].split("<");
            return Rule.builder()
                    .field(criteriaParts[0])
                    .criteria("<")
                    .value(Integer.parseInt(criteriaParts[1]))
                    .workflowName(parts[1])
                    .build();
        }

        private Rule mapGreaterThanRule(final String s) {
            final String[] parts = s.split(":");
            final String[] criteriaParts = parts[0].split(">");
            return Rule.builder()
                    .field(criteriaParts[0])
                    .criteria(">")
                    .value(Integer.parseInt(criteriaParts[1]))
                    .workflowName(parts[1])
                    .build();
        }
    }

    @Data
    @Builder
    private static final class Rule {

        private String field;
        private String criteria;
        private int value;
        private String workflowName;
    }

    @Data
    @Builder
    private static final class Path {

        private Path parent;
        private String source;
        private List<Path> destinations;
        private int xLow;
        private int xHigh;
        private int mLow;
        private int mHigh;
        private int aLow;
        private int aHigh;
        private int sLow;
        private int sHigh;
    }

    @Data
    private static final class Part {

        private int x;
        private int m;
        private int a;
        private int s;

        public Part(final String line) {
            final String cleanLine = line.substring(1, line.length() - 1);
            final String[] parts = cleanLine.split(",");
            this.x = Integer.parseInt(parts[0].split("=")[1]);
            this.m = Integer.parseInt(parts[1].split("=")[1]);
            this.a = Integer.parseInt(parts[2].split("=")[1]);
            this.s = Integer.parseInt(parts[3].split("=")[1]);
        }

    }
}
