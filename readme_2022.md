# Advent of Code 2022

## 2022

### Day 25: Full of Hot Air

Hmm. Having skim-read this, it looks like just encoding numbers in base 5 with symbols. Probably more complicated
thinking than programming at this point.

Lol. Way harder. Instead of counting up powers of 5, you have to shift your counts - you can't have a count of any power
of 5 greater than 2. So if you wanted say 3 x 5^2, you actually end up having to have an extra 1 x 5^3, and then you
have to take away 5 x 5^2 to balance, ending up with 1 x 5^3 and -2 x 5^2.

I got a brute force working quite quickly, but the real sample was too large, so I had to work out how to map it
directly. Attempts to do it directly, starting from the beginning or the end, turned out too hard to figure out - so I
did it with a 'mancala' style approach, divvying it up into a normal base 5 number, and then re-distributing numbers
repeatedly : if I have say 3 of one power, add 1 to the next higher power and take 5 away from this one.

https://adventofcode.com/2022/day/25

### Day 24: Blizzard Basin

Another map : you have to get from one corner to another, avoiding storms that move across the map; you'll end up moving
in one of the 4 cardinal directions (including retracing a previous step) or even waiting. I can see the storms location
is deterministic; I don't know if I can map a graph of the storms given they keep changing.

OK, so a simple approach is to just use A* but make the map time sensitive. This works nicely for the sample; and indeed
once I'd sorted out a couple of bugs, both part 1 and 2.

![Part 2](images/2022-24.2.png)

https://adventofcode.com/2022/day/24

### Day 23: Unstable Diffusion

Again a map, this time with elves spread around, but wanting to move so not too close to each other. Initially I thought
this would be a cellular automaton, but it turned out to be simpler (once I read the question again)
and you just need to keep track of which elf goes where, and not move any that would collide.

https://adventofcode.com/2022/day/23

### Day 22: Monkey Map (1/2)

First part awesome : you get a map with space and walls, and a list of instructions. Following the instructions, move
through the map to find out where you end up. The second part revealed that the map was actually a cube, flattened out,
and you need to redo it, given that being on the side of a cube changes orientation of the instructions. I haven't yet
understood how the orientation works.

https://adventofcode.com/2022/day/22

### Day 21: Monkey Math

First part fun, second part annoying - 36s to hill climb a result. You have a troop of monkeys which either yell out a
number, or are listening for two other monkeys, at which point they perform some operation on those two numbers and yell
that out. The second part saw you having to decide what number a particular monkey yelled out to get a given result -
and I couldn't figure out how else to do it apart from try different numbers and see if you're getting warmer or colder.

https://adventofcode.com/2022/day/21

### Day 20: Grove Positioning System

Sequences of numbers, which define how to move them around to "decrypt" them; the trick being you need to keep track of
the original order, which has a couple of nice gotchas. Second part just made them very long.

https://adventofcode.com/2022/day/20

### Day 19: Not Enough Minerals

Super excited by this one, but haven't yet figured out how to do it. 4 different kinds of robots, each mining a
different kind of ore, and then you have recipes to use different amounts of ore to make the robot. Aim is to end with
the most of the ore (actually geodes) collected by the most complicated robot. You're given different recipes to
evaluate - but end up having to make decisions (do I use up minerals early to make more simple robots, or save them up
for the most complex ones)

Update : absolutely loved this one. Wrote it up.

https://adventofcode.com/2022/day/19

### Day 18: Boiling Boulders (1/2)

A 3d puzzle. Model cubes in 3d and for part 1, work out if any cubes are touching another; part 2 I think is to find
contiguous space between cubes.

https://adventofcode.com/2022/day/18

### Day 17: Pyroclastic Flow

Tetris ! Given a cavern with 5 suspiciously Tetris shaped rocks falling down and stacking up on each other, work out how
high the stack will be at a given time.

Part 1 : 2,022 rocks, no worries.

Part 2 : 1,000,000,000,000. Logic will take 11 years. On a hunch, I thought we might get a repeating sequence - and we
do, so it's a matter of finding it and then using it.

https://adventofcode.com/2022/day/17

### Day 16: Proboscidea Volcanium (1/2)

Loved it. Super challenging - build a graph of rooms, some of which have useful valves in, then work out how to travel
around those rooms in limited time, turning certain valves on to maximise the pressure. So I needed to (a) parse the
input and build the graph (b) use A* to build up a map of distances, and then (c) create an array of states - at this
point I have visited these rooms and turned these valves on a these times, and use a BFS to figure out the best route.

Part 1 done. Part 2 changes it so it's you and an elephant (!) both walking around and turning valves on. I think I can
extend my approach - but reading the subreddit, part 2 seems to take a lot longer.

https://adventofcode.com/2022/day/16

### Day 15: Beacon Exclusion Zone (0/2)

Driving me up the wall : not complete yet. Given an array of sensors, each of which can pick up the closet beacon, use
the overlapping areas to figure out where a missing beacon CAN'T be. Currently got example working, but not part 1.

https://adventofcode.com/2022/day/15

### Day 14: Regolith Reservoir

Much easier and nice to do. Drip sand onto a pile of rocks, figuring out which way it goes, until it fills up.

![Part 1](images/2022-14.1.png)

![Part 2](images/2022-14.2.png)

https://adventofcode.com/2022/day/14

### Day 13: Distress Signal

Frustrating. Given some arrays of arrays (recursive) figure out if they are equal.

```
[1,1,3,1,1]
[1,1,5,1,1]

[[1],[2,3,4]]
[[1],4]
```

Got there eventually.

- Read the damn question carefully. Now read it again.
- Realize that they are JSON lists, so you don't actually need to write your own parser.
- Learn how Comparators work, and how you deal with lists that don't ever have equal items.

https://adventofcode.com/2022/day/13

### Day 12: Hill Climbing Algorithm

Nice. As it says, a hill climbing algorithm. You need to construct a 3d model, and then navigate to the top under
certain rules. I used A* and it worked just fine. Did see a lovely example on Reddit using Minecraft, though.

https://adventofcode.com/2022/day/12

### Day 11: Monkey in the Middle

Awesome. First part is reasonably straight forward, just detailed - you need to script some monkeys handling items and
changing them before passing them around. Second part gets challenging, with very large numbers - bigger than longs.
However, the question gives you an insight into how you can manage this.

https://adventofcode.com/2022/day/11

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
all the trees between it and the edge) and then how big an area you can see around it. Cross
reference [day 8](https://adventofcode.com/2021/day/9) last year,

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

![Part 2](images/2022-05.1.png)

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

