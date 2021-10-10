import sys

avg = 0
count = 0
for line in sys.stdin:
    line = line.strip()
    try:
        key, value = line.split("\t")
        value = value.split(",")
        cur_avg, cur_count = float(value[0]), int(value[1])
    except Exception as e:
        continue
    avg = (count * avg + cur_avg * cur_count) / (count + cur_count)
    count = count + cur_count

print(str(avg))
