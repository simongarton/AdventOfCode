package com.simongarton.adventofcode.year2020;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Data;
import lombok.Getter;

import java.util.*;

public class Year2020Day14 extends AdventOfCodeChallenge {

    @Override
    public String title() {
        return "Day 14: Docking Data";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2020, 14);
    }

    @Override
    public String part1(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        final CPU cpu = new CPU();
        lines.forEach(cpu::processInstruction);
        return String.valueOf(cpu.getTotalValue());
    }

    @Override
    public String part2(final String[] input) {
        final List<String> lines = Arrays.asList(input);
        final CPU cpu = new CPU();
        lines.forEach(cpu::processFloatyInstruction);
        // 869919719786 is too low ...
        return String.valueOf(cpu.getTotalValue());
    }

    @Data
    public static final class CPU {
        private String mask;
        Map<Integer, Long> memory = new HashMap<>();

        public void processInstruction(final String line) {
            final String[] parts = line.split(" = ");
            if (parts[0].equalsIgnoreCase("mask")) {
                this.setMask(parts[1]);
            } else {
                final int address = Integer.parseInt(parts[0].replace("mem[", "").replace("]", ""));
                this.setMaskedValue(address, Long.parseLong(parts[1]));
            }
        }

        public void processFloatyInstruction(final String line) {
            final String[] parts = line.split(" = ");
            if (parts[0].equalsIgnoreCase("mask")) {
                this.setMask(parts[1]);
            } else {
                final int address = Integer.parseInt(parts[0].replace("mem[", "").replace("]", ""));
                final List<Integer> addresses = this.processAddresses(address);
                final long value = Long.parseLong(parts[1]);
                for (final int newAddress : addresses) {
                    this.memory.put(newAddress, value);
                }
            }
        }

        public List<Integer> processAddresses(final int originalAddress) {
            List<Integer> addresses = new ArrayList<>();
            addresses.add(originalAddress);
            for (int i = 0; i < this.mask.length(); i++) {
                final char c = this.mask.charAt(i);
                if (c == '0') {
                    continue;
                }
                if (c == '1') {
                    addresses = this.handleBit1(addresses, i);
                }
                if (c == 'X') {
                    addresses = this.handleBitX(addresses, i);
                }
            }
            return addresses;
        }

        private List<Integer> handleBit1(final List<Integer> originalAddresses, final int i) {
            final List<Integer> addresses = new ArrayList<>();
            for (final int originalAddress : originalAddresses) {
                addresses.add(this.setBit1(originalAddress, i));
            }
            return addresses;
        }

        private int setBit1(final int originalAddress, final int i) {
            final Integer36 integer36 = new Integer36(originalAddress);
            final String bits = integer36.getBits();
            final String newBits = bits.substring(0, i) + "1" + bits.substring(i + 1);
            return new Integer36(newBits).getIntValue();
        }

        private int setBitX(final int originalAddress, final int i, final boolean setBit) {
            final Integer36 integer36 = new Integer36(originalAddress);
            final String bits = integer36.getBits();
            final String newBits = bits.substring(0, i) + (setBit ? "1" : "0") + bits.substring(i + 1);
            return new Integer36(newBits).getIntValue();
        }

        private List<Integer> handleBitX(final List<Integer> originalAddresses, final int i) {
            final List<Integer> addresses = new ArrayList<>();
            for (final int originalAddress : originalAddresses) {
                addresses.add(this.setBitX(originalAddress, i, false));
                addresses.add(this.setBitX(originalAddress, i, true));
            }
            return addresses;
        }

        public void setMask(final String mask) {
            this.mask = mask;
        }

        public void setMaskedValue(final int address, final long value) {
            final long newValue = this.applyMask(value);
            this.memory.put(address, newValue);
        }

        private long applyMask(final long value) {
            final Integer36 integer36 = new Integer36(value);
            final String bitsValue = integer36.getBits();
            final StringBuilder newValue = new StringBuilder();
            for (int i = 0; i < 36; i++) {
                if (this.mask.substring(i, i + 1).equalsIgnoreCase("X")) {
                    newValue.append(bitsValue.charAt(i));
                } else {
                    newValue.append(this.mask.charAt(i));
                }
            }
            final Integer36 result = new Integer36(newValue.toString());
            return result.getValue();
        }

        public long getTotalValue() {
            return this.memory.values().stream().reduce(0L, Long::sum);
        }

        public static final class Integer36 {
            @Getter
            private final long value;

            public Integer36(final long value) {
                this.value = value;
            }

            public Integer36(final String bits) {
                this.value = this.parseBits(bits);
            }

            private long parseBits(final String bits) {
                long value = 0;
                for (int i = 0; i < 36; i++) {
                    if (bits.charAt(i) == '1') {
                        value = value + (1L << (35 - i));
                    }
                }
                return value;
            }

            public String getBits() {
                final StringBuilder bits = new StringBuilder();
                long runningValue = this.value;
                for (int power = 35; power >= 0; power--) {
                    if (runningValue >= (1L << power)) {
                        bits.append("1");
                        runningValue -= (1L << power);
                    } else {
                        bits.append("0");
                    }
                }
                return bits.toString();
            }

            public int getIntValue() {
                return (int) this.value;
            }
        }
    }

}
