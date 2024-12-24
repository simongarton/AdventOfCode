import os

year = 2024

filename = '/Users/simongarton/projects/java/AdventOfCode/src/main/resources/{}'.format(year)
for file in os.listdir(filename):
    if not '1-sample' in file:
        continue
    full_name = filename + "/" + file
    new_name = full_name.replace("-1-sample", "-sample")
    print(full_name, new_name)
    os.rename(full_name, new_name)
