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


def classes():
    pass


if __name__ == '__main__':
    # data(2021)
    classes()
