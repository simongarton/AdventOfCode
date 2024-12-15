package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;

public class Year2024Day15 extends AdventOfCodeChallenge {

    private static final String WALL = "#";
    private static final String BOX = "O";
    private static final String EMPTY = ".";
    private static final String ROBOT = "@";

    private List<Box> boxes;
    private Robot robot;
    private List<String> moves;

    private List<ThingWithCoord> thingsToMove;
    private String moveDirection;

    @Override
    public String title() {
        return "Day 0: Template code";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 0);
    }

    @Override
    public String part1(final String[] input) {

        this.loadMap(input);

        this.findRobotAndBoxes();
        this.drawChallengeMap();

        for (final String move : this.moves) {
            this.move(move);
            this.drawChallengeMap();
        }

        return String.valueOf(this.sumBoxCoordinates());
    }

    private boolean move(final String move) {

        this.thingsToMove.clear();
        this.moveDirection = move;

        final boolean outcome = this.recursiveMove(move);
        if (!outcome) {
            return outcome;
        }
        this.moveStuff();
        return outcome;
    }

    private void moveStuff() {

        for (final ThingWithCoord thingWithCoord : this.thingsToMove) {
            final AoCCoord position = thingWithCoord.position;
            final AoCCoord newPosition = this.getNewPosition(position, this.moveDirection);
            this.setChallengeMapLetter(position, EMPTY);

            if (this.robot.position.equals(position)) {
                this.robot.position = newPosition;
                this.setChallengeMapLetter(newPosition, ROBOT);
                continue;
            }
            boolean foundBox = false;
            for (final Box box : this.boxes) {
                if (box.position.equals(position)) {
                    box.position = newPosition;
                    this.setChallengeMapLetter(newPosition, BOX);
                    foundBox = true;
                    break;
                }
            }
            if (!foundBox) {
                throw new RuntimeException("oops");
            }
        }
    }

    private AoCCoord getNewPosition(final AoCCoord position, final String moveDirection) {

        if (moveDirection.equalsIgnoreCase("<")) {
            return new AoCCoord(position.x - 1, position.y);
        }
        if (moveDirection.equalsIgnoreCase(">")) {
            return new AoCCoord(position.x + 1, position.y);
        }
        if (moveDirection.equalsIgnoreCase("^")) {
            return new AoCCoord(position.x, position.y - 1);
        }
        if (moveDirection.equalsIgnoreCase("v")) {
            return new AoCCoord(position.x, position.y + 1);
        }
        throw new RuntimeException("oops");
    }

    private boolean recursiveMove(final String move) {

        // yet to actually move things

        if (move.equalsIgnoreCase("<")) {
            return this.moveLeft();
        }
        if (move.equalsIgnoreCase(">")) {
            return this.moveRight();
        }
        if (move.equalsIgnoreCase("^")) {
            return this.moveUp();
        }
        if (move.equalsIgnoreCase("v")) {
            return this.moveDown();
        }
        throw new RuntimeException("oops");
    }

    private boolean moveDown() {

        final AoCCoord nextPosition = new AoCCoord(this.robot.position.x, this.robot.position.y + 1);
        return this.checkMove(nextPosition, "v");
    }

    private boolean moveUp() {

        final AoCCoord nextPosition = new AoCCoord(this.robot.position.x, this.robot.position.y - 1);
        return this.checkMove(nextPosition, "^");
    }

    private boolean moveRight() {

        final AoCCoord nextPosition = new AoCCoord(this.robot.position.x + 1, this.robot.position.y);
        return this.checkMove(nextPosition, ">");

    }

    private boolean moveLeft() {

        final AoCCoord nextPosition = new AoCCoord(this.robot.position.x - 1, this.robot.position.y);
        return this.checkMove(nextPosition, ">");
    }

    private boolean checkMove(final AoCCoord nextPosition, final String nextMove) {

        final String thing = this.getChallengeMapLetter(nextPosition);
        if (thing.equalsIgnoreCase(WALL)) {
            return false;
        }
        if (thing.equalsIgnoreCase(EMPTY)) {
            this.thingsToMove.add(this.robot);
            return true;
        }
        if (thing.equalsIgnoreCase(BOX)) {
            this.thingsToMove.add(this.robot);
            return this.recursiveMove(nextMove);
        }
        throw new RuntimeException("oops");
    }

    private Integer sumBoxCoordinates() {

        return 0;
    }

    private void findRobotAndBoxes() {

        this.boxes = new ArrayList<>();
        for (int x = 0; x < this.mapWidth; x++) {
            for (int y = 0; y < this.mapHeight; y++) {
                final String thing = this.getChallengeMapLetter(x, y);
                if (thing.equalsIgnoreCase("@")) {
                    final AoCCoord c = new AoCCoord(x, y);
                    this.robot = new Robot(c);
                }
                if (thing.equalsIgnoreCase("O")) {
                    final AoCCoord c = new AoCCoord(x, y);
                    this.boxes.add(new Box(c));
                }
            }
        }
    }

    private void loadMap(final String[] input) {

        this.challengeMap = new ArrayList<>();
        this.mapWidth = input[0].length();
        this.mapHeight = 0;

        this.moves = new ArrayList<>();

        int index = 0;
        while (true) {
            final String line = input[index];
            if (line.isEmpty()) {
                break;
            }
            this.challengeMap.add(line);
            index++;
            this.mapHeight++;
        }
        index++;

        while (true) {
            final String line = input[index];
            if (line.isEmpty()) {
                break;
            }
            for (int i = 0; i < line.length(); i++) {
                this.moves.add(line.charAt(i) + "");
            }
            index++;
            if (index == input.length) {
                break;
            }
        }
    }

    @Override
    public String part2(final String[] input) {
        return null;
    }

    static class ThingWithCoord {

        AoCCoord position;
        String symbol;
    }

    static class Robot extends ThingWithCoord {

        public Robot(final AoCCoord position) {

            this.position = position;
            this.symbol = ROBOT;
        }

    }

    static class Box extends ThingWithCoord {

        public Box(final AoCCoord position) {

            this.position = position;
            this.symbol = BOX;
        }

    }
}
