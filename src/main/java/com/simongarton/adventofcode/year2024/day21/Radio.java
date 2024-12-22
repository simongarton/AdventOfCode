package com.simongarton.adventofcode.year2024.day21;

import java.text.DecimalFormat;

public class Radio {

    public static int tick = 0;

    public static void broadcast(final Keypad keypad, final String message) {

        final DecimalFormat df = new DecimalFormat("### ");

        System.out.println(df.format(tick) + " ".repeat(keypad.getLevel() * 2) + keypad.getName() + ":" + message);
    }

    public static void tick() {
        tick++;
    }

    public static void resetTick() {
        tick = 0;
    }


}
