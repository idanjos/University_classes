

matrix = [[7,24],[3,12],[4,40],[5,25]]
fattasWeight = 10

n_maxes = 0
def nomedafuncao(i):
	global matrix
	global n_maxes
	if i == 0:
		return 0
	if i == 1:
		return matrix[0]
	n_maxes+=1
	return max(matrix[i] + nomedafuncao(i -2),nomedafuncao(i-1))


print(nomedafuncao(5))
print(n_maxes)
