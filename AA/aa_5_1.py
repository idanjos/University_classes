import random

def toss(p=0.5):
	return 1 if random.random() >p else 0


def rollDice(p=0):
	output = random.randint(0,5+p)
	return output if output <= 5 else 0 

result = [0]
for i in range(1,11):
	result += [0]

for k in range(0,1000000):
	heads = 0
	for n in range(0,10):
		heads +=1 if toss((0.55)) == 1 else 0
	result[heads] += 1

print(result)