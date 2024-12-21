package com.simongarton.adventofcode.year2024.day21;

import java.util.List;

public class Program {

    public Keypad keypad;
    public List<String> commands;

    public Program(final Keypad keypad, final List<String> commands) {
        this.keypad = keypad;
        this.commands = commands;
    }
}
