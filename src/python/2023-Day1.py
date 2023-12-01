data = {
    "one":1,
    "two":2,
    "three":3,
    "four":4,
    "five":5,
    "six":6,
    "seven":7,
    "eight":8,
    "nine":9
}

data2 = [
    ["one",1],
    ["two",2],
    ["three",3],
    ["four",4],
    ["five",5],
    ["six",6],
    ["seven",7],
    ["eight",8],
    ["nine",9]
]

def load_data():
    filename = '../main/resources/2023/2023-Day1-1.txt'
    # filename = '../main/resources/2023/sample.txt'
    with open(filename, 'r') as input:
        lines = input.readlines()

    return [line.replace('\n','') for line in lines]

def is_number(c):
    try:
        int(c)
        return True
    except:
        return False


def first_and_last(original_line, line):
    f = None
    l = None
    for c in line:
        if is_number(c):
            f = c if f is None else f
            l = c
    # print("{} -> {} = {}".format(original_line, line,int(f + l) ))
    return int(f + l)


def figure_number(line):
    result = ''
    position = 0
    while position < len(line):
        found = False
        replacement = line[position:position + 1]
        for thing in data2:
            if found:
                break
            text = thing[0]
            number = thing[1]
            if position + len(text) > len(line):
                continue
            if line[position:position + len(text)] == text:
                found = True
                replacement = str(number)

        position = position + len(replacement)
        result = result + replacement


    answer = first_and_last(line, result)
    print('{} : {}'.format(answer, result))
    return answer


def part1():
    lines = load_data()
    total = 0
    for line in lines:
        total = total + figure_number(line)
    print(total)

def part2():
    lines = load_data()


def run():
    part1()
    part2()



if __name__ == '__main__':
    run()



