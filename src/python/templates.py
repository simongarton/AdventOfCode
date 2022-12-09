import os


def create_file(full_name):
    if os.path.exists(full_name):
        return
    with open(full_name, 'w') as output:
        output.write("1\n")


def data(year):
    root = '/Users/simongarton/projects/java/AdventOfCode/src/main/resources/{}/{}'
    for day in range(1, 26):
        create_file(root.format(year, '{}-Day{}-1.txt'.format(year, day)))
        create_file(root.format(year, '{}-Day{}-1-answer.txt'.format(year, day)))
        create_file(root.format(year, '{}-Day{}-2.txt'.format(year, day)))
        create_file(root.format(year, '{}-Day{}-2-answer.txt'.format(year, day)))


def classes(year):
    root = '/Users/simongarton/projects/java/AdventOfCode/src/main/java/com/simongarton/adventofcode/year{}/Year{}Day{}.java'
    for day in range(1, 26):
        from_name = root.format(year, year, 0)
        to_name = root.format(year, year, day)
        if os.path.exists(to_name):
            continue
        with open(from_name, 'r') as input:
            lines = input.readlines()
        new_lines = []
        for line in lines:
            new_lines.append(line
                             .replace('Day{}'.format(0), 'Day{}'.format(day))
                             .replace(', {});'.format(0), ', {});'.format(day)))
        with open(to_name, 'w') as output:
            output.writelines(new_lines)
        

if __name__ == '__main__':
    # data(2021)
    classes(2021)
