package com.simongarton.adventofcode;

import com.simongarton.adventofcode.year2025.Year2025Day1;

public class CurrentChallenge {

    public static String DAY = "1";
    public static int YEAR = 2025;

    public static AdventOfCodeChallenge getCurrentChallenge() {

        return new Year2025Day1();
    }
}
