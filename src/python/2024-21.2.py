import json

'''
140A:87513499934
180A:90594397580
176A:89741193602
805A:86475783012
638A:86475783010

169137886514152
'''

# in desperation - my Java code is so close to being right, but isn't ... I rewrote the second part
# in python, using the sequences generated by the Java code for the keypads.
# and it just worked.
#
# eventually I will go and look at the cached values and try and spot where my Java code is going wrong ..
# ... but it's the same algorithm, and works for part 1 :facepalm

cache = {}

def setup_dirs():

    filename = '../../dirPadPaths.json'
    with open(filename, 'r') as input:
        dirs = json.load(input)
    return dirs

def setup_nums():

    filename = '../../numPadPaths.json'
    with open(filename, 'r') as input:
        dirs = json.load(input)
    return dirs

def setup():

    dirs = setup_dirs()
    nums = setup_nums()

    return nums, dirs


def debug():

    nums, dirs = setup()
    for k,e in dirs.items():
        for k1,e1 in e.items():
            if (len(e1)> 1):
                print('{} {} {}'.format(k, k1, e1))

    for k,e in nums.items():
        for k1,e1 in e.items():
            if (len(e1)> 1):
                print('{} {} {}'.format(k, k1, e1))


def recursive(sequence, level, max_level, nums, dirs):

    key = '{}:{}'.format(level, sequence)
    if key in cache:
        return cache[key]

    if (level == max_level):
        return len(sequence)

    from_key = "A"
    length = 0
    for i in range(len(sequence)):
        to_key = sequence[i:i+1]
        sub_sequences = nums[from_key][to_key] if level == 0 else  dirs[from_key][to_key]
        shortest = None

        for sub_sequence in sub_sequences:
            option = recursive(sub_sequence, level + 1, max_level, nums, dirs)
            if shortest == None or shortest > option:
                shortest = option

        length = length + shortest
        from_key = to_key

    cache[key] = length
    return length

def run():

    nums, dirs = setup()

    total = 0
    for code in [
        '140A',
        '180A',
        '176A',
        '805A',
        '638A'
            ]:

            length = recursive(code, 0, 26, nums, dirs )
            print(code + ':' + str(length))
            total = total + int(code[:3]) * length

    print(total)


if __name__ == '__main__':
    run()
