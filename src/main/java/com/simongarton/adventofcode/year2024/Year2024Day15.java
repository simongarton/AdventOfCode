package com.simongarton.adventofcode.year2024;

import com.simongarton.adventofcode.AdventOfCodeChallenge;
import com.simongarton.adventofcode.common.ChallengeCoord;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Year2024Day15 extends AdventOfCodeChallenge {

    private static final boolean DEBUG = true;

    private static final int BITMAP_SCALE = 20;

    private static final String BOX = "O";
    private static final String LEFT_BOX = "[";
    private static final String RIGHT_BOX = "]";
    private static final String ROBOT = "@";
    public static final String RIGHT = ">";
    public static final String LEFT = "<";
    public static final String UP = "^";
    public static final String DOWN = "v";

    private List<Box> boxes;
    private Robot robot;
    private List<String> moves;

    private List<String> thingsToMove;
    private String moveDirection;

    @Override
    public String title() {
        return "Day 15: Warehouse Woes";
    }

    @Override
    public Outcome run() {
        return this.runChallenge(2024, 15);
    }

    @Override
    public String part1(final String[] input) {

        this.loadMap(input);

        this.findRobotAndBoxes();
        if (DEBUG) {
            this.drawChallengeMap();
        }

        this.thingsToMove = new ArrayList<>();

        int i = 0;
        for (final String move : this.moves) {
            this.move(move);
            if (DEBUG) {
//                this.drawChallengeMap();
                this.paintMap(i++);
            }
        }

        return String.valueOf(this.sumBoxCoordinates());
    }

    private void paintMap(final int seconds) {

        final String filename = "/Users/simongarton/projects/java/AdventOfCode/temp/warehouse-" + String.format("%05d", seconds) + ".png";

        final BufferedImage bufferedImage = new BufferedImage(this.mapWidth * BITMAP_SCALE, this.mapHeight * BITMAP_SCALE, TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        this.clearBackground(graphics2D);
        this.paintFloor(graphics2D);
        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
//            System.out.println(seconds + ":" + filename);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        graphics2D.dispose();
    }

    private void paintFloor(final Graphics2D graphics2D) {

        for (int row = 0; row < this.mapWidth; row++) {
            for (int col = 0; col < this.mapHeight; col++) {
                final String thing = this.getChallengeMapSymbol(col, row);
                if (thing.equalsIgnoreCase(EMPTY)) {
                    continue;
                }
                graphics2D.setPaint(new Color(50, 50, 50));
                if (thing.equalsIgnoreCase(BOX) || thing.equalsIgnoreCase(LEFT_BOX) || thing.equalsIgnoreCase(RIGHT_BOX)) {
                    graphics2D.setPaint(new Color(0, 200, 0));
                }
                if (thing.equalsIgnoreCase(ROBOT)) {
                    graphics2D.setPaint(new Color(200, 0, 0));
                }
                graphics2D.fillRect(col * BITMAP_SCALE, row * BITMAP_SCALE, BITMAP_SCALE, BITMAP_SCALE);
            }
        }
    }

    private void clearBackground(final Graphics2D graphics2D) {

        graphics2D.setPaint(Color.BLACK);
        graphics2D.fillRect(0, 0, this.mapWidth * BITMAP_SCALE, this.mapHeight * BITMAP_SCALE);
    }

    private void move(final String move) {

        this.thingsToMove.clear();
        this.moveDirection = move;

        final boolean outcome = this.recursiveMove(move, this.robot.position);
        if (!outcome) {
            return;
        }
        this.moveStuff();
    }

    private void doubleMove(final String move) {

        this.thingsToMove.clear();
        this.moveDirection = move;

        final boolean outcome = this.recursiveDoubleMove(move, this.robot.position);
        if (!outcome) {
            return;
        }
        this.moveDoubleStuff();
    }

    private void moveStuff() {

        for (final String uuidToMove : this.thingsToMove) {
            final ThingWithCoord thingToMove = this.findThingToMove(uuidToMove);
            final ChallengeCoord position = thingToMove.position;
            this.setChallengeMapLetter(position, EMPTY);
        }

        for (final String uuidToMove : this.thingsToMove) {
            final ThingWithCoord thingToMove = this.findThingToMove(uuidToMove);
            final ChallengeCoord position = thingToMove.position;
            final ChallengeCoord newPosition = this.getNewPosition(position, this.moveDirection);

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

    private void moveDoubleStuff() {

        for (final String uuidToMove : this.thingsToMove) {
            final ThingWithCoord thingToMove = this.findThingToMove(uuidToMove);
            final ChallengeCoord position = thingToMove.position;
            this.setChallengeMapLetter(position, EMPTY);
            if (thingToMove.getClass().getSimpleName().equalsIgnoreCase("Box")) {
                final ChallengeCoord otherHalfOfBox = new ChallengeCoord(position.getX() + 1, position.getY());
                this.setChallengeMapLetter(otherHalfOfBox, EMPTY);
            }
        }

        for (final String uuidToMove : this.thingsToMove) {
            final ThingWithCoord thingToMove = this.findThingToMove(uuidToMove);
            final ChallengeCoord position = thingToMove.position;
            final ChallengeCoord newPosition = this.getNewPosition(position, this.moveDirection);

            if (thingToMove.getClass().getSimpleName().equalsIgnoreCase("Robot")) {
                this.robot.position = newPosition;
                this.setChallengeMapLetter(newPosition, ROBOT);
                continue;
            }

            final Box boxToMove = (Box) thingToMove;
            boxToMove.position = newPosition;
            this.setChallengeMapLetter(newPosition, LEFT_BOX);
            final ChallengeCoord otherHalfOfBox = new ChallengeCoord(newPosition.getX() + 1, newPosition.getY());
            this.setChallengeMapLetter(otherHalfOfBox, RIGHT_BOX);
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

    private ChallengeCoord getNewPosition(final ChallengeCoord position, final String moveDirection) {

        if (moveDirection.equalsIgnoreCase(LEFT)) {
            return new ChallengeCoord(position.getX() - 1, position.getY());
        }
        if (moveDirection.equalsIgnoreCase(RIGHT)) {
            return new ChallengeCoord(position.getX() + 1, position.getY());
        }
        if (moveDirection.equalsIgnoreCase(UP)) {
            return new ChallengeCoord(position.getX(), position.getY() - 1);
        }
        if (moveDirection.equalsIgnoreCase(DOWN)) {
            return new ChallengeCoord(position.getX(), position.getY() + 1);
        }
        throw new RuntimeException("oops");
    }

    private boolean recursiveMove(final String move, final ChallengeCoord position) {

        if (move.equalsIgnoreCase(LEFT)) {
            final ChallengeCoord nextPosition = new ChallengeCoord(position.getX() - 1, position.getY());
            return this.checkMove(nextPosition, move);
        }
        if (move.equalsIgnoreCase(RIGHT)) {
            final ChallengeCoord nextPosition = new ChallengeCoord(position.getX() + 1, position.getY());
            return this.checkMove(nextPosition, move);
        }
        if (move.equalsIgnoreCase(UP)) {
            final ChallengeCoord nextPosition = new ChallengeCoord(position.getX(), position.getY() - 1);
            return this.checkMove(nextPosition, move);
        }
        if (move.equalsIgnoreCase(DOWN)) {
            final ChallengeCoord nextPosition = new ChallengeCoord(position.getX(), position.getY() + 1);
            return this.checkMove(nextPosition, move);
        }
        throw new RuntimeException("oops");
    }

    private boolean recursiveDoubleMove(final String move, final ChallengeCoord position) {

        if (move.equalsIgnoreCase(LEFT)) {
            final ChallengeCoord nextPosition = new ChallengeCoord(position.getX() - 1, position.getY());
            return this.checkDoubleMove(nextPosition, move);
        }
        if (move.equalsIgnoreCase(RIGHT)) {
            final ChallengeCoord nextPosition = new ChallengeCoord(position.getX() + 1, position.getY());
            return this.checkDoubleMove(nextPosition, move);
        }
        if (move.equalsIgnoreCase(UP)) {
            final ChallengeCoord nextPosition = new ChallengeCoord(position.getX(), position.getY() - 1);
            return this.checkDoubleMove(nextPosition, move);
        }
        if (move.equalsIgnoreCase(DOWN)) {
            final ChallengeCoord nextPosition = new ChallengeCoord(position.getX(), position.getY() + 1);
            return this.checkDoubleMove(nextPosition, move);
        }
        throw new RuntimeException("oops");
    }

    private boolean checkMove(final ChallengeCoord nextPosition, final String nextMove) {

        final String thing = this.getChallengeMapSymbol(nextPosition);
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

    private boolean checkDoubleMove(final ChallengeCoord nextPosition, final String nextMove) {

        final String thing = this.getChallengeMapSymbol(nextPosition);
        if (thing.equalsIgnoreCase(WALL)) {
            return false;
        }
        if (thing.equalsIgnoreCase(EMPTY)) {
            if (!this.thingsToMove.contains(this.robot.id)) {
                this.thingsToMove.add(this.robot.id);
            }
            return true;
        }
        if (thing.equalsIgnoreCase(LEFT_BOX)) {
            // it all depends on my next move now.
            return this.specialDoubleMoveLeft(nextPosition, nextMove);
        }
        if (thing.equalsIgnoreCase(RIGHT_BOX)) {
            // it all depends on my next move now.
            return this.specialDoubleMoveRight(nextPosition, nextMove);
        }
        throw new RuntimeException("oops");
    }

    private boolean specialDoubleMoveLeft(final ChallengeCoord nextPosition, final String nextMove) {

        // I'm hitting the left side of a box
        // if I'm going right it's normal
        if (nextMove.equalsIgnoreCase(RIGHT)) {
            final Box box = this.getBoxAt(nextPosition);
            if (!this.thingsToMove.contains(box.id)) {
                this.thingsToMove.add(box.id);
            }
            return this.recursiveDoubleMove(nextMove, nextPosition);
        }
        // if I'm going left - is this an error ? or should I just skip it and continue
        if (nextMove.equalsIgnoreCase(LEFT)) {
            return this.recursiveDoubleMove(nextMove, nextPosition);
        }
        // if I'm going up, I need to check both coord above and one to the right
        if (nextMove.equalsIgnoreCase(UP) || nextMove.equalsIgnoreCase(DOWN)) {
            final Box box = this.getBoxAt(nextPosition);
            if (!this.thingsToMove.contains(box.id)) {
                this.thingsToMove.add(box.id);
            }
            final boolean leftSide = this.recursiveDoubleMove(nextMove, nextPosition);
            final ChallengeCoord otherHalfOfBox = new ChallengeCoord(nextPosition.getX() + 1, nextPosition.getY());
            final boolean rightSide = this.recursiveDoubleMove(nextMove, otherHalfOfBox);
            return leftSide && rightSide;
        }
        throw new RuntimeException("oops");
    }

    private boolean specialDoubleMoveRight(final ChallengeCoord nextPosition, final String nextMove) {

        // I'm hitting the right side of a box
        // if I'm going left it's normal
        if (nextMove.equalsIgnoreCase(LEFT)) {
            final ChallengeCoord otherHalfOfBox = new ChallengeCoord(nextPosition.getX() - 1, nextPosition.getY());
            final Box box = this.getBoxAt(otherHalfOfBox);
            // could have done this as a set or map ...
            if (!this.thingsToMove.contains(box.id)) {
                this.thingsToMove.add(box.id);
            }
            return this.recursiveDoubleMove(nextMove, nextPosition);
        }
        // if I'm going right - is this an error ? or should I just skip it and continue
        if (nextMove.equalsIgnoreCase(RIGHT)) {
            return this.recursiveDoubleMove(nextMove, nextPosition);
        }
        // if I'm going up, I need to check both coord above and one to the right
        if (nextMove.equalsIgnoreCase(UP) || nextMove.equalsIgnoreCase(DOWN)) {
            final ChallengeCoord otherHalfOfBox = new ChallengeCoord(nextPosition.getX() - 1, nextPosition.getY());
            final Box box = this.getBoxAt(otherHalfOfBox);
            if (!this.thingsToMove.contains(box.id)) {
                this.thingsToMove.add(box.id);
            }
            final boolean leftSide = this.recursiveDoubleMove(nextMove, nextPosition);
            final boolean rightSide = this.recursiveDoubleMove(nextMove, otherHalfOfBox);
            return leftSide && rightSide;
        }
        throw new RuntimeException("oops");
    }

    private Box getBoxAt(final ChallengeCoord nextPosition) {

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
            total += (box.position.getY() * 100L + box.position.getX());
        }
        return total;
    }

    private void findRobotAndBoxes() {

        this.boxes = new ArrayList<>();
        for (int x = 0; x < this.mapWidth; x++) {
            for (int y = 0; y < this.mapHeight; y++) {
                final String thing = this.getChallengeMapSymbol(x, y);
                if (thing.equalsIgnoreCase("@")) {
                    final ChallengeCoord c = new ChallengeCoord(x, y);
                    this.robot = new Robot(c);
                }
                if (thing.equalsIgnoreCase("O")) {
                    final ChallengeCoord c = new ChallengeCoord(x, y);
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

        this.loadDoubleMap(input);

        this.findRobotAndDoubleBoxes();
        if (DEBUG) {
            this.drawChallengeMap();
        }

        this.thingsToMove = new ArrayList<>();

        for (final String move : this.moves) {
            this.doubleMove(move);
            if (DEBUG) {
                this.drawChallengeMap();
                this.listBoxes();
                System.out.println();
            }
        }

        return String.valueOf(this.sumBoxCoordinates());
    }

    private void listBoxes() {

        this.boxes.forEach(System.out::println);
    }

    private void findRobotAndDoubleBoxes() {

        this.boxes = new ArrayList<>();
        for (int x = 0; x < this.mapWidth; x++) {
            for (int y = 0; y < this.mapHeight; y++) {
                final String thing = this.getChallengeMapSymbol(x, y);
                if (thing.equalsIgnoreCase("@")) {
                    final ChallengeCoord c = new ChallengeCoord(x, y);
                    this.robot = new Robot(c);
                }
                if (thing.equalsIgnoreCase("[")) {
                    final ChallengeCoord c = new ChallengeCoord(x, y);
                    this.boxes.add(new Box(c));
                }
                // don't worry about the second side of the box
            }
        }
    }

    private void loadDoubleMap(final String[] input) {

        this.loadMap(input);

        final List<String> doubleMap = new ArrayList<>();
        for (final String line : this.challengeMap) {
            final String mutatedLine = this.mutateLine(line);
            doubleMap.add(mutatedLine);
        }
        this.challengeMap.clear();
        this.challengeMap.addAll(doubleMap);
        this.mapWidth = this.mapWidth * 2;
    }

    private String mutateLine(final String line) {

        final StringBuilder mutatedLine = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            final String letter = line.charAt(i) + "";
            if (letter.equalsIgnoreCase("#")) {
                mutatedLine.append("##");
                continue;
            }
            if (letter.equalsIgnoreCase("O")) {
                mutatedLine.append("[]");
                continue;
            }
            if (letter.equalsIgnoreCase("@")) {
                mutatedLine.append("@.");
                continue;
            }
            mutatedLine.append("..");
        }
        return mutatedLine.toString();
    }

    static class ThingWithCoord {

        String id;
        ChallengeCoord position;
        String symbol;
    }

    static class Robot extends ThingWithCoord {

        public Robot(final ChallengeCoord position) {

            this.id = UUID.randomUUID().toString();
            this.position = position;
            this.symbol = ROBOT;
        }
    }

    static class Box extends ThingWithCoord {

        public Box(final ChallengeCoord position) {

            this.id = UUID.randomUUID().toString();
            this.position = position;
            this.symbol = BOX;
        }

        @Override
        public String toString() {
            return this.id + " " + this.position;
        }
    }
}
