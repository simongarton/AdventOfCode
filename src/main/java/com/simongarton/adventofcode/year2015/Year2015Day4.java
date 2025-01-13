package com.simongarton.adventofcode.year2015;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Year2015Day4 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 4: The Ideal Stocking Stuffer";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2015, 4);
    }

    @Override
    public String part1(final String[] input) {

        int i = 0;
        while (true) {
            final String output = this.hash(input[0] + i);
            if (output.substring(0, 5).equalsIgnoreCase("00000")) {
                break;
            }
            i++;
        }

        return String.valueOf(i);
    }

    private String hash(final String password) {

        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            final byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String part2(final String[] input) {

        int i = 0;
        while (true) {
            final String output = this.hash(input[0] + i);
            if (output.substring(0, 6).equalsIgnoreCase("000000")) {
                break;
            }
            i++;
        }

        return String.valueOf(i);
    }
}
