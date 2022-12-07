package com.simongarton.adventofcode.exceptions;

public class WrongAnswerException extends RuntimeException {

    private static final long serialVersionUID = -7669164871464856265L;

    public WrongAnswerException(final int year, final int day, final int part, final String expected, final String actual) {
        super(String.format("Attempted %s.%s.%s expected %s got %s", year, day, part, expected, actual));
    }
}
