package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.*;

public class Year2023Day7 extends AdventOfCodeChallenge {

    List<HandAndBid> handAndBidList;

    @Override
    public String title() {
        return "Day 7: Camel Cards";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 7);
    }

    @Override
    public String part1(final String[] input) {

        this.handAndBidList = this.readCards(input);
        this.handAndBidList.sort(new HandAndBitComparator());
        long total = 0;
        for (int i = 0; i < this.handAndBidList.size(); i++) {
            total = total + ((long) this.handAndBidList.get(i).getBid() * (i + 1));
        }
        return String.valueOf(total);
    }

    private List<HandAndBid> readCards(final String[] input) {
        final List<HandAndBid> handAndBidList = new ArrayList<>();
        for (final String hand : input) {
            final String[] parts = hand.split(" ");
            final HandAndBid handAndBid = HandAndBid.builder()
                    .cards(parts[0])
                    .bid(Integer.parseInt(parts[1]))
                    .build();
            handAndBid.setHand(handAndBid.calculateHand());
            handAndBidList.add(handAndBid);
        }
        return handAndBidList;
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    @Data
    @Builder
    private static final class HandAndBid {

        private String cards;
        private int bid;
        private Hand hand;

        public Hand calculateHand() {
            if (this.fiveOfAKind(this.cards)) {
                return Hand.FIVE_OF_A_KIND;
            }
            if (this.fourOfAKind(this.cards)) {
                return Hand.FOUR_OF_A_KIND;
            }
            if (this.fullHouse(this.cards)) {
                return Hand.FULL_HOUSE;
            }
            if (this.threeOfAKind(this.cards)) {
                return Hand.THREE_OF_A_KIND;
            }
            if (this.twoPair(this.cards)) {
                return Hand.TWO_PAIR;
            }
            if (this.onePair(this.cards)) {
                return Hand.ONE_PAIR;
            }
            return Hand.HIGH_CARD;
        }

        private boolean onePair(final String cards) {
            int pairs = 0;
            final Map<String, Integer> counts = this.countCards(cards);
            for (final Map.Entry<String, Integer> count : counts.entrySet()) {
                if (count.getValue() == 2) {
                    pairs++;
                }
            }
            return pairs == 1;
        }

        private boolean fullHouse(final String cards) {
            boolean got2 = false;
            boolean got3 = false;
            final Map<String, Integer> counts = this.countCards(cards);
            for (final Map.Entry<String, Integer> count : counts.entrySet()) {
                if (count.getValue() == 2) {
                    got2 = true;
                }
                if (count.getValue() == 3) {
                    got3 = true;
                }
            }
            return got2 && got3;
        }

        private boolean twoPair(final String cards) {
            int pairs = 0;
            final Map<String, Integer> counts = this.countCards(cards);
            for (final Map.Entry<String, Integer> count : counts.entrySet()) {
                if (count.getValue() == 2) {
                    pairs++;
                }
            }
            return pairs == 2;
        }

        private boolean threeOfAKind(final String cards) {
            final Map<String, Integer> counts = this.countCards(cards);
            for (final Map.Entry<String, Integer> count : counts.entrySet()) {
                if (count.getValue() == 3) {
                    return true;
                }
            }
            return false;
        }

        private boolean fourOfAKind(final String cards) {
            final Map<String, Integer> counts = this.countCards(cards);
            for (final Map.Entry<String, Integer> count : counts.entrySet()) {
                if (count.getValue() == 4) {
                    return true;
                }
            }
            return false;
        }

        private boolean fiveOfAKind(final String cards) {
            final Map<String, Integer> counts = this.countCards(cards);
            return counts.size() == 1;
        }

        private Map<String, Integer> countCards(final String cards) {
            final Map<String, Integer> counts = new HashMap<>();
            for (int i = 0; i < 5; i++) {
                final String c = cards.substring(i, i + 1);
                counts.put(c, counts.getOrDefault(c, 0) + 1);
            }
            return counts;
        }

        public int betterThan(final HandAndBid otherHand) {
            for (int i = 0; i < 5; i++) {
                final int me = Rank.fromFace(this.cards.substring(i, i + 1)).getValue();
                final int them = Rank.fromFace(otherHand.cards.substring(i, i + 1)).getValue();
                if (me > them) {
                    return 1;
                }
                if (me < them) {
                    return -1;
                }
            }
            System.out.println(this);
            System.out.println(otherHand);
            throw new RuntimeException("No ranking");
        }
    }

    @Getter
    private enum Hand {
        FIVE_OF_A_KIND(7),
        FOUR_OF_A_KIND(6),
        FULL_HOUSE(5),
        THREE_OF_A_KIND(4),
        TWO_PAIR(3),
        ONE_PAIR(2),
        HIGH_CARD(1);

        private final int value;

        Hand(final int value) {
            this.value = value;
        }
    }

    @Getter
    private enum Rank {
        ACE(14, "A"),
        KING(13, "K"),
        QUEEN(12, "Q"),
        JACK(11, "J"),
        TEN(10, "T"),
        NINE(9, "9"),
        EIGHT(8, "8"),
        SEVEN(7, "7"),
        SIX(6, "6"),
        FIVE(5, "5"),
        FOUR(4, "4"),
        THREE(3, "3"),
        TWO(2, "2");

        private final int value;
        private final String face;

        Rank(final int value, final String face) {
            this.value = value;
            this.face = face;
        }

        public static Rank fromFace(final String face) {
            for (final Rank rank : Rank.values()) {
                if (rank.getFace().equalsIgnoreCase(face)) {
                    return rank;
                }
            }
            throw new RuntimeException("Not found : " + face);
        }
    }

    private class HandAndBitComparator implements Comparator<HandAndBid> {

        @Override
        public int compare(final HandAndBid firstHand, final HandAndBid secondHand) {
            if (firstHand.getHand().getValue() == secondHand.getHand().getValue()) {
                return firstHand.betterThan(secondHand);
            } else {
                return Integer.compare(firstHand.getHand().getValue(), secondHand.getHand().getValue());
            }

        }

    }
}
