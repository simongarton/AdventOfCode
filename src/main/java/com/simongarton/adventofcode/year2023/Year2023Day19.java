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
        // isn't right
        return String.valueOf(-1);
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

    /*

    Example : in{s<5:A,R} and a max of 10

    I arrive with 1:10 1:10 1:10 1:10
    I evaluate the first rule
    I go to A with 1:10 1:10 1:10 1:4
    This means that I now have 1:10 1:10 1:10 5:10 for the next rule
    I go to R with 1:10 1:10 1:10 5:10

    Example : in{s<5:A,a>8:p,R} p{s>6:R,m<5:A,R} and a max of 10

    I arrive with 1:10 1:10 1:10 1:10
    I evaluate the first rule
    I go to A with 1:10 1:10 1:10 1:4
    This means that I now have 1:10 1:10 1:10 5:10 for the next rule
    I evaluate the second rule
    I go to p with 1:10 1:10 9:10 1:4
    This means that I now have 1:10 1:10 1:8 1:4 for the next rule
    I go to R with 1:10 1:10 1:8 1:4

    I arrive at p with 1:10 1:10 9:10 1:4
    I evaluate the first rule
    It makes no sense, s must be greater than 6 but I only have up to 4, so ... something dies.
    I evaluate the second rule
    I go to A with 1:10 1:4 9:10 1:4
    This means that I now have 1:10 5:10 9:10 1:4 for the next rule
    I go to R with 1:10 5:10 9:10 1:4

    I think.

    So I need the concept of a set of ranges;
    and I need an operation to apply a rule and get the two outcomes, pass and fail
    pass gets handed down to the next destination
    fail gets handed across to the next rule
    the operation will affect the low and may just invalidate the rule

    write some scenarios and tests.

     */

    private void buildPathMap(final Path node) {
        final Workflow workflow = this.workflowMap.get(node.getSource());
        for (final Rule rule : workflow.getRules()) {
            // this isn't valid. if I've arrived at this rule I will have
            // x * m * a * s possibilities. For each rule, I need to work out
            // how many go down that path - but then I need to subtract that from the total available.
            // the rules are sequential so I need to apply them in order.
            final Path next = Path.builder()
                    .parent(node)
                    .source(rule.getWorkflowName())
                    .destinations(new ArrayList<>())
                    .ranges(new HashMap<>()) // this isn't done
                    .build();
            node.destinations.add(next);
        }
        for (final Path destination : node.getDestinations()) {
            if (List.of("A", "R").contains(destination.getSource())) {
                this.handleEndOfLine(destination);
                continue;
            }
            this.buildPathMap(destination);
        }
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
    private static final class Rule {

        private String field;
        private String criteria;
        private int value;
        private String workflowName;
    }

    @Data
    @Builder
    private static final class Range {

        private int low;
        private int high;
        private boolean valid;
    }

    @Data
    @Builder
    private static final class Path {

        private Path parent;
        private String source;
        private List<Path> destinations;
        private Map<String, Range> ranges = new HashMap<>();
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
