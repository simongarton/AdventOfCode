package com.simongarton.adventofcode.year2020;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.Arrays;
import java.util.List;

public class Year2020Day13 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 13: Shuttle Search";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2020, 13);
    }

    @Override
    public String part1(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        return this.busAndTime(lines);
    }

    @Override
    public String part2(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        return this.periodicity(lines);
    }

    /*
     naive approach, works for small file
     to speed it up I could figure out the biggest interval and loop through that, with
     a little bit of adjustment, and that worked for small files, but still takes too long.
     */

    private String magicTime(final List<String> lines) {
        final String[] busIds = lines.get(1).split(",");
        int timestamp = 0;
        while (true) {
            boolean failed = false;
            for (int i = 0; i < busIds.length; i++) {
                final String busId = busIds[i];
                if (busId.equalsIgnoreCase("x")) {
                    continue;
                }
                if ((timestamp + i) % Integer.parseInt(busId) != 0) {
                    failed = true;
                    break;
                }
            }
            if (!failed) {
                return String.valueOf(timestamp);
            }
            timestamp++;
        }
    }

    /*
     simply translated from SilverDrake11 at https://www.reddit.com/r/adventofcode/comments/kc4njx/2020_day_13_solutions/ 'cos my maths skills weren't up to it.
     Given that the buses have a stagger based on their ID, we're actually looking for a schedule where all the buses start on the same day
     and the stagger is a bit of a red herring.
     How this works is that I move forward in time 1 day at a time until I have the first bus leaving (say 7) ... any further solutions involving this bus will
     involve a multiple of 7, so I now move forward 7 days at a time until I get the second bus as well (say 13). Given they are all primes (!) this is (probably ?)
     an LCM = 7 * 13 = 91 days. I've now got 2 buses leaving in step every 91 days, and I keep adding 91 days to the timestamp until I can add the third bus.
     Repeat as necessary.
     */

    private String periodicity(final List<String> lines) {
        final String[] busIds = lines.get(1).split(",");
        long timestamp = 1;
        long waitTime = 1;
        for (int i = 0; i < busIds.length; i++) {
            final String busId = busIds[i];
            if (busId.equalsIgnoreCase("x")) {
                continue;
            }
            final long busMinutes = Long.parseLong(busId);
            while (true) {
                if ((timestamp + i) % busMinutes == 0) {
                    waitTime *= busMinutes;
                    break;
                }
                timestamp += waitTime;
            }
        }
        return String.valueOf(timestamp);
    }

    private String busAndTime(final List<String> lines) {
        final int leaveTime = Integer.parseInt(lines.get(0));
        final String[] busIds = lines.get(1).split(",");
        int timestamp = 0;
        while (true) {
            String departingBus = "";
            for (final String busId : busIds) {
                if (busId.equalsIgnoreCase("x")) {
                    continue;
                }
                if (timestamp % Integer.parseInt(busId) == 0) {
                    departingBus = busId;
                }
            }
            if (timestamp >= leaveTime && !departingBus.equalsIgnoreCase("")) {
                final int wait = timestamp - leaveTime;
                final int magic = Integer.parseInt(departingBus) * wait;
                return String.valueOf(magic);
            }
            timestamp++;
        }
    }
}
