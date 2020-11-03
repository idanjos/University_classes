

somas = 0
matrix = list()
i = 2
j = 2
import pprint
def init(i,j):
	global matrix
	global somas
	for i in range(0,i+1):
		matrix += [[]]
		for j in range(0,j+1):
			if i == 0 or j == 0:
				matrix[i]+=[1]
			else:
				somas += 2
				matrix[i]+=[matrix[i-1][j]+matrix[i][j-1]+matrix[i-1][j-1]]
	
def D(i,j):
	global somas
	if i == 0 or j == 0:
		return 1
	somas += 2
	return D(i-1,j)+D(i-1,j-1)+D(i,j-1)

#print(D(9,9))
init(i,j)
for i in range(0,i+1):
	pprint.pprint(matrix[i])


print(somas)