package com.simongarton.adventofcode;

import com.simongarton.adventofcode.year2015.Year2015Day2;

public class CurrentChallenge {

    public static String DAY = "2";
    public static int YEAR = 2015;

    public static AdventOfCodeChallenge getCurrentChallenge() {

        return new Year2015Day2();
    }
}
