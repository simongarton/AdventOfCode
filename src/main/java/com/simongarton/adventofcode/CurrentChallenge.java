package com.simongarton.adventofcode;

import com.simongarton.adventofcode.year2016.Year2016Day2;

public class CurrentChallenge {

    public static String DAY = "2";
    public static int YEAR = 2016;

    public static AdventOfCodeChallenge getCurrentChallenge() {

        return new Year2016Day2();
    }
}
