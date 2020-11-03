import itertools
import time
import random
class Graph:
	#Constructor
	def __init__(self,matrix,CpuLimite=0,NSolutions=0,k=100):
		self.limitCpuTime = CpuLimite					# Return current result after limit reached
		self.numSolutions = NSolutions					# Number of current solutions
		self.matrix = matrix							# graphs
		self.bestSolution = 0							# Collection of nodes that forms the best solution
		self.iters = 0									# Number of operations
		self.solutions = set()							# Set of solutions, no repeats
		self.n = len(matrix[0])							# Calculating a solution for current N nodes
		self.combinations = [tuple(range(0,self.n))]	# Current combination list
		self.nodes = [x for x in range(0,self.n)]		# Nodes in a list
		self.bestNodes = self.getBestNode()				# Collection of best nodes
		self.rc = set()									# Reviewed combinations
		self.k = k
		
	# Returns a new n numbers between 1-(2^n) - 1
	def getNewCombs(self,n):
		# Variables
		output = []
		temp = []
		if 2**self.n <= n:
			return self.getAllCombinations()
		while(len(temp) < n):
			if len(self.rc) >= 2**self.n:
				break
			x = random.randint(1,((2**self.n)-1))
			
			if x not in self.rc:
				self.rc.add(x)
				temp +=[self.getBitComb(x)]
		for comb in sorted(temp,reverse=True):
			output += [comb[1]]
		
		return output
	
	# The number is a combination, where the bit position referes to the nodes included in the combination
	# 3 -> 000011, means that its a combination of node[-1] and node[-2]
	def getBitComb(self,num):
		# Variables
		output = []
		temp = num
		
		for i in range(0,self.n)[::-1]:
			if temp == 0:
				break
			if temp >= 2**i:
				temp -= 2**i
				output+=[self.nodes[i]]
			
		return (len(tuple(output)),tuple(output))


	#Returns all possible combinations, used for version 4
	def getAllCombinations(self):
		#Variables
		output = []
		nodes = self.n

		while nodes > 0:
			output+=list(itertools.combinations(range(0,self.n),nodes))
			nodes-=1
		return output

	#Get combinations for the 3rd version of the exhaustive algorithm
	def getCombinationsV3(self,n):
		if n>= self.n:
			return self.combinations
		temp = list(itertools.combinations(range(0,self.n),n))
		output = []
		for tup in temp:
			for node in self.bestNodes:
				if node in tup:
					output += [tup]
					break
		return output

	#Generate combinations of N nodes
	def getCombinations(self,n):
		return list(itertools.combinations(range(0,self.n),n)) if n < self.n else self.combinations

	#Returning the node the most 
	def getBestNode(self):
		# Variables
		bestNode = []
		best = 0
		temp = 0

		for line in range(0,len(self.matrix)):
			temp = 0
			if best > (len(self.matrix)-(line+1)):
				break
			for cell in self.matrix[line]:
				if cell == 1:
					temp += 1
			if temp > best:
				best = temp
				bestNode = [line]
			elif temp == best:
				bestNode += [line]
		return bestNode

	#Version 1
	def solveV1(self):
		# Variables
		startTime = time.time()
		cardinal = 0
		numNodes = self.n
		self.iters = 0
		self.bestSolution = []

		#Solve until considering 0 nodes.
		while numNodes >= 1:
			self.solutions = set()
			for perm in self.getCombinations(numNodes):
				if self.isClique(perm,startTime):
					self.iters += 1
					self.solutions.add(tuple(sorted(perm)))
					if cardinal == 0:
						self.iters += 3
						self.bestSolution = perm
						cardinal = len(perm)
					#Dont need to check 0, because self.s will always be > 0, here
					if self.numSolutions == len(self.solutions): 
							return (self.bestSolution,(time.time()-startTime),cardinal,len(self.solutions),self.iters)
							
				#Cpu time limiter
				if self.limitCpuTime > 0:
					if (time.time() - startTime) > self.limitCpuTime:
						return (self.bestSolution,(time.time()-startTime),cardinal,len(self.solutions),self.iters)
		
			numNodes-=1
			 # Acquire all solutions of Ki sub graphs, instead of all Kn sub graphs
			if len(self.solutions) > 0:
				return (self.bestSolution,(time.time()-startTime),cardinal,len(self.solutions),self.iters)		
				
		return (self.bestSolution,(time.time()-startTime),cardinal,len(self.solutions),self.iters)

	#Version 2
	def solveV2(self):
		# Variables
		startTime = time.time()
		cardinal = 0
		numNodes = self.n
		self.iters = 0
		self.bestSolution = []

		#Solve until considering 0 nodes.
		while numNodes >= 1:
			self.solutions = set()
			for perm in self.getCombinations(numNodes):
				if self.isClique2(perm,startTime):
					self.iters += 1
					self.solutions.add(tuple(sorted(perm)))
					if cardinal == 0:
						self.iters += 3
						self.bestSolution = perm
						cardinal = len(perm)
					#Dont need to check 0, because self.s will aklways be > 0, here
					if self.numSolutions == len(self.solutions): 
							return (self.bestSolution,(time.time()-startTime),cardinal,len(self.solutions),self.iters)
							
				#Cpu time limiter
				if self.limitCpuTime > 0:
					if (time.time() - startTime) > self.limitCpuTime:
						return (self.bestSolution,(time.time()-startTime),cardinal,len(self.solutions),self.iters)
						
			numNodes-=1
			 # Acquire all solutions of Ki sub graphs, instead of all Kn sub graphs
			if len(self.solutions) > 0:
				return (self.bestSolution,(time.time()-startTime),cardinal,len(self.solutions),self.iters)		

		return (self.bestSolution,(time.time()-startTime),cardinal,len(self.solutions),self.iters)
		
	#Version 3
	def solveV3(self):
		# Variables 
		startTime = time.time()
		cardinal = 0
		numNodes = self.n
		self.iters = 0
		self.bestSolution = []

		#Solve until considering 0 nodes.
		while numNodes >= 1:
			self.solutions = set()
			for perm in self.getCombinationsV3(numNodes):
				if self.isClique2(perm,startTime):
					self.iters += 1
					self.solutions.add(tuple(sorted(perm)))
					if cardinal == 0:
						self.iters += 3
						self.bestSolution = perm
						cardinal = len(perm)
					#Dont need to check 0, because self.s will aklways be > 0, here
					if self.numSolutions == len(self.solutions): 
							return (self.bestSolution,(time.time()-startTime),cardinal,len(self.solutions),self.iters)
							
				#Cpu time limiter
				if self.limitCpuTime > 0:
					if (time.time() - startTime) > self.limitCpuTime:
						return (self.bestSolution,(time.time()-startTime),cardinal,len(self.solutions),self.iters)
						
			numNodes-=1
			 # Acquire all solutions of Ki sub graphs, instead of all Kn sub graphs
			if len(self.solutions) > 0:
				return (self.bestSolution,(time.time()-startTime),cardinal,len(self.solutions),self.iters)	
	
		return (self.bestSolution,(time.time()-startTime),cardinal,len(self.solutions),self.iters)

	def solveV4(self):
		# Variables 
		startTime = time.time()
		cardinal = 0
		self.iters = 0
		self.bestSolution = []

		
		while True:
			self.solutions = set()
			for perm in self.getNewCombs(self.k):
				if self.isClique2(perm,startTime):
					self.iters += 1
					self.solutions.add(tuple(sorted(perm)))
					if cardinal == 0:
						self.iters += 3
						self.bestSolution = perm
						cardinal = len(perm)
					#Dont need to check 0, because self.s will aklways be > 0, here
					if self.numSolutions == len(self.solutions): 
							return (tuple(sorted(self.bestSolution)),(time.time()-startTime),cardinal,len(self.solutions),self.iters)
							
				#Cpu time limiter
				if self.limitCpuTime > 0:
					if (time.time() - startTime) > self.limitCpuTime:
						return (tuple(sorted(self.bestSolution)),(time.time()-startTime),cardinal,len(self.solutions),self.iters)
						
			
			 # Acquire all solutions of Ki sub graphs, instead of all Kn sub graphs
			if len(self.solutions) > 0:
				return (tuple(sorted(self.bestSolution)),(time.time()-startTime),cardinal,len(self.solutions),self.iters)	
			break;
		return (tuple(sorted(self.bestSolution)),(time.time()-startTime),cardinal,len(self.solutions),self.iters)

	# Verifying seq if is a clique version 2
	def isClique2(self,seq,startTime):
		
		for i in range(0,self.n):
			self.iters += 1
			if i not in seq:
				continue
			for j in range(i+1,self.n):
				self.iters += 1
				if j not in seq:
					continue
				self.iters += 1
				if self.matrix[i][j] == 0:
					return False
				if self.limitCpuTime > 0:
					if (time.time() - startTime) > self.limitCpuTime:
						return False
		return True

	# Verifying seq if is a clique version 1
	def isClique(self,seq,startTime):
		
		for i in range(0,self.n):
			self.iters += 1
			if i not in seq:
				continue
			for j in range(0,self.n):
				self.iters += 2
				if j not in seq:
					continue
				if self.matrix[i][j] == 0:
					return False
				if self.limitCpuTime > 0:
					if (time.time() - startTime) > self.limitCpuTime:
						return False
		return True

		