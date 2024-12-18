package com.simongarton.adventofcode.year2024;

public class ChronoMan {

    public static void main(final String[] args) {

        /*

    Part 1 program : 2,4,1,1,7,5,4,4,1,4,0,3,5,5,3,0
    2,4 - mod A by 8, store in B.
    1,1 - XOR B and 1, store in B (mod by 10, if even add 1 else sub 1)
    7,5 - divide A by 2**B and store in C
    4,4 - XOR B and C, store in B
    1,4 - XOR B and 4, store in B
    0,3 - divide A by 2**3 (8) and store in A
    5,5 - output B
    3,0 - if A > 0, jump to 0

     */

        int a = 46337277;
        int b = 0;
        int c = 0;

        String output = "";

        while (true) {
            b = a % 8;
            b = b ^ 1;
            c = Double.valueOf(Math.floor(1.0D * a / Math.pow(2, b))).intValue();
            b = b ^ c;
            b = b ^ 4;
            a = a / 8;
            output = output + (b % 8) + ",";
            if (a > 0) {
                continue;
            }
            break;
        }

        System.out.println(output);
    }
}
