def count(cfg, nums):
    print(cfg, nums)
    if cfg == '':
        print("scoring cfg " + str(1 if nums == () else 0))
        return 1 if nums == () else 0

    if nums == ():
        print("scoring nums " + str(0 if '#' in cfg else 1))
        return 0 if '#' in cfg else 1

    result = 0

    if cfg[0] in '.?':
        result += count(cfg[1:], nums)

    if cfg[0] in '#?':
        if nums[0] <= len(cfg) and '.' not in cfg[:nums[0]] and (nums[0] == len(cfg) or cfg[nums[0]] != '#'):
            result += count(cfg[nums[0] + 1:], nums[1:])

    return result


total = 0
# for line in open('2023-Day12-1.txt'):
for line in open('sample.txt'):
    cfg, nums = line.split()
    nums = tuple(map(int, nums.split(',')))
    total += count(cfg, nums)

# print(total)
