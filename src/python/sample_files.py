for i in range(6, 26):
    filenames = [
        '../main/resources/2024/2024-Day{}-1.txt',
        '../main/resources/2024/2024-Day{}-1-answer.txt',
        '../main/resources/2024/2024-Day{}-2-answer.txt',
        '../main/resources/2024/2024-Day{}-sample.txt'
    ]
    for filename in filenames:
        with open(filename.format(i), 'w') as output:
            output.write('')
