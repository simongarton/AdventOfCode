# Advent of Code

[Advent of Code](https://adventofcode.com/) is an annual programming competition, with initially simple puzzles starting
on 1 Dec, and getting progressively harder - I rarely finish the last few days.

## 2022

### Day 10: Cathode Ray Tube

Awesome. And sneaky. Again. Build a tiny little two instruction computer that parses input ready for setting a register.
And then use that to display an image on a CRT - the CRT is scanning through pixels linearly, and if the register at the
same time has a value that overlaps the current pixel, it's drawn on.

https://adventofcode.com/2022/day/10

### Day 9: Rope Bridge

Awesome. And sneaky. Given two points (head and tail of a rope), set up a system where if the head moves, the tail has
to follow it within certain constraints. Ok, not bad. Then have ten knots on the rope, not just the 2 of head and tail.
Refactor. Still works. Phew.

https://adventofcode.com/2022/day/9

### Day 8: Treetop Tree House

Nice. Given a grid of tree heights, work out first how many trees are visible from any edge (a tree must be taller than
all the trees between it and the edge) and then how big an area you can see around it.

https://adventofcode.com/2022/day/8

### Day 7: No Space Left On Device

Awesome. Given some terminal output, where the user has moved around directories and listed their contents, recreate the
disk structure and calculate the size of each directory. Tree searching and recursion.

https://adventofcode.com/2022/day/7

### Day 6: Tuning Trouble

Given a long string of random characters, find the first sequence of length 4 (part 1) and 14 (part 2) where all the
characters are different.

https://adventofcode.com/2022/day/6

### Day 5: Supply Stacks

The first fun one this year; build up a series of stacks of crates, and then move crates from one stack to another
following a script provided. Part 1 moved one crate at a time, part 2 moved 3 at a time (preserving the order, so not
the same as moving 1, three times.)

https://adventofcode.com/2022/day/5

### Day 4: Camp Cleanup

Given a list of ranges (pairs of numbers), figure out which overlap.

https://adventofcode.com/2022/day/4

### Day 3: Rucksack Reorganization

Look through strings of random characters, finding characters that appear in both halves of the string.

https://adventofcode.com/2022/day/3

### Day 2: Rock Paper Scissors

Simulate a game of Rock Paper Scissors, and then read a script to play the game in a certain way.

https://adventofcode.com/2022/day/2

### Day 1: Calorie Counting

Look through a list of numbers broken up into groups, build up totals per group, and pick (part 1) the biggest group (
part 2) the top 3 groups.

https://adventofcode.com/2022/day/1

## 2021

### Day 5: Hydrothermal Venture

Analyse a range of vectors to build lines, and then work out where the lines intersect.

https://adventofcode.com/2021/day/5

### Day 4: Giant Squid

Simulate bingo games, given a stream of numbers work out when certain boards will win

https://adventofcode.com/2021/day/4

### Day 3: Binary Diagnostic

Read some binary numbers and manipulate them

https://adventofcode.com/2021/day/3

### Day 2: Dive!

Follow a list of instructions to move forward / down / at an angle.

https://adventofcode.com/2021/day/2

### Day 1: Sonar Sweep

Find number of increases in a list; and then staggered over 3.

https://adventofcode.com/2021/day/1

## 2020

## Day 14: Docking Data

Build a binary processor that works with values and applies masks.

https://adventofcode.com/2020/day/14

## Day 13: Shuttle Search

Periods and cycles. Part 1 straight forward, part 2 needs a clever solution. The subreddit suggested Chinese Remainder
Theorem, but my maths isn't up to it. However, other examples suggested periodicity, and I translated an algorithm which
then made sense.

https://adventofcode.com/2020/day/13

## Day 12: Rain Risk

Straightforward example of moving a ship around following directions, first absolutely, and then in relation to a
waypoint that also moves.

https://adventofcode.com/2020/day/12

### Day 11: Seating System

A nice little cellular automata. Seats on the ferry fill up or empty depending on
(part 1) who is sitting next to you or (part 2) who you can see. Wrote a couple of versions of Life recently so nice and
quick.

https://adventofcode.com/2020/day/11

### Day 10: Adapter Array

Give a list of adapters, that can connect to certain other adapters, how many ways can I get through the adapters to the
end. A really challenging one in that a simple recursive solution worked fine for the small dataset, but the big dataset
had 97 adapters, which gave over 3 trillion solutions and recursion died. So I borrowed a Dynamic Programming /
Memoisation idea from the subreddit and it worked fine. The subreddit had many solutions, some of which make no sense.

https://adventofcode.com/2020/day/10

### Day 9: Encoding Error

Given a stream of numbers, find the first one that isn't the sum of a pair within the previous 25; then the sum of some
other numbers determined by that value. Straightforward.

https://adventofcode.com/2020/day/9

### Day 8: Handheld Halting

Build a little 3 instruction processor, debug a program to find the infinite loop and then figure out which instruction
is wrong to allow it to terminate. Nice.

https://adventofcode.com/2020/day/8

### Day 7: Handy Haversacks

Recursive objects, bags holding bags. Nice.

https://adventofcode.com/2020/day/7

### Day 6: Custom Customs

Set manipulation. Disappointingly easy.

https://adventofcode.com/2020/day/6

### Day 5: Binary Boarding

You're given a string of chars that identifies a specific seat on a plan using binary space partitioning; decode each
string e.g. FBFBBFFRLR to work out which row and column you are in. Then from the list, find the missing entry.

https://adventofcode.com/2020/day/5

### Day 4: Passport Processing

Parse a list of strings which have key-value pairs to make up
'passports' and find out with validation rules which are valid or not. e.g.

```'''  
ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
byr:1937 iyr:2017 cid:147 hgt:183cm
```

https://adventofcode.com/2020/day/4

## Day 3: Toboggan Trajectory

Definitely cute : a map of a hillside with trees, given a certain angle to ride the toboggan down, how many trees do you
hit ? The trees repeat in patterns out to the right.

```
O.##.......
#.O.#...#..
.#..X.#..#.
..#.#.O.#.#
.#...##.O#.
```

https://adventofcode.com/2020/day/3

### Day 2: Password Philosophy

Parse strings to find out which follow specified rules.

https://adventofcode.com/2020/day/2

### Day 1: Report Repair

A list of 200 numbers, first part is to find a combination of 2 that adds up to 2020 and then multiply them together;
second part was same thing but for 3 numbers.

https://adventofcode.com/2020/day/1

## 2019

### Day 6: Universal Orbit Map

Neat. Building graphs from orbit data. I borrow GraphViz as usual to dump out the output into images so I can see what
is going on. The first part is a huge graph, but not particularly complex to build, though you do have to calculate
direct and indirect orbits; the second required Djikstra to find shortest path.

https://adventofcode.com/2019/day/6

### Day 5: Sunny with a Chance of Asteroids

More IntCode computer stuff.

https://adventofcode.com/2019/day/5

### Day 4: Secure Container

Password combinations.

https://adventofcode.com/2019/day/4

### Day 3: Crossed Wires

Calculation of distances on a grid with wires. Rather neat.

https://adventofcode.com/2019/day/3

### Day 2: 1202 Program Alarm

IntCode computer : a list of instructions and they control how to jump back and forth. Will come back again later.

https://adventofcode.com/2019/day/2

### Day 1: The Tyranny of the Rocket Equation

Repeated additions - to carry more cargo, a rocket needs more fuel, but that fuel weighs something, so that needs more
fuel ...

https://adventofcode.com/2019/day/1
