counter = 0
adds_counter = 0
def f1(n):
	global counter
	
	r = 0
	for i in range (1,n+1):
		r+=i
		counter +=1
	return r
    
def r1(n):
    
    assert(type(n)==int) and (n>=0), "Wrong arg"
    global adds_counter
    if n == 0:
        return 0
    adds_counter +=1
    return 1+ r1(n-1)

print(f1(4))
print(counter)
	