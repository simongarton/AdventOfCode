package com.simongarton.adventofcode.year2021;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class Year2021Day16 extends AdventOfCodeChallenge {

    private final Map<String, String> binaryTable = new HashMap<>();

    @Override
    public String title() {
        return "Day 16: Packet Decoder";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2021, 16);
    }

    @Override
    public String part1(final String[] input) {
        this.buildBinaryTable();
        final String hex = input[0];
        final String binary = this.readPaddedBinaryFromHex(hex);
        final Packet packet = new Packet(binary);
        final long result = packet.versionSum();
        return String.valueOf(result);
    }

    @Override
    public String part2(final String[] input) {
        this.buildBinaryTable();
        final String hex = input[0];
        final String binary = this.readPaddedBinaryFromHex(hex);
        final Packet packet = new Packet(binary);
        final long result = packet.calculation();
        return String.valueOf(result);
    }

    private String readPaddedBinaryFromHex(final String hex) {
        final StringBuilder paddedBinary = new StringBuilder();
        for (int i = 0; i < hex.length(); i++) {
            final String c = hex.charAt(i) + "";
            paddedBinary.append(this.binaryTable.get(c));
        }
        return paddedBinary.toString();
    }

    private void buildBinaryTable() {
        this.binaryTable.clear();
        for (int i = 0; i < 16; i++) {
            final String hex = Integer.toString(i, 16);
            final String binary = this.padTo("" + new BigInteger(hex, 16).toString(2), 4, "0");
            this.binaryTable.put(hex.toUpperCase(Locale.ROOT), binary);
        }
    }

    private String padTo(final String s, final int size, final String replacement) {
        if (s.length() > size) {
            return new String(new char[size]).replace("\0", "*");
        }
        return new String(new char[size - s.length()]).replace("\0", replacement) + s;
    }


    public enum PacketType {
        SUM(0),
        PRODUCT(1),
        MIN(2),
        MAX(3),
        LITERAL_VALUE(4),
        GREATER(5),
        LESSER(6),
        EQUAL(7);

        final int typeId;

        PacketType(final int typeId) {
            this.typeId = typeId;
        }

        public static PacketType from(final int typeId) {
            return Arrays.stream(values()).filter(pt -> pt.typeId == typeId).findFirst().orElse(null);
        }
    }

    public static final class Packet {

        private int bitsRead;
        private final Header header;
        private final PacketType packetType;
        private Long literalValue;
        private List<Packet> subPackets;

        public Packet(final String originalBinary) {
            this.header = new Header(originalBinary.substring(0, 6));
            this.bitsRead += 6;
            this.packetType = this.figurePacketType(this.header);
            this.bitsRead += this.buildPacket(originalBinary.substring(6));
        }

        @Override
        public String toString() {
            if (this.packetType == PacketType.LITERAL_VALUE) {
                return "Packet[" + this.packetType + " " + this.literalValue + "]";
            } else {
                return "Packet[" + this.packetType + " " + this.subPackets.size() + "]";
            }
        }

        private int buildPacket(final String binary) {
            if (this.packetType == PacketType.LITERAL_VALUE) {
                return this.buildPacketFromLiteralValue(binary);
            }
            return this.buildPacketFromOperator(binary);
        }

        private int buildPacketFromOperator(final String binary) {
            int bitsReadForOperator = 0;
            final int lengthTypeId = Integer.parseInt(binary.substring(0, 1));
            bitsReadForOperator += 1;
            switch (lengthTypeId) {
                case 0:
                    return bitsReadForOperator + this.readOperatorPacketsFromLength(binary.substring(1));
                case 1:
                default:
                    return bitsReadForOperator + this.readOperatorPacketsFromCount(binary.substring(1));
            }
        }

        private int readOperatorPacketsFromCount(final String substring) {
            int bitsReadForOperatorLength = 0;
            final int subPacketsCount = Integer.parseInt(substring.substring(0, 11), 2);
            bitsReadForOperatorLength += 11;
            this.subPackets = new ArrayList<>();
            int subPacketsBitsRead = 0;
            while (this.subPackets.size() < subPacketsCount) {
                final String subsubString = substring.substring(11 + subPacketsBitsRead);
                final Packet packet = new Packet(subsubString);
                this.subPackets.add(packet);
                subPacketsBitsRead += packet.bitsRead;
            }
            return bitsReadForOperatorLength + subPacketsBitsRead;
        }

        private int readOperatorPacketsFromLength(final String substring) {
            int bitsReadForOperatorLength = 0;
            final int subPacketsBitLength = Integer.parseInt(substring.substring(0, 15), 2);
            bitsReadForOperatorLength += 15;
            int subPacketsBitsRead = 0;
            this.subPackets = new ArrayList<>();
            while (subPacketsBitsRead < subPacketsBitLength) {
                final Packet packet = new Packet(substring.substring(15 + subPacketsBitsRead));
                this.subPackets.add(packet);
                subPacketsBitsRead += packet.bitsRead;
            }
            return bitsReadForOperatorLength + subPacketsBitsRead;
        }

        private int buildPacketFromLiteralValue(final String binary) {
            int bitsReadForLiteralValue = 0;
            final List<FiveBitInteger> numbers = new ArrayList<>();
            while (true) {
                final String numberString = binary.substring(bitsReadForLiteralValue, bitsReadForLiteralValue + 5);
                bitsReadForLiteralValue += 5;
                final FiveBitInteger number = new FiveBitInteger(numberString);
                numbers.add(number);
                if (number.lastBit) {
                    break;
                }
            }
            final String binaryString = numbers.stream().map(n -> n.value).collect(Collectors.joining());
            this.literalValue = Long.parseLong(binaryString, 2);
            return bitsReadForLiteralValue;
        }

        private PacketType figurePacketType(final Header header) {
            return PacketType.from(header.typeId);
        }

        public int versionSum() {
            int versionSum = this.header.version;
            if (this.subPackets != null) {
                for (final Packet subPacket : this.subPackets) {
                    versionSum += subPacket.versionSum();
                }
            }
            return versionSum;
        }

        public long calculation() {
            switch (this.packetType) {
                case SUM:
                    long sum = 0;
                    for (final Packet subPacket : this.subPackets) {
                        sum += subPacket.calculation();
                    }
                    return sum;
                case PRODUCT:
                    long product = 1;
                    for (final Packet subPacket : this.subPackets) {
                        product = product * subPacket.calculation();
                    }
                    return product;
                case MIN:
                    final List<Long> minValues = this.subPackets.stream()
                            .map(Packet::calculation)
                            .sorted(Comparator.naturalOrder())
                            .collect(Collectors.toList());
                    return minValues.get(0);
                case MAX:
                    final List<Long> maxValues = this.subPackets.stream()
                            .map(Packet::calculation)
                            .sorted(Comparator.reverseOrder())
                            .collect(Collectors.toList());
                    return maxValues.get(0);
                case LITERAL_VALUE:
                    return this.literalValue;
                case GREATER:
                    return this.subPackets.get(0).calculation() > this.subPackets.get(1).calculation() ? 1 : 0;
                case LESSER:
                    return this.subPackets.get(0).calculation() < this.subPackets.get(1).calculation() ? 1 : 0;
                case EQUAL:
                    return this.subPackets.get(0).calculation() == this.subPackets.get(1).calculation() ? 1 : 0;
                default:
                    throw new RuntimeException("Nah");
            }
        }

        public final class Header {

            private final int version;
            private final int typeId;

            public Header(final String binary) {
                final String versionString = binary.substring(0, 3);
                final String typeIdString = binary.substring(3, 6);
                this.version = Integer.parseInt(versionString, 2);
                this.typeId = Integer.parseInt(typeIdString, 2);
            }
        }

        public final class FiveBitInteger {
            private final boolean lastBit;
            private final String value;

            public FiveBitInteger(final String bits) {
                this.lastBit = bits.charAt(0) == '0';
                this.value = bits.substring(1, 5);
            }

            @Override
            public String toString() {
                return "FiveBitInteger(" + this.lastBit + ":" + this.value + ")";
            }
        }
    }
}
