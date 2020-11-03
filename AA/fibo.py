add = 0
def fibo(n):
	global add
	if n<2:
		return n
	add+=1
	return fibo(n-1) + fibo(n-2)



def fiboL(n):
	global add
	if n<2:
		return n
	add+=1
	return fibo(n-1) + fibo(n-2)
for i in range(1,11):
	print(str(i)+"-"+str(fibo(i))+"-"+str(add))
