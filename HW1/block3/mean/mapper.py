import sys


sum = 0
count = 0

for line in sys.stdin:
    line = line.strip()
    try:
        line = line.split(",")
        price = int(line[-7])
    except Exception as e:
        continue
    sum += price
    count += 1

key = 0  # only one reducer
avg = sum / count if count > 0 else 0
print(str(key) + "\t" + str(avg) + "," + str(count))
