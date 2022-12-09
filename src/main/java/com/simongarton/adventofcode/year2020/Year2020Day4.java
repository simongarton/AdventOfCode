package com.simongarton.adventofcode.year2020;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Year2020Day4 extends AdventOfCodeChallenge {

    /*
        Interesting. I could only find one part in my last code; I have then retried it - getting
        new input to deal with - and it's not giving the right answer for part 1. Need to revisit so
        I can then see what part 2 is all about.
     */

    @Override
    public boolean run() {
        return this.runChallenge(2020, 4);
    }

    @Override
    public String part1(final String[] input) {

        final List<String> lines = Arrays.asList(input);
        final List<String> section = new ArrayList<>();
        final List<Passport> passports = new ArrayList<>();
        for (final String line : lines) {
            if (line.equalsIgnoreCase("")) {
                final Passport passport = new Passport(section);
                passports.add(passport);
                section.clear();
            } else {
                section.add(line);
            }
        }
        if (section.size() > 0) {
            final Passport passport = new Passport(section);
            passports.add(passport);
            section.clear();
        }
        return String.valueOf(passports.stream().filter(Passport::valid).count());
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    @Data
    public static final class Passport {

        // could be done more efficently as a map but then supporting code gets more complex
        private String byr; // (Birth Year)
        private String iyr; // (Issue Year)
        private String eyr; // (Expiration Year)
        private String hgt; // (Height)
        private String hcl; // (Hair Color)
        private String ecl; // (Eye Color)
        private String pid; // (Passport ID)
        private String cid; // (Country ID)

        public Passport(final List<String> lines) {
            for (final String line : lines) {
                this.processLine(line);
            }
        }

        private void processLine(final String line) {
            final String[] parts = line.split(" ");
            for (final String part : parts) {
                this.processPart(part);
            }
        }

        public boolean valid() {
            if (this.byr == null || !this.byrIsValid()) {
                return false;
            }
            if (this.iyr == null || !this.iyrIsValid()) {
                return false;
            }
            if (this.eyr == null || !this.eyrIsValid()) {
                return false;
            }
            if (this.hgt == null || !this.hgtIsValid()) {
                return false;
            }
            if (this.hcl == null || !this.hclIsValid()) {
                return false;
            }
            if (this.ecl == null || !this.eclIsValid()) {
                return false;
            }
            if (this.pid == null || !this.pidIsValid()) {
                return false;
            }
            return true;
        }

        private boolean byrIsValid() {
            final int year = Integer.parseInt(this.byr);
            return (year >= 1920 && year <= 2002);
        }

        private boolean iyrIsValid() {
            final int year = Integer.parseInt(this.iyr);
            return (year >= 2010 && year <= 2020);
        }

        private boolean eyrIsValid() {
            final int year = Integer.parseInt(this.eyr);
            return (year >= 2020 && year <= 2030);
        }

        private boolean hgtIsValid() {
            if (this.hgt.contains("cm")) {
                return this.hgtIsValidCM();
            }
            if (this.hgt.contains("in")) {
                return this.hgtIsValidIN();
            }
            return false;
        }

        private boolean hgtIsValidIN() {
            final String measurement = this.hgt.replace("in", "");
            if (!this.hgt.equalsIgnoreCase(measurement + "in")) {
                return false;
            }
            final int measure = Integer.parseInt(measurement);
            return (measure >= 59 && measure <= 76);
        }

        private boolean hgtIsValidCM() {
            final String measurement = this.hgt.replace("cm", "");
            if (!this.hgt.equalsIgnoreCase(measurement + "cm")) {
                return false;
            }
            final int measure = Integer.parseInt(measurement);
            return (measure >= 150 && measure <= 193);
        }

        public boolean hclIsValid() {
            return this.hcl.matches("#[0-9a-f]{6}");
        }

        public boolean eclIsValid() {
            final String[] values = {"amb", "blu", "brn", "gry", "grn", "hzl", "oth"};
            return Arrays.asList(values).contains(this.ecl);
        }

        public boolean pidIsValid() {
            return this.pid.matches("[0-9]{9}");
        }

        private void processPart(final String part) {
            final String[] details = part.split(":");
            final String key = details[0];
            final String value = details[1];
            switch (key) {
                case "byr":
                    this.byr = value;
                    break;
                case "iyr":
                    this.iyr = value;
                    break;
                case "eyr":
                    this.eyr = value;
                    break;
                case "hgt":
                    this.hgt = value;
                    break;
                case "hcl":
                    this.hcl = value;
                    break;
                case "ecl":
                    this.ecl = value;
                    break;
                case "pid":
                    this.pid = value;
                    break;
                case "cid":
                    this.cid = value;
                    break;
            }
        }
    }
}
