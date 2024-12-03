package com.simongarton.adventofcode.year2024;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Year2024Day3Test {


    @Test
    void testRegex() {

        // given
        final String sample = "mul(1,2)";
        final String regex = "mul\\((\\d+,\\d+\\))";

        // when
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(sample);
        final boolean matchFound = matcher.find();

        // then
        assertTrue(matchFound);
    }

    @Test
    void testRegexGroup() {

        // given
        final String sample = "mul(1,2)xxxml(3,4)mul(5,6)mul(7,8]";
        final String regex = "mul\\((\\d+,\\d+)\\)*";

        // when
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(sample);

        // then
        assertTrue(matcher.find());
        // I don't like this - I can't find a second group ?
        // but apparently that's how regex works. the group is the thing(s) I'm looking for,
        // not what I found.
        assertEquals("1,2", matcher.group(1));
    }

    @Test
    void testRegexTwoGroups() {

        // given
        final String sample = "mul(1,2)xxxml(3,4)mul(5,6)mul(7,8]";
        final String regex = "mul\\((\\d+,\\d+)\\)";

        // when
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sample);
        final List<String> matches = new ArrayList<>();
        int index = 0;
        System.out.println(sample);
        while (matcher.find()) {
            matches.add(matcher.group(1));
            index = index + matcher.start() + matcher.group(1).length() + 5;
            final String restOfLine = sample.substring(index);
            matcher = pattern.matcher(restOfLine);
        }

        // then
        for (final String group : matches) {
            System.out.println("found " + group);
        }
        assertEquals(2, matches.size());
    }

    @ParameterizedTest
    @MethodSource("getStringsAndResults")
    void testManyRegexes(final String sample, final List<String> targetGroups) {

        // given
        final String regex = "mul\\((\\d+,\\d+)\\)";
        System.out.printf("%s%n", sample);

        // when
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sample);
        final List<String> matches = new ArrayList<>();
        final List<Integer> indices = new ArrayList<>();
        int index = 0;
        while (matcher.find()) {
            matches.add(matcher.group(1));
            indices.add(index);
            // index = index + matcher.start() + matcher.group(1).length() + 5;
            index = index + matcher.end();
            final String restOfLine = sample.substring(index);
            matcher = pattern.matcher(restOfLine);
        }

        // then
        assertEquals(targetGroups.size(), matches.size());
        for (int i = 0; i < targetGroups.size(); i++) {
            System.out.printf("found %s at %s%n", matches.get(i), indices.get(i));
            assertEquals(targetGroups.get(i), matches.get(i));
        }
    }

    static Stream<Arguments> getStringsAndResults() {

        return Stream.of(
                Arguments.of("mul(1,2)",
                        List.of("1,2")),
                Arguments.of("mul(1,2)xxxml(3,4)mul(5,6)mul(7,8]",
                        List.of("1,2", "5,6")),
                Arguments.of("ml(1,2)",
                        List.of())
        );
    }
}