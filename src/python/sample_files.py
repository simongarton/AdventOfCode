for i in range(2, 26):
    filenames = [
        '../main/resources/2025/2025-Day{}-1.txt',
        '../main/resources/2025/2025-Day{}-2.txt',
        '../main/resources/2025/2025-Day{}-1-answer.txt',
        '../main/resources/2025/2025-Day{}-2-answer.txt',
        '../main/resources/2025/2025-Day{}-sample.txt'
    ]
    for filename in filenames:
        with open(filename.format(i), 'w') as output:
            output.write('0')
