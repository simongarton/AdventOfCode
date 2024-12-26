package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

public class Year2024Day21Alternate extends AdventOfCodeChallenge {

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

        int total = 0;
        for (final String numericCode : input) {
            final String fullSequence = this.fullSequence(numericCode);
            final int numericPart = Integer.parseInt(numericCode.replace("A", ""));
            total += numericPart * fullSequence.length();
        }
        return String.valueOf(total);
    }

    private String fullSequence(final String numericCode) {

        /*

         OK, my original approach looks super good but gets directions in the wrong order - right number, and the sample
         works OK (different program, right number) but I get a too high. So I must be taking the wrong path.

         Debugging it is proving difficult as it's on multiple levels.

         The directions on the Numpad are reasonably straightforward. I need to make a decision about going left before going up
         or not - I don't think diagonals are valid.

         I can break any move down into a series of up/down/left/right/activate.
         For a given letter, I can drive that from a dirpad, by creating a series of up/down/left/right/activate -
         and so on.

         The only things I need to be aware of is that (a) I have dead spots to avoid, and (b) I want to avoid diagonals.

         I think there is a trick in that each dirpaid controlling another one needs to return to A each time it wants to do
         anything on it's child.

         Why would left be worse than up ? On my first Dirpad, going from A to < is 3, A to v is 2, and the other two are both 1
         (and then same again in reverse to activate.)

         But on the second dirpad ... no I still can't get my head around it.


         */

        return null;
    }

    @Override
    public String part2(final String[] input) {

        return null;
    }
}
