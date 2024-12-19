package com.simongarton.adventofcode.year2024;

public class CompiledProgram {

    public String run(long a) {

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

        return output.toString();
    }
}
