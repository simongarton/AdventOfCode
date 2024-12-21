package com.simongarton.adventofcode.year2024.day21;

import java.util.List;
import java.util.Map;

public interface Keypad {

    Keypad getController();

    void setupMovements();

    Map<String, String> getPositionsForUp();

    Map<String, String> getPositionsForRight();

    Map<String, String> getPositionsForDown();

    Map<String, String> getPositionsForLeft();

    void activate();

    Program getProgramFor(List<String> commandsNeeded, Map<Keypad, String> status);

    List<String> damnItIllDoItMyself(List<String> commandsNeeded, Map<Keypad, String> status);

    List<String> keysPressed();
}
