import os
import matplotlib.pyplot as plt
#print(os.system("python aa.py 5 -p 1.0"))
N=20
data = {}

data['x'] = range(12,N+1)
data['g1'] = [0 for i in range(12,N+1)]
data['g2'] = [0 for i in range(12,N+1)]
data['g3'] = [0 for i in range(12,N+1)]
data['g4'] = [0 for i in range(12,N+1)]
data['g1i'] = [0 for i in range(12,N+1)]
data['g2i'] = [0 for i in range(12,N+1)]
data['g3i'] = [0 for i in range(12,N+1)]
data['g4i'] = [0 for i in range(12,N+1)]
data['data'] = []
def firstSolution(N,p=0.5):
	global data
	#pprint.pprint(data)
	print("Find the first solution")
	for j in range(0,10):
		for i in range(12,N+1):
			print("N=%i"%(i))
			
			string = os.popen("python aa.py "+str(i)+" -s 1 -k 10000 -p " + str(p)).read()
				#string2 = os.system("python aa.py "+str(i)+" -p 1")
			temp = string.split("\n")[:-1]
			index = i-12
			print(eval(temp[0])[0][1])
			data['g1'][index] += float(eval(temp[0])[0][1])
			data['g2'][index] += float(eval(temp[0])[1][1])
			data['g3'][index] += float(eval(temp[0])[2][1])
			data['g4'][index] += float(eval(temp[0])[3][1])
			data['g1i'][index] = eval(temp[0])[0]
			data['g2i'][index] = eval(temp[0])[1]
			data['g3i'][index] = eval(temp[0])[2]
			data['g4i'][index] = eval(temp[0])[3]

			for line in temp:
				print(line)
				#print(string.split("\n")[:-1])
	print([x/10 for x in data['g1']])
	print([x/10 for x in data['g2']])
	print([x/10 for x in data['g3']])
	print([x/10 for x in data['g4']])


firstSolution(N,0.5)
line1,=plt.plot(data['x'],[x/10 for x in data['g1']],label="Graph 2")
line2,=plt.plot(data['x'],[x/10 for x in data['g2']],label="Graph 1")
line3,=plt.plot(data['x'],[x/10 for x in data['g3']],label="Graph 3")
line4,=plt.plot(data['x'],[x/10 for x in data['g4']],label="Graph 4")
plt.legend(handles=[line2,line1,line3,line4], loc='upper left')


for i in range(1,5):
	print("Graph "+str(i))
	print("N	Best solution 					Num. Operations		Time AVG (s)")
	for n in range(12,N+1):
		var =data['g'+str(i)+'i'][n-12]
		print("%s\t%s\t\t\t\t\t%s\t\t\t%s" % (n,var[0],var[4],var[1]))

plt.show()


	