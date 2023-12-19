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
        return null;
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
