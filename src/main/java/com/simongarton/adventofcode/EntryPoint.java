package com.simongarton.adventofcode;

import com.simongarton.adventofcode.year2019.Year2019Day8;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class EntryPoint {

    public static void main(final String[] args) throws IOException {

        final Year2019Day8 year2019Day8 = new Year2019Day8();
//        final Path path = Paths.get("src/main/resources/2019/sample.txt");
        final Path path = Paths.get("src/main/resources/2019/2019-Day8-1.txt");
        final List<String> lines = Files.lines(path, StandardCharsets.UTF_8).collect(Collectors.toList());
        System.out.println(year2019Day8.part2(lines.toArray(new String[0])));
    }
}
