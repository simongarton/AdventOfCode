package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.year2024.day21.DirectionalKeypad;
import com.simongarton.adventofcode.year2024.day21.Keypad;
import com.simongarton.adventofcode.year2024.day21.NumericKeypad;
import com.simongarton.adventofcode.year2024.day21.Program;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Year2024Day21FiguringOutProblem extends AdventOfCodeChallenge {

    // there's a sneaky shortcut here where I could go back to A on each pad after each key press so I know where I am
    // but the question rules it out.

    // What I need to do is translate things ... so instead of explicitly typing out a sequence, I say what I want to happen.

    public final static String UP = "^";
    public final static String RIGHT = ">";
    public final static String LEFT = "<";
    public final static String DOWN = "v";
    public final static String ACTIVATE = "A";

    @Override
    public String title() {
        return "Day 21: Keypad Conundrum";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 21);
    }

    @Override
    public String part1(final String[] input) {

        /* I want to press 1.

        I need to set up my keypads and then ask each of them what needs to be pressed.

         */

        final NumericKeypad main = new NumericKeypad("main");
        final DirectionalKeypad robot1 = new DirectionalKeypad("robot1", null, main);
        final DirectionalKeypad robot2 = new DirectionalKeypad("robot2", robot1, null);
        final DirectionalKeypad robot3 = new DirectionalKeypad("robot3", robot2, null);

        final Map<Keypad, String> status = new HashMap<>();
        status.put(main, "A");
        status.put(robot1, "A");
        status.put(robot2, "A");
        status.put(robot3, "A");


        // I want to press a 0
        final Program program = main.getProgramFor(List.of("0"), status);

        return null;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

}
