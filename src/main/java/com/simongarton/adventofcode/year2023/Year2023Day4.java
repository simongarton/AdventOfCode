package com.simongarton.adventofcode.year2023;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Year2023Day4 extends AdventOfCodeChallenge {


    @Override
    public String title() {
        return "Day 4: Scratchcards";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2023, 3);
    }

    @Override
    public String part1(final String[] input) {
        final List<Card> cards = this.readCards(input);
        long total = 0;
        for (final Card card : cards) {
            total += this.scoreCard(card);
        }

        return String.valueOf(total);
    }

    private long scoreCard(final Card card) {
        int matching = 0;
        for (final Integer myNumber : card.getMyNumbers()) {
            if (card.getWinningNumbers().contains(myNumber)) {
                if (matching == 0) {
                    matching = 1;
                } else {
                    matching *= 2;
                }
            }
        }
        return matching;
    }

    private int countWinners(final Card card) {
        int matching = 0;
        for (final Integer myNumber : card.getMyNumbers()) {
            if (card.getWinningNumbers().contains(myNumber)) {
                matching++;
            }
        }
        return matching;
    }

    private List<Card> readCards(final String[] input) {
        final List<Card> cards = new ArrayList<>();
        for (final String line : input) {
            cards.add(this.readCard(line));
        }
        return cards;
    }

    private Card readCard(final String line) {
        final String[] parts = line.split(":");
        final String name = parts[0];
        final int id = Integer.valueOf(name.replace("Card ", "").replace(" ", " ").trim());
        final String[] numberParts = parts[1].trim().split("\\|");
        final String winningNumbersString = this.cleanString(numberParts[0].trim());
        final String myNumbersString = this.cleanString(numberParts[1].trim());
        final List<Integer> winningNumbers = Stream.of(winningNumbersString.split(" ")).map(Integer::valueOf).collect(Collectors.toList());
        final List<Integer> myNumbers = Stream.of(myNumbersString.split(" ")).map(Integer::valueOf).collect(Collectors.toList());
        return Card.builder()
                .id(id)
                .name(name)
                .winningNumbers(winningNumbers)
                .myNumbers(myNumbers)
                .build();
    }

    private String cleanString(String example) {
        example = example.trim();
        while (example.contains("  ")) {
            example = example.replace("  ", " ");
        }
        return example;
    }

    @Override
    public String part2(final String[] input) {

        final List<Card> cards = this.readCards(input);
        final List<Card> originalCards = new ArrayList<>(cards);
        final List<Card> newCards = new ArrayList<>();
        int index = 0;

        while (true) {
            newCards.clear();
            for (int i = index; i < cards.size(); i++) {
                final Card card = cards.get(i);
                final int winners = this.countWinners(card);
                for (int j = 0; j < winners; j++) {
                    newCards.add(originalCards.get(card.getId() + j));
                }
            }
            if (index == cards.size()) {
                break;
            }
            index = cards.size();
            cards.addAll(newCards);
        }

        return String.valueOf(cards.size());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static final class Card {
        private int id;
        private String name;
        private List<Integer> winningNumbers;
        private List<Integer> myNumbers;
    }
}
