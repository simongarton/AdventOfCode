# Advent of Code

[Advent of Code](https://adventofcode.com/) is an annual programming competition, with initially simple puzzles starting
on 1 Dec, and getting progressively harder - I rarely finish the last few days. I started in 2019, and have been picking
up more and more each year (plus going back and looking at old ones.)

I've also been loudly praising it at work, with a
private leaderboard ... which got nicely
competitive, and 2024 is looking pretty good.

![Progress](AdventOfCode.png)

Performance is generally good : green is <= 1s, yellow <= 5s, orange <= 10s, red <= 30s and purple needs another look.
Plenty of days when I was so happy just to get the damn thing working, it wasn't worth optimizing.

![Progress](AdventOfCodeSpeed.png)

## 2024

## Day 25 : Code Chronicle (1/1)

Day 25 is always easy and is always only 1 part - the second part is having 49 other stars by now, which ... I don't
quite. Let's go back and try and fix them.

https://adventofcode.com/2024/day/25

## Day 24 : Crossed Wires

Very nice. Build a circuit of wires and logic gates, and analyse it. Yeah, OK. Oh, wait, 4 pairs of the gates are cross
wired - you can figure it out by looking at the inputs and outputs. Well, there's a lot of combinations to check ...

![Part 2](2024-24.2-original.png)

I love [GraphViz](https://graphviz.org/). Dumped it out, added some colors - and I could spot 3 of the 4 problems : the
wiring is different. But I couldn't spot the fourth ... until I looped through the 45 powers of 2 and tested if my
circuit worked adding two numbers, and it broke at a certain pair of inputs ...

https://adventofcode.com/2024/day/24

## Day 23 : LAN Party

Nice. Given a bunch of pairs - connected PCs - work out whos in a network of at least 3 ... and for part 2, work out the
largest network. I quite liked my solution for part 2 - build up alphabetical lists, for each pair, check to see if the
first is in the network, the second isn't yet ... but can join if it connects to all the others there.

https://adventofcode.com/2024/day/23

## Day 22 : Monkey Magic

Easy, probably making us feel better for the difficult ones over the weekend. Manipulating lists of numbers. My solution
takes a few seconds, so could probably be optimized, but I need to get back to days 20 and 21.

https://adventofcode.com/2024/day/22

## Day 21 : Race Condition (1/2)

Brilliant. But my nemesis.

Imagine a simple numerical keypad, and you have to type out a 3 digit number and press # (well, A). Only you're doing
this remotely, and so have a 4-way directional pad to move a pointer around the keypad until it's over the number you
want, when you can push it. Only you can't even do that ... there's a chain of keypads, and you need to write commands
for a directional pad to drive a second directional pad which actually operates the numerical one.

After some days of effort, I've got part 1 working very fast, 27ms. But part 2 bogs down in very deep BFS searches - and
I think the way I've done this isn't going to make speeding it up easy.

https://adventofcode.com/2024/day/21

## Day 20 : Race Condition

This was fun, even though I don't have part 2 complete yet.

It's a maze with a single path through it. But for part 1, you can knock down a single wall. How many times does this
help you get to the end, quicker ? And it's a doozy of a maze - you're going from green to red.

![Part 1](2024-Day20-1.png)

Part 2 then became evil : you can knock up to 20 walls. Actually this is getting a bit vague, and I may have
misinterpreted the question. Does the phrase `(but can still only end when the program is on normal track)` mean that
if you're carving a shortcut, you have to stop _at the first normal track_ or can you skip over some and go into
more walls ? It's the latter.

Here's an example.

![Part 2](2024-Day20-2.png)

Bright green and red are the start and end; purple is the 'origin' of the shortcut, where I am on the track before I
step off into the blue portal and pop out on the yellow one (incurring travel costs, though) before
continuing on the red. This saved 60-odd picoseconds - obviously going straight down and popping out higher on that last
bit would be better.

I'm getting the first couple of counts correct (3 cheats that save 76 picoseconds, 4 for 74) but am only finding 17
cheats that saves 72 picoseconds - and there should be 22. Been staring for too long. It's also really slow, so not the
right approach.

And then I suddenly realized that most of the question is irrelevant. The cheats have to start and end on the trail, so
I don't need to worry about walls or anything - just iterate over the 3k steps in the trail twice, checking the distance
you'd save by short-cutting between two points and checking the manhattan distance. :facepalm.

<deletes many many lines of code.>

https://adventofcode.com/2024/day/20

## Day 19 : Linen Layout

Lots of recursion and memoisation. Made part 2 far too hard, went to read Reddit, realised I could chop out about 90% of
my code, move the cache to a different place, and away we go. Sometimes the numbers are so stupidly big, it becomes
obvious you're on the wrong path.

https://adventofcode.com/2024/day/19

## Day 18 : RAM Run

And I thought the Day 16 map was evil. A* delivers a shortest path very quickly, but the really shortest one
takes AGES to find.

![Part 1](2024-Day18-1.png)

But part 2 was disappointingly easy, just find when you've added too many walls to navigate the maze.

https://adventofcode.com/2024/day/18

## Day 17: Chronospatial Computer (1,2)

A famous OpCode computer. I've really struggled with these in the past, but this time I persevered, and got
part 1 working - I'd written the computer which reads the program, it was passing all the tests, but
failing on the real data, so I hand-coded the specific program into a 10 line Java file and spotted
a bug.

Part 2 gave me a lot of trouble. Tried brute-forcing it, obviously not going to work. Spent a lot of time staring at my
compiled version, trying to work out what would do. Then reading some [Reddit](https://www.reddit.com/r/adventofcode/)
articles, two facts slowly became obvious:

- "3 bits" and all the mod 8, % 8 made it obvious we should be working in octal numbers
- the loop in the code basically tests the last 3 bits of the a register, and shifts values on each loop, meaning I am
  working with just 16 numbers - the 16 numbers of the program, being eight opcodes and eight operands.

What I needed to do was find a number that would go through _a single iteration_ of the loop and emit the last number of
the program; then I need to multiply it by eight, and find another number that when added, would go through _two loops_
, emitting the last _two numbers_ of the program; and so on.

I got a simple version working, which produced something almost right; detailed inspection revealed it wasn't finding a
couple of numbers and was (ahem) defaulting to zero for those; I then noticed that a previous stage had more than one
option, so I need Djikstra to find all the branches.

That led on to a late night, but eventually tracked down a typo - ending a range too early - and an int/long precision
issue, and it all works beautifully. Phew.

This diagram shows the eventual path I found. I built it up left to right, but the computer will run right to left. My
actual value ended up being the green line, but you can see other paths which would have led me to other starting
numbers - looks like I have nine that would have worked - and you can also see some branches that just die.

![Part 2](2024-Day17-2.png)

https://adventofcode.com/2024/day/17

## Day 16: Reindeer Maze

Sometimes you just get stuck. Today was a maze-navigating algorithm, straight forward enough, except that making a turn
counts as a move, and is expensive ... meaning you have to be careful about pruning nodes. With hindsight, and looking
at my final code, it was reasonably straight forward with A* ... but I got stuck with a solution that was so almost
right ... both samples working, part 1 mapped out looks sane, animated as movies it looked sane ... but "That's not the
right answer."

![Part 1](2024-Day16-1.png)

Part 2 just added a level - there are multiple equivalent paths, and by default A* only gets the first. A very bright
friend really struggled with this - they've been beating me consistently all month - but because I'd spent so much time
on part 1, I must have been in the zone.

https://adventofcode.com/2024/day/16

## Day 15: Warehouse Woes

Lovely. Those dang robots again, only this time they are pushing boxes around in a warehouse ...
but if the boxes pile up against a wall, they stop.

Part 1 was straight forward. Part 2 was wonderfully devious, the boxes got twice as wide, _and so
would overlap_ if you pushed them in a certain way. Heaps of fun.

Wrote far too much code, but got the answer.

https://adventofcode.com/2024/day/15

## Day 14: Restroom Redoubt

Much nicer. A map, with robots moving around in different directions. Part 1 : at time N, count how many are in each
quadrant. Easy. Part 2 : at time X, they draw a picture of an Xmas tree : what's X ?

Wait, what ? How do I know what it should look like ? I spent some time thinking it was going to be a pointy empty
shape - a triangle - but seemed to be getting into the millions of iterations. Tried again with a solid shape, assuming
everyone would have a neighbour. Wasn't getting anywhere ... so I changed it to check to see if at least half of them
had a neigbour ... and this popped out after about seven thousand cycles (and repeatedly afterwards.)

![Part 2](2024-Day14-2.png)

https://adventofcode.com/2024/day/14

## Day 13: Claw Contraption

One of the deceptively simple ones, which bit hard. Given two buttons, each with a different deltaX, deltaY and a cost
try and hit a target with the number of minimum presses (and costs.)

Spent some time getting A* and Djikstra running and then realized that was total overkill.

Came up with a nice little mathematical solution : pick one button, mash it until you're about
to overshoot the prize on one axis, then back up one press at a time and hit the other button
lots of times until you overshoot again OR hit the target.

That worked fine for part1, but part2 has very big numbers. I have added a couple of optimisations
to my logic, but it's taking 10s of seconds and there are 400+. I'm also not seeing any hits yet ...
though it works for the part1 data.

I've tried some optimisations and I have smart algorithms working fine for the simple case, but overshooting -
wildly - with the big numbers. So I think I have a rounding error somewhere.

Spent far too long on it. Came back to it once all was quiet in the house ... and found not one but two places
where I was rounding numbers with 15 digits of precision :facepalm.

It now works. Oddly enough there are a couple of ones where it pauses for a good few seconds ...

```
[A: 16,70 B: 45,48] -> 10000000002546,10000000004886 failed in 33043ms
[A: 14,87 B: 67,67] -> 10000000002338,10000000004747 failed in 21540ms
[A: 19,99 B: 81,83] -> 10000000005945,10000000007921 failed in 8848ms
```

But it's time to move on.

https://adventofcode.com/2024/day/13

## Day 12: Garden Groups

Really enjoying this now. This was a 2d map of gardens identified by letters, and you have
to work out the contiguous area of each patch - including worrying about ponds inside islands
inside lakes etc. Came up with a nice little flood-fill approach ... which when tidying this up
I think it's not actually as efficient as it could be - but it worked.

Part 2 - the next 1 1/2 hours - is calculating the perimeter of the shapes BUT straight sides
are always length one. This had some nasty gotchas - inside the smallest letter C you can build
is an empty space that has 3 sides, in 3 different directions - and I had to write code
to print it out to see what was going on. 5 sample files.

![Part 2](2024-Day12-reverse-terrain.png)

https://adventofcode.com/2024/day/12

## Day 11: Plutonian pebbles

Loved it ! Take a list of numbers, and iterate with different rules : only 3 rules, but
crucially one of them will split the number into two. Which means exponential growth.

Part 1 was quick, part 2 I knew was going to be painful ... the first approach worked,
the second approach - split the original list into individual numbers and take it from there -
fell over a step later ... and that was the clue, recursion would be my friend. And of course,
having done this challenge for 6 years now, if you recurse, you're probably going to need to short
cut with memoisation. Wrote the code, ran it, and it finished instantly. Oh bugger, it crashed, I
thought. Nope, right answer in <smug_mode> 85 milliseconds </smug_mode>.

This is an example at 10 levels - part two was 75 - showing the cache hits in green.

![Part 2](2024-11.2-level-10.png)

https://adventofcode.com/2024/day/11

## Day 10: Hoof it

OK, now I'm happier again. A map and all about gradients : find paths from low to high, that are orthogonal and always
go up. Took about 1:15 for the first part and 30 seconds for the second part as I'd already got the info I wanted.

https://adventofcode.com/2024/day/10

## Day 9: Disk Fragmenter

... mutter mutter, grumble grumble ... whose idea was this, anyway ?

In reflection, a really good puzzle. With probably the most devious "omission" I have seen from an AdventOfCode :
the examples carefully dealt with ids going no further than 9, packing them into a string, where the ID takes up
one character. So what happens when you get 10 ?

First attempt worked fine (hah !) on the small and big samples, but fell over with a nasty range check error on
the real data. Looking at it, I did notice that I had overlaps ... I'm tracking two pointers, one moving up a string
and one moving down, and they had overlapped. Handling it explicitly gave me an answer, but it was too low.

In frustration I turned to Python which is far better for string manipulation. But same problem.

So I went and had dinner. And came back and thought ... I haven't really handled those file IDs properly. Instead
of treating it as a string, let's treat it as an array of integers. And low and behold (a) the right answer in (b)
about 25% of the code I had previously used.

Harrumph.

https://adventofcode.com/2024/day/9

## Day 8: Resonant Collinearity

A good challenge, but grumpy with myself. Again. 2024 is heading downhill at this point ... part 1 took 2:16, but part 2
only took 2 minutes. It's another map, with antennas, and you have to figure out where they interact ... I managed to
go down three different wrong paths before eventually figuring it all out, at which point part 1 was a simplified
version of part 2.

https://adventofcode.com/2024/day/8

## Day 7: Bridge Repair

A good challenge, but grumpy with myself. Take an array of numbers and some operators, and see if you can combine them
in a way to get to an answer. For part 1 I spent far too long trying to work out how to get combinations; for part 2
I merrily spent half an hour solving a slightly different (and much harder) question - but reviewing the challenge,
it did say what I should have done.

https://adventofcode.com/2024/day/7

## Day 6: Guard Gallivant

Excellent. A map with obstructions, and you have to map out where a guard walks, following certain rules. Part 2
involved placing additional obstructions to try and get him to walk in an infinite loop. I came up with a brute force
solution for this which took some minutes ... so came up with two optimisations : only put obstructions where the guard
would have walked anyway; keep track of where I have been each time _and in which direction I was going_ and if I
repeat, then I must be in an infinite loop. Now finishing in about 86 seconds.

![Part 1](2024-06.1.png)

https://adventofcode.com/2024/day/6

## Day 5: Print Queue

Excellent. A list of pairs of pages, where for A|B A must go before B in any combined list; and then a series of
combined lists, so you have to make sure all the pairs are valid. So A,B,C is valid; C,A,B is valid, but not B,A,C.

Part 2 : some of them weren't valid, but could be : sort them out. I tried brute force and ran out of memory; then I
tried building up a new list one item at a time, and that worked. My friend Rudy tried a Comparator which sounds
way more sophisticated.

https://adventofcode.com/2024/day/5

## Day 4: Ceres search

Given a grid of letters, do a word search. I started off with a nice clever algorithm using objects and recursion,
which worked perfectly with the tiny sample data, but gave me one extra for the bigger sample. After some lengthy
investigation, I worked out I was being too clever - and my words were changing direction, which wasn't allowed. Then
adding a limit to changing direction, I then realised I had no efficient way of trying to start from the same X twice.

So I threw that all away, and an hour in, started over with string manipulation. Took 30 minutes to get the first part
and a further 10 for the second part. Kicking myself.

Good puzzle, though.

https://adventofcode.com/2024/day/4

## Day 3:  Mull it over

Parse a string - no, wait, six strings (:facepalm) - looking for valid calculations inside garbage text.  
Severe bruising on leg following kicking myself for not having learned regex.  
Then got stuck on part 2 until I realised I cannot treat the strings individually. That was evil for day 3, Eric. We
were saying day 2 was disappointingly easy ...

https://adventofcode.com/2024/day/3

## Day 2:  Red-Nosed Reports

Server crashed several times while trying to get puzzle and submit answers :rofl  
Straight forward - suspicious, not sure what's going on here - parse data into lists, and look
at deltas.

https://adventofcode.com/2024/day/2

## Day 1:  Historian Hysteria.

Spent more time trying to find the IntelliJ full line completion setting to turn it off - I'd already turned off
Copilot - than I had hoped. Straight forward read two lists, sort, compare.

https://adventofcode.com/2024/day/1

## 2023

## Day 25: Snowverload. (0/2)

Lovely challenge. I can dump a graph into GraphViz and _almost_ see the solution - I can see 3 edges that link the two
clusters, just can't read their nodes - but can't figure out how to do it. I have a fast solution for the sample. For
part 1, of course. Part 2 is going to take a further 27330.794 seconds ...

https://adventofcode.com/2023/day/25

## Day 24:

Part 1 was nice enough : hailstones flying around in 3 dimensions, will a given pair collide within a given volume ?
To make it manageable, part 1 was only in 2 dimensions, so was about figuring out and solving pairs of linear equations.

Part 2 introduces the 3rd dimension, and for the hell of it, adds a 4th dimension, time. Given all these hailstones,
where do you have to start throwing a rock, and in which 3d dimension, to hit all of them ? Probably math.

https://adventofcode.com/2023/day/24

## Day 23: A long walk. (1/2)

Route finding, A*, priority queue, yeah, yeah, done this before ... wait, you want the _longest_ distance ?

Turns out to be one of the best bits of logic / coding I think I've ever done. Use DFS to split the map into a graph
of nodes and edges, where the nodes are intersections. Then use A* to find the shortest path between each pair of
nodes - only some are valid, given slopes. Then finally use that to build a new graph and use DFS to find all the
(valid) routes between the start, intersections and nodes.

This was very important - mapping out by hand in Sheets where the sub-routes could go.

![Part 1](2023-23.1.png)

I might just leave part 2 for another day.

https://adventofcode.com/2023/day/23

## Day 22: Sand Slabs

Very nice. 3d Tetris / Jenga : a few shapes, made up of cubes, floating in positive integer space, let them all fall and
then count the ones you can remove without affecting anything else. Hopefully someone has done this in Minecraft.

The second part is going to be some kind of recursive trick : if I disintegrate A, which causes B and C to fall, what
then also falls ? As long as you (spoilers) keep track of which ones have been removed, it's easy. Just takes about
4 minutes.

https://adventofcode.com/2023/day/22

## Day 21: Step Counter (1/2)

Nice ! Immediately thought this looks like a cellular automaton, and indeed Part 1 fell very nicely.

Started reading part 2 : "the inexplicably-infinite farm layout." Deep sigh. I think this should be a feature of the
fact that it's a regular layout, so I expect the CA pattern to start repeating, and I just need to find some way of
figuring out the period, and how much it is expanding by. I found with the sample (11 x 11) that it repeated every
11 (!) cycles, and that I could predict on each of those 11-step boundaries, how many "complete" - once filled, they
just oscillate between two states - subgrids (11x11) I would have and and how many plots they each would contain; but
(a) I don't know how many incomplete subgrids I have and (b) it doesn't work with the real 131x131 grid.

https://adventofcode.com/2023/day/21

## Day 20: Pulse Propagation (1/2)

Nice ! A set of modules that receive input signals, modify them and send them on, into a network. I initially got
bogged down in worries about timings before eventually re-reading the question very carefully, rebuilding it all
and then it worked nicely.

Well, part 1 did. I have no idea on how to do part 2.

![Part 1](src/graphs/modules.png)

https://adventofcode.com/2023/day/20

## Day 19: Aplenty (1/2)

A decision tree : a list of workflows, involving rules and criteria pointing you to different workflows.

Part 1 was easy.

Part 2 mentions a number `167409079868000` which just screams "brute force will not work !". I think I have a plan in
mind : I'm building up a graph of routes through the rules; each keypadNode has ranges of values for x,m,a,s, and each
time
I arrive at a keypadNode, I need to subdivide those ranges among the possible destinations. But this is complicated and
I'm
tired.

https://adventofcode.com/2023/day/19

## Day 18: Lavaduct Lagoon (1/2)

Part 1 reasonable enough, another grid problem and follow instructions to carve out a tunnel; then flood-fill it to
work out how much space is enclosed. Curiously the instructions include wall colors, which I implemented fearing it
would be needed later - but wasn't.

![Part 1](holey-moley-filled.png)

Part 2 extends the grid to 14,207,222 x 20,211,216 and I am at a loss to work out how to even start this without running
out of memory. I suspect it will be something like ray-marching, but to be continued.

https://adventofcode.com/2023/day/18

## Day 17: Clumsy Crucible

Defeated completely. Superficially it's a route-finding algorithm, but it's a minimal cost from start to end _while
never taking more than 3 steps in one direction_ (and also only turning, never backtracking : I don't think this is an
issue though.)

I've got a complex idea, which is giving me a result - the wrong one - and I eventually gave up. Read some more stuff
on reddit, adapted a new algorithm and initially it was wrong ...

![Part 1](2023-17-1-sample.png)

... but I finally figured out it was an equals/hashcode problem !

![Part 1](2023-17.1.png)

https://adventofcode.com/2023/day/17

## Day 16: The Floor Will Be Lava

Heaps of fun, a grid with mirrors and splitters directing light beams. There is a trick to it, which I eventually
tumbled to after reading the reddit : the beams split infinitely, _but you don't need to worry about tracing an infinite
number of beams_ : as soon as you hit a tile that you have energised before, going in the same direction as last time,
you can stop that beam. Part 2 trivial.

https://adventofcode.com/2023/day/16

## Day 15: Lens Library

Easy enough : ascii, hashcodes, and parsing strings & managing lists. And selling a truck.

https://adventofcode.com/2023/day/15

## Day 14: Parabolic Reflector Dish

Sliding rocks on a grid. Was at an AWS thing for the first part so delayed start, then got stuck with logic until
I simplified it ;-) Then part 2 took nearly 24 hours : I almost had it right several times, but each time I fixed it
I broke something else, until I could get to the gym and plan it out in my head.

https://adventofcode.com/2023/day/14

### Day 13: Point of Incidence

Ok, an easy one to help me get over yesterday. Looking for lines of reflection in a grid. Curiously part 2 didn't add
that much more processing time : a more complex algorithm, sure, but still brute-forcey, and just 63ms vs 21ms for
part 1.

¯\\_(ツ)_/¯

https://adventofcode.com/2023/day/13

### Day 12: Hot Springs

First part 2 failure of this year. Given a string with various symbols and different replacement options, how many
groups can you make (paraphrased.) Part 1 was fun, part 2 increased the size by a factor of 5 and just takes too long.

I have explored some ideas about recursing into it and abandoning branches that are never going to work, but it still
takes far too long.

Update several days later : I ended up adapting [this solution](https://www.youtube.com/watch?v=NmxHw_bHhGM) by
a young Canadian called HyperNeutrino : it's the best explained version I found on Reddit. What is really interesting
is how much extra work I had to do with Java and string manipulation that just works in Python.

https://adventofcode.com/2023/day/12

### Day 11: Cosmic Expansion

Not sure if I love or hate this one <grin> Ostensibly it's a map, with the sum of shortest distances; the first wrinkle
is that certain rows and/or columns are twice as wide/deep as others - yeah, I can manage this in memory - then part 2
means they are 1,000,000 times as wide/deep as others. Madness. Took about 90 minutes, answers < 200ms.

https://adventofcode.com/2023/day/11

### Day 10: Pipe Maze

Loved it ! And made up for the easy yesterday. A maze / topology problem with a couple of really nice twists : a
continuous pipe winds around, eventually meeting itself. Given a starting point, (i) what's the furthest point and (ii)
how much space is ENCLOSED by the pipe. The two twists are (a) a lot of junk pipes, which look as though they should
connect, but don't; and (b) the pipes run side by side, meaning you can't use simple ray-casting or flood-fill
algorithms to solve for point-in-polygon.

So this is one of the samples : blue is space enclosed, red is space NOT enclosed, which includes the central area.
White is the start point.

![Part 2](2023-10-2-sample.png)

And this is the beast.

![Part 2](2023-10-2.png)

https://adventofcode.com/2023/day/10

### Day 9: Mirage Maintenance

Curiously easy ? Lists of sequences, for each figure out the next item. The worked examples made it very clear how to
do it, and even the "Surely it's safe ..." warning about how to handle part 2 ... turned out to be safe.

¯\\_(ツ)_/¯

https://adventofcode.com/2023/day/9

### Day 8: Haunted Wasteland

I've seen this before ... basically you hop about through a map until you find an end; the first part is trivial,
the second part is having to do six at once, and all 6 must hit the end at the same time. Can't do it brute force,
but if you look for patterns, each one of the six is repeating a cycle; and we then just need to find the least
common multiple.

https://adventofcode.com/2023/day/8

### Day 7: Camel Cards

Another nice example, where the first part is manageable, but the second part needs a new approach. Playing poker,
work out from a list of hands what the best hand is - that's the first part, the second part then adds wild cards.

Including 5 wild cards which kind of breaks the brute force approach unless you're expecting it. 30 seconds.

https://adventofcode.com/2023/day/7

### Day 6: Wait for it

Phew, a slightly more sensible one. Optimization : given a distance to travel, how do you optimize spending time
at the start to get going fast vs getting going quickly ? First part easy to iterate over every step , second part
too big but easy to convert to maths.

https://adventofcode.com/2023/day/6

### Day 5: If You Give A Seed A Fertilizer

I read part 1 and thought : uh-oh. And I was right. Basically a set of ranges, and you traverse through them, hopping
from one to another, to try and end up on the smallest end value. Which is easy when you have a few points, but not
when end up with over 4 billion numbers to check.

Luckily my mate Craig explained how he did it, and that gave me an idea.

https://adventofcode.com/2023/day/5

### Day 4: Scratchcards

OK, here we go : part 1 deceptively simple, part 2 on of
those [Orb](https://en.wikipedia.org/wiki/A_Huge_Ever_Growing_Pulsating_Brain_That_Rules_from_the_Centre_of_the_Ultraworld)
-like problems which get a bit out of hand. Tribble-like, to choose another metaphor. Got stuck with concurrent
modifications on the list and it's iterator but banged out a solution just in time.

https://adventofcode.com/2023/day/4

### Day 3: Gear Ratios

Still happy. Read a map and place various entities : some are single challengeCoord, some are rows of several coords
which makes it trickier. Good data structures and supporting methods, so part 2 was straight forward.
Top 3k for both parts so `<smug_mode>on</smug_mode>`

https://adventofcode.com/2023/day/3

### Day 2: Cube Conundrum

Faith restored in myself. Simple string parsing, and having done this competition a few times, I know to set up
some nice sensible data structures in the first part, 'cos they'll probably be useful in part 2. Into the top 4k
globally which feels a bit better than not making the top 10k on part 2 yesterday.

https://adventofcode.com/2023/day/2

### Day 1: Trebuchet ?!

Ostensibly simple, I took a couple of hours to do this because I misinterpreted part 2. Ironically, only when rewriting
it a third time - this time switching to Python - and getting the logic wrong but getting the right answer did I figure
out what the problem was.

It's to do with finding digits in a string : find the first and last and append them, so `x3jd7gshr8kg` becomes `38`.

Part two involved spelled out numbers e.g. `a3eightwox` and this is where I went wrong : I assumed that because `eight`
was
in there, I should substitute `8` and continue with `a38wox` leading to `38`; but what they meant was (easier) just find
the last written number, so the `two` should still be valid, hence `32`. :facepalm.

https://adventofcode.com/2023/day/1

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

![Part 2](2022-24.2.png)

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

![Part 1](2022-14.1.png)

![Part 2](2022-14.2.png)

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

![Part 2](2022-05.1.png)

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

### Day 25: Sea Cucumber

A map with the sea-floor covered in sea cucumbers; at each tick, they either move right or down, scrolling around the
map, but won't move onto another ... how many ticks until they get gridlocked ?

https://adventofcode.com/2022/day/25

### Day 21: Dirac Dice

Yes, a big numbers one. Part 1 is a simple little game about two players rolling dice and moving around a circular
track. Part 2 gets cool, and each time you throw the dice, the universe splits into 3, 3 different rolls, and 3
different states of the game ... 3, 9, 27, 81 ... final answer 433315766324816.

https://adventofcode.com/2022/day/21

### Day 20: Trench Map

Two bitmaps, one an algorithm to 'enhance the image' found on the other. Looks straight forward enough, can't remember
if there was a catch.

https://adventofcode.com/2022/day/20

### Day 18: Snailfish

Long strings of multiple-nested brackets with numbers, expand and add items with in it. Complicated.

https://adventofcode.com/2022/day/18

### Day 17: Trick Shot

In 2 dimensions simulate firing shells that arc up and are then pulled down by gravity, see if they hit a target area.
From memory this was disappointingly easy, but I did write a lot of similar stuff for fun years ago.

https://adventofcode.com/2022/day/17

### Day 16: Packet Decoder

String manipulation / decryption. Straight forward and fun.

https://adventofcode.com/2022/day/16

### Day 15: Chiton

Two dimensional topographic map, route find through it to have the lowest total height. A*, though my comments suggest
I was less than confident about it's implementation.

https://adventofcode.com/2022/day/15

### Day 14: Extended Polymerization

String manipulation : parse a string to decode rules which in turn tells you how to update the string.

https://adventofcode.com/2022/day/14

### Day 13: Transparent Origami

This was fun. A big 2d bitmap with some points marked with symbols; fold it repeatedly so the symbols start overlapping,
and when you're done you can visually see a sequence of letters.

https://adventofcode.com/2022/day/13

### Day 12: Passage Pathing

Fun; build a graph and then count the number of unique ways you can travel through it from one end to another.

https://adventofcode.com/2022/day/12

### Day 11: Dumbo Octopus

Fun; a 2d map of octopi, when one flashes, it encourages the other to flash; eventually all are flashing together.
Solved with a cellular automaton.

https://adventofcode.com/2022/day/11

### Day 10: Syntax Scoring

Parsing lots of brackets to figure out if it's valid.

https://adventofcode.com/2021/day/10

### Day 9: Smoke Basin

A map of heights - cross reference https://adventofcode.com/2022/day/8. Wrote what I think is a flood-fill to solve part
2, and it worked first time ! Though I have recreated the code in 2022, can't find the class I used in 2021.

https://adventofcode.com/2021/day/9

### Day 8: Seven Segment Search

Fun - based around 7 segment displays with mixed up wiring, analyse input and output to figure out the wiring. Had to
use some frequency analysis as part of the solution.

https://adventofcode.com/2021/day/8

### Day 7: The Treachery of Whales

Cute : align a group of crabs in a vertical line by minimizing their horizontal moves, using two algorithms.

https://adventofcode.com/2021/day/7

### Day 6: Lanternfish

A population of fish that are breeding at various intervals. Not unlike Wa-Tor.

https://adventofcode.com/2021/day/6

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

## 2018

### Day 1: Chronal Calibration

Summing numbers. Part 1 in one line of code;

https://adventofcode.com/2018/day/1

## 2017

### Day 1: Inverse Captcha

Looking for sequences in strings.

https://adventofcode.com/2017/day/1

## 2016

### Day 1: No Time for a Taxicab

Simple coordinates with left and right turns.

https://adventofcode.com/2015/day/1

## 2015

### Day 1: Not Quite Lisp

The very first AoC puzzle. Simple string manipulation, the first one I did with regex, the second I had to write a tiny
loop.

https://adventofcode.com/2015/day/1