package com.simongarton.adventofcode;

import com.simongarton.adventofcode.year2018.Year2018Day1;

public class CurrentChallenge {

    public static String DAY = "1";
    public static int YEAR = 2018;

    public static AdventOfCodeChallenge getCurrentChallenge() {

        return new Year2018Day1();
    }
}
