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
        this.handAndBidList.sort(new HandAndBitComparator(true));
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
            handAndBidList.add(HandAndBid.builder()
                    .cards(parts[0])
                    .originalCards(parts[0])
                    .bid(Integer.parseInt(parts[1]))
                    .build()
                    .calculateHand());
        }
        return handAndBidList;
    }

    @Override
    public String part2(final String[] input) {

        this.handAndBidList = this.readCards(input);
        this.handAndBidList.sort(new HandAndBitWildComparator());
        long total = 0;
        for (int i = 0; i < this.handAndBidList.size(); i++) {
            final HandAndBid handAndBid = this.handAndBidList.get(i);
            handAndBid.calculateHand();
            total = total + ((long) this.handAndBidList.get(i).getBid() * (i + 1));
        }
        return String.valueOf(total);
    }

    @Data
    @Builder
    private static final class HandAndBid {

        private String cards;
        private String originalCards;
        private int bid;
        private Hand hand;

        public HandAndBid calculateHand() {
            if (this.fiveOfAKind(this.cards)) {
                this.hand = Hand.FIVE_OF_A_KIND;
                return this;
            }
            if (this.fourOfAKind(this.cards)) {
                this.hand = Hand.FOUR_OF_A_KIND;
                return this;
            }
            if (this.fullHouse(this.cards)) {
                this.hand = Hand.FULL_HOUSE;
                return this;
            }
            if (this.threeOfAKind(this.cards)) {
                this.hand = Hand.THREE_OF_A_KIND;
                return this;
            }
            if (this.twoPair(this.cards)) {
                this.hand = Hand.TWO_PAIR;
                return this;
            }
            if (this.onePair(this.cards)) {
                this.hand = Hand.ONE_PAIR;
                return this;
            }
            this.hand = Hand.HIGH_CARD;
            return this;
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

        private boolean onePair(final String cards) {
            return this.countPairs(cards) == 1;
        }

        private boolean twoPair(final String cards) {
            return this.countPairs(cards) == 2;
        }

        private int countPairs(final String cards) {
            int pairs = 0;
            final Map<String, Integer> counts = this.countCards(cards);
            for (final Map.Entry<String, Integer> count : counts.entrySet()) {
                if (count.getValue() == 2) {
                    pairs++;
                }
            }
            return pairs;
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

        public int betterThanOriginal(final HandAndBid otherHand) {
            for (int i = 0; i < 5; i++) {
                int me = Rank.fromFace(this.originalCards.substring(i, i + 1)).getValue();
                if (this.originalCards.substring(i, i + 1).equalsIgnoreCase(Rank.JACK.getFace())) {
                    me = 1;
                }
                int them = Rank.fromFace(otherHand.originalCards.substring(i, i + 1)).getValue();
                if (otherHand.originalCards.substring(i, i + 1).equalsIgnoreCase(Rank.JACK.getFace())) {
                    them = 1;
                }
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

        public static Rank fromValue(final int value) {
            for (final Rank rank : Rank.values()) {
                if (rank.getValue() == value) {
                    return rank;
                }
            }
            throw new RuntimeException("Not found : " + value);
        }
    }

    private static class HandAndBitComparator implements Comparator<HandAndBid> {

        private final boolean useOriginalCards;

        HandAndBitComparator(final boolean useOriginalCards) {
            this.useOriginalCards = useOriginalCards;
        }

        @Override
        public int compare(final HandAndBid firstHand, final HandAndBid secondHand) {
            if (firstHand.getHand().getValue() == secondHand.getHand().getValue()) {
                if (this.useOriginalCards) {
                    return firstHand.betterThanOriginal(secondHand);
                } else {
                    return firstHand.betterThan(secondHand);
                }
            } else {
                return Integer.compare(firstHand.getHand().getValue(), secondHand.getHand().getValue());
            }
        }
    }

    private static class HandAndBitWildComparator implements Comparator<HandAndBid> {

        @Override
        public int compare(final HandAndBid firstHand, final HandAndBid secondHand) {
            final HandAndBid bestFirstHand = this.figureOutBestHand(firstHand);
            final HandAndBid bestSecondHand = this.figureOutBestHand(secondHand);
            final HandAndBitComparator handAndBitComparator = new HandAndBitComparator(true);
            return handAndBitComparator.compare(bestFirstHand, bestSecondHand);
        }

        private HandAndBid figureOutBestHand(final HandAndBid hand) {
            if (hand.getCards().equalsIgnoreCase("JJJJJ")) {
                return HandAndBid.builder()
                        .cards("AAAAA")
                        .originalCards(hand.getCards())
                        .bid(hand.getBid())
                        .build()
                        .calculateHand();
            }
            final List<HandAndBid> allPossibleHands = new ArrayList<>();
            for (int a = this.minValue(hand.cards.substring(0, 1)); a <= this.maxValue(hand.cards.substring(0, 1)); a++) {
                for (int b = this.minValue(hand.cards.substring(1, 2)); b <= this.maxValue(hand.cards.substring(1, 2)); b++) {
                    for (int c = this.minValue(hand.cards.substring(2, 3)); c <= this.maxValue(hand.cards.substring(2, 3)); c++) {
                        for (int d = this.minValue(hand.cards.substring(3, 4)); d <= this.maxValue(hand.cards.substring(3, 4)); d++) {
                            for (int e = this.minValue(hand.cards.substring(4, 5)); e <= this.maxValue(hand.cards.substring(4, 5)); e++) {
                                final String newHand = this.getCard(a) +
                                        this.getCard(b) +
                                        this.getCard(c) +
                                        this.getCard(d) +
                                        this.getCard(e);
                                final HandAndBid handAndBid = HandAndBid.builder()
                                        .cards(newHand)
                                        .originalCards(hand.getCards())
                                        .bid(hand.getBid())
                                        .build()
                                        .calculateHand();
                                if (!allPossibleHands.contains(handAndBid)) {
                                    allPossibleHands.add(handAndBid);
                                }
                            }
                        }
                    }
                }
            }
            allPossibleHands.sort(new HandAndBitComparator(false).reversed());
            return allPossibleHands.get(0);
        }

        private String getCard(final int value) {
            return Rank.fromValue(value).getFace();
        }

        private int maxValue(final String card) {
            if (card.equals(Rank.JACK.getFace())) {
                return Rank.ACE.getValue();
            }
            return Rank.fromFace(card).getValue();
        }

        private int minValue(final String card) {
            if (card.equals(Rank.JACK.getFace())) {
                return Rank.TWO.getValue();
            }
            return Rank.fromFace(card).getValue();
        }
    }
}
