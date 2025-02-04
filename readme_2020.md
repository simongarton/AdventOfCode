# Advent of Code 2020

## 2020

### Day 14: Docking Data

Build a binary processor that works with values and applies masks.

https://adventofcode.com/2020/day/14

### Day 13: Shuttle Search

Periods and cycles. Part 1 straight forward, part 2 needs a clever solution. The subreddit suggested Chinese Remainder
Theorem, but my maths isn't up to it. However, other examples suggested periodicity, and I translated an algorithm which
then made sense.

https://adventofcode.com/2020/day/13

### Day 12: Rain Risk

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

### Day 3: Toboggan Trajectory

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
