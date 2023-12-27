package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

public class Year2023Day19 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = false;
    private static final int MAX = 10;

    private List<Workflow> workflows;
    private List<Part> parts;
    private Map<String, Workflow> workflowMap;
    private Path root;
    private Map<String, Long> endpoints;

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
                .ranges(this.rootRanges())
                .build();
        this.buildPathMap(this.root);
        if (DEBUG) {
            this.debugPathMap(this.root, 0);
            System.out.println();
            for (final Map.Entry<String, Long> entry : this.endpoints.entrySet()) {
                System.out.println(entry.getKey() + "=" + entry.getValue());
            }
        }
        final long total = this.endpoints.get("A");
        return String.valueOf(total);
    }

    private Map<String, Range> rootRanges() {
        final Map<String, Range> rangeMap = new HashMap<>();
        rangeMap.put("x", Range.builder().low(1).high(MAX).valid(true).build());
        rangeMap.put("m", Range.builder().low(1).high(MAX).valid(true).build());
        rangeMap.put("a", Range.builder().low(1).high(MAX).valid(true).build());
        rangeMap.put("s", Range.builder().low(1).high(MAX).valid(true).build());
        return rangeMap;
    }

    private void debugPathMap(final Path node, final int i) {
        System.out.println(" ".repeat(i) + node.getSource() + " " + this.displayRange(node));
        for (final Path destination : node.destinations) {
            this.debugPathMap(destination, i + 1);
        }
    }

    private String displayRange(final Path node) {
        final StringBuilder display = new StringBuilder();
        for (final String field : List.of("x", "m", "a", "s")) {
            final Optional<Range> optionalRange = this.getRange(node, field);
            if (optionalRange.isEmpty()) {
                display.append("-:-" + " ");
            } else {
                final Range range = optionalRange.get();
                display.append(range.getLow()).append(":").append(range.getHigh());
            }
            display.append(" ");
        }
        return display.toString().trim();
    }

    private Optional<Range> getRange(final Path node, final String field) {
        if (!node.getRanges().containsKey(field)) {
            return Optional.empty();
        }
        final Range range = node.getRanges().get(field);
        if (range.isValid()) {
            return Optional.of(range);
        } else {
            return Optional.empty();
        }
    }

    private void buildPathMap(final Path node) {

        final Workflow workflow = this.workflowMap.get(node.getSource());
        final List<Rule> rules = workflow.getRules();
        this.recursivelyAllocateStuff(rules, node, node.getRanges());
    }

    private void recursivelyAllocateStuff(final List<Rule> rules, final Path node, final Map<String, Range> ranges) {
        if (rules.isEmpty()) {
            return;
        }
        final Rule rule = rules.remove(0);
//        System.out.println("recursing on " + node.getSource() + " with rule " + rule.getWorkflowName() + " and have " + rules.size() + " rules left.");
        if (rules.isEmpty()) {
            final Path next = Path.builder()
                    .parent(node)
                    .source(rule.getWorkflowName())
                    .destinations(new ArrayList<>())
                    .ranges(ranges)
                    .build();
//            System.out.println("Built " + next.getSource() + " added to " + node.getSource() + " and leaving.");
            node.destinations.add(next);
            return;
        }
        final Map<String, Range> pass = this.figureOutPassFail(rule, node, true);
        final Map<String, Range> fail = this.figureOutPassFail(rule, node, false);
        final Path next = Path.builder()
                .parent(node)
                .source(rule.getWorkflowName())
                .destinations(new ArrayList<>())
                .ranges(pass)
                .build();
//        System.out.println("Built " + next.getSource() + " added to " + node.getSource() + " and continuing.");
        node.destinations.add(next);
        this.recursivelyAllocateStuff(rules, node, fail);

        for (final Path destination : node.getDestinations()) {
            if (destination.getSource().equalsIgnoreCase("A")) {
                this.handleEndOfLine(destination);
                return;
            }
            if (destination.getSource().equalsIgnoreCase("R")) {
                this.handleEndOfLine(destination);
                return;
            }
            final Workflow workflow = this.workflowMap.get(destination.getSource());
            if (workflow == null) {
                throw new RuntimeException("No workflow for " + destination.getSource());
            }
            if (workflow.getRules() == null) {
                throw new RuntimeException("No rules for " + workflow.getName());
            }
            final List<Rule> moreRules = workflow.getRules();
            this.recursivelyAllocateStuff(moreRules, destination, destination.getRanges());
        }
    }

    public Map<String, Range> figureOutPassFail(final Rule rule, final Path node, final boolean pass) {
        final Map<String, Range> newRange = this.copyRangesFromNode(node);
        newRange.put("x", this.passFailRule("x", rule, newRange.get("x"), pass));
        newRange.put("m", this.passFailRule("m", rule, newRange.get("m"), pass));
        newRange.put("a", this.passFailRule("a", rule, newRange.get("a"), pass));
        newRange.put("s", this.passFailRule("s", rule, newRange.get("s"), pass));
        return newRange;
    }

    private Map<String, Range> copyRangesFromNode(final Path node) {
        final Map<String, Range> newRangeMap = new HashMap<>();
        for (final Map.Entry<String, Range> entry : node.getRanges().entrySet()) {
            final Range newRange = Range.builder()
                    .low(entry.getValue().getLow())
                    .high(entry.getValue().getHigh())
                    .valid(entry.getValue().isValid())
                    .build();
            newRangeMap.put(entry.getKey(), newRange);
        }
        return newRangeMap;
    }


    private Range passFailRule(final String field, final Rule rule, final Range namedRange, final boolean pass) {
        final String ruleField = rule.getField();
        if (!field.equalsIgnoreCase(ruleField)) {
            return namedRange;
        }
        if (pass) {
            return this.passRule(rule, namedRange);
        } else {
            return this.failRule(rule, namedRange);
        }
    }

    private Range passRule(final Rule rule, final Range namedRange) {
        if (rule.getCriteria().equalsIgnoreCase("<")) {
            if (namedRange.getLow() >= rule.getValue()) {
                namedRange.setLow(rule.getValue() - 1);
            }
            if (namedRange.getHigh() >= rule.getValue()) {
                namedRange.setHigh(rule.getValue() - 1);
            }
            return namedRange;
        }
        if (namedRange.getLow() <= rule.getValue()) {
            namedRange.setLow(rule.getValue() + 1);
        }
        if (namedRange.getHigh() <= rule.getValue()) {
            namedRange.setHigh(rule.getValue() + 1);
        }
        return namedRange;
    }

    private Range failRule(final Rule rule, final Range namedRange) {
        if (rule.getCriteria().equalsIgnoreCase(">")) {
            if (namedRange.getLow() <= rule.getValue()) {
                namedRange.setLow(rule.getValue());
            }
            if (namedRange.getHigh() <= rule.getValue()) {
                namedRange.setHigh(rule.getValue());
            }
            return namedRange;
        }
        if (namedRange.getLow() >= rule.getValue()) {
            namedRange.setLow(rule.getValue());
        }
        if (namedRange.getHigh() >= rule.getValue()) {
            namedRange.setHigh(rule.getValue());
        }
        return namedRange;
    }

    private void handleEndOfLine(final Path node) {
        final long result = this.totalForNode(node);
        // some kind of recursive thing going up from node to each parent, until parent is null.
        // where each level multiplies each of the four ranges by each other.
        this.endpoints.put(node.getSource(), this.endpoints.getOrDefault(node.getSource(), 0L) + result);
    }

    // I can't remember why I thought I needed to do this
    private long recursiveTotal(final Path node) {
        long total = this.totalForNode(node);
        if (node.parent != null) {
            total = total + this.recursiveTotal(node.parent);
        }
        return total;
    }

    private long totalForNode(final Path node) {

        // +1 because range is inclusive.
        long total = 1;

        for (final String field : List.of("x", "m", "a", "s")) {
            final Optional<Range> optionalRange = this.getRange(node, field);
            if (optionalRange.isEmpty()) {
                return 0;
            } else {
                final Range range = optionalRange.get();
                final long thisRange = 1 + range.getHigh() - range.getLow();
                total = total * thisRange;
            }
        }
        return total;
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
    public static final class Rule {

        private String field;
        private String criteria;
        private int value;
        private String workflowName;
    }

    @Data
    @Builder
    public static final class Range {

        private int low;
        private int high;
        private boolean valid;
    }

    @Data
    @Builder
    public static final class Path {

        private Path parent;
        private String source;
        private List<Path> destinations;
        private Map<String, Range> ranges;

        @Override
        public String toString() {
            return "Path {" +
                    "parent=" + this.parent +
                    ", source=" + this.source +
                    ", destinations=" + this.destinations.size() +
                    ", ranges=" + this.ranges.size() +
                    "}";
        }
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
