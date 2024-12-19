package com.simongarton.adventofcode.year2024;

import java.util.Arrays;

public class CompiledProgram {

    public static void main(final String[] args) {

        // Part 1 program : 2,4,1,1,7,5,4,4,1,4,0,3,5,5,3,0

        /*

        I need to find 16 numbers.

        How I find these numbers is that I take the last number, and add (0..7) to it until
        I get the output digit I'm expecting - and there may be one.
        I then take that last number and shift it left - multiply it by 8 - before repeating the process.

        Will it work without BFS or do I have to do it smartly ? It does not appear to work without BFS,
        I seem to get negatives (so I haven't set a value)

         */

        final CompiledProgram compiledProgram = new CompiledProgram();

        final int[] ratchet = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        final int[] target = new int[]{2, 4, 1, 1, 7, 5, 4, 4, 1, 4, 0, 3, 5, 5, 3, 0};
        final int[] torget = new int[]{2, 2, 4, 1, 1, 7, 2, 5, 5, 1, 4, 0, 3, 5, 5, 3, 0,};
        int checkingIndex = 15;
        long working = 0;

        while (checkingIndex >= 0) {
            int bestI = -2;
            for (int i = 0; i < 7; i++) {
                final long a = working + i;
                final String singleShot = compiledProgram.singleShot(a);
//                System.out.println("solvedIndex " + checkingIndex + " i " + i + " a " + a + singleShot);
                final int hit = Integer.parseInt(singleShot.substring(0, 1));
                if (hit == target[checkingIndex]) {
                    System.out.println(" hit target " + hit + " at solvedIndex " + checkingIndex + " i " + i + " a " + a + singleShot);
                    working = a;
                    bestI = i;
                    break;
                }
            }
            ratchet[checkingIndex] = bestI;
            checkingIndex--;
            System.out.println(compiledProgram.run(working) + " " + Arrays.toString(ratchet));
            working = working << 3;
        }

        System.out.println("All done : " + working);
        System.out.println(compiledProgram.run(working));
        System.out.println(Arrays.toString(ratchet));


    }

//
//    final int a0 = 5;
//    final int a1 = 6; // becomes 46
//    final int a2 = 0; // 0 or 1, becomes 368 or 369
//    final int a3 = 0; // 0 or 1 or 4, becomes 2944 or 2945 or 2948
//    final int a0d = a0 << 3;
//    final int a1d = 46 << 3;
//    final int a2d = 368 << 3;
//        for (int i = 0; i < 7; i++) {
//        final int a = a2d + i;
//        System.out.println("with a = " + a + " i " + i + " i got " + chronoMan.singleShot(a));
//    }

    private static String buildOctal(final int[] ratchet, final int solvedIndex) {
        return null;
    }

    private static void thingy3() {

        final int a = 12;
        final int b = a << 3;
        System.out.println(a + " " + b);


    }

    private static void thingy2() {

        for (int k = 0; k < 8; k++) {
            for (int x = 0; x < 10; x++) {
                System.out.println("i " + x + " k " + k + " f(8x + k) " + (8 * x + k) + " x " + x);
            }
        }
    }

    private static void thingy() {

        // i^1 will add 1 if it's even, subtract one if it's odd. So I can reverse it
        // i^4 will add 4 if it's mod is less than 4, otherwise subtract it. So I can reverse it.

        for (int i = 0; i < 20; i++) {
            System.out.println(i + "^4 = " + (i ^ 4));
        }
    }
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

        /*

        So for part 2 I want to generate the program. Which in turn means I need to generate a 2 first of all
        How do I backtrack with my commands ?

        b must be a multiple of 8 + 2 ... because if I mod it by 8, I get 2.
        before that b was XORed against 4
        before that b was XORed against c
        c was set to be a divided by 2**b
        before that b was XORed against 1
        before that b was the remainder when a is modded by 8.

         */

        /* new approach.

        I know I need to emit 2,4,1,1,7,5,4,4,1,4,0,3,5,5,3,0

        Can I work backwards so on the last iteration, I can figure out what a was  ?
        a must be 0 on the last iteration to leave, so must have been between 1 and 7,
        and b must have been % 8 = 0.
        which means I need to reverse XOR (?!) b by 4, then c (I calculate c) then by 1
        to then get a which would have been a multiple of 8

         */

    public String run(long a) {

//        int a = 46337277;
//        int a = 7;
        long b = 0;
        long c = 0;

        final StringBuilder output = new StringBuilder();

        while (true) {
            b = a % 8;
            b = b ^ 1;
            c = Double.valueOf(Math.floor(1.0D * a / Math.pow(2, b))).intValue();
            b = b ^ c;
            b = b ^ 4;
            a = a / 8;
            output.append(b % 8).append(",");
            if (a > 0) {
                continue;
            }
            break;
        }

        return output.toString();
    }

    public String singleShot(long a) {

        long b = 0;
        long c = 0;

        final StringBuilder output = new StringBuilder();

        b = a % 8;
        b = b ^ 1;
        c = Double.valueOf(Math.floor(1.0D * a / Math.pow(2, b))).intValue();
        b = b ^ c;
        b = b ^ 4;
        a = a / 8;
        output.append(b % 8).append(",");

//        System.out.println("a " + a + " b " + b + " c " + " out " + output);
        return output.toString();
    }
}
