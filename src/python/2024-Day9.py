

with open('sample.txt') as f:
    data = f.readline().strip()

print(data)

expanded = ''
i = 0
id = 0
while True:

    count = int(data[i:i+1])
    expanded = expanded + (str(id) * count)
    id +=1
    i = i + 1
    if i == len(data):
        break
    count = int(data[i:i+1])
    expanded = expanded + ('.' * count)
    i = i + 1

print(expanded)

shuffled = ''
reversed = expanded[::-1]
back_index = 0

for front_index in range(0, len(expanded)):
    front = expanded[front_index:front_index + 1]
    if front == '.':
        shuffled += reversed[back_index]
        back_index += 1
    else:
        shuffled += front
    print(shuffled, " ", expanded)
    if (len(expanded) - back_index) == front_index:
        while(len(shuffled)< len(expanded)):
            shuffled += "."
        break
print(shuffled)
