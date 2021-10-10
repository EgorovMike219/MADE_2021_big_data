import sys

sum_x_1 = 0
sum_x_2 = 0
count = 0
for line in sys.stdin:
    line = line.strip()
    try:
        key, value = line.split("\t")
        value = value.split(",")
        cur_sum_x_1, cur_sum_x_2, cur_count = int(value[0]), int(value[1]), int(value[2])
    except Exception as e:
        continue
    sum_x_1 += cur_sum_x_1
    sum_x_2 += cur_sum_x_2
    count += cur_count

var = 0
if count > 1:
    mean = sum_x_1 / count
    var = ((sum_x_2 - 2 * mean * sum_x_1 + count * (mean ** 2)) / (count - 1)) ** 0.5

print(str(var))