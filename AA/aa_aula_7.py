
import matplotlib.pyplot as plt
import random
def counterP(p=0.5):
	return 1 if random.random() < p else 0

x = []
y = []
for i in range(0,10000):
	counter = 0
	for j in range (0,10000):
		counter += counterP(1/32)
	#print("10^"+str(i)+":"+str(counter))
	x += ["10^"+str(i)]
	y+=[counter]

plt.hist(y)
plt.show()