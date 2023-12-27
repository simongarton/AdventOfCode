package com.simongarton.adventofcode.year2023;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class Year2023Day19Test {

    @Test
    void figureOutPassFail() {

        // given
        final Year2023Day19 year2023Day19 = new Year2023Day19();
        final Year2023Day19.Rule rule = Year2023Day19.Rule.builder()
                .field("x")
                .criteria(">")
                .value(5)
                .build();
        final Map<String, Year2023Day19.Range> ranges = new HashMap<>();
        ranges.put("x", Year2023Day19.Range.builder().low(0).high(10).build());
        ranges.put("m", Year2023Day19.Range.builder().low(0).high(10).build());
        ranges.put("a", Year2023Day19.Range.builder().low(0).high(10).build());
        ranges.put("s", Year2023Day19.Range.builder().low(0).high(10).build());
        final Year2023Day19.Path path = Year2023Day19.Path.builder()
                .ranges(ranges)
                .build();

        // when
        final Map<String, Year2023Day19.Range> passRanges = year2023Day19.figureOutPassFail(rule, path, true);
        final Map<String, Year2023Day19.Range> failRanges = year2023Day19.figureOutPassFail(rule, path, false);

        // then
        for (final Map.Entry<String, Year2023Day19.Range> entry : passRanges.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        System.out.println("");
        for (final Map.Entry<String, Year2023Day19.Range> entry : failRanges.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }
}