package com.simongarton.adventofcode;

import com.simongarton.adventofcode.year2015.Year2015Day1;

public class CurrentChallenge {

    public static String DAY = "Day1";
    public static int YEAR = 2015;

    public static AdventOfCodeChallenge getCurrentChallenge() {

        return new Year2015Day1();
    }
}
