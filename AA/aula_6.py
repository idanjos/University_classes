import random
n = 1000
def mon(num_points):
	domain_area = 4
	inside_counter = 0
	for i in range (num_points):
		x = random.uniform(-1,1)
		y= random.uniform(-1,1)
		if x*x + y*y <= 1.0:
			inside_counter +=1
	return (inside_counter/num_points)*domain_area

#print(mon(n))

def monv2(num_points):
	domain_area = 4
	inside_counter = 0
	for i in range (num_points):
		x = random.uniform(0,1)
		y= random.uniform(0,1)
		if x*x + y*y <= 1.0:
			inside_counter +=1
	return (inside_counter/num_points)*domain_area

#print(monv2(n))
def isprimev2(P,K):
	a = random.randint(2,P-2)
	for i in range(0,K):
		if(a**(P-1)) % P != 1:
			return False
	return True


def isprime(n):
	if n == 1 or n == 2:
		return True
	for i in range(2,n):
		if (n % i) == 0:
			return False
	return True
n = 3000
k = 1000
for i in range(4,n):
	print(i) if isprimev2(i,k) else 0

