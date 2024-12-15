package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Year2024Day15 extends AdventOfCodeChallenge {

    private static final String WALL = "#";
    private static final String BOX = "O";
    private static final String EMPTY = ".";
    private static final String ROBOT = "@";

    private List<Box> boxes;
    private Robot robot;
    private List<String> moves;

    private List<String> thingsToMove;
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

        this.thingsToMove = new ArrayList<>();

        for (final String move : this.moves) {
            this.move(move);
            this.drawChallengeMap();
        }

        return String.valueOf(this.sumBoxCoordinates());
    }

    private boolean move(final String move) {

        this.thingsToMove.clear();
        this.moveDirection = move;

        final boolean outcome = this.recursiveMove(move, this.robot.position);
        if (!outcome) {
            return outcome;
        }
        this.moveStuff();
        return outcome;
    }

    private void moveStuff() {

        System.out.println("Moving stuff ...");

        for (final String uuidToMove : this.thingsToMove) {
            final ThingWithCoord thingToMove = this.findThingToMove(uuidToMove);
            final AoCCoord position = thingToMove.position;
            this.setChallengeMapLetter(position, EMPTY);
        }

        for (final String uuidToMove : this.thingsToMove) {
            final ThingWithCoord thingToMove = this.findThingToMove(uuidToMove);
            final AoCCoord position = thingToMove.position;
            final AoCCoord newPosition = this.getNewPosition(position, this.moveDirection);

            if (thingToMove.getClass().getSimpleName().equalsIgnoreCase("Robot")) {
                this.robot.position = newPosition;
                this.setChallengeMapLetter(newPosition, ROBOT);
                continue;
            }

            final Box boxToMove = (Box) thingToMove;
            boxToMove.position = newPosition;
            this.setChallengeMapLetter(newPosition, BOX);
        }
    }

    private ThingWithCoord findThingToMove(final String uuidToMove) {

        if (this.robot.id.equalsIgnoreCase(uuidToMove)) {
            return this.robot;
        }
        for (final Box box : this.boxes) {
            if (box.id.equalsIgnoreCase(uuidToMove)) {
                return box;
            }
        }
        throw new RuntimeException("oops");
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

    private boolean recursiveMove(final String move, final AoCCoord position) {

        if (move.equalsIgnoreCase("<")) {
            final AoCCoord nextPosition = new AoCCoord(position.x - 1, position.y);
            return this.checkMove(nextPosition, move);
        }
        if (move.equalsIgnoreCase(">")) {
            final AoCCoord nextPosition = new AoCCoord(position.x + 1, position.y);
            return this.checkMove(nextPosition, move);
        }
        if (move.equalsIgnoreCase("^")) {
            final AoCCoord nextPosition = new AoCCoord(position.x, position.y - 1);
            return this.checkMove(nextPosition, move);
        }
        if (move.equalsIgnoreCase("v")) {
            final AoCCoord nextPosition = new AoCCoord(position.x, position.y + 1);
            return this.checkMove(nextPosition, move);
        }
        throw new RuntimeException("oops");
    }

    private boolean checkMove(final AoCCoord nextPosition, final String nextMove) {

        final String thing = this.getChallengeMapLetter(nextPosition);
        System.out.println("Checking " + nextPosition + " for " + nextMove + " and got " + thing);
        if (thing.equalsIgnoreCase(WALL)) {
            return false;
        }
        if (thing.equalsIgnoreCase(EMPTY)) {
            this.thingsToMove.add(this.robot.id);
            return true;
        }
        if (thing.equalsIgnoreCase(BOX)) {
            final Box box = this.getBoxAt(nextPosition);
            this.thingsToMove.add(box.id);
            return this.recursiveMove(nextMove, nextPosition);
        }
        throw new RuntimeException("oops");
    }

    private Box getBoxAt(final AoCCoord nextPosition) {

        for (final Box box : this.boxes) {
            if (box.position.equals(nextPosition)) {
                return box;
            }
        }
        throw new RuntimeException("oops");
    }

    private Long sumBoxCoordinates() {

        long total = 0;
        for (final Box box : this.boxes) {
            total += (box.position.y * 100L + box.position.x);
        }
        return total;
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

        String id;
        AoCCoord position;
        String symbol;
    }

    static class Robot extends ThingWithCoord {

        public Robot(final AoCCoord position) {

            this.id = UUID.randomUUID().toString();
            this.position = position;
            this.symbol = ROBOT;
        }

    }

    static class Box extends ThingWithCoord {

        public Box(final AoCCoord position) {

            this.id = UUID.randomUUID().toString();
            this.position = position;
            this.symbol = BOX;
        }
    }
}
